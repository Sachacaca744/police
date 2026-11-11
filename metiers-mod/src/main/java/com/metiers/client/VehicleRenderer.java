package com.metiers.client;

import com.metiers.entity.VehicleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** Modèle "vite fait" : le véhicule est dessiné avec des blocs de béton colorés (bleu = police, gris = camion). */
public class VehicleRenderer extends EntityRenderer<VehicleEntity> {
    private final BlockRenderDispatcher blocks;

    public VehicleRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.blocks = ctx.getBlockRenderDispatcher();
        this.shadowRadius = 1.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(VehicleEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }

    @Override
    public void render(VehicleEntity e, float yaw, float partialTick, PoseStack ps, MultiBufferSource buf, int light) {
        ps.pushPose();
        ps.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        // Repère local : l'avant du véhicule est vers -Z
        BlockState black = Blocks.BLACK_CONCRETE.defaultBlockState();
        if (e.isTruck()) {
            BlockState gray = Blocks.GRAY_CONCRETE.defaultBlockState();
            BlockState lightGray = Blocks.LIGHT_GRAY_CONCRETE.defaultBlockState();
            box(ps, buf, light, gray, 0.0F, 0.35F, 0.9F, 1.8F, 0.3F, 3.4F);      // châssis
            box(ps, buf, light, gray, 0.0F, 0.55F, 0.05F, 2.0F, 1.4F, 1.3F);      // cabine
            box(ps, buf, light, lightGray, 0.0F, 0.55F, 1.7F, 2.0F, 1.9F, 2.0F);  // remorque
            for (int sx = -1; sx <= 1; sx += 2) {
                box(ps, buf, light, black, 1.0F * sx, 0.0F, 0.2F, 0.3F, 0.8F, 0.8F);
                box(ps, buf, light, black, 1.0F * sx, 0.0F, 2.0F, 0.3F, 0.8F, 0.8F);
            }
        } else {
            BlockState blue = Blocks.BLUE_CONCRETE.defaultBlockState();
            BlockState red = Blocks.RED_CONCRETE.defaultBlockState();
            box(ps, buf, light, blue, 0.0F, 0.3F, 0.0F, 1.8F, 0.6F, 3.0F);        // carrosserie
            box(ps, buf, light, blue, 0.0F, 0.9F, 0.25F, 1.6F, 0.5F, 1.5F);       // habitacle
            box(ps, buf, light, red, 0.0F, 1.4F, 0.25F, 0.8F, 0.12F, 0.3F);       // gyrophare
            for (int sx = -1; sx <= 1; sx += 2) {
                for (int sz = -1; sz <= 1; sz += 2) {
                    box(ps, buf, light, black, 0.95F * sx, 0.0F, 0.95F * sz, 0.3F, 0.6F, 0.6F);
                }
            }
        }
        ps.popPose();
        super.render(e, yaw, partialTick, ps, buf, light);
    }

    /** Boîte centrée en (x, z), posée à la hauteur y. */
    private void box(PoseStack ps, MultiBufferSource buf, int light, BlockState state,
                     float x, float y, float z, float w, float h, float l) {
        ps.pushPose();
        ps.translate(x - w / 2.0F, y, z - l / 2.0F);
        ps.scale(w, h, l);
        blocks.renderSingleBlock(state, ps, buf, light, OverlayTexture.NO_OVERLAY);
        ps.popPose();
    }
}
