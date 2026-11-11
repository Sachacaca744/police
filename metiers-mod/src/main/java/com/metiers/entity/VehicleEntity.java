package com.metiers.entity;

import com.metiers.job.Job;
import com.metiers.job.JobData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;

/** Véhicule rapide : voiture de police ou camion. Se conduit avec Z/S (avancer/reculer) et Q/D (tourner). */
public class VehicleEntity extends Mob {
    private final boolean truck;
    private UUID ownerId;
    private int generation;

    public VehicleEntity(EntityType<? extends VehicleEntity> type, Level level, boolean truck) {
        super(type, level);
        this.truck = truck;
        this.setPersistenceRequired();
    }

    public boolean isTruck() {
        return truck;
    }

    public void setOwner(UUID owner, int generation) {
        this.ownerId = owner;
        this.generation = generation;
    }

    public static AttributeSupplier.Builder createAttributes(boolean truck) {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, truck ? 0.28 : 0.45)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 0.6);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.ownerId != null && this.tickCount % 20 == 0) {
            ServerPlayer owner = ((ServerLevel) this.level()).getServer().getPlayerList().getPlayer(this.ownerId);
            if (owner != null) {
                Job job = JobData.getJob(owner);
                boolean wrongJob = truck ? job != Job.CHAUFFEUR : job != Job.POLICIER;
                if (wrongJob || JobData.getGeneration(owner) != this.generation) {
                    this.discard(); // le propriétaire a changé de métier : véhicule perdu
                }
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide) {
            player.startRiding(this);
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.level().isClientSide) return false;
        if (source.getEntity() instanceof Player p && (p.getUUID().equals(this.ownerId) || p.isCreative())) {
            this.discard(); // le propriétaire peut détruire son véhicule en le frappant
            return true;
        }
        return source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && super.hurt(source, amount);
    }

    @Override
    public LivingEntity getControllingPassenger() {
        if (this.getFirstPassenger() instanceof Player p) return p;
        return null;
    }

    public double getPassengersRidingOffset() {
        return truck ? 1.0 : 0.6;
    }

    @Override
    public void travel(Vec3 input) {
        LivingEntity driver = this.getControllingPassenger();
        if (!this.isAlive() || driver == null) {
            super.travel(input);
            return;
        }
        float fwd = driver.zza;
        this.yRotO = this.getYRot();
        if (fwd != 0.0F) {
            float dir = fwd > 0.0F ? 1.0F : -1.0F;
            this.setYRot(this.getYRot() - driver.xxa * 4.0F * dir);
        }
        this.setXRot(0.0F);
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.yBodyRot;

        if (this.isControlledByLocalInstance()) {
            this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
            super.travel(new Vec3(0.0, input.y, fwd > 0.0F ? fwd : fwd * 0.4F));
        } else {
            this.setDeltaMovement(Vec3.ZERO);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (ownerId != null) tag.putUUID("Owner", ownerId);
        tag.putInt("Gen", generation);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("Owner")) ownerId = tag.getUUID("Owner");
        generation = tag.getInt("Gen");
    }
}
