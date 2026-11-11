package com.metiers.network;

import com.metiers.client.ClientHooks;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenMenuPacket {
    public final String job;
    public final long remainingMs;

    public OpenMenuPacket(String job, long remainingMs) {
        this.job = job;
        this.remainingMs = remainingMs;
    }

    public static void encode(OpenMenuPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.job);
        buf.writeLong(msg.remainingMs);
    }

    public static OpenMenuPacket decode(FriendlyByteBuf buf) {
        return new OpenMenuPacket(buf.readUtf(64), buf.readLong());
    }

    public static void handle(OpenMenuPacket msg, Supplier<NetworkEvent.Context> ctx) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHooks.openMenu(msg.job, msg.remainingMs));
    }
}
