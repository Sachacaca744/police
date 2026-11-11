package com.recherche.network;

import com.recherche.client.ClientWanted;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class WantedPacket {
    public final int stars;

    public WantedPacket(int stars) {
        this.stars = stars;
    }

    public static void encode(WantedPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.stars);
    }

    public static WantedPacket decode(FriendlyByteBuf buf) {
        return new WantedPacket(buf.readInt());
    }

    public static void handle(WantedPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ClientWanted.stars = msg.stars;
    }
}
