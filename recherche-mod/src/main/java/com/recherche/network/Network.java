package com.recherche.network;

import com.recherche.RechercheMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class Network {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(RechercheMod.MODID, "main"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    public static void register() {
        CHANNEL.messageBuilder(WantedPacket.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(WantedPacket::encode).decoder(WantedPacket::decode)
                .consumerMainThread(WantedPacket::handle).add();
    }
}
