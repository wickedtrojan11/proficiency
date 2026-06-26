package com.trojan.proficiency.menu;

import com.trojan.proficiency.block.SolarComposterBlockEntity;
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

public class SolarComposterMenu extends AbstractContainerMenu {

    private static final int INPUT_SLOT_COUNT = 5;
    private static final int OUTPUT_SLOT = 5;
    private static final int CONTAINER_SLOT_COUNT = 6;
    private static final int INVENTORY_START = 6;
    private static final int INVENTORY_END = 33;
    private static final int HOTBAR_START = 33;
    private static final int HOTBAR_END = 42;

    private final Container container;
    private final ContainerData data;

    public SolarComposterMenu(
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

    public SolarComposterMenu(
            int containerId,
            Inventory playerInventory,
            Container container,
            ContainerData data
    ) {

        super(
                ModMenus.SOLAR_COMPOSTER,
                containerId
        );

        checkContainerSize(
                container,
                CONTAINER_SLOT_COUNT
        );

        this.container = container;
        this.data = data;

        container.startOpen(playerInventory.player);

        for (int slot = 0; slot < INPUT_SLOT_COUNT; slot++) {

            addSlot(
                    new CompostableInputSlot(
                            container,
                            slot,
                            28 + slot * 18,
                            25
                    )
            );
        }

        addSlot(
                new BonemealOutputSlot(
                        container,
                        OUTPUT_SLOT,
                        158,
                        25
                )
        );

        for (int row = 0; row < 3; row++) {

            for (int column = 0; column < 9; column++) {

                addSlot(
                        new Slot(
                                playerInventory,
                                column + row * 9 + 9,
                                8 + column * 18,
                                84 + row * 18
                        )
                );
            }
        }

        for (int column = 0; column < 9; column++) {

            addSlot(
                    new Slot(
                            playerInventory,
                            column,
                            8 + column * 18,
                            142
                    )
            );
        }

        addDataSlots(data);
    }

    @Override
    public boolean stillValid(
            Player player
    ) {

        return container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(
            Player player,
            int index
    ) {

        ItemStack original =
                ItemStack.EMPTY;

        Slot slot =
                slots.get(index);

        if (
                slot == null
                        || !slot.hasItem()
        ) {

            return original;
        }

        ItemStack stack =
                slot.getItem();

        original = stack.copy();

        if (index == OUTPUT_SLOT) {

            if (!moveItemStackTo(
                    stack,
                    INVENTORY_START,
                    HOTBAR_END,
                    true
            )) {

                return ItemStack.EMPTY;
            }

            slot.onQuickCraft(
                    stack,
                    original
            );

        } else if (index < CONTAINER_SLOT_COUNT) {

            if (!moveItemStackTo(
                    stack,
                    INVENTORY_START,
                    HOTBAR_END,
                    false
            )) {

                return ItemStack.EMPTY;
            }

        } else if (
                SolarComposterBlockEntity.isCompostable(stack)
        ) {

            if (!moveItemStackTo(
                    stack,
                    0,
                    INPUT_SLOT_COUNT,
                    false
            )) {

                return ItemStack.EMPTY;
            }

        } else if (
                index >= INVENTORY_START
                        && index < INVENTORY_END
        ) {

            if (!moveItemStackTo(
                    stack,
                    HOTBAR_START,
                    HOTBAR_END,
                    false
            )) {

                return ItemStack.EMPTY;
            }

        } else if (
                index >= HOTBAR_START
                        && index < HOTBAR_END
                        && !moveItemStackTo(
                        stack,
                        INVENTORY_START,
                        INVENTORY_END,
                        false
                )
        ) {

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

        slot.onTake(
                player,
                stack
        );

        return original;
    }

    @Override
    public void removed(
            Player player
    ) {

        super.removed(player);
        container.stopOpen(player);
    }

    public int getProgressWidth() {

        int progress =
                data.get(0);

        int maxProgress =
                data.get(1);

        if (
                progress <= 0
                        || maxProgress <= 0
        ) {

            return 0;
        }

        return progress * 24 / maxProgress;
    }

    private static class CompostableInputSlot extends Slot {

        private CompostableInputSlot(
                Container container,
                int slot,
                int x,
                int y
        ) {

            super(
                    container,
                    slot,
                    x,
                    y
            );
        }

        @Override
        public boolean mayPlace(
                ItemStack stack
        ) {

            return SolarComposterBlockEntity.isCompostable(
                    stack
            );
        }
    }

    private static class BonemealOutputSlot extends Slot {

        private BonemealOutputSlot(
                Container container,
                int slot,
                int x,
                int y
        ) {

            super(
                    container,
                    slot,
                    x,
                    y
            );
        }

        @Override
        public boolean mayPlace(
                ItemStack stack
        ) {

            return false;
        }

        @Override
        public boolean mayPickup(
                Player player
        ) {

            return getItem().is(Items.BONE_MEAL);
        }
    }
}
