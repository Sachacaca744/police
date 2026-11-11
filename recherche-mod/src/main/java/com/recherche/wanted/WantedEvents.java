package com.recherche.wanted;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.recherche.RechercheMod;
import com.recherche.entity.PoliceEntity;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RechercheMod.MODID)
public class WantedEvents {

    /** Frapper ou tuer l'un d'eux = crime. */
    private static boolean isProtected(Entity e) {
        return e instanceof Player || e instanceof AbstractVillager || e instanceof PoliceEntity;
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent e) {
        if (!(e.getSource().getEntity() instanceof ServerPlayer p)) return;
        LivingEntity victim = e.getEntity();
        if (victim == p || !isProtected(victim)) return;
        WantedManager.crimeHit(p, victim instanceof PoliceEntity);
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent e) {
        if (e.getEntity() instanceof ServerPlayer dead) {
            WantedManager.set(dead, 0); // "Wasted" : la recherche est remise à zéro
        }
        if (e.getSource().getEntity() instanceof ServerPlayer killer
                && e.getEntity() != killer && isProtected(e.getEntity())) {
            WantedManager.crimeKill(killer, e.getEntity() instanceof PoliceEntity);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END || !(e.player instanceof ServerPlayer p)) return;
        if (p.level().getGameTime() % 20 == 0) {
            WantedManager.tickSecond(p);
        }
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getEntity() instanceof ServerPlayer p) {
            WantedManager.forget(p.getUUID());
            WantedManager.sync(p);
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent e) {
        WantedManager.forget(e.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent e) {
        WantedManager.clearAll();
    }

    /** /recherche set <0-5> (ops) pour tester sans commettre de crime. */
    @SubscribeEvent
    public static void onCommands(RegisterCommandsEvent e) {
        e.getDispatcher().register(Commands.literal("recherche")
                .requires(s -> s.hasPermission(2))
                .then(Commands.literal("set")
                        .then(Commands.argument("etoiles", IntegerArgumentType.integer(0, 5))
                                .executes(c -> {
                                    ServerPlayer p = c.getSource().getPlayerOrException();
                                    int n = IntegerArgumentType.getInteger(c, "etoiles");
                                    WantedManager.set(p, n);
                                    p.sendSystemMessage(Component.literal("Niveau de recherche : " + n + " ★"));
                                    return 1;
                                }))));
    }
}
