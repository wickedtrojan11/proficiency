package com.trojan.proficiency.mixin;

import com.trojan.proficiency.SkillManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MushroomBlock.class)
public abstract class MushroomGrowthMixin {

    private static final double GATHERING_RADIUS_SQUARED =
            16.0 * 16.0;

    @Inject(
            method = "randomTick",
            at = @At("TAIL")
    )
    private void proficiency$applyMushroomSpreadBonus(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            RandomSource random,
            CallbackInfo callbackInfo
    ) {

        if (
                random.nextFloat() >= 0.20f
                        || !hasNearbyPerk(
                        level,
                        pos,
                        "mushroom_expert"
                )
        ) {
            return;
        }

        BlockState mushroomState =
                level.getBlockState(pos);

        if (
                !mushroomState.is(Blocks.BROWN_MUSHROOM)
                        && !mushroomState.is(
                        Blocks.RED_MUSHROOM
                )
                        || hasTooManyNearbyMushrooms(
                        level,
                        pos
                )
        ) {
            return;
        }

        BlockPos target = pos.offset(
                random.nextInt(7) - 3,
                random.nextInt(3) - 1,
                random.nextInt(7) - 3
        );

        if (
                level.getBlockState(target).isAir()
                        && mushroomState.canSurvive(
                        level,
                        target
                )
        ) {

            level.setBlock(
                    target,
                    mushroomState,
                    2
            );
        }
    }

    private static boolean hasTooManyNearbyMushrooms(
            ServerLevel level,
            BlockPos pos
    ) {

        int mushroomCount = 0;

        for (BlockPos nearbyPos : BlockPos.betweenClosed(
                pos.offset(-4, -1, -4),
                pos.offset(4, 1, 4)
        )) {

            BlockState nearbyState =
                    level.getBlockState(nearbyPos);

            if (
                    nearbyState.is(Blocks.BROWN_MUSHROOM)
                            || nearbyState.is(
                            Blocks.RED_MUSHROOM
                    )
            ) {

                mushroomCount++;

                if (mushroomCount >= 5) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean hasNearbyPerk(
            ServerLevel level,
            BlockPos pos,
            String perkId
    ) {

        return level.getPlayers(
                        player ->
                                player.distanceToSqr(
                                        pos.getX() + 0.5,
                                        pos.getY() + 0.5,
                                        pos.getZ() + 0.5
                                ) <= GATHERING_RADIUS_SQUARED
                )
                .stream()
                .anyMatch(
                        player ->
                                SkillManager.hasFarmingPerk(
                                        player.getUUID(),
                                        perkId
                                )
                );
    }
}
