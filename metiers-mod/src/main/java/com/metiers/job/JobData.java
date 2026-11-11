package com.metiers.job;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public final class JobData {
    public static final long COOLDOWN_MS = 2L * 60L * 60L * 1000L; // 2 heures

    private JobData() {}

    private static CompoundTag tag(Player p) {
        CompoundTag root = p.getPersistentData();
        if (!root.contains(Player.PERSISTED_NBT_TAG, 10)) {
            root.put(Player.PERSISTED_NBT_TAG, new CompoundTag());
        }
        CompoundTag persisted = root.getCompound(Player.PERSISTED_NBT_TAG);
        if (!persisted.contains("metiers", 10)) {
            persisted.put("metiers", new CompoundTag());
        }
        return persisted.getCompound("metiers");
    }

    public static Job getJob(Player p) {
        Job j = Job.byId(tag(p).getString("job"));
        return j == null ? Job.CIVIL : j;
    }

    public static void setJob(Player p, Job job) {
        tag(p).putString("job", job.id);
    }

    public static long getNextChange(Player p) {
        return tag(p).getLong("next");
    }

    public static void setNextChange(Player p, long time) {
        tag(p).putLong("next", time);
    }

    public static long remainingMs(Player p) {
        return Math.max(0L, getNextChange(p) - System.currentTimeMillis());
    }

    public static int getGeneration(Player p) {
        return tag(p).getInt("gen");
    }

    public static void bumpGeneration(Player p) {
        tag(p).putInt("gen", getGeneration(p) + 1);
    }

    public static long getHealCooldown(Player p) {
        return tag(p).getLong("healcd");
    }

    public static void setHealCooldown(Player p, long t) {
        tag(p).putLong("healcd", t);
    }
}
