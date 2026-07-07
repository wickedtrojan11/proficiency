package com.trojan.proficiency.event;

import com.trojan.proficiency.SkillManager;
import com.trojan.proficiency.item.AlchemyIngredientRegistry;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class AlchemyTastingEvents {

    private static final int NEW_TASTE_XP = 2;
    private static final int RETASTE_COOLDOWN_TICKS = 40;
    private static final Map<UUID, Integer> COOLDOWNS = new HashMap<>();

    private AlchemyTastingEvents() {
    }

    public static void register() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (!(player instanceof ServerPlayer serverPlayer)) {
                return InteractionResultHolder.pass(stack);
            }

            AlchemyIngredientRegistry.Entry ingredient =
                    AlchemyIngredientRegistry.get(stack);
            if (ingredient == null) {
                return InteractionResultHolder.pass(stack);
            }

            int tick = serverPlayer.server.getTickCount();
            if (COOLDOWNS.getOrDefault(
                    serverPlayer.getUUID(),
                    0
            ) > tick) {
                return shouldPreserveVanillaUse(stack)
                        ? InteractionResultHolder.pass(stack)
                        : InteractionResultHolder.success(stack);
            }

            COOLDOWNS.put(
                    serverPlayer.getUUID(),
                    tick + RETASTE_COOLDOWN_TICKS
            );
            boolean newlyDiscovered = SkillManager.discoverAlchemyIngredient(
                    serverPlayer,
                    ingredient.key()
            );

            serverPlayer.addEffect(new MobEffectInstance(
                    ingredient.sampleEffect(),
                    ingredient.durationTicks(),
                    ingredient.amplifier(),
                    false,
                    true,
                    true
            ));
            serverPlayer.level().playSound(
                    null,
                    serverPlayer.blockPosition(),
                    SoundEvents.BREWING_STAND_BREW,
                    SoundSource.PLAYERS,
                    0.35f,
                    newlyDiscovered ? 1.35f : 1.1f
            );
            serverPlayer.serverLevel().sendParticles(
                    ParticleTypes.ENCHANT,
                    serverPlayer.getX(),
                    serverPlayer.getY() + 1.0,
                    serverPlayer.getZ(),
                    5,
                    0.2,
                    0.25,
                    0.2,
                    0.01
            );

            if (newlyDiscovered) {
                SkillManager.addAlchemyXp(serverPlayer, NEW_TASTE_XP);
                serverPlayer.sendSystemMessage(Component.literal(
                        "\u00A7dDiscovered: "
                                + ingredient.knownEffect().getString()
                ));
            }

            return shouldPreserveVanillaUse(stack)
                    ? InteractionResultHolder.pass(stack)
                    : InteractionResultHolder.success(stack);
        });
    }

    private static boolean shouldPreserveVanillaUse(ItemStack stack) {
        return stack.is(Items.HONEY_BOTTLE);
    }
}
