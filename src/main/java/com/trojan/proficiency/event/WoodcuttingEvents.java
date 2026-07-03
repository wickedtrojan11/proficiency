package com.trojan.proficiency.event;

import com.trojan.proficiency.SkillManager;
import com.trojan.proficiency.skill.SkillType;
import com.trojan.proficiency.perk.SkillPerk;
import com.trojan.proficiency.perk.WoodcuttingPerkEffects;
import com.trojan.proficiency.perk.WoodcuttingPerks;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class WoodcuttingEvents {

    private static final float PROPER_GRIP_SAVE_CHANCE = 0.10f;
    private static final float REINFORCED_HAFT_SAVE_CHANCE = 0.20f;
    private static final float CALLUSED_HANDS_SAVE_CHANCE = 0.35f;
    private static final float SEASONED_HAFT_SAVE_CHANCE = 0.50f;
    private static final float VETERAN_WOODSMAN_SAVE_CHANCE = 0.75f;
    private static final float TWIGS_EVERYWHERE_CHANCE = 0.10f;
    private static final float GREEN_THUMB_CHANCE = 0.05f;
    private static final float APPLE_PICKER_CHANCE = 0.05f;
    private static final float NATURES_GIFT_CHANCE = 0.02f;
    private static final float FRICTION_FIRE_CHANCE = 0.03f;
    private static final long CHOPPING_STREAK_TIMEOUT = 60L;
    private static final int MASTER_ARBORIST_LOG_LIMIT = 64;
    private static final int MASTER_ARBORIST_MIN_LOGS = 4;
    private static final int MASTER_ARBORIST_MIN_HEIGHT = 3;
    private static final int MASTER_ARBORIST_MIN_LEAVES = 4;
    private static final int FAST_DECAY_DELAY_TICKS = 300;
    private static final int AUTUMN_WINDS_DELAY_TICKS = 200;
    private static final int LEAF_DECAY_JITTER_TICKS = 100;
    private static final int LEAF_DECAY_SCAN_RADIUS = 6;

    private static final Map<UUID, Integer> CHOPPING_STREAKS =
            new HashMap<>();

    private static final Map<UUID, Long> LAST_LOG_CHOP_TICKS =
            new HashMap<>();

    private static final Set<UUID> ACTIVE_TREE_FELLING =
            new HashSet<>();

    private static final Map<
            net.minecraft.server.level.ServerLevel,
            Map<BlockPos, Long>
            > PENDING_LEAF_DECAY =
            new HashMap<>();

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

            boolean bonusDropsEnabled =
                    SkillManager.isWoodcuttingBonusDropsEnabled(
                            player.getUUID()
                    );

            ServerPlayer serverPlayer = null;

            if (player instanceof ServerPlayer castPlayer) {

                serverPlayer = castPlayer;
            }

            float durabilitySaveChance = 0.0f;

            if (
                    SkillManager.hasWoodcuttingPerk(
                            player.getUUID(),
                            "veteran_woodsman"
                    )
            ) {

                durabilitySaveChance =
                        VETERAN_WOODSMAN_SAVE_CHANCE;

            } else if (
                    SkillManager.hasWoodcuttingPerk(
                            player.getUUID(),
                            "seasoned_haft"
                    )
            ) {

                durabilitySaveChance =
                        SEASONED_HAFT_SAVE_CHANCE;

            } else if (
                    SkillManager.hasWoodcuttingPerk(
                            player.getUUID(),
                            "callused_hands"
                    )
            ) {

                durabilitySaveChance =
                        CALLUSED_HANDS_SAVE_CHANCE;

            } else if (
                    SkillManager.hasWoodcuttingPerk(
                            player.getUUID(),
                            "reinforced_haft"
                    )
            ) {

                durabilitySaveChance =
                        REINFORCED_HAFT_SAVE_CHANCE;

            } else if (
                    SkillManager.hasWoodcuttingPerk(
                            player.getUUID(),
                            "proper_grip"
                    )
            ) {

                durabilitySaveChance =
                        PROPER_GRIP_SAVE_CHANCE;
            }

            if (
                    holdingAxe
                            && durabilitySaveChance > 0.0f
                            && world.random.nextFloat()
                                    < SkillManager.scalePerkChance(
                                    player.getUUID(),
                                    SkillType.WOODCUTTING,
                                    durabilitySaveChance
                            )
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
                            && holdingAxe
                            && SkillManager.hasWoodcuttingPerk(
                            player.getUUID(),
                            "felling_momentum"
                    )
            ) {

                if (serverPlayer != null) {

                    WoodcuttingPerkEffects.activateFellingMomentum(
                            serverPlayer
                    );
                }
            }

            if (
                    isLog
                            && holdingAxe
                            && SkillManager.hasWoodcuttingPerk(
                            player.getUUID(),
                            "rhythm_of_the_forest"
                    )
            ) {

                int choppingStreak =
                        increaseChoppingStreak(
                                player.getUUID(),
                                world.getGameTime()
                        );

                int amplifier =
                        choppingStreak >= 8
                                ? 3
                                : choppingStreak >= 4
                                ? 2
                                : 1;

                player.addEffect(
                        new net.minecraft.world.effect.MobEffectInstance(
                                net.minecraft.world.effect.MobEffects.DIG_SPEED,
                                100,
                                amplifier,
                                false,
                                false,
                                true
                        )
                );
            }

            if (
                    isLog
                            && bonusDropsEnabled
                            && SkillManager.hasWoodcuttingPerk(
                                    player.getUUID(),
                                    "twigs_everywhere"
                            )
                            && world.random.nextFloat()
                                    < SkillManager.scalePerkChance(player.getUUID(), SkillType.WOODCUTTING, TWIGS_EVERYWHERE_CHANCE)
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
                            && bonusDropsEnabled
                            && SkillManager.hasWoodcuttingPerk(
                                    player.getUUID(),
                                    "green_thumb"
                            )
                            && world.random.nextFloat()
                                    < SkillManager.scalePerkChance(player.getUUID(), SkillType.WOODCUTTING, GREEN_THUMB_CHANCE)
            ) {

                Block.popResource(
                        world,
                        pos,
                        new ItemStack(
                                getSaplingForState(state)
                        )
                );
            }

            if (
                    (isLog || isLeaves)
                            && bonusDropsEnabled
                            && isOakOrDarkOak(state)
                            && SkillManager.hasWoodcuttingPerk(
                                    player.getUUID(),
                                    "apple_picker"
                            )
                            && world.random.nextFloat()
                                    < SkillManager.scalePerkChance(player.getUUID(), SkillType.WOODCUTTING, APPLE_PICKER_CHANCE)
            ) {

                Block.popResource(
                        world,
                        pos,
                        new ItemStack(
                                Items.APPLE
                        )
                );
            }

            if (
                    isLog
                            && holdingAxe
                            && bonusDropsEnabled
                            && SkillManager.hasWoodcuttingPerk(
                                    player.getUUID(),
                                    "friction_fire"
                            )
                            && world.random.nextFloat()
                            < SkillManager.scalePerkChance(player.getUUID(), SkillType.WOODCUTTING, FRICTION_FIRE_CHANCE)
            ) {

                Block.popResource(
                        world,
                        pos,
                        new ItemStack(
                                Items.CHARCOAL
                        )
                );

                if (serverPlayer != null) {

                    serverPlayer.serverLevel().sendParticles(
                            ParticleTypes.FLAME,
                            pos.getX() + 0.5,
                            pos.getY() + 0.5,
                            pos.getZ() + 0.5,
                            2,
                            0.2,
                            0.2,
                            0.2,
                            0.01
                    );

                    serverPlayer.serverLevel().sendParticles(
                            ParticleTypes.SMOKE,
                            pos.getX() + 0.5,
                            pos.getY() + 0.5,
                            pos.getZ() + 0.5,
                            3,
                            0.2,
                            0.2,
                            0.2,
                            0.01
                    );

                    world.playSound(
                            null,
                            pos,
                            SoundEvents.FIRECHARGE_USE,
                            SoundSource.BLOCKS,
                            0.25f,
                            1.5f
                    );
                }
            }

            if (
                    isLog
                            && bonusDropsEnabled
                            && SkillManager.hasWoodcuttingPerk(
                                    player.getUUID(),
                                    "natures_gift"
                            )
                            && world.random.nextFloat()
                                    < SkillManager.scalePerkChance(player.getUUID(), SkillType.WOODCUTTING, NATURES_GIFT_CHANCE)
            ) {

                Block.popResource(
                        world,
                        pos,
                        getNatureGiftReward(
                                state,
                                world.random
                        )
                );
            }

            if (isLog && serverPlayer != null) {

                boolean leveledUp =
                        SkillManager.addWoodcuttingXp(serverPlayer, 1);

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
                    player.sendSystemMessage(
                            Component.literal(
                                    "§bPerk points earned: "
                                            + SkillManager.getPerkPointsAwardForLevel(level)
                                            + ". Total: "
                                            + SkillManager.getWoodcuttingPerkPoints(
                                            player.getUUID()
                                    )
                            )
                    );

                    announceAvailableWoodcuttingPerks(
                            player,
                            level
                    );
                }
            }

            if (
                    serverPlayer != null
                            && isLog
                            && holdingAxe
                            && SkillManager.isWoodcuttingLeafDecayEnabled(
                            player.getUUID()
                    )
                            && state.is(
                            BlockTags.OVERWORLD_NATURAL_LOGS
                    )
                            && !ACTIVE_TREE_FELLING.contains(
                            player.getUUID()
                    )
                            && (
                            SkillManager.hasWoodcuttingPerk(
                                    player.getUUID(),
                                    "fast_decay"
                            )
                                    || SkillManager.hasWoodcuttingPerk(
                                    player.getUUID(),
                                    "autumn_winds"
                            )
                    )
            ) {

                scheduleNearbyLeafDecay(
                        serverPlayer,
                        pos
                );
            }

            if (
                    serverPlayer != null
                            && isLog
                            && holdingAxe
                            && SkillManager.isWoodcuttingWholeTreeEnabled(
                            player.getUUID()
                    )
                            && !ACTIVE_TREE_FELLING.contains(
                            player.getUUID()
                    )
                            && SkillManager.hasWoodcuttingPerk(
                            player.getUUID(),
                            "master_arborist"
                    )
            ) {

                fellConnectedTree(
                        serverPlayer,
                        pos,
                        state
                );
            }
        });

        ServerTickEvents.END_SERVER_TICK.register(server ->
                processPendingLeafDecay()
        );
    }

    private static void scheduleNearbyLeafDecay(
            ServerPlayer player,
            BlockPos brokenLogPos
    ) {

        net.minecraft.server.level.ServerLevel level =
                player.serverLevel();

        int delay =
                SkillManager.hasWoodcuttingPerk(
                        player.getUUID(),
                        "autumn_winds"
                )
                        ? AUTUMN_WINDS_DELAY_TICKS
                        : FAST_DECAY_DELAY_TICKS;

        Map<BlockPos, Long> pendingForLevel =
                PENDING_LEAF_DECAY.computeIfAbsent(
                        level,
                        ignored -> new HashMap<>()
                );

        for (BlockPos leafPos
                : BlockPos.betweenClosed(
                brokenLogPos.offset(
                        -LEAF_DECAY_SCAN_RADIUS,
                        -2,
                        -LEAF_DECAY_SCAN_RADIUS
                ),
                brokenLogPos.offset(
                        LEAF_DECAY_SCAN_RADIUS,
                        LEAF_DECAY_SCAN_RADIUS,
                        LEAF_DECAY_SCAN_RADIUS
                )
        )) {

            BlockState leafState =
                    level.getBlockState(leafPos);

            if (
                    leafState.is(BlockTags.LEAVES)
                            && leafState.hasProperty(
                            LeavesBlock.PERSISTENT
                    )
                            && !leafState.getValue(
                            LeavesBlock.PERSISTENT
                    )
            ) {

                pendingForLevel.merge(
                        leafPos.immutable(),
                        level.getGameTime()
                                + delay
                                + level.random.nextInt(
                                LEAF_DECAY_JITTER_TICKS + 1
                        ),
                        Math::min
                );
            }
        }
    }

    private static void processPendingLeafDecay() {

        Iterator<
                Map.Entry<
                        net.minecraft.server.level.ServerLevel,
                        Map<BlockPos, Long>
                        >
                > levelIterator =
                PENDING_LEAF_DECAY.entrySet()
                        .iterator();

        while (levelIterator.hasNext()) {

            Map.Entry<
                    net.minecraft.server.level.ServerLevel,
                    Map<BlockPos, Long>
                    > levelEntry =
                    levelIterator.next();

            net.minecraft.server.level.ServerLevel level =
                    levelEntry.getKey();

            Iterator<Map.Entry<BlockPos, Long>> leafIterator =
                    levelEntry.getValue()
                            .entrySet()
                            .iterator();

            while (leafIterator.hasNext()) {

                Map.Entry<BlockPos, Long> leafEntry =
                        leafIterator.next();

                if (
                        level.getGameTime()
                                < leafEntry.getValue()
                ) {

                    continue;
                }

                BlockState leafState =
                        level.getBlockState(
                                leafEntry.getKey()
                        );

                if (
                        leafState.is(BlockTags.LEAVES)
                                && leafState.hasProperty(
                                LeavesBlock.PERSISTENT
                        )
                                && !leafState.getValue(
                                LeavesBlock.PERSISTENT
                        )
                                && leafState.hasProperty(
                                LeavesBlock.DISTANCE
                        )
                                && leafState.getValue(
                                LeavesBlock.DISTANCE
                        ) >= LeavesBlock.DECAY_DISTANCE
                ) {

                    level.destroyBlock(
                            leafEntry.getKey(),
                            true
                    );
                }

                leafIterator.remove();
            }

            if (levelEntry.getValue().isEmpty()) {

                levelIterator.remove();
            }
        }
    }

    private static void fellConnectedTree(
            ServerPlayer player,
            BlockPos brokenPos,
            BlockState brokenState
    ) {

        if (
                !brokenState.is(
                        BlockTags.OVERWORLD_NATURAL_LOGS
                )
                        || !player.serverLevel()
                        .getBlockState(
                                brokenPos.below()
                        )
                        .is(BlockTags.DIRT)
        ) {

            return;
        }

        List<BlockPos> connectedLogs =
                findNaturalTreeLogs(
                        player,
                        brokenPos,
                        brokenState
                );

        if (connectedLogs.isEmpty()) {

            return;
        }

        connectedLogs.sort(
                Comparator.comparingInt(
                        (BlockPos logPos) ->
                                logPos.getY()
                ).reversed()
        );

        int brokenLogs = 0;
        ACTIVE_TREE_FELLING.add(
                player.getUUID()
        );

        try {

            for (BlockPos logPos : connectedLogs) {

                if (
                        !(player.getMainHandItem().getItem()
                        instanceof AxeItem)
                                || player.getMainHandItem()
                                .isEmpty()
                ) {

                    break;
                }

                if (
                        player.gameMode.destroyBlock(
                                logPos
                        )
                ) {

                    brokenLogs++;
                }
            }

        } finally {

            ACTIVE_TREE_FELLING.remove(
                    player.getUUID()
            );
        }

        if (brokenLogs > 0) {

            player.serverLevel().sendParticles(
                    ParticleTypes.HAPPY_VILLAGER,
                    brokenPos.getX() + 0.5,
                    brokenPos.getY() + 0.8,
                    brokenPos.getZ() + 0.5,
                    5,
                    0.35,
                    0.45,
                    0.35,
                    0.0
            );

            player.serverLevel().playSound(
                    null,
                    brokenPos,
                    SoundEvents.WOOD_BREAK,
                    SoundSource.BLOCKS,
                    0.45f,
                    0.8f
            );
        }
    }

    private static List<BlockPos> findNaturalTreeLogs(
            ServerPlayer player,
            BlockPos brokenPos,
            BlockState brokenState
    ) {

        net.minecraft.server.level.ServerLevel level =
                player.serverLevel();

        Set<BlockPos> visited =
                new HashSet<>();

        LinkedHashSet<BlockPos> logs =
                new LinkedHashSet<>();

        ArrayDeque<BlockPos> pending =
                new ArrayDeque<>();

        visited.add(
                brokenPos.immutable()
        );

        logs.add(
                brokenPos.immutable()
        );

        pending.add(
                brokenPos.immutable()
        );

        int highestY =
                brokenPos.getY();

        while (!pending.isEmpty()) {

            BlockPos current =
                    pending.removeFirst();

            for (int offsetX = -1;
                    offsetX <= 1;
                    offsetX++) {

                for (int offsetY = -1;
                        offsetY <= 1;
                        offsetY++) {

                    for (int offsetZ = -1;
                            offsetZ <= 1;
                            offsetZ++) {

                        if (
                                offsetX == 0
                                        && offsetY == 0
                                        && offsetZ == 0
                        ) {

                            continue;
                        }

                        BlockPos next =
                                current.offset(
                                        offsetX,
                                        offsetY,
                                        offsetZ
                                ).immutable();

                        if (
                                !visited.add(next)
                                        || !level.hasChunkAt(next)
                        ) {

                            continue;
                        }

                        BlockState nextState =
                                level.getBlockState(next);

                        if (
                                nextState.getBlock()
                                        != brokenState.getBlock()
                                        || !nextState.is(
                                        BlockTags.OVERWORLD_NATURAL_LOGS
                                )
                        ) {

                            continue;
                        }

                        logs.add(next);

                        if (
                                logs.size()
                                        > MASTER_ARBORIST_LOG_LIMIT
                        ) {

                            return List.of();
                        }

                        highestY =
                                Math.max(
                                        highestY,
                                        next.getY()
                                );

                        pending.addLast(next);
                    }
                }
            }
        }

        if (
                logs.size()
                        < MASTER_ARBORIST_MIN_LOGS
                        || highestY - brokenPos.getY() + 1
                        < MASTER_ARBORIST_MIN_HEIGHT
                        || countNearbyLeaves(
                        level,
                        logs
                ) < MASTER_ARBORIST_MIN_LEAVES
        ) {

            return List.of();
        }

        List<BlockPos> extraLogs =
                new ArrayList<>(
                        logs
                );

        extraLogs.remove(
                brokenPos
        );

        return extraLogs;
    }

    private static int countNearbyLeaves(
            net.minecraft.server.level.ServerLevel level,
            Set<BlockPos> logs
    ) {

        Set<BlockPos> leaves =
                new HashSet<>();

        for (BlockPos logPos : logs) {

            for (int offsetX = -2;
                    offsetX <= 2;
                    offsetX++) {

                for (int offsetY = -2;
                        offsetY <= 2;
                        offsetY++) {

                    for (int offsetZ = -2;
                            offsetZ <= 2;
                            offsetZ++) {

                        BlockPos nearby =
                                logPos.offset(
                                        offsetX,
                                        offsetY,
                                        offsetZ
                                );

                        if (
                                level.hasChunkAt(nearby)
                                        && level.getBlockState(
                                        nearby
                                ).is(BlockTags.LEAVES)
                        ) {

                            leaves.add(
                                    nearby.immutable()
                            );

                            if (
                                    leaves.size()
                                            >= MASTER_ARBORIST_MIN_LEAVES
                            ) {

                                return leaves.size();
                            }
                        }
                    }
                }
            }
        }

        return leaves.size();
    }

    private static int increaseChoppingStreak(
            UUID playerId,
            long gameTime
    ) {

        long lastChopTick =
                LAST_LOG_CHOP_TICKS.getOrDefault(
                        playerId,
                        Long.MIN_VALUE
                );

        int choppingStreak =
                gameTime - lastChopTick
                        <= CHOPPING_STREAK_TIMEOUT
                        ? CHOPPING_STREAKS.getOrDefault(
                        playerId,
                        0
                ) + 1
                        : 1;

        CHOPPING_STREAKS.put(
                playerId,
                choppingStreak
        );

        LAST_LOG_CHOP_TICKS.put(
                playerId,
                gameTime
        );

        return choppingStreak;
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

    private static boolean isOakOrDarkOak(
            BlockState state
    ) {

        return state.is(Blocks.OAK_LOG)
                || state.is(Blocks.OAK_WOOD)
                || state.is(Blocks.STRIPPED_OAK_LOG)
                || state.is(Blocks.STRIPPED_OAK_WOOD)
                || state.is(Blocks.OAK_LEAVES)
                || state.is(Blocks.DARK_OAK_LOG)
                || state.is(Blocks.DARK_OAK_WOOD)
                || state.is(Blocks.STRIPPED_DARK_OAK_LOG)
                || state.is(Blocks.STRIPPED_DARK_OAK_WOOD)
                || state.is(Blocks.DARK_OAK_LEAVES);
    }

    private static ItemStack getNatureGiftReward(
            BlockState state,
            RandomSource random
    ) {

        int reward =
                random.nextInt(20);

        return switch (reward) {

            case 17 -> new ItemStack(
                    Items.APPLE
            );

            case 18 -> new ItemStack(
                    getSaplingForState(state)
            );

            case 19 -> new ItemStack(
                    Items.HONEYCOMB
            );

            default -> new ItemStack(
                    Items.STICK,
                    2
            );
        };
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
