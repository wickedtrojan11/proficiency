package com.trojan.proficiency.event;

import com.trojan.proficiency.SkillManager;
import com.trojan.proficiency.block.AutoFarmerPlantPotBlockEntity;
import com.trojan.proficiency.block.ModBlocks;
import com.trojan.proficiency.block.SolarComposterBlockEntity;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class FarmingUtilityEvents {

    private FarmingUtilityEvents() {
    }

    public static void register() {

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {

            if (!(player instanceof ServerPlayer serverPlayer)) {
                return InteractionResult.PASS;
            }

            BlockEntity blockEntity =
                    world.getBlockEntity(
                            hitResult.getBlockPos()
                    );

            if (
                    blockEntity instanceof AutoFarmerPlantPotBlockEntity plantPot
            ) {

                return usePlantPot(
                        serverPlayer,
                        plantPot,
                        player.getItemInHand(hand)
                );
            }

            if (
                    blockEntity instanceof SolarComposterBlockEntity solarComposter
            ) {

                if (!hasGreenhouseGenius(serverPlayer)) {
                    return InteractionResult.FAIL;
                }

                serverPlayer.openMenu(
                        solarComposter
                );

                return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
        });
    }

    private static InteractionResult usePlantPot(
            ServerPlayer player,
            AutoFarmerPlantPotBlockEntity plantPot,
            ItemStack heldItem
    ) {

        if (!hasGreenhouseGenius(player)) {
            return InteractionResult.FAIL;
        }

        if (plantPot.hasCrop()) {

            player.displayClientMessage(
                    Component.literal(
                            "Proficient Pot growth: "
                                    + plantPot.getGrowthPercent()
                                    + "%"
                    ).withStyle(ChatFormatting.GREEN),
                    true
            );

            return InteractionResult.SUCCESS;
        }

        if (
                heldItem.isEmpty()
                        || !plantPot.setCrop(
                        heldItem,
                        player.getUUID()
                )
        ) {

            return InteractionResult.PASS;
        }

        if (!player.isCreative()) {
            heldItem.shrink(1);
        }

        player.displayClientMessage(
                Component.literal(
                        "Proficient Pot crop set."
                ).withStyle(ChatFormatting.GREEN),
                true
        );

        return InteractionResult.SUCCESS;
    }

    private static boolean hasGreenhouseGenius(
            ServerPlayer player
    ) {

        if (
                SkillManager.hasFarmingPerk(
                        player.getUUID(),
                        "greenhouse_genius"
                )
        ) {

            return true;
        }

        player.displayClientMessage(
                Component.literal(
                        "Requires Greenhouse Genius."
                ).withStyle(ChatFormatting.RED),
                true
        );

        return false;
    }
}
