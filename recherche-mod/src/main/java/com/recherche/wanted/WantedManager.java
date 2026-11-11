package com.recherche.wanted;

import com.recherche.entity.ModEntities;
import com.recherche.entity.PoliceEntity;
import com.recherche.item.ModItems;
import com.recherche.network.Network;
import com.recherche.network.WantedPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Gère le niveau de recherche (0 à 5 étoiles) de chaque joueur. Les données ne sont pas sauvegardées. */
public final class WantedManager {
    public static final int MAX_STARS = 5;
    /** Secondes sans être vu par un policier avant de perdre une étoile. */
    public static final int CALM_SECONDS = 40;

    // [0] = étoiles, [1] = secondes de calme
    private static final Map<UUID, int[]> DATA = new HashMap<>();

    private WantedManager() {}

    public static int get(UUID id) {
        int[] d = DATA.get(id);
        return d == null ? 0 : d[0];
    }

    public static void set(ServerPlayer p, int stars) {
        stars = Mth.clamp(stars, 0, MAX_STARS);
        if (stars == 0) {
            DATA.remove(p.getUUID());
        } else {
            DATA.computeIfAbsent(p.getUUID(), k -> new int[2]);
            DATA.get(p.getUUID())[0] = stars;
            DATA.get(p.getUUID())[1] = 0;
        }
        sync(p);
    }

    public static void forget(UUID id) {
        DATA.remove(id);
    }

    public static void clearAll() {
        DATA.clear();
    }

    public static void sync(ServerPlayer p) {
        Network.CHANNEL.send(PacketDistributor.PLAYER.with(() -> p), new WantedPacket(get(p.getUUID())));
    }

    private static boolean exempt(ServerPlayer p) {
        return p.isCreative() || p.isSpectator();
    }

    /** Le joueur a frappé quelqu'un (joueur, villageois ou policier). */
    public static void crimeHit(ServerPlayer p, boolean victimIsCop) {
        if (exempt(p)) return;
        int cur = get(p.getUUID());
        raise(p, cur, Math.max(cur, victimIsCop ? 2 : 1));
    }

    /** Le joueur a tué quelqu'un (joueur, villageois ou policier). */
    public static void crimeKill(ServerPlayer p, boolean victimIsCop) {
        if (exempt(p)) return;
        int cur = get(p.getUUID());
        int target = victimIsCop ? Math.max(cur + 2, 3) : Math.max(cur + 1, 2);
        raise(p, cur, target);
    }

    private static void raise(ServerPlayer p, int cur, int target) {
        target = Math.min(target, MAX_STARS);
        if (target > cur) {
            set(p, target);
            p.displayClientMessage(Component.literal(cur == 0
                    ? "§c★ Tu es recherché par la police !"
                    : "§cNiveau de recherche : " + target + " ★"), true);
        } else if (cur > 0) {
            DATA.get(p.getUUID())[1] = 0; // crime récent : le compte à rebours repart
        }
    }

    /** Appelé chaque seconde pour chaque joueur : baisse des étoiles + apparition des policiers. */
    public static void tickSecond(ServerPlayer p) {
        int[] d = DATA.get(p.getUUID());
        if (d == null) return;
        if (exempt(p)) {
            set(p, 0);
            return;
        }
        ServerLevel level = p.serverLevel();
        List<PoliceEntity> cops = level.getEntitiesOfClass(PoliceEntity.class, p.getBoundingBox().inflate(64.0),
                c -> p.getUUID().equals(c.getWantedPlayer()));

        // Perdre les policiers de vue pendant assez longtemps = perdre une étoile
        boolean seen = false;
        for (PoliceEntity c : cops) {
            if (c.hasLineOfSight(p)) {
                seen = true;
                break;
            }
        }
        if (seen) {
            d[1] = 0;
        } else {
            d[1]++;
            if (d[1] >= CALM_SECONDS) {
                int next = d[0] - 1;
                set(p, next);
                p.displayClientMessage(Component.literal(next > 0
                        ? "§eTu as perdu une étoile (" + next + " ★)"
                        : "§aTu as semé la police !"), true);
                return;
            }
        }

        int desired = d[0] + 1;
        if (cops.size() < desired) {
            spawnCop(level, p);
        }
    }

    private static void spawnCop(ServerLevel level, ServerPlayer p) {
        RandomSource r = level.random;
        for (int i = 0; i < 12; i++) {
            double angle = r.nextDouble() * Math.PI * 2.0;
            double dist = 14 + r.nextInt(10);
            int x = Mth.floor(p.getX() + Math.cos(angle) * dist);
            int z = Mth.floor(p.getZ() + Math.sin(angle) * dist);
            int baseY = p.getBlockY();
            if (!level.hasChunkAt(new BlockPos(x, baseY, z))) continue;
            for (int dy = 6; dy >= -8; dy--) {
                BlockPos pos = new BlockPos(x, baseY + dy, z);
                if (level.getBlockState(pos).isAir()
                        && level.getBlockState(pos.above()).isAir()
                        && level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)) {
                    PoliceEntity cop = ModEntities.POLICE.get().create(level);
                    if (cop == null) return;
                    cop.moveTo(x + 0.5, pos.getY(), z + 0.5, r.nextFloat() * 360.0F, 0.0F);
                    cop.setWantedPlayer(p.getUUID());
                    cop.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.PISTOL.get()));
                    level.addFreshEntity(cop);
                    return;
                }
            }
        }
    }
}
