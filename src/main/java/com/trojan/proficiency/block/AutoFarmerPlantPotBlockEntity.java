package com.trojan.proficiency.block;

import com.trojan.proficiency.SkillManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.UUID;

public class AutoFarmerPlantPotBlockEntity extends BlockEntity {

    private static final int GROW_TICKS = 20 * 90;
    private static final float BONUS_HARVEST_CHANCE = 0.10f;

    private Item cropItem;
    private UUID ownerId;
    private int growthTicks;

    public AutoFarmerPlantPotBlockEntity(
            BlockPos pos,
            BlockState blockState
    ) {

        super(
                ModBlocks.AUTO_FARMER_PLANT_POT_ENTITY,
                pos,
                blockState
        );
    }

    public static void serverTick(
            net.minecraft.world.level.Level level,
            BlockPos pos,
            BlockState state,
            AutoFarmerPlantPotBlockEntity blockEntity
    ) {

        if (
                !(level instanceof ServerLevel serverLevel)
                        || blockEntity.cropItem == null
                        || blockEntity.ownerId == null
                        || !SkillManager.hasFarmingPerk(
                        blockEntity.ownerId,
                        "greenhouse_genius"
                )
                        || !SkillManager.isFarmingAutoReplantEnabled(
                        blockEntity.ownerId
                )
        ) {

            return;
        }

        blockEntity.growthTicks++;
        blockEntity.updateVisualStage();

        if (
                serverLevel.random.nextInt(80) == 0
                        && blockEntity.growthTicks < GROW_TICKS
        ) {

            serverLevel.sendParticles(
                    ParticleTypes.HAPPY_VILLAGER,
                    pos.getX() + 0.5,
                    pos.getY() + 0.65,
                    pos.getZ() + 0.5,
                    1,
                    0.15,
                    0.08,
                    0.15,
                    0.0
            );
        }

        if (blockEntity.growthTicks < GROW_TICKS) {
            return;
        }

        List<ItemStack> drops =
                blockEntity.createHarvestDrops(
                        serverLevel
                );

        if (
                drops.stream()
                        .allMatch(
                                drop -> blockEntity.outputItem(
                                        drop.copy()
                                )
                        )
        ) {

            blockEntity.growthTicks = 0;
            blockEntity.updateVisualStage();
            serverLevel.sendParticles(
                    ParticleTypes.COMPOSTER,
                    pos.getX() + 0.5,
                    pos.getY() + 0.65,
                    pos.getZ() + 0.5,
                    4,
                    0.2,
                    0.1,
                    0.2,
                    0.0
            );
            blockEntity.setChanged();
        }
    }

    public boolean setCrop(
            ItemStack stack,
            UUID playerId
    ) {

        Item supportedCrop =
                getSupportedCropItem(stack);

        if (
                supportedCrop == null
                        || cropItem != null
        ) {

            return false;
        }

        cropItem = supportedCrop;
        ownerId = playerId;
        growthTicks = 0;
        updateVisualStage();
        setChanged();
        return true;
    }

    public boolean hasCrop() {

        return cropItem != null;
    }

    public void dropStoredCrop() {

        if (
                level == null
                        || cropItem == null
        ) {

            return;
        }

        Block.popResource(
                level,
                worldPosition,
                new ItemStack(cropItem)
        );

        cropItem = null;
        updateVisualStage();
        setChanged();
    }

    private void updateVisualStage() {

        if (level == null) {
            return;
        }

        int stage = getVisualStage();

        BlockState state =
                getBlockState();

        if (
                state.getBlock()
                        instanceof AutoFarmerPlantPotBlock
                        && state.getValue(
                        AutoFarmerPlantPotBlock.STAGE
                ) != stage
        ) {

            level.setBlock(
                    worldPosition,
                    state.setValue(
                            AutoFarmerPlantPotBlock.STAGE,
                            stage
                    ),
                    3
            );
        }
    }

    private int getVisualStage() {

        if (cropItem == null) {
            return 0;
        }

        if (growthTicks >= GROW_TICKS) {
            return 4;
        }

        float progress =
                growthTicks / (float) GROW_TICKS;

        if (progress >= 0.75f) {
            return 3;
        }

        if (progress >= 0.45f) {
            return 2;
        }

        return 1;
    }

    public int getGrowthPercent() {

        return Math.min(
                100,
                Math.round(
                        growthTicks * 100.0f / GROW_TICKS
                )
        );
    }

    private List<ItemStack> createHarvestDrops(
            ServerLevel level
    ) {

        Item harvestItem =
                getHarvestItem(cropItem);

        int count =
                getBaseHarvestCount(cropItem);

        if (
                ownerId != null
                        && SkillManager.isFarmingBonusHarvestsEnabled(
                        ownerId
                )
                        && SkillManager.hasFarmingPerk(
                        ownerId,
                        "better_yields"
                )
                        && level.random.nextFloat()
                        < BONUS_HARVEST_CHANCE
        ) {

            count++;
        }

        return List.of(
                new ItemStack(
                        harvestItem,
                        count
                )
        );
    }

    private boolean outputItem(
            ItemStack stack
    ) {

        if (level == null) {
            return false;
        }

        for (Direction direction : List.of(
                Direction.DOWN,
                Direction.NORTH,
                Direction.SOUTH,
                Direction.EAST,
                Direction.WEST,
                Direction.UP
        )) {

            BlockEntity blockEntity =
                    level.getBlockEntity(
                            worldPosition.relative(direction)
                    );

            if (
                    blockEntity instanceof Container container
                            && insertIntoContainer(
                            container,
                            stack
                    )
            ) {

                return true;
            }
        }

        Block.popResource(
                level,
                worldPosition.above(),
                stack
        );

        return true;
    }

    private static boolean insertIntoContainer(
            Container container,
            ItemStack stack
    ) {

        for (int slot = 0; slot < container.getContainerSize(); slot++) {

            ItemStack existing =
                    container.getItem(slot);

            if (existing.isEmpty()) {

                container.setItem(
                        slot,
                        stack
                );

                container.setChanged();
                return true;
            }

            if (
                    ItemStack.isSameItemSameComponents(
                            existing,
                            stack
                    )
                            && existing.getCount()
                            < existing.getMaxStackSize()
            ) {

                int transfer =
                        Math.min(
                                stack.getCount(),
                                existing.getMaxStackSize()
                                        - existing.getCount()
                        );

                existing.grow(transfer);
                stack.shrink(transfer);
                container.setChanged();

                if (stack.isEmpty()) {
                    return true;
                }
            }
        }

        return stack.isEmpty();
    }

    public static Item getSupportedCropItem(
            ItemStack stack
    ) {

        if (stack.is(Items.WHEAT_SEEDS)) {
            return Items.WHEAT_SEEDS;
        }

        if (stack.is(Items.CARROT)) {
            return Items.CARROT;
        }

        if (stack.is(Items.POTATO)) {
            return Items.POTATO;
        }

        if (stack.is(Items.BEETROOT_SEEDS)) {
            return Items.BEETROOT_SEEDS;
        }

        if (stack.is(Items.NETHER_WART)) {
            return Items.NETHER_WART;
        }

        if (stack.is(Items.SWEET_BERRIES)) {
            return Items.SWEET_BERRIES;
        }

        return null;
    }

    private static Item getHarvestItem(
            Item crop
    ) {

        if (crop == Items.WHEAT_SEEDS) {
            return Items.WHEAT;
        }

        if (crop == Items.BEETROOT_SEEDS) {
            return Items.BEETROOT;
        }

        return crop;
    }

    private static int getBaseHarvestCount(
            Item crop
    ) {

        if (crop == Items.SWEET_BERRIES) {
            return 2;
        }

        return 1;
    }

    @Override
    protected void saveAdditional(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {

        super.saveAdditional(
                tag,
                registries
        );

        if (cropItem != null) {

            tag.putString(
                    "CropItem",
                    BuiltInRegistries.ITEM
                            .getKey(cropItem)
                            .toString()
            );
        }

        if (ownerId != null) {
            tag.putUUID(
                    "Owner",
                    ownerId
            );
        }

        tag.putInt(
                "GrowthTicks",
                growthTicks
        );
    }

    @Override
    protected void loadAdditional(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {

        super.loadAdditional(
                tag,
                registries
        );

        if (tag.contains("CropItem")) {

            cropItem =
                    BuiltInRegistries.ITEM.get(
                            ResourceLocation.parse(
                                    tag.getString("CropItem")
                            )
                    );
        }

        if (tag.hasUUID("Owner")) {
            ownerId = tag.getUUID("Owner");
        }

        growthTicks =
                tag.getInt("GrowthTicks");

        updateVisualStage();
    }
}
