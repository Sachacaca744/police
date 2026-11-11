package com.recherche.item;

import com.recherche.entity.BulletEntity;
import com.recherche.entity.ModEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/** Pistolet : clic droit pour tirer. Munitions = pépites de fer (gratuit en créatif). */
public class PistolItem extends Item {
    public PistolItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }
        boolean creative = player.getAbilities().instabuild;
        ItemStack ammo = null;
        if (!creative) {
            for (ItemStack s : player.getInventory().items) {
                if (s.is(Items.IRON_NUGGET)) {
                    ammo = s;
                    break;
                }
            }
            if (ammo == null) {
                if (!level.isClientSide) {
                    player.displayClientMessage(Component.literal("Plus de munitions (pépites de fer) !"), true);
                }
                return InteractionResultHolder.fail(stack);
            }
        }
        if (!level.isClientSide) {
            BulletEntity bullet = new BulletEntity(ModEntities.BULLET.get(), level);
            bullet.setOwner(player);
            bullet.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
            bullet.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3.5F, 0.5F);
            bullet.setDamage(6.0F);
            level.addFreshEntity(bullet);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 1.0F, 1.6F);
            if (ammo != null) ammo.shrink(1);
        }
        player.getCooldowns().addCooldown(this, 15);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
