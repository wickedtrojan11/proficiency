package com.trojan.proficiency.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public final class CropGrowthOverlay {

    private CropGrowthOverlay() {
    }

    public static void register() {

        HudRenderCallback.EVENT.register(
                CropGrowthOverlay::render
        );
    }

    private static void render(
            GuiGraphics graphics,
            net.minecraft.client.DeltaTracker tickCounter
    ) {

        Minecraft minecraft =
                Minecraft.getInstance();

        if (
                minecraft.player == null
                        || minecraft.level == null
                        || minecraft.hitResult == null
                        || minecraft.hitResult.getType()
                        != HitResult.Type.BLOCK
                        || minecraft.options.hideGui
        ) {

            return;
        }

        BlockHitResult hitResult =
                (BlockHitResult) minecraft.hitResult;

        BlockState state =
                minecraft.level.getBlockState(
                        hitResult.getBlockPos()
                );

        String growthText =
                getGrowthText(state);

        if (growthText == null) {

            return;
        }

        int centerX =
                graphics.guiWidth() / 2;

        int y =
                graphics.guiHeight() / 2
                        + 14;

        graphics.drawCenteredString(
                minecraft.font,
                growthText,
                centerX,
                y,
                0xFFE6D68A
        );
    }

    private static String getGrowthText(
            BlockState state
    ) {

        if (state.getBlock() instanceof CropBlock crop) {

            return formatGrowth(
                    crop.getAge(state),
                    crop.getMaxAge()
            );
        }

        if (state.is(Blocks.SWEET_BERRY_BUSH)) {

            return "Gatherer's Eye: "
                    + formatGrowth(
                    state.getValue(
                            SweetBerryBushBlock.AGE
                    ),
                    SweetBerryBushBlock.MAX_AGE
            );
        }

        if (state.is(Blocks.NETHER_WART)) {

            return formatGrowth(
                    state.getValue(
                            NetherWartBlock.AGE
                    ),
                    NetherWartBlock.MAX_AGE
            );
        }

        if (state.is(Blocks.COCOA)) {

            return formatGrowth(
                    state.getValue(
                            CocoaBlock.AGE
                    ),
                    CocoaBlock.MAX_AGE
            );
        }

        if (
                state.is(Blocks.BROWN_MUSHROOM)
                        || state.is(Blocks.RED_MUSHROOM)
        ) {

            return "Gatherer's Eye: Mushroom Ready";
        }

        if (
                state.is(Blocks.BEEHIVE)
                        || state.is(Blocks.BEE_NEST)
        ) {

            int honeyLevel = state.getValue(
                    BeehiveBlock.HONEY_LEVEL
            );

            return honeyLevel
                    >= BeehiveBlock.MAX_HONEY_LEVELS
                    ? "Gatherer's Eye: Honey Ready"
                    : "Gatherer's Eye: Honey "
                    + honeyLevel
                    + "/"
                    + BeehiveBlock.MAX_HONEY_LEVELS;
        }

        return null;
    }

    private static String formatGrowth(
            int age,
            int maxAge
    ) {

        if (age >= maxAge) {

            return "Growth: Mature";
        }

        int percent =
                Math.round(
                        age * 100.0f
                                / maxAge
                );

        return "Growth: "
                + percent
                + "%";
    }
}
