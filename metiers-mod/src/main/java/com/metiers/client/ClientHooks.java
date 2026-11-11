package com.metiers.client;

import net.minecraft.client.Minecraft;

public class ClientHooks {
    public static void openMenu(String job, long remainingMs) {
        Minecraft.getInstance().setScreen(new JobScreen(job, remainingMs));
    }
}
