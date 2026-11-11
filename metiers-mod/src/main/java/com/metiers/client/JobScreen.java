package com.metiers.client;

import com.metiers.job.Job;
import com.metiers.job.JobManager;
import com.metiers.network.Network;
import com.metiers.network.SelectJobPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class JobScreen extends Screen {
    private final Job current;
    private final long remainingMs;
    private boolean civilPage = false;

    public JobScreen(String currentId, long remainingMs) {
        super(Component.literal("Choisis ton métier"));
        Job j = Job.byId(currentId);
        this.current = j == null ? Job.CIVIL : j;
        this.remainingMs = remainingMs;
    }

    @Override
    protected void init() {
        // null = bouton spécial (sous-menu Civil / Retour)
        Job[] list = civilPage
                ? new Job[]{Job.CIVIL, Job.CHAUFFEUR, Job.CRIMINEL, Job.AGRICULTEUR, Job.MINEUR, null}
                : new Job[]{Job.POMPIER, Job.POLICIER, Job.MEDECIN, Job.COMMERCANT, Job.CHOMEUR, null};

        int w = 150, h = 20, gap = 6;
        int x0 = this.width / 2 - w - gap / 2;
        int y0 = this.height / 2 - 40;

        for (int i = 0; i < list.length; i++) {
            int x = x0 + (i % 2) * (w + gap);
            int y = y0 + (i / 2) * (h + gap);
            Job job = list[i];
            Button b;
            if (job == null) {
                b = Button.builder(Component.literal(civilPage ? "< Retour" : "Civil >"), btn -> {
                    civilPage = !civilPage;
                    this.rebuildWidgets();
                }).bounds(x, y, w, h).build();
            } else {
                String label = job.display + (job == current ? " (actuel)" : "");
                b = Button.builder(Component.literal(label), btn -> select(job))
                        .bounds(x, y, w, h)
                        .tooltip(Tooltip.create(Component.literal(job.description)))
                        .build();
                b.active = remainingMs <= 0 && job != current;
            }
            this.addRenderableWidget(b);
        }
    }

    private void select(Job job) {
        Network.CHANNEL.sendToServer(new SelectJobPacket(job.id));
        this.onClose();
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g);
        g.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 85, 0xFFFFFF);
        g.drawCenteredString(this.font, Component.literal("Métier actuel : " + current.display),
                this.width / 2, this.height / 2 - 68, 0xFFD700);
        String info = remainingMs > 0
                ? "Prochain changement possible dans " + JobManager.format(remainingMs)
                : "Tu peux changer de métier (ensuite : 2 h d'attente)";
        g.drawCenteredString(this.font, Component.literal(info), this.width / 2, this.height / 2 - 55,
                remainingMs > 0 ? 0xFF7777 : 0x77FF77);
        super.render(g, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
