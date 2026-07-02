package com.trojan.proficiency.event;

import com.trojan.proficiency.SkillManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class FarmingEvents {

    private static final int HONEY_COLLECTION_XP = 2;

    private static final int HARVEST_XP = 1;
    private static final int PLANTING_XP = 1;
    private static final int GATHERING_XP = 1;

    private static final List<PendingBlockInteraction>
            PENDING_INTERACTIONS =
            new ArrayList<>();

    private static final Map<HarvestKey, BlockState>
            CAPTURED_HARVESTS =
            new HashMap<>();

    private static final List<PendingAutoReplant>
            PENDING_AUTO_REPLANTS =
            new ArrayList<>();

    private static final Set<HarvestKey>
            BOUNTIFUL_HARVESTS =
            new HashSet<>();

    private static final Set<Item> PLANTING_ITEMS =
            Set.of(
                    Items.WHEAT_SEEDS,
                    Items.BEETROOT_SEEDS,
                    Items.CARROT,
                    Items.POTATO,
                    Items.NETHER_WART,
                    Items.COCOA_BEANS,
                    Items.MELON_SEEDS,
                    Items.PUMPKIN_SEEDS,
                    Items.TORCHFLOWER_SEEDS,
                    Items.PITCHER_POD,
                    Items.SWEET_BERRIES,
                    Items.BROWN_MUSHROOM,
                    Items.RED_MUSHROOM
            );

    private FarmingEvents() {
    }

    public static void register() {

        PlayerBlockBreakEvents.BEFORE.register(
                (world, player, pos, state, blockEntity) -> {

                    if (
                            player instanceof ServerPlayer serverPlayer
                                    && world instanceof ServerLevel serverLevel
                                    && isMatureCrop(state)
                    ) {

                        CAPTURED_HARVESTS.put(
                                new HarvestKey(
                                        serverPlayer.getUUID(),
                                        serverLevel,
                                        pos.immutable()
                                ),
                                state
                        );
                    }

                    return true;
                }
        );

        PlayerBlockBreakEvents.CANCELED.register(
                (world, player, pos, state, blockEntity) -> {

                    if (
                            player instanceof ServerPlayer serverPlayer
                                    && world instanceof ServerLevel serverLevel
                    ) {

                        CAPTURED_HARVESTS.remove(
                                new HarvestKey(
                                        serverPlayer.getUUID(),
                                        serverLevel,
                                        pos
                                )
                        );
                    }
                }
        );

        PlayerBlockBreakEvents.AFTER.register(
                (world, player, pos, state, blockEntity) -> {

                    if (
                            !(player instanceof ServerPlayer serverPlayer)
                                    || !(world instanceof ServerLevel serverLevel)
                    ) {

                        return;
                    }

                    BlockState harvestedState =
                            CAPTURED_HARVESTS.remove(
                                    new HarvestKey(
                                            serverPlayer.getUUID(),
                                            serverLevel,
                                            pos
                                    )
                            );

                    if (harvestedState == null) {

                        harvestedState = state;
                    }

                    HarvestKey harvestKey =
                            new HarvestKey(
                                    serverPlayer.getUUID(),
                                    serverLevel,
                                    pos
                            );

                    boolean automatedBountifulHarvest =
                            BOUNTIFUL_HARVESTS.remove(
                                    harvestKey
                            );

                    if (isMatureCrop(harvestedState)) {

                        if (!automatedBountifulHarvest) {

                            SkillManager.addFarmingXp(
                                    serverPlayer,
                                    HARVEST_XP
                            );
                        }

                        applyBetterYields(
                                serverPlayer,
                                pos,
                                harvestedState
                        );

                        queueAutoReplant(
                                serverPlayer,
                                pos,
                                harvestedState
                        );

                        if (!automatedBountifulHarvest) {

                            applyBountifulHarvest(
                                    serverPlayer,
                                    serverLevel,
                                    pos
                            );
                        }

                    } else if (isMushroom(harvestedState)) {

                        SkillManager.addFarmingXp(
                                serverPlayer,
                                GATHERING_XP
                        );

                        applyBonusMushroom(
                                serverPlayer,
                                pos,
                                harvestedState
                        );
                    }
                }
        );

        UseBlockCallback.EVENT.register(
                (player, world, hand, hitResult) -> {

                    if (
                            !(player instanceof ServerPlayer serverPlayer)
                                    || !(world instanceof ServerLevel serverLevel)
                    ) {

                        return InteractionResult.PASS;
                    }

                    BlockPos clickedPos =
                            hitResult.getBlockPos();

                    BlockState clickedState =
                            serverLevel.getBlockState(
                                    clickedPos
                            );

                    ItemStack heldItem =
                            player.getItemInHand(hand);

                    awardHoneyCollectionXp(
                            serverPlayer,
                            clickedState,
                            heldItem
                    );

                    applyHoneyGatherer(
                            serverPlayer,
                            clickedPos,
                            clickedState,
                            heldItem
                    );

                    if (
                            clickedState.is(
                                    Blocks.SWEET_BERRY_BUSH
                            )
                                    && clickedState.getValue(
                                    SweetBerryBushBlock.AGE
                            ) >= 2
                    ) {

                        queueInteraction(
                                PendingBlockInteraction.harvest(
                                        serverPlayer,
                                        serverLevel,
                                        clickedPos,
                                        serverLevel.getServer()
                                                .getTickCount()
                                                + 2
                                )
                        );
                    }

                    if (
                            !heldItem.isEmpty()
                                    && PLANTING_ITEMS.contains(
                                    heldItem.getItem()
                            )
                    ) {

                        queueInteraction(
                                PendingBlockInteraction.plant(
                                        serverPlayer,
                                        serverLevel,
                                        clickedPos,
                                        clickedPos.relative(
                                                hitResult.getDirection()
                                        ),
                                        clickedState,
                                        serverLevel.getBlockState(
                                                clickedPos.relative(
                                                        hitResult.getDirection()
                                                )
                                        ),
                                        serverLevel.getServer()
                                                .getTickCount()
                                                + 2
                                )
                        );
                    }

                    return InteractionResult.PASS;
                }
        );

        ServerTickEvents.END_SERVER_TICK.register(server -> {

            processPendingAutoReplants(
                    server
            );

            Iterator<PendingBlockInteraction> iterator =
                    PENDING_INTERACTIONS.iterator();

            while (iterator.hasNext()) {

                PendingBlockInteraction pending =
                        iterator.next();

                if (
                        server.getTickCount()
                                < pending.checkTick()
                ) {

                    continue;
                }

                ServerPlayer player =
                        server.getPlayerList()
                                .getPlayer(
                                        pending.playerId()
                                );

                if (player != null) {

                    if (
                            pending.harvest()
                                    && didHarvestBerryBush(
                                    pending.level(),
                                    pending.primaryPos()
                            )
                    ) {

                        SkillManager.addFarmingXp(
                                player,
                                GATHERING_XP
                        );

                        applyBonusBerries(
                                player,
                                pending.primaryPos()
                        );

                    } else if (
                            !pending.harvest()
                                    && (
                                    didPlantCrop(
                                            pending.originalPrimaryState(),
                                            pending.level()
                                                    .getBlockState(
                                                            pending.primaryPos()
                                                    )
                                    )
                                            || didPlantCrop(
                                            pending.originalSecondaryState(),
                                            pending.level()
                                                    .getBlockState(
                                                            pending.secondaryPos()
                                                    )
                                    )
                            )
                    ) {

                        SkillManager.addFarmingXp(
                                player,
                                PLANTING_XP
                        );
                    }
                }

                iterator.remove();
            }
        });
    }

    private static void awardHoneyCollectionXp(
            ServerPlayer player,
            BlockState state,
            ItemStack heldItem
    ) {

        if (
                !(state.is(Blocks.BEEHIVE)
                        || state.is(Blocks.BEE_NEST))
                        || state.getValue(BeehiveBlock.HONEY_LEVEL)
                        < BeehiveBlock.MAX_HONEY_LEVELS
                        || !(
                        heldItem.is(Items.SHEARS)
                                || heldItem.is(Items.GLASS_BOTTLE)
                )
        ) {
            return;
        }

        SkillManager.addFarmingXp(
                player,
                HONEY_COLLECTION_XP
        );
    }

    private static void applyBonusMushroom(
            ServerPlayer player,
            BlockPos pos,
            BlockState harvestedState
    ) {

        if (
                !SkillManager
                .isFarmingGatheringBonusDropsEnabled(
                        player.getUUID()
                )
                        || !SkillManager.hasFarmingPerk(
                        player.getUUID(),
                        "mushroom_expert"
                )
                        || player.getRandom().nextFloat()
                        >= 0.20f
        ) {
            return;
        }

        Item item = harvestedState.is(Blocks.RED_MUSHROOM)
                ? Items.RED_MUSHROOM
                : Items.BROWN_MUSHROOM;

        Block.popResource(
                player.serverLevel(),
                pos,
                new ItemStack(item)
        );
    }

    private static void applyBonusBerries(
            ServerPlayer player,
            BlockPos pos
    ) {

        if (
                !SkillManager
                .isFarmingGatheringBonusDropsEnabled(
                        player.getUUID()
                )
                        || !SkillManager.hasFarmingPerk(
                        player.getUUID(),
                        "berry_harvester"
                )
                        || player.getRandom().nextFloat()
                        >= 0.20f
        ) {
            return;
        }

        Block.popResource(
                player.serverLevel(),
                pos,
                new ItemStack(
                        Items.SWEET_BERRIES,
                        1
                )
        );
    }

    private static void applyHoneyGatherer(
            ServerPlayer player,
            BlockPos pos,
            BlockState state,
            ItemStack heldItem
    ) {

        if (
                !heldItem.is(Items.SHEARS)
                        || !(
                        state.is(Blocks.BEEHIVE)
                                || state.is(Blocks.BEE_NEST)
                )
                        || state.getValue(
                        BeehiveBlock.HONEY_LEVEL
                ) < BeehiveBlock.MAX_HONEY_LEVELS
                        || !SkillManager
                        .isFarmingBeekeepingEnabled(
                                player.getUUID()
                        )
                        || !SkillManager.hasFarmingPerk(
                        player.getUUID(),
                        "honey_gatherer"
                )
                        || player.getRandom().nextFloat()
                        >= 0.15f
        ) {
            return;
        }

        Block.popResource(
                player.serverLevel(),
                pos,
                new ItemStack(Items.HONEYCOMB)
        );
    }

    private static void applyBountifulHarvest(
            ServerPlayer player,
            ServerLevel level,
            BlockPos harvestedPos
    ) {

        if (
                !SkillManager.hasFarmingPerk(
                        player.getUUID(),
                        "bountiful_harvest"
                )
        ) {
            return;
        }

        for (BlockPos adjacentPos : List.of(
                harvestedPos.north(),
                harvestedPos.south(),
                harvestedPos.east(),
                harvestedPos.west()
        )) {

            if (!isMatureCrop(
                    level.getBlockState(adjacentPos)
            )) {
                continue;
            }

            HarvestKey adjacentKey =
                    new HarvestKey(
                            player.getUUID(),
                            level,
                            adjacentPos
                    );

            BOUNTIFUL_HARVESTS.add(adjacentKey);

            if (!player.gameMode.destroyBlock(adjacentPos)) {
                BOUNTIFUL_HARVESTS.remove(adjacentKey);
            }
        }
    }

    private static void applyBetterYields(
            ServerPlayer player,
            BlockPos pos,
            BlockState harvestedState
    ) {

        if (
                !SkillManager.isFarmingBonusHarvestsEnabled(
                        player.getUUID()
                )
                        || !SkillManager.hasFarmingPerk(
                        player.getUUID(),
                        "better_yields"
                )
                        || player.getRandom().nextFloat()
                        >= 0.10f
        ) {

            return;
        }

        Item bonusItem =
                getHarvestItem(
                        harvestedState
                );

        if (bonusItem != null) {

            Block.popResource(
                    player.serverLevel(),
                    pos,
                    new ItemStack(bonusItem)
            );
        }
    }

    private static void queueAutoReplant(
            ServerPlayer player,
            BlockPos pos,
            BlockState harvestedState
    ) {

        if (
                !SkillManager.isFarmingAutoReplantEnabled(
                        player.getUUID()
                )
                        || !SkillManager.hasFarmingPerk(
                        player.getUUID(),
                        "auto_replant"
                )
        ) {

            return;
        }

        Item seedItem =
                getReplantItem(
                        harvestedState
                );

        BlockState replantedState =
                getReplantedState(
                        harvestedState
                );

        if (
                seedItem == null
                        || replantedState == null
        ) {

            return;
        }

        PENDING_AUTO_REPLANTS.add(
                new PendingAutoReplant(
                        player.getUUID(),
                        player.serverLevel(),
                        pos.immutable(),
                        seedItem,
                        replantedState,
                        player.server.getTickCount()
                                + 1
                )
        );
    }

    private static void processPendingAutoReplants(
            net.minecraft.server.MinecraftServer server
    ) {

        Iterator<PendingAutoReplant> iterator =
                PENDING_AUTO_REPLANTS.iterator();

        while (iterator.hasNext()) {

            PendingAutoReplant pending =
                    iterator.next();

            if (
                    server.getTickCount()
                            < pending.replantTick()
            ) {

                continue;
            }

            ServerPlayer player =
                    server.getPlayerList()
                            .getPlayer(
                                    pending.playerId()
                            );

            if (
                    player != null
                            && pending.level()
                            == player.serverLevel()
                            && pending.level()
                            .getBlockState(
                                    pending.pos()
                            )
                            .isAir()
                            && pending.replantedState()
                            .canSurvive(
                                    pending.level(),
                                    pending.pos()
                            )
            ) {

                int seedSlot =
                        findInventoryItem(
                                player,
                                pending.seedItem()
                        );

                if (seedSlot >= 0) {

                    if (!player.isCreative()) {

                        player.getInventory()
                                .getItem(seedSlot)
                                .shrink(1);
                    }

                    pending.level().setBlock(
                            pending.pos(),
                            pending.replantedState(),
                            3
                    );
                }
            }

            iterator.remove();
        }
    }

    private static int findInventoryItem(
            ServerPlayer player,
            Item item
    ) {

        for (
                int slot = 0;
                slot < player.getInventory()
                        .getContainerSize();
                slot++
        ) {

            if (
                    player.getInventory()
                            .getItem(slot)
                            .is(item)
            ) {

                return slot;
            }
        }

        return -1;
    }

    private static Item getHarvestItem(
            BlockState state
    ) {

        if (state.is(Blocks.WHEAT)) {
            return Items.WHEAT;
        }

        if (state.is(Blocks.BEETROOTS)) {
            return Items.BEETROOT;
        }

        if (state.is(Blocks.CARROTS)) {
            return Items.CARROT;
        }

        if (state.is(Blocks.POTATOES)) {
            return Items.POTATO;
        }

        if (state.is(Blocks.NETHER_WART)) {
            return Items.NETHER_WART;
        }

        if (state.is(Blocks.COCOA)) {
            return Items.COCOA_BEANS;
        }

        if (state.is(Blocks.PUMPKIN)) {
            return Items.PUMPKIN;
        }

        if (state.is(Blocks.MELON)) {
            return Items.MELON_SLICE;
        }

        if (state.is(Blocks.TORCHFLOWER_CROP)) {
            return Items.TORCHFLOWER;
        }

        if (state.is(Blocks.SWEET_BERRY_BUSH)) {
            return Items.SWEET_BERRIES;
        }

        return null;
    }

    private static Item getReplantItem(
            BlockState state
    ) {

        if (state.is(Blocks.WHEAT)) {
            return Items.WHEAT_SEEDS;
        }

        if (state.is(Blocks.BEETROOTS)) {
            return Items.BEETROOT_SEEDS;
        }

        if (state.is(Blocks.CARROTS)) {
            return Items.CARROT;
        }

        if (state.is(Blocks.POTATOES)) {
            return Items.POTATO;
        }

        if (state.is(Blocks.NETHER_WART)) {
            return Items.NETHER_WART;
        }

        if (state.is(Blocks.COCOA)) {
            return Items.COCOA_BEANS;
        }

        if (state.is(Blocks.TORCHFLOWER_CROP)) {
            return Items.TORCHFLOWER_SEEDS;
        }

        if (state.is(Blocks.SWEET_BERRY_BUSH)) {
            return Items.SWEET_BERRIES;
        }

        return null;
    }

    private static BlockState getReplantedState(
            BlockState state
    ) {

        if (state.getBlock() instanceof CropBlock crop) {

            return crop.getStateForAge(0);
        }

        if (state.is(Blocks.NETHER_WART)) {

            return state.setValue(
                    NetherWartBlock.AGE,
                    0
            );
        }

        if (state.is(Blocks.COCOA)) {

            return state.setValue(
                    CocoaBlock.AGE,
                    0
            );
        }

        if (state.is(Blocks.SWEET_BERRY_BUSH)) {

            return state.setValue(
                    SweetBerryBushBlock.AGE,
                    0
            );
        }

        return null;
    }

    private static void queueInteraction(
            PendingBlockInteraction interaction
    ) {

        boolean alreadyPending =
                PENDING_INTERACTIONS.stream()
                        .anyMatch(
                                pending ->
                                        pending.matches(
                                                interaction
                                        )
                        );

        if (!alreadyPending) {

            PENDING_INTERACTIONS.add(
                    interaction
            );
        }
    }

    private static boolean isMatureCrop(
            BlockState state
    ) {

        if (state.getBlock() instanceof CropBlock crop) {

            return crop.isMaxAge(state);
        }

        if (state.is(Blocks.NETHER_WART)) {

            return state.getValue(
                    NetherWartBlock.AGE
            ) == NetherWartBlock.MAX_AGE;
        }

        if (state.is(Blocks.COCOA)) {

            return state.getValue(
                    CocoaBlock.AGE
            ) == CocoaBlock.MAX_AGE;
        }

        if (state.is(Blocks.SWEET_BERRY_BUSH)) {

            return state.getValue(
                    SweetBerryBushBlock.AGE
            ) == SweetBerryBushBlock.MAX_AGE;
        }

        return state.is(Blocks.PUMPKIN)
                || state.is(Blocks.MELON);
    }

    private static boolean isMushroom(
            BlockState state
    ) {

        return state.is(Blocks.BROWN_MUSHROOM)
                || state.is(Blocks.RED_MUSHROOM);
    }

    private static boolean isFarmingPlant(
            BlockState state
    ) {

        return state.getBlock()
                instanceof CropBlock
                || state.is(Blocks.NETHER_WART)
                || state.is(Blocks.COCOA)
                || state.is(Blocks.SWEET_BERRY_BUSH)
                || state.is(Blocks.BROWN_MUSHROOM)
                || state.is(Blocks.RED_MUSHROOM);
    }

    private static boolean didPlantCrop(
            BlockState originalState,
            BlockState currentState
    ) {

        return !currentState.equals(originalState)
                && isFarmingPlant(currentState);
    }

    private static boolean didHarvestBerryBush(
            ServerLevel level,
            BlockPos pos
    ) {

        BlockState state =
                level.getBlockState(pos);

        return state.is(Blocks.SWEET_BERRY_BUSH)
                && state.getValue(
                SweetBerryBushBlock.AGE
        ) == 1;
    }

    private record PendingBlockInteraction(
            UUID playerId,
            ServerLevel level,
            BlockPos primaryPos,
            BlockPos secondaryPos,
            BlockState originalPrimaryState,
            BlockState originalSecondaryState,
            int checkTick,
            boolean harvest
    ) {

        private boolean matches(
                PendingBlockInteraction other
        ) {

            return playerId.equals(other.playerId)
                    && level == other.level
                    && primaryPos.equals(
                    other.primaryPos
            )
                    && secondaryPos.equals(
                    other.secondaryPos
            )
                    && harvest == other.harvest;
        }

        private static PendingBlockInteraction plant(
                ServerPlayer player,
                ServerLevel level,
                BlockPos primaryPos,
                BlockPos secondaryPos,
                BlockState originalPrimaryState,
                BlockState originalSecondaryState,
                int checkTick
        ) {

            return new PendingBlockInteraction(
                    player.getUUID(),
                    level,
                    primaryPos.immutable(),
                    secondaryPos.immutable(),
                    originalPrimaryState,
                    originalSecondaryState,
                    checkTick,
                    false
            );
        }

        private static PendingBlockInteraction harvest(
                ServerPlayer player,
                ServerLevel level,
                BlockPos pos,
                int checkTick
        ) {

            return new PendingBlockInteraction(
                    player.getUUID(),
                    level,
                    pos.immutable(),
                    pos.immutable(),
                    level.getBlockState(pos),
                    level.getBlockState(pos),
                    checkTick,
                    true
            );
        }
    }

    private record HarvestKey(
            UUID playerId,
            ServerLevel level,
            BlockPos pos
    ) {
    }

    private record PendingAutoReplant(
            UUID playerId,
            ServerLevel level,
            BlockPos pos,
            Item seedItem,
            BlockState replantedState,
            int replantTick
    ) {
    }
}
