package com.metiers.network;

import com.metiers.MetiersMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class Network {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MetiersMod.MODID, "main"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    public static void register() {
        int id = 0;
        CHANNEL.messageBuilder(RequestMenuPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(RequestMenuPacket::encode).decoder(RequestMenuPacket::decode)
                .consumerMainThread(RequestMenuPacket::handle).add();
        CHANNEL.messageBuilder(SelectJobPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(SelectJobPacket::encode).decoder(SelectJobPacket::decode)
                .consumerMainThread(SelectJobPacket::handle).add();
        CHANNEL.messageBuilder(OpenMenuPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(OpenMenuPacket::encode).decoder(OpenMenuPacket::decode)
                .consumerMainThread(OpenMenuPacket::handle).add();
    }
}
