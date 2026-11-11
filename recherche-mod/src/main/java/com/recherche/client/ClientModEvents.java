package com.recherche.client;

import com.recherche.RechercheMod;
import com.recherche.entity.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RechercheMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void onRenderers(EntityRenderersEvent.RegisterRenderers e) {
        e.registerEntityRenderer(ModEntities.POLICE.get(), PoliceRenderer::new);
        e.registerEntityRenderer(ModEntities.BULLET.get(), BulletRenderer::new);
    }

    /** Étoiles de recherche en haut à droite, qui clignotent rouge/bleu comme dans GTA. */
    @SubscribeEvent
    public static void onOverlays(RegisterGuiOverlaysEvent e) {
        e.registerAboveAll("wanted", (gui, g, partialTick, width, height) -> {
            int s = ClientWanted.stars;
            if (s <= 0) return;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                sb.append(i < s ? "★" : "☆");
            }
            String text = sb.toString();
            Font font = Minecraft.getInstance().font;
            int color = ((System.currentTimeMillis() / 400L) % 2L == 0L) ? 0xFF4040 : 0x4060FF;
            float scale = 2.0F;
            g.pose().pushPose();
            g.pose().translate(width - 10 - font.width(text) * scale, 10.0F, 0.0F);
            g.pose().scale(scale, scale, 1.0F);
            g.drawString(font, text, 0, 0, color, true);
            g.pose().popPose();
        });
    }
}
