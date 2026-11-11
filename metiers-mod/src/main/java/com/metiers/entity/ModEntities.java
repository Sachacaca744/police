package com.metiers.entity;

import com.metiers.MetiersMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MetiersMod.MODID);

    public static final RegistryObject<EntityType<VehicleEntity>> POLICE_CAR = ENTITIES.register("police_car",
            () -> EntityType.Builder.<VehicleEntity>of((t, l) -> new VehicleEntity(t, l, false), MobCategory.MISC)
                    .sized(1.9F, 1.5F).clientTrackingRange(10).build("police_car"));

    public static final RegistryObject<EntityType<VehicleEntity>> TRUCK = ENTITIES.register("truck",
            () -> EntityType.Builder.<VehicleEntity>of((t, l) -> new VehicleEntity(t, l, true), MobCategory.MISC)
                    .sized(2.2F, 2.2F).clientTrackingRange(10).build("truck"));

    @Mod.EventBusSubscriber(modid = MetiersMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Attributes {
        @SubscribeEvent
        public static void onAttributes(EntityAttributeCreationEvent e) {
            e.put(POLICE_CAR.get(), VehicleEntity.createAttributes(false).build());
            e.put(TRUCK.get(), VehicleEntity.createAttributes(true).build());
        }
    }
}
