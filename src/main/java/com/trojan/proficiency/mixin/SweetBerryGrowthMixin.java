package com.trojan.proficiency.mixin;

import com.trojan.proficiency.SkillManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SweetBerryBushBlock.class)
public abstract class SweetBerryGrowthMixin {

    private static final double GATHERING_RADIUS_SQUARED =
            16.0 * 16.0;

    @Inject(
            method = "randomTick",
            at = @At("TAIL")
    )
    private void proficiency$applyBerryGrowthBonus(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            RandomSource random,
            CallbackInfo callbackInfo
    ) {

        if (
                random.nextFloat() >= 0.25f
                        || !hasNearbyBerryHarvester(
                        level,
                        pos
                )
        ) {
            return;
        }

        BlockState currentState =
                level.getBlockState(pos);

        if (!currentState.is(Blocks.SWEET_BERRY_BUSH)) {
            return;
        }

        int age = currentState.getValue(
                SweetBerryBushBlock.AGE
        );

        if (age < SweetBerryBushBlock.MAX_AGE) {

            level.setBlock(
                    pos,
                    currentState.setValue(
                            SweetBerryBushBlock.AGE,
                            age + 1
                    ),
                    2
            );
        }
    }

    private static boolean hasNearbyBerryHarvester(
            ServerLevel level,
            BlockPos pos
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
                                        "berry_harvester"
                                )
                );
    }
}
