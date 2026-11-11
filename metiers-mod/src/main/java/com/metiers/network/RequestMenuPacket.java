package com.metiers.network;

import com.metiers.job.JobManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RequestMenuPacket {
    public static void encode(RequestMenuPacket msg, FriendlyByteBuf buf) {}

    public static RequestMenuPacket decode(FriendlyByteBuf buf) {
        return new RequestMenuPacket();
    }

    public static void handle(RequestMenuPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer sp = ctx.get().getSender();
        if (sp != null) JobManager.openMenu(sp);
    }
}
