package com.recherche.entity;

import com.recherche.wanted.WantedManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.Level;

import java.util.UUID;

/** Policier : poursuit le joueur recherché et lui tire dessus. */
public class PoliceEntity extends PathfinderMob implements RangedAttackMob {
    private UUID wantedPlayer;

    public PoliceEntity(EntityType<? extends PoliceEntity> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob_createBase()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.MOVEMENT_SPEED, 0.30)
                .add(Attributes.FOLLOW_RANGE, 64.0)
                .add(Attributes.ARMOR, 4.0);
    }

    private static AttributeSupplier.Builder Mob_createBase() {
        return net.minecraft.world.entity.Mob.createMobAttributes();
    }

    public void setWantedPlayer(UUID id) {
        this.wantedPlayer = id;
    }

    public UUID getWantedPlayer() {
        return wantedPlayer;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.1D, 20, 15.0F));
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide || this.tickCount % 10 != 0) return;

        ServerPlayer target = null;
        if (wantedPlayer != null) {
            target = ((ServerLevel) this.level()).getServer().getPlayerList().getPlayer(wantedPlayer);
        }
        // Plus de recherche, joueur parti, ou trop loin : le policier disparaît
        if (target == null || WantedManager.get(target.getUUID()) <= 0
                || target.level() != this.level() || this.distanceToSqr(target) > 56.0 * 56.0) {
            this.discard();
            return;
        }
        if (this.getTarget() != target) {
            this.setTarget(target);
        }
    }

    /** Le policier ne tire QUE sur sa cible (le joueur recherché). */
    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        BulletEntity bullet = new BulletEntity(ModEntities.BULLET.get(), this.level());
        bullet.setOwner(this);
        bullet.setPos(this.getX(), this.getEyeY() - 0.1, this.getZ());
        double dx = target.getX() - this.getX();
        double dy = target.getY(0.5D) - bullet.getY();
        double dz = target.getZ() - this.getZ();
        bullet.shoot(dx, dy, dz, 3.0F, 5.0F);
        bullet.setDamage(3.0F);
        this.level().addFreshEntity(bullet);
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.HOSTILE, 1.0F, 1.4F);
    }
}
