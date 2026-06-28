package com.trojan.proficiency.mixin;

import com.trojan.proficiency.SkillManager;
import com.trojan.proficiency.perk.SkillPerk;
import com.trojan.proficiency.perk.WoodcuttingPerks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;

@Mixin(SaplingBlock.class)
public abstract class SaplingGrowthMixin {

    private static final int TREE_XP = 3;
    private static final double PLAYER_RADIUS_SQUARED =
            24.0 * 24.0;

    @Inject(
            method = "advanceTree",
            at = @At("TAIL")
    )
    private void proficiency$awardTreeGrowthXp(
            ServerLevel level,
            BlockPos pos,
            BlockState state,
            RandomSource random,
            CallbackInfo callbackInfo
    ) {

        if (
                level.getBlockState(pos)
                        .is((Block) (Object) this)
                        || !hasGeneratedLog(level, pos)
        ) {
            return;
        }

        ServerPlayer nearestPlayer =
                level.getPlayers(
                        player ->
                                player.distanceToSqr(
                                        pos.getX() + 0.5,
                                        pos.getY() + 0.5,
                                        pos.getZ() + 0.5
                                ) <= PLAYER_RADIUS_SQUARED
                )
                        .stream()
                        .min(
                                Comparator.comparingDouble(
                                        player ->
                                                player.distanceToSqr(
                                                        pos.getX() + 0.5,
                                                        pos.getY() + 0.5,
                                                        pos.getZ() + 0.5
                                                )
                                )
                        )
                        .orElse(null);

        if (nearestPlayer == null) {
            return;
        }

        boolean leveledUp =
                SkillManager.addWoodcuttingXp(
                        nearestPlayer,
                        TREE_XP
                );

        if (leveledUp) {
            announceLevelUp(nearestPlayer);
        }
    }

    private static boolean hasGeneratedLog(
            ServerLevel level,
            BlockPos origin
    ) {

        for (BlockPos candidate
                : BlockPos.betweenClosed(
                        origin.offset(-3, 0, -3),
                        origin.offset(3, 16, 3)
                )) {

            if (level.getBlockState(candidate).is(BlockTags.LOGS)) {
                return true;
            }
        }

        return false;
    }

    private static void announceLevelUp(
            ServerPlayer player
    ) {

        int level = SkillManager.getWoodcuttingLevel(
                player.getUUID()
        );

        player.sendSystemMessage(
                Component.literal(
                        "\u00A72Woodcutting Level Up! \u2192 Level "
                                + level
                )
        );
        player.sendSystemMessage(
                Component.literal(
                        "\u00A7bPerk points earned: "
                                + SkillManager
                                .getPerkPointsAwardForLevel(level)
                                + ". Total: "
                                + SkillManager
                                .getWoodcuttingPerkPoints(
                                        player.getUUID()
                                )
                )
        );
        player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.PLAYER_LEVELUP,
                SoundSource.PLAYERS,
                0.7f,
                1.0f
        );

        for (SkillPerk perk : WoodcuttingPerks.ALL_PERKS) {

            if (level == perk.getRequiredLevel()) {

                player.sendSystemMessage(
                        Component.literal(
                                "\u00A7aNEW PERK AVAILABLE: "
                                        + perk.getName()
                        )
                );
            }
        }
    }
}
