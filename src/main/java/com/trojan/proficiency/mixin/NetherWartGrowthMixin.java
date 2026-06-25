package com.trojan.proficiency.mixin;

import com.trojan.proficiency.SkillManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetherWartBlock.class)
public abstract class NetherWartGrowthMixin {

    private static final double FASTER_GROWTH_RADIUS_SQUARED =
            16.0 * 16.0;

    @Inject(
            method = "randomTick",
            at = @At("TAIL")
    )
    private void proficiency$applyFasterGrowth(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            RandomSource random,
            CallbackInfo callbackInfo
    ) {

        if (
                random.nextFloat() >= 0.10f
                        || !hasNearbyFasterGrowthPlayer(
                        level,
                        pos
                )
        ) {

            return;
        }

        BlockState currentState =
                level.getBlockState(pos);

        if (!currentState.is(Blocks.NETHER_WART)) {

            return;
        }

        int age =
                currentState.getValue(
                        NetherWartBlock.AGE
                );

        if (
                age < NetherWartBlock.MAX_AGE
                        && random.nextInt(10) == 0
        ) {

            level.setBlock(
                    pos,
                    currentState.setValue(
                            NetherWartBlock.AGE,
                            age + 1
                    ),
                    2
            );
        }
    }

    private static boolean hasNearbyFasterGrowthPlayer(
            ServerLevel level,
            BlockPos pos
    ) {

        return !level.getPlayers(
                player ->
                        player.distanceToSqr(
                                pos.getX() + 0.5,
                                pos.getY() + 0.5,
                                pos.getZ() + 0.5
                        ) <= FASTER_GROWTH_RADIUS_SQUARED
                                && SkillManager.hasFarmingPerk(
                                player.getUUID(),
                                "cultivation_faster_growth"
                        )
        ).isEmpty();
    }
}
