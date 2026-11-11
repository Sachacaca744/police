package com.recherche.item;

import com.recherche.RechercheMod;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, RechercheMod.MODID);

    public static final RegistryObject<Item> PISTOL =
            ITEMS.register("pistol", () -> new PistolItem(new Item.Properties().stacksTo(1)));

    @Mod.EventBusSubscriber(modid = RechercheMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Tabs {
        @SubscribeEvent
        public static void onTab(BuildCreativeModeTabContentsEvent e) {
            if (e.getTabKey() == CreativeModeTabs.COMBAT) {
                e.accept(PISTOL);
            }
        }
    }
}
