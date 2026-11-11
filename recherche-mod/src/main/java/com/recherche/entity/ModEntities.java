package com.recherche.entity;

import com.recherche.RechercheMod;
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
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, RechercheMod.MODID);

    public static final RegistryObject<EntityType<PoliceEntity>> POLICE = ENTITIES.register("police",
            () -> EntityType.Builder.<PoliceEntity>of(PoliceEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.95F).clientTrackingRange(10).build("police"));

    public static final RegistryObject<EntityType<BulletEntity>> BULLET = ENTITIES.register("bullet",
            () -> EntityType.Builder.<BulletEntity>of(BulletEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(8).updateInterval(1).build("bullet"));

    @Mod.EventBusSubscriber(modid = RechercheMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Attributes {
        @SubscribeEvent
        public static void onAttributes(EntityAttributeCreationEvent e) {
            e.put(POLICE.get(), PoliceEntity.createAttributes().build());
        }
    }
}
