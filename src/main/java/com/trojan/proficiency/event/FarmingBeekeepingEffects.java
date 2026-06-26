package com.trojan.proficiency.event;

import com.trojan.proficiency.SkillManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public final class FarmingBeekeepingEffects {

    private static final int POLLINATION_INTERVAL_TICKS = 40;
    private static final int HONEY_INTERVAL_TICKS = 200;
    private static final int CALM_DELAY_TICKS = 2;
    private static final double BEE_SEARCH_RADIUS = 16.0;
    private static final double POLLINATION_RADIUS_BUSY_BEES = 4.0;
    private static final double POLLINATION_RADIUS_EXPERT = 6.0;
    private static final double HIVE_SEARCH_RADIUS = 12.0;
    private static final float BUSY_BEES_GROWTH_CHANCE = 0.18f;
    private static final float POLLINATION_EXPERT_GROWTH_CHANCE = 0.30f;
    private static final float HONEY_MASTERY_FILL_CHANCE = 0.18f;
    private static final float MASTER_BEEKEEPER_EXTRA_CHANCE = 0.25f;
    private static final float MASTER_BEEKEEPER_CALM_CHANCE = 0.75f;

    private static final List<PendingBeeCalm> PENDING_BEE_CALMS =
            new ArrayList<>();

    private FarmingBeekeepingEffects() {
    }

    public static void register() {

        ServerTickEvents.END_SERVER_TICK.register(server -> {

            processPendingBeeCalms(server.getTickCount());

            if (server.getTickCount() % POLLINATION_INTERVAL_TICKS == 0) {
                server.getPlayerList()
                        .getPlayers()
                        .forEach(FarmingBeekeepingEffects::applyBeePollination);
            }

            if (server.getTickCount() % HONEY_INTERVAL_TICKS == 0) {
                server.getPlayerList()
                        .getPlayers()
                        .forEach(FarmingBeekeepingEffects::applyHoneyMastery);
            }
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {

            if (
                    !(player instanceof ServerPlayer serverPlayer)
                            || !(world instanceof ServerLevel serverLevel)
            ) {

                return InteractionResult.PASS;
            }

            BlockPos pos =
                    hitResult.getBlockPos();

            BlockState state =
                    serverLevel.getBlockState(pos);

            if (
                    !isHive(state)
                            || state.getValue(BeehiveBlock.HONEY_LEVEL)
                            < BeehiveBlock.MAX_HONEY_LEVELS
                            || !SkillManager.isFarmingBeekeepingEnabled(
                            serverPlayer.getUUID()
                    )
                            || !SkillManager.hasFarmingPerk(
                            serverPlayer.getUUID(),
                            "master_beekeeper"
                    )
            ) {

                return InteractionResult.PASS;
            }

            ItemStack heldItem =
                    serverPlayer.getItemInHand(hand);

            if (
                    heldItem.is(Items.SHEARS)
                            && serverPlayer.getRandom().nextFloat()
                            < MASTER_BEEKEEPER_EXTRA_CHANCE
            ) {

                Block.popResource(
                        serverLevel,
                        pos,
                        new ItemStack(Items.HONEYCOMB)
                );
            } else if (
                    heldItem.is(Items.GLASS_BOTTLE)
                            && serverPlayer.getRandom().nextFloat()
                            < MASTER_BEEKEEPER_EXTRA_CHANCE
            ) {

                Block.popResource(
                        serverLevel,
                        pos,
                        new ItemStack(Items.HONEY_BOTTLE)
                );
            }

            if (
                    serverPlayer.getRandom().nextFloat()
                            < MASTER_BEEKEEPER_CALM_CHANCE
            ) {

                PENDING_BEE_CALMS.add(
                        new PendingBeeCalm(
                                serverLevel,
                                pos.immutable(),
                                serverPlayer.getUUID(),
                                serverLevel.getServer()
                                        .getTickCount()
                                        + CALM_DELAY_TICKS
                        )
                );
            }

            return InteractionResult.PASS;
        });
    }

    private static void applyBeePollination(
            ServerPlayer player
    ) {

        if (
                !SkillManager.isFarmingBeekeepingEnabled(
                        player.getUUID()
                )
                        || !SkillManager.hasFarmingPerk(
                        player.getUUID(),
                        "busy_bees"
                )
        ) {

            return;
        }

        ServerLevel level =
                player.serverLevel();

        List<Bee> activeBees =
                level.getEntitiesOfClass(
                        Bee.class,
                        player.getBoundingBox()
                                .inflate(BEE_SEARCH_RADIUS),
                        bee -> !bee.isDeadOrDying()
                                && !bee.isBaby()
                                && (
                                bee.hasNectar()
                                        || bee.hasSavedFlowerPos()
                        )
                );

        if (activeBees.isEmpty()) {
            return;
        }

        double radius =
                SkillManager.hasFarmingPerk(
                        player.getUUID(),
                        "pollination_expert"
                )
                        ? POLLINATION_RADIUS_EXPERT
                        : POLLINATION_RADIUS_BUSY_BEES;

        float chance =
                SkillManager.hasFarmingPerk(
                        player.getUUID(),
                        "pollination_expert"
                )
                        ? POLLINATION_EXPERT_GROWTH_CHANCE
                        : BUSY_BEES_GROWTH_CHANCE;

        for (Bee bee : activeBees) {

            if (level.random.nextFloat() >= chance) {
                continue;
            }

            growNearbyCrop(
                    level,
                    bee.blockPosition(),
                    (int) radius
            );
        }
    }

    private static void applyHoneyMastery(
            ServerPlayer player
    ) {

        if (
                !SkillManager.isFarmingBeekeepingEnabled(
                        player.getUUID()
                )
                        || !SkillManager.hasFarmingPerk(
                        player.getUUID(),
                        "honey_mastery"
                )
        ) {

            return;
        }

        ServerLevel level =
                player.serverLevel();

        BlockPos center =
                player.blockPosition();

        int radius =
                (int) HIVE_SEARCH_RADIUS;

        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-radius, -4, -radius),
                center.offset(radius, 4, radius)
        )) {

            BlockState state =
                    level.getBlockState(pos);

            if (!isHive(state)) {
                continue;
            }

            int honeyLevel =
                    state.getValue(BeehiveBlock.HONEY_LEVEL);

            if (
                    honeyLevel >= BeehiveBlock.MAX_HONEY_LEVELS
                            || level.random.nextFloat()
                            >= HONEY_MASTERY_FILL_CHANCE
            ) {
                continue;
            }

            level.setBlock(
                    pos,
                    state.setValue(
                            BeehiveBlock.HONEY_LEVEL,
                            honeyLevel + 1
                    ),
                    3
            );

            break;
        }
    }

    private static void growNearbyCrop(
            ServerLevel level,
            BlockPos center,
            int radius
    ) {

        List<BlockPos> growableCrops =
                new ArrayList<>();

        for (BlockPos cropPos : BlockPos.betweenClosed(
                center.offset(-radius, -2, -radius),
                center.offset(radius, 2, radius)
        )) {

            BlockState state =
                    level.getBlockState(cropPos);

            if (
                    state.getBlock() instanceof CropBlock crop
                            && !crop.isMaxAge(state)
            ) {

                growableCrops.add(
                        cropPos.immutable()
                );
            }
        }

        if (growableCrops.isEmpty()) {
            return;
        }

        BlockPos cropPos =
                growableCrops.get(
                        level.random.nextInt(
                                growableCrops.size()
                        )
                );

        BlockState state =
                level.getBlockState(cropPos);

        if (state.getBlock() instanceof CropBlock crop) {

            level.setBlock(
                    cropPos,
                    crop.getStateForAge(
                            crop.getAge(state) + 1
                    ),
                    2
            );
        }
    }

    private static void processPendingBeeCalms(
            int tickCount
    ) {

        Iterator<PendingBeeCalm> iterator =
                PENDING_BEE_CALMS.iterator();

        while (iterator.hasNext()) {

            PendingBeeCalm pending =
                    iterator.next();

            if (tickCount < pending.tick()) {
                continue;
            }

            pending.level()
                    .getEntitiesOfClass(
                            Bee.class,
                            new AABB(pending.pos())
                                    .inflate(12.0)
                    )
                    .forEach(bee -> calmBee(
                            bee,
                            pending.playerId()
                    ));

            iterator.remove();
        }
    }

    private static void calmBee(
            Bee bee,
            UUID playerId
    ) {

        if (
                bee instanceof NeutralMob neutralMob
                        && (
                        playerId.equals(
                                neutralMob.getPersistentAngerTarget()
                        )
                                || neutralMob.isAngry()
                )
        ) {

            neutralMob.stopBeingAngry();
            bee.setTarget(null);
        }
    }

    private static boolean isHive(
            BlockState state
    ) {

        return state.is(Blocks.BEEHIVE)
                || state.is(Blocks.BEE_NEST);
    }

    private record PendingBeeCalm(
            ServerLevel level,
            BlockPos pos,
            UUID playerId,
            int tick
    ) {
    }
}
