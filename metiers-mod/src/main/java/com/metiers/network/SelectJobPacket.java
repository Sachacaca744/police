package com.metiers.network;

import com.metiers.job.Job;
import com.metiers.job.JobManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SelectJobPacket {
    public final String jobId;

    public SelectJobPacket(String jobId) {
        this.jobId = jobId;
    }

    public static void encode(SelectJobPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.jobId);
    }

    public static SelectJobPacket decode(FriendlyByteBuf buf) {
        return new SelectJobPacket(buf.readUtf(64));
    }

    public static void handle(SelectJobPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer sp = ctx.get().getSender();
        if (sp != null) JobManager.tryChange(sp, Job.byId(msg.jobId));
    }
}
