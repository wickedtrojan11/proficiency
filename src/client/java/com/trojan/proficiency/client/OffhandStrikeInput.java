package com.trojan.proficiency.client;

import com.trojan.proficiency.network.OffhandStrikeRequestPayload;
import com.trojan.proficiency.util.OneHandedWeapons;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class OffhandStrikeInput {

    private static final int MIN_COOLDOWN_TICKS = 6;
    private static long nextStrikeTick;
    private static long lastGameTime;

    private OffhandStrikeInput() {
    }

    public static void register() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (hand != InteractionHand.MAIN_HAND
                    || !OneHandedWeapons.isSupported(player.getMainHandItem())
                    || !OneHandedWeapons.isSupported(player.getOffhandItem())
                    || !ClientSkillState.hasOneHandedPerk(
                    player.getUUID(),
                    "offhand_strike"
            )
                    || !ClientSkillState.isOneHandedToggleEnabled("dual_wield")
                    || !ClientPlayNetworking.canSend(
                    OffhandStrikeRequestPayload.TYPE
            )) {
                return InteractionResultHolder.pass(player.getItemInHand(hand));
            }

            long gameTime = world.getGameTime();
            if (gameTime < lastGameTime) {
                nextStrikeTick = 0;
            }
            lastGameTime = gameTime;
            if (gameTime < nextStrikeTick) {
                return InteractionResultHolder.fail(player.getItemInHand(hand));
            }

            double attackSpeed = Math.max(
                    0.5,
                    player.getAttributeValue(Attributes.ATTACK_SPEED)
            );
            nextStrikeTick = gameTime + Math.max(
                    MIN_COOLDOWN_TICKS,
                    (int) Math.ceil(20.0 / attackSpeed)
            );
            ClientPlayNetworking.send(new OffhandStrikeRequestPayload());
            player.swing(InteractionHand.OFF_HAND);
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        });
    }
}
