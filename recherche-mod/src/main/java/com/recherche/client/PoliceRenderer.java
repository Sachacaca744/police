package com.recherche.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.recherche.RechercheMod;
import com.recherche.entity.PoliceEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class PoliceRenderer extends HumanoidMobRenderer<PoliceEntity, HumanoidModel<PoliceEntity>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(RechercheMod.MODID, "textures/entity/police.png");

    public PoliceRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(PoliceEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(PoliceEntity entity, float yaw, float partialTick, PoseStack ps, MultiBufferSource buf, int light) {
        // Bras tendus à deux mains quand il vise
        HumanoidModel.ArmPose pose = entity.isAggressive() ? HumanoidModel.ArmPose.BOW_AND_ARROW : HumanoidModel.ArmPose.ITEM;
        this.model.rightArmPose = pose;
        this.model.leftArmPose = pose;
        super.render(entity, yaw, partialTick, ps, buf, light);
    }
}
