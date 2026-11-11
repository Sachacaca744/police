package com.metiers.job;

import com.metiers.MetiersMod;
import com.metiers.entity.VehicleEntity;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MetiersMod.MODID)
public class JobEvents {

    // ---------- Commandes : /metier et /métier ----------
    @SubscribeEvent
    public static void onCommands(RegisterCommandsEvent e) {
        register(e.getDispatcher(), "metier");
        register(e.getDispatcher(), "métier");
    }

    private static void register(CommandDispatcher<CommandSourceStack> d, String name) {
        d.register(Commands.literal(name)
                .executes(c -> {
                    JobManager.openMenu(c.getSource().getPlayerOrException());
                    return 1;
                })
                .then(Commands.literal("vehicule").executes(c -> {
                    JobManager.respawnVehicle(c.getSource().getPlayerOrException());
                    return 1;
                }))
                .then(Commands.literal("reset").requires(s -> s.hasPermission(2)).executes(c -> {
                    ServerPlayer p = c.getSource().getPlayerOrException();
                    JobData.setNextChange(p, 0L);
                    p.sendSystemMessage(Component.literal("§aDélai de changement de métier réinitialisé."));
                    return 1;
                })));
    }

    // ---------- Garder les données après la mort ----------
    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone e) {
        CompoundTag old = e.getOriginal().getPersistentData();
        if (old.contains(Player.PERSISTED_NBT_TAG, 10)) {
            e.getEntity().getPersistentData().put(Player.PERSISTED_NBT_TAG,
                    old.getCompound(Player.PERSISTED_NBT_TAG).copy());
        }
    }

    // ---------- Policier : ne peut pas tuer de villageois ----------
    @SubscribeEvent
    public static void onAttack(LivingAttackEvent e) {
        if (e.getSource().getEntity() instanceof Player p
                && e.getEntity() instanceof AbstractVillager
                && JobData.getJob(p) == Job.POLICIER) {
            if (!p.level().isClientSide) {
                p.displayClientMessage(Component.literal("§cUn policier ne peut pas blesser un villageois !"), true);
            }
            e.setCanceled(true);
        }
    }

    // ---------- Policier : +50% de dégâts contre les criminels ----------
    @SubscribeEvent
    public static void onHurt(LivingHurtEvent e) {
        if (e.getSource().getEntity() instanceof Player cop
                && e.getEntity() instanceof Player target
                && JobData.getJob(cop) == Job.POLICIER
                && JobData.getJob(target) == Job.CRIMINEL) {
            e.setAmount(e.getAmount() * 1.5F);
        }
    }

    // ---------- Effets passifs ----------
    private static void effect(ServerPlayer p, MobEffect eff, int amp) {
        p.addEffect(new MobEffectInstance(eff, 100, amp, true, false, true));
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END || !(e.player instanceof ServerPlayer p)) return;
        long t = p.level().getGameTime();
        if (t % 40 != 0) return;
        switch (JobData.getJob(p)) {
            case POMPIER -> {
                effect(p, MobEffects.FIRE_RESISTANCE, 0);
                if (p.isOnFire()) p.clearFire();
            }
            case MINEUR -> effect(p, MobEffects.DIG_SPEED, 0);
            case CRIMINEL -> effect(p, MobEffects.MOVEMENT_SPEED, 0);
            case COMMERCANT -> effect(p, MobEffects.HERO_OF_THE_VILLAGE, 1);
            case CHOMEUR -> {
                if (t % 12000 == 0) {
                    JobManager.give(p, new ItemStack(Items.BREAD, 3));
                    p.displayClientMessage(Component.literal("Allocation chômage : 3 pains !"), true);
                }
            }
            default -> {}
        }
    }

    // ---------- Médecin : shift + clic droit pour soigner ----------
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract e) {
        Player pl = e.getEntity();
        if (pl.level().isClientSide || e.getHand() != InteractionHand.MAIN_HAND) return;
        if (JobData.getJob(pl) != Job.MEDECIN || !pl.isShiftKeyDown()) return;
        if (!(e.getTarget() instanceof LivingEntity target) || target instanceof VehicleEntity) return;

        long now = pl.level().getGameTime();
        if (now < JobData.getHealCooldown(pl)) {
            pl.displayClientMessage(Component.literal("Patiente quelques secondes..."), true);
        } else {
            target.heal(6.0F);
            JobData.setHealCooldown(pl, now + 100L);
            ((ServerLevel) pl.level()).sendParticles(ParticleTypes.HEART,
                    target.getX(), target.getY() + 1.0, target.getZ(), 6, 0.3, 0.3, 0.3, 0.0);
            pl.displayClientMessage(Component.literal("§aSoin effectué (+3 coeurs)"), true);
        }
        e.setCancellationResult(InteractionResult.SUCCESS);
        e.setCanceled(true);
    }

    // ---------- Agriculteur : récoltes mûres doublées ----------
    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent e) {
        Player p = e.getPlayer();
        if (p == null || JobData.getJob(p) != Job.AGRICULTEUR) return;
        BlockState st = e.getState();
        if (st.getBlock() instanceof CropBlock crop && crop.isMaxAge(st)
                && e.getLevel() instanceof ServerLevel sl) {
            for (ItemStack s : Block.getDrops(st, sl, e.getPos(), null, p, p.getMainHandItem())) {
                Block.popResource(sl, e.getPos(), s);
            }
        }
    }
}
