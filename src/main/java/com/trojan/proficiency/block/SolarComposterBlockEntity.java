package com.trojan.proficiency.block;

import com.trojan.proficiency.menu.SolarComposterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.NonNullList;

public class SolarComposterBlockEntity extends BlockEntity
        implements Container, MenuProvider {

    private static final int INPUT_SLOTS = 5;
    private static final int OUTPUT_SLOT = 5;
    private static final int SLOT_COUNT = 6;
    private static final int PROCESS_THRESHOLD = 1200;

    private final NonNullList<ItemStack> items =
            NonNullList.withSize(
                    SLOT_COUNT,
                    ItemStack.EMPTY
            );

    private int compostProgress;

    private final ContainerData data =
            new ContainerData() {

                @Override
                public int get(
                        int index
                ) {

                    return index == 0
                            ? compostProgress
                            : PROCESS_THRESHOLD;
                }

                @Override
                public void set(
                        int index,
                        int value
                ) {

                    if (index == 0) {
                        compostProgress = value;
                    }
                }

                @Override
                public int getCount() {

                    return 2;
                }
            };

    public SolarComposterBlockEntity(
            BlockPos pos,
            BlockState blockState
    ) {

        super(
                ModBlocks.SOLAR_COMPOSTER_ENTITY,
                pos,
                blockState
        );
    }

    public static void serverTick(
            net.minecraft.world.level.Level level,
            BlockPos pos,
            BlockState state,
            SolarComposterBlockEntity blockEntity
    ) {

        if (
                !(level instanceof ServerLevel serverLevel)
                        || !blockEntity.hasSunlight(serverLevel)
                        || !blockEntity.canOutputBonemeal()
        ) {

            return;
        }

        int inputSlot =
                blockEntity.findCompostableInputSlot();

        if (inputSlot < 0) {
            return;
        }

        ItemStack input =
                blockEntity.items.get(inputSlot);

        float compostability =
                ComposterBlock.COMPOSTABLES.getFloat(
                        input.getItem()
                );

        blockEntity.compostProgress += Math.max(
                1,
                Math.round(compostability * 5.0f)
        );

        if (blockEntity.compostProgress < PROCESS_THRESHOLD) {
            blockEntity.setChanged();
            return;
        }

        blockEntity.compostProgress = 0;
        input.shrink(1);

        ItemStack output =
                blockEntity.items.get(OUTPUT_SLOT);

        if (output.isEmpty()) {

            blockEntity.items.set(
                    OUTPUT_SLOT,
                    new ItemStack(Items.BONE_MEAL)
            );

        } else {

            output.grow(1);
        }

        blockEntity.setChanged();
    }

    public boolean insertCompostable(
            ItemStack stack
    ) {

        if (!isCompostable(stack)) {
            return false;
        }

        ItemStack single =
                stack.copyWithCount(1);

        for (int slot = 0; slot < INPUT_SLOTS; slot++) {

            ItemStack existing =
                    items.get(slot);

            if (existing.isEmpty()) {

                items.set(
                        slot,
                        single
                );

                setChanged();
                return true;
            }

            if (
                    ItemStack.isSameItemSameComponents(
                            existing,
                            single
                    )
                            && existing.getCount()
                            < existing.getMaxStackSize()
            ) {

                existing.grow(1);
                setChanged();
                return true;
            }
        }

        return false;
    }

    public ItemStack takeOutput() {

        ItemStack output =
                items.get(OUTPUT_SLOT);

        if (output.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack taken =
                output.copy();

        items.set(
                OUTPUT_SLOT,
                ItemStack.EMPTY
        );

        setChanged();
        return taken;
    }

    public static boolean isCompostable(
            ItemStack stack
    ) {

        return !stack.isEmpty()
                && ComposterBlock.COMPOSTABLES.containsKey(
                stack.getItem()
        );
    }

    @Override
    public Component getDisplayName() {

        return Component.translatable(
                "block.proficiency.solar_composter"
        );
    }

    @Override
    public AbstractContainerMenu createMenu(
            int containerId,
            Inventory playerInventory,
            Player player
    ) {

        return new SolarComposterMenu(
                containerId,
                playerInventory,
                this,
                data
        );
    }

    private boolean hasSunlight(
            ServerLevel level
    ) {

        BlockPos skyPos =
                worldPosition.above();

        return level.isDay()
                && level.canSeeSky(skyPos)
                && !level.isRainingAt(skyPos);
    }

    private boolean canOutputBonemeal() {

        ItemStack output =
                items.get(OUTPUT_SLOT);

        return output.isEmpty()
                || (
                output.is(Items.BONE_MEAL)
                        && output.getCount()
                        < output.getMaxStackSize()
        );
    }

    private int findCompostableInputSlot() {

        for (int slot = 0; slot < INPUT_SLOTS; slot++) {

            if (isCompostable(items.get(slot))) {
                return slot;
            }
        }

        return -1;
    }

    @Override
    public int getContainerSize() {

        return SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {

        return items.stream()
                .allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(
            int slot
    ) {

        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(
            int slot,
            int amount
    ) {

        ItemStack removed =
                ContainerHelper.removeItem(
                        items,
                        slot,
                        amount
                );

        if (!removed.isEmpty()) {
            setChanged();
        }

        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(
            int slot
    ) {

        return ContainerHelper.takeItem(
                items,
                slot
        );
    }

    @Override
    public void setItem(
            int slot,
            ItemStack stack
    ) {

        items.set(
                slot,
                stack
        );

        if (
                stack.getCount()
                        > getMaxStackSize()
        ) {

            stack.setCount(
                    getMaxStackSize()
            );
        }

        setChanged();
    }

    @Override
    public boolean canPlaceItem(
            int slot,
            ItemStack stack
    ) {

        return slot < INPUT_SLOTS
                && isCompostable(stack);
    }

    @Override
    public boolean stillValid(
            Player player
    ) {

        return Container.stillValidBlockEntity(
                this,
                player
        );
    }

    @Override
    public void clearContent() {

        items.clear();
        setChanged();
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

        ContainerHelper.saveAllItems(
                tag,
                items,
                registries
        );

        tag.putInt(
                "CompostProgress",
                compostProgress
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

        items.clear();

        ContainerHelper.loadAllItems(
                tag,
                items,
                registries
        );

        compostProgress =
                tag.getInt("CompostProgress");
    }
}
