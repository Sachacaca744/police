package com.metiers.client;

import com.metiers.MetiersMod;
import com.metiers.network.Network;
import com.metiers.network.RequestMenuPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MetiersMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEvents {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        while (ClientModEvents.OPEN_KEY.consumeClick()) {
            if (mc.player != null && mc.screen == null) {
                Network.CHANNEL.sendToServer(new RequestMenuPacket());
            }
        }
    }

    /** "P" est aussi la touche vanilla des interactions sociales : on la bloque pour laisser P au menu des métiers. */
    @SubscribeEvent
    public static void onScreenOpening(ScreenEvent.Opening e) {
        if (e.getNewScreen() instanceof SocialInteractionsScreen
                && ClientModEvents.OPEN_KEY.isDown()
                && Minecraft.getInstance().options.keySocialInteractions.isDown()) {
            e.setCanceled(true);
        }
    }
}
