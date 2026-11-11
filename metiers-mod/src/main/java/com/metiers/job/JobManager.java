package com.metiers.job;

import com.metiers.entity.ModEntities;
import com.metiers.entity.VehicleEntity;
import com.metiers.network.Network;
import com.metiers.network.OpenMenuPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

public final class JobManager {
    private JobManager() {}

    public static void openMenu(ServerPlayer p) {
        Network.CHANNEL.send(PacketDistributor.PLAYER.with(() -> p),
                new OpenMenuPacket(JobData.getJob(p).id, JobData.remainingMs(p)));
    }

    public static void tryChange(ServerPlayer p, Job job) {
        if (job == null) return;
        Job current = JobData.getJob(p);
        if (job == current) {
            p.displayClientMessage(Component.literal("Tu es déjà " + job.display + " !"), true);
            return;
        }
        long remaining = JobData.remainingMs(p);
        if (remaining > 0) {
            p.displayClientMessage(Component.literal("Tu pourras changer de métier dans " + format(remaining) + "."), true);
            return;
        }
        JobData.setJob(p, job);
        JobData.setNextChange(p, System.currentTimeMillis() + JobData.COOLDOWN_MS);
        JobData.bumpGeneration(p); // supprime les anciens véhicules
        kit(p, job);
        giveVehicle(p, job);
        p.sendSystemMessage(Component.literal("§6[Métiers] §fTu es maintenant §e" + job.display + "§f. " + job.description));
    }

    public static void respawnVehicle(ServerPlayer p) {
        Job job = JobData.getJob(p);
        if (job != Job.POLICIER && job != Job.CHAUFFEUR) {
            p.sendSystemMessage(Component.literal("§cTon métier n'a pas de véhicule."));
            return;
        }
        JobData.bumpGeneration(p);
        giveVehicle(p, job);
        p.sendSystemMessage(Component.literal("§aTon véhicule a été renvoyé."));
    }

    public static void giveVehicle(ServerPlayer p, Job job) {
        EntityType<VehicleEntity> type;
        if (job == Job.CHAUFFEUR) type = ModEntities.TRUCK.get();
        else if (job == Job.POLICIER) type = ModEntities.POLICE_CAR.get();
        else return;

        ServerLevel level = p.serverLevel();
        VehicleEntity v = type.create(level);
        if (v == null) return;
        Vec3 look = p.getLookAngle();
        v.moveTo(p.getX() + look.x * 3.0, p.getY(), p.getZ() + look.z * 3.0, p.getYRot(), 0.0F);
        v.setOwner(p.getUUID(), JobData.getGeneration(p));
        level.addFreshEntity(v);
    }

    public static void give(ServerPlayer p, ItemStack s) {
        if (!p.getInventory().add(s)) {
            p.drop(s, false);
        }
    }

    private static void kit(ServerPlayer p, Job job) {
        switch (job) {
            case POMPIER -> {
                give(p, new ItemStack(Items.WATER_BUCKET));
                give(p, new ItemStack(Items.IRON_AXE));
            }
            case POLICIER -> {
                give(p, new ItemStack(Items.IRON_SWORD));
                give(p, new ItemStack(Items.SHIELD));
                give(p, new ItemStack(Items.COOKED_BEEF, 16));
            }
            case MEDECIN -> {
                give(p, new ItemStack(Items.GOLDEN_APPLE, 3));
                give(p, new ItemStack(Items.MILK_BUCKET));
            }
            case COMMERCANT -> give(p, new ItemStack(Items.EMERALD, 32));
            case CHAUFFEUR -> give(p, new ItemStack(Items.COOKED_CHICKEN, 16));
            case CRIMINEL -> {
                give(p, new ItemStack(Items.STONE_SWORD));
                give(p, new ItemStack(Items.ENDER_PEARL, 2));
            }
            case AGRICULTEUR -> {
                give(p, new ItemStack(Items.IRON_HOE));
                give(p, new ItemStack(Items.WHEAT_SEEDS, 32));
                give(p, new ItemStack(Items.BONE_MEAL, 16));
            }
            case MINEUR -> {
                give(p, new ItemStack(Items.IRON_PICKAXE));
                give(p, new ItemStack(Items.TORCH, 32));
                give(p, new ItemStack(Items.BREAD, 8));
            }
            default -> {}
        }
    }

    public static String format(long ms) {
        long totalMin = (ms + 59_999L) / 60_000L;
        long h = totalMin / 60;
        long m = totalMin % 60;
        if (h > 0) return h + " h " + m + " min";
        return m + " min";
    }
}
