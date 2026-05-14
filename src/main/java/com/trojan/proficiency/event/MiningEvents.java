package com.trojan.proficiency.event;
import net.minecraft.server.level.ServerPlayer;
import com.trojan.proficiency.SkillManager;
import com.trojan.proficiency.util.MiningUtils;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import com.trojan.proficiency.perk.MiningPerks;
import com.trojan.proficiency.perk.SkillPerk;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
public class MiningEvents {

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {if (MiningUtils.isStoneType(state)) {

                ServerPlayer serverPlayer =
                        (ServerPlayer) player;

                boolean leveledUp =
                        SkillManager.addMiningXp(serverPlayer, 1);
                SkillManager.increaseMiningStreak(
                        player.getUUID()
                );
            int streak =
                    SkillManager.getMiningStreak(
                            player.getUUID()
                    );

// =========================
// MINER'S MOMENTUM
// =========================

            if (
                    SkillManager.hasMiningPerk(
                            player.getUUID(),
                            "miners_momentum"
                    )
            ) {

                if (streak >= 10) {

                    int amplifier = 0;

                    if (streak >= 50) {

                        amplifier = 2;

                    } else if (streak >= 30) {

                        amplifier = 1;
                    }

                    // refresh every 3 blocks
                    if (streak % 3 == 0) {

                        serverPlayer.addEffect(
                                new MobEffectInstance(
                                        MobEffects.DIG_SPEED,
                                        120,
                                        amplifier,
                                        false,
                                        false,
                                        true
                                )
                        );
                    }
                }
            }
// =========================
// HEAVY SWINGS
// =========================

            if (
                    SkillManager.hasMiningPerk(
                            player.getUUID(),
                            "heavy_swings"
                    )
            ) {

                if (world.random.nextFloat() < 0.15f) {

                    world.destroyBlock(
                            pos,
                            true,
                            player
                    );
                    BlockPos[] nearbyBlocks = {

                            pos.above(),
                            pos.below(),
                            pos.north(),
                            pos.south(),
                            pos.east(),
                            pos.west()
                    };

                    // Direction player is facing
                    BlockPos behindPos =
                            pos.relative(
                                    player.getDirection()
                            );

                    BlockState behindState =
                            world.getBlockState(
                                    behindPos
                            );

                    if (
                            MiningUtils.isStoneType(
                                    behindState
                            )
                    ) {

                        world.destroyBlock(
                                behindPos,
                                true,
                                player
                        );
                    }
                    player.level().playSound(
                            null,
                            player.blockPosition(),
                            SoundEvents.ANVIL_LAND,
                            SoundSource.PLAYERS,
                            0.15f,
                            1.8f
                    );
                }
            }

                int xp =
                        SkillManager.getMiningXp(player.getUUID());

                int level =
                        SkillManager.getMiningLevel(player.getUUID());

                if (leveledUp) {

                    player.sendSystemMessage(
                            Component.literal(
                                    "§6Mining Level Up! → Level " + level
                            )
                    );
                }

                if (leveledUp) {
                    player.level().playSound(
                            null,
                            player.blockPosition(),
                            SoundEvents.PLAYER_LEVELUP,
                            SoundSource.PLAYERS,
                            0.7f,
                            1.0f
                    );
                    player.sendSystemMessage(
                            Component.literal(
                                    "§6Mining Level Up! → Level " + level
                            )
                    );

                    // NEW PERK NOTIFICATIONS

                    for (SkillPerk perk : MiningPerks.ALL_PERKS) {

                        if (level == perk.getRequiredLevel()) {

                            player.sendSystemMessage(
                                    Component.literal(
                                            "§aNEW PERK AVAILABLE: "
                                                    + perk.getName()
                                    )
                            );
                        }
                    }
                }
            }
        });
    }

}
