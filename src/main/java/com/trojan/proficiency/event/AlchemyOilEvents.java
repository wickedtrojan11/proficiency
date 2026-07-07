package com.trojan.proficiency.event;

import com.trojan.proficiency.item.OilRegistry;
import com.trojan.proficiency.SkillManager;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;

public final class AlchemyOilEvents {

    private AlchemyOilEvents() {
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                applyHeldToolSpeed(player);
            }
        });

        ServerLivingEntityEvents.AFTER_DAMAGE.register(
                (entity, source, baseDamage, damageTaken, blocked) -> {
                    if (!(source.getEntity() instanceof ServerPlayer player)
                            || source.getDirectEntity() != player
                            || blocked
                            || damageTaken <= 0.0f) {
                        return;
                    }
                    applyWeaponOil(player, entity);
                }
        );

        PlayerBlockBreakEvents.AFTER.register(
                (world, player, pos, state, blockEntity) -> {
                    if (!(player instanceof ServerPlayer serverPlayer)) {
                        return;
                    }
                    ItemStack stack = serverPlayer.getMainHandItem();
                    if (OilRegistry.getAppliedOils(stack).isEmpty()) {
                        return;
                    }

                    if (OilRegistry.hasUsableOil(
                            serverPlayer,
                            stack,
                            "miners"
                    )
                            && stack.getItem() instanceof PickaxeItem) {
                        OilRegistry.consumeCharge(stack, "miners");
                    }
                    if (OilRegistry.hasUsableOil(
                            serverPlayer,
                            stack,
                            "lumber"
                    )
                            && stack.getItem() instanceof AxeItem
                            && state.is(BlockTags.LOGS)) {
                        OilRegistry.consumeCharge(stack, "lumber");
                    }

                    OilRegistry.tryPreserveDurability(
                            serverPlayer,
                            stack,
                            serverPlayer.getRandom()
                    );
                }
        );
    }

    private static void applyHeldToolSpeed(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        if (OilRegistry.getAppliedOils(stack).isEmpty()) {
            return;
        }
        if (!SkillManager.isAlchemyToggleEnabled(player.getUUID(), "oils")) {
            return;
        }

        boolean speedOil = (OilRegistry.hasUsableOil(player, stack, "miners")
                && stack.getItem() instanceof PickaxeItem)
                || (OilRegistry.hasUsableOil(player, stack, "lumber")
                && stack.getItem() instanceof AxeItem);
        if (!speedOil) {
            return;
        }

        player.addEffect(new MobEffectInstance(
                MobEffects.DIG_SPEED,
                40,
                0,
                false,
                false,
                true
        ));
    }

    private static void applyWeaponOil(
            ServerPlayer player,
            LivingEntity target
    ) {
        ItemStack stack = player.getMainHandItem();
        if (OilRegistry.getAppliedOils(stack).isEmpty()) {
            return;
        }
        if (!SkillManager.isAlchemyToggleEnabled(player.getUUID(), "oils")) {
            return;
        }

        if (OilRegistry.hasUsableOil(player, stack, "fire")) {
            target.setRemainingFireTicks(Math.max(
                    target.getRemainingFireTicks(),
                    OilRegistry.getFireTicks(player)
            ));
            OilRegistry.consumeCharge(stack, "fire");
            feedback(player, target, true);
        }
        if (OilRegistry.hasUsableOil(player, stack, "frost")) {
            target.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SLOWDOWN,
                    OilRegistry.getFrostTicks(player),
                    0,
                    false,
                    true,
                    true
            ));
            OilRegistry.consumeCharge(stack, "frost");
            feedback(player, target, false);
        }
        OilRegistry.tryPreserveDurability(player, stack, player.getRandom());
    }

    private static void feedback(
            ServerPlayer player,
            LivingEntity target,
            boolean fire
    ) {
        player.level().playSound(
                null,
                target.blockPosition(),
                fire ? SoundEvents.FIRECHARGE_USE : SoundEvents.GLASS_HIT,
                SoundSource.PLAYERS,
                0.35f,
                fire ? 1.35f : 0.85f
        );
        player.serverLevel().sendParticles(
                fire ? ParticleTypes.FLAME : ParticleTypes.SNOWFLAKE,
                target.getX(),
                target.getY() + target.getBbHeight() * 0.55,
                target.getZ(),
                4,
                0.18,
                0.25,
                0.18,
                0.01
        );
    }
}
