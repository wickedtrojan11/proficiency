package com.trojan.proficiency.event;

import com.trojan.proficiency.SkillManager;
import com.trojan.proficiency.perk.SkillPerk;
import com.trojan.proficiency.perk.WoodcuttingPerks;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class WoodcuttingEvents {

    private static final float PROPER_GRIP_SAVE_CHANCE = 0.10f;
    private static final float TWIGS_EVERYWHERE_CHANCE = 0.10f;
    private static final float GREEN_THUMB_CHANCE = 0.05f;

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            boolean isLog =
                    state.is(BlockTags.LOGS);

            boolean isLeaves =
                    state.is(BlockTags.LEAVES);

            ItemStack heldItem =
                    player.getMainHandItem();

            boolean holdingAxe =
                    heldItem.getItem()
                            instanceof AxeItem;

            ServerPlayer serverPlayer = null;

            if (player instanceof ServerPlayer castPlayer) {

                serverPlayer = castPlayer;
            }

            if (
                    holdingAxe
                            && SkillManager.hasWoodcuttingPerk(
                                    player.getUUID(),
                                    "proper_grip"
                            )
                            && world.random.nextFloat()
                                    < PROPER_GRIP_SAVE_CHANCE
            ) {

                int damage =
                        heldItem.getDamageValue();

                if (damage > 0) {

                    heldItem.setDamageValue(
                            damage - 1
                    );

                    if (serverPlayer != null) {

                        serverPlayer.serverLevel().sendParticles(
                                ParticleTypes.ENCHANT,
                                serverPlayer.getX(),
                                serverPlayer.getY() + 1.0,
                                serverPlayer.getZ(),
                                4,
                                0.2,
                                0.3,
                                0.2,
                                0.01
                        );
                    }
                }
            }

            if (
                    isLog
                            && SkillManager.hasWoodcuttingPerk(
                                    player.getUUID(),
                                    "twigs_everywhere"
                            )
                            && world.random.nextFloat()
                                    < TWIGS_EVERYWHERE_CHANCE
            ) {

                Block.popResource(
                        world,
                        pos,
                        new ItemStack(
                                Items.STICK
                        )
                );
            }

            if (
                    (isLog || isLeaves)
                            && SkillManager.hasWoodcuttingPerk(
                                    player.getUUID(),
                                    "green_thumb"
                            )
                            && world.random.nextFloat()
                                    < GREEN_THUMB_CHANCE
            ) {

                Block.popResource(
                        world,
                        pos,
                        new ItemStack(
                                getSaplingForState(state)
                        )
                );
            }

            if (isLog) {

                boolean leveledUp =
                        SkillManager.addWoodcuttingXp(player.getUUID(), 1);

                int xp =
                        SkillManager.getWoodcuttingXp(player.getUUID());

                int level =
                        SkillManager.getWoodcuttingLevel(player.getUUID());

                if (leveledUp) {

                    player.sendSystemMessage(
                            Component.literal(
                                    "§2Woodcutting Level Up! → Level " + level
                            )
                    );

                    announceAvailableWoodcuttingPerks(
                            player,
                            level
                    );
                }
            }
        });
    }

    private static void announceAvailableWoodcuttingPerks(
            net.minecraft.world.entity.player.Player player,
            int level
    ) {

        for (SkillPerk perk
                : WoodcuttingPerks.ALL_PERKS) {

            if (level == perk.getRequiredLevel()) {

                player.sendSystemMessage(
                        Component.literal(
                                "\u00A7aNEW PERK AVAILABLE: "
                                        + perk.getName()
                        )
                );

                player.level().playSound(
                        null,
                        player.blockPosition(),
                        SoundEvents.ENCHANTMENT_TABLE_USE,
                        SoundSource.PLAYERS,
                        0.5f,
                        1.2f
                );
            }
        }
    }

    private static Item getSaplingForState(
            BlockState state
    ) {

        if (
                state.is(Blocks.SPRUCE_LOG)
                        || state.is(Blocks.SPRUCE_WOOD)
                        || state.is(Blocks.STRIPPED_SPRUCE_LOG)
                        || state.is(Blocks.STRIPPED_SPRUCE_WOOD)
                        || state.is(Blocks.SPRUCE_LEAVES)
        ) {

            return Items.SPRUCE_SAPLING;
        }

        if (
                state.is(Blocks.BIRCH_LOG)
                        || state.is(Blocks.BIRCH_WOOD)
                        || state.is(Blocks.STRIPPED_BIRCH_LOG)
                        || state.is(Blocks.STRIPPED_BIRCH_WOOD)
                        || state.is(Blocks.BIRCH_LEAVES)
        ) {

            return Items.BIRCH_SAPLING;
        }

        if (
                state.is(Blocks.JUNGLE_LOG)
                        || state.is(Blocks.JUNGLE_WOOD)
                        || state.is(Blocks.STRIPPED_JUNGLE_LOG)
                        || state.is(Blocks.STRIPPED_JUNGLE_WOOD)
                        || state.is(Blocks.JUNGLE_LEAVES)
        ) {

            return Items.JUNGLE_SAPLING;
        }

        if (
                state.is(Blocks.ACACIA_LOG)
                        || state.is(Blocks.ACACIA_WOOD)
                        || state.is(Blocks.STRIPPED_ACACIA_LOG)
                        || state.is(Blocks.STRIPPED_ACACIA_WOOD)
                        || state.is(Blocks.ACACIA_LEAVES)
        ) {

            return Items.ACACIA_SAPLING;
        }

        if (
                state.is(Blocks.DARK_OAK_LOG)
                        || state.is(Blocks.DARK_OAK_WOOD)
                        || state.is(Blocks.STRIPPED_DARK_OAK_LOG)
                        || state.is(Blocks.STRIPPED_DARK_OAK_WOOD)
                        || state.is(Blocks.DARK_OAK_LEAVES)
        ) {

            return Items.DARK_OAK_SAPLING;
        }

        if (
                state.is(Blocks.CHERRY_LOG)
                        || state.is(Blocks.CHERRY_WOOD)
                        || state.is(Blocks.STRIPPED_CHERRY_LOG)
                        || state.is(Blocks.STRIPPED_CHERRY_WOOD)
                        || state.is(Blocks.CHERRY_LEAVES)
        ) {

            return Items.CHERRY_SAPLING;
        }

        if (
                state.is(Blocks.MANGROVE_LOG)
                        || state.is(Blocks.MANGROVE_WOOD)
                        || state.is(Blocks.STRIPPED_MANGROVE_LOG)
                        || state.is(Blocks.STRIPPED_MANGROVE_WOOD)
                        || state.is(Blocks.MANGROVE_LEAVES)
        ) {

            return Items.MANGROVE_PROPAGULE;
        }

        return Items.OAK_SAPLING;
    }
}
