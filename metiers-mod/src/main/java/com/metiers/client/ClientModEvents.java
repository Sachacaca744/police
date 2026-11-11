package com.metiers.client;

import com.metiers.MetiersMod;
import com.metiers.entity.ModEntities;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MetiersMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    public static final KeyMapping OPEN_KEY =
            new KeyMapping("key.metiers.open", InputConstants.KEY_P, "key.categories.metiers");

    @SubscribeEvent
    public static void onKeys(RegisterKeyMappingsEvent e) {
        e.register(OPEN_KEY);
    }

    @SubscribeEvent
    public static void onRenderers(EntityRenderersEvent.RegisterRenderers e) {
        e.registerEntityRenderer(ModEntities.POLICE_CAR.get(), VehicleRenderer::new);
        e.registerEntityRenderer(ModEntities.TRUCK.get(), VehicleRenderer::new);
    }
}
