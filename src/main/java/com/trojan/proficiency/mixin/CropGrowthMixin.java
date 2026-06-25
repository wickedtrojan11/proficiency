package com.trojan.proficiency.mixin;

import com.trojan.proficiency.SkillManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CropBlock.class)
public abstract class CropGrowthMixin {

    private static final double FASTER_GROWTH_RADIUS_SQUARED =
            16.0 * 16.0;

    @Shadow
    public abstract int getAge(
            BlockState state
    );

    @Shadow
    public abstract int getMaxAge();

    @Shadow
    public abstract BlockState getStateForAge(
            int age
    );

    @Shadow
    protected static float getGrowthSpeed(
            Block block,
            BlockGetter level,
            BlockPos pos
    ) {

        throw new AssertionError();
    }

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

        if (
                !(currentState.getBlock()
                        instanceof CropBlock)
                        || level.getRawBrightness(
                        pos,
                        0
                ) < 9
        ) {

            return;
        }

        int age =
                getAge(currentState);

        if (
                age < getMaxAge()
                        && random.nextInt(
                        (int) (
                                25.0f
                                        / getGrowthSpeed(
                                        (Block) (Object) this,
                                        level,
                                        pos
                                )
                        ) + 1
                ) == 0
        ) {

            level.setBlock(
                    pos,
                    getStateForAge(age + 1),
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
