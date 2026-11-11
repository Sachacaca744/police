package com.recherche.client;

import com.recherche.entity.BulletEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/** La balle est invisible : seule sa traînée de particules se voit. */
public class BulletRenderer extends EntityRenderer<BulletEntity> {
    public BulletRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public ResourceLocation getTextureLocation(BulletEntity entity) {
        return new ResourceLocation("textures/entity/projectiles/arrow.png");
    }
}
