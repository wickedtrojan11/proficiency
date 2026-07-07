package com.trojan.proficiency.menu;

import com.trojan.proficiency.block.ProficientBrewStandBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ProficientBrewStandMenu extends AbstractContainerMenu {

    private static final int CONTAINER_SLOT_COUNT = 8;
    private static final int INVENTORY_START = 8;
    private static final int INVENTORY_END = 35;
    private static final int HOTBAR_START = 35;
    private static final int HOTBAR_END = 44;

    private final Container container;
    private final ContainerData data;

    public ProficientBrewStandMenu(
            int containerId,
            Inventory playerInventory
    ) {
        this(
                containerId,
                playerInventory,
                new SimpleContainer(CONTAINER_SLOT_COUNT),
                new SimpleContainerData(2)
        );
    }

    public ProficientBrewStandMenu(
            int containerId,
            Inventory playerInventory,
            Container container,
            ContainerData data
    ) {
        super(ModMenus.PROFICIENT_BREW_STAND, containerId);
        checkContainerSize(container, CONTAINER_SLOT_COUNT);
        this.container = container;
        this.data = data;
        container.startOpen(playerInventory.player);

        addPotionSlots(container);
        addSlot(new IngredientSlot(container, 6, 89, 22));
        addSlot(new FuelSlot(container, 7, 17, 22));
        addPlayerInventory(playerInventory);
        addDataSlots(data);
    }

    private void addPotionSlots(Container container) {
        int[][] positions = {
                {56, 56},
                {79, 62},
                {102, 56},
                {56, 100},
                {79, 106},
                {102, 100}
        };
        for (int slot = 0; slot < positions.length; slot++) {
            addSlot(new PotionSlot(
                    container,
                    slot,
                    positions[slot][0],
                    positions[slot][1]
            ));
        }
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(
                        playerInventory,
                        column + row * 9 + 9,
                        8 + column * 18,
                        139 + row * 18
                ));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(
                    playerInventory,
                    column,
                    8 + column * 18,
                    197
            ));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack original = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return original;
        }

        ItemStack stack = slot.getItem();
        original = stack.copy();

        if (index < CONTAINER_SLOT_COUNT) {
            if (!moveItemStackTo(stack, INVENTORY_START, HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (stack.is(Items.BLAZE_POWDER)) {
            if (!moveItemStackTo(stack, 7, 8, false)) {
                return ItemStack.EMPTY;
            }
        } else if (ProficientBrewStandBlockEntity.isPotionSlotItem(stack)) {
            if (!moveItemStackTo(stack, 0, 6, false)) {
                return ItemStack.EMPTY;
            }
        } else if (ProficientBrewStandBlockEntity.isIngredientItem(
                player.level(),
                stack
        )) {
            if (!moveItemStackTo(stack, 6, 7, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index >= INVENTORY_START && index < INVENTORY_END) {
            if (!moveItemStackTo(stack, HOTBAR_START, HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index >= HOTBAR_START
                && index < HOTBAR_END
                && !moveItemStackTo(stack, INVENTORY_START, INVENTORY_END, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, stack);
        return original;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
    }

    public int getBrewProgressHeight() {
        int brewTime = data.get(0);
        return brewTime <= 0 ? 0 : brewTime * 28 / 400;
    }

    public int getFuelWidth() {
        int fuel = data.get(1);
        return Math.min(18, fuel * 18 / 20);
    }

    private static class PotionSlot extends Slot {

        private PotionSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return ProficientBrewStandBlockEntity.isPotionSlotItem(stack);
        }
    }

    private static class IngredientSlot extends Slot {

        private IngredientSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return true;
        }
    }

    private static class FuelSlot extends Slot {

        private FuelSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.is(Items.BLAZE_POWDER);
        }
    }
}
