package com.trojan.proficiency.block;

import com.trojan.proficiency.event.AlchemyEvents;
import com.trojan.proficiency.menu.ProficientBrewStandMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ProficientBrewStandBlockEntity extends BlockEntity
        implements WorldlyContainer, MenuProvider {

    public static final int POTION_SLOTS = 6;
    public static final int INGREDIENT_SLOT = 6;
    public static final int FUEL_SLOT = 7;
    public static final int SLOT_COUNT = 8;
    private static final int BREW_TIME = 400;
    private static final int[] TOP_SLOTS = {INGREDIENT_SLOT};
    private static final int[] BOTTOM_SLOTS = {0, 1, 2, 3, 4, 5, INGREDIENT_SLOT};
    private static final int[] SIDE_SLOTS = {0, 1, 2, 3, 4, 5, FUEL_SLOT};

    private final NonNullList<ItemStack> items =
            NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private int brewTime;
    private int fuel;
    private Item ingredient;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return index == 0 ? brewTime : fuel;
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                brewTime = value;
            } else {
                fuel = value;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public ProficientBrewStandBlockEntity(
            BlockPos pos,
            BlockState blockState
    ) {
        super(ModBlocks.PROFICIENT_BREW_STAND_ENTITY, pos, blockState);
    }

    public static void serverTick(
            Level level,
            BlockPos pos,
            BlockState state,
            ProficientBrewStandBlockEntity blockEntity
    ) {
        ItemStack fuelStack = blockEntity.items.get(FUEL_SLOT);
        if (blockEntity.fuel <= 0 && fuelStack.is(Items.BLAZE_POWDER)) {
            blockEntity.fuel = 20;
            fuelStack.shrink(1);
            setChanged(level, pos, state);
        }

        boolean brewable = blockEntity.isBrewable(level, pos);
        ItemStack ingredientStack = blockEntity.items.get(INGREDIENT_SLOT);
        if (blockEntity.brewTime > 0) {
            int speedBonus = AlchemyEvents.getNearbyBrewingSpeedBonusPercent(
                    level,
                    pos
            );
            blockEntity.brewTime -= 1 + Math.max(0, speedBonus / 10);
            if (blockEntity.brewTime <= 0 && brewable) {
                blockEntity.doBrew(level, pos);
                setChanged(level, pos, state);
            } else if (!brewable
                    || !ingredientStack.is(blockEntity.ingredient)) {
                blockEntity.brewTime = 0;
                setChanged(level, pos, state);
            }
            return;
        }

        if (brewable && blockEntity.fuel > 0) {
            blockEntity.fuel--;
            blockEntity.brewTime = BREW_TIME;
            blockEntity.ingredient = ingredientStack.getItem();
            setChanged(level, pos, state);
        }
    }

    private boolean isBrewable(Level level, BlockPos pos) {
        if (items.get(INGREDIENT_SLOT).isEmpty()) {
            return false;
        }
        if (isCustomBrewableBank(level, pos, 0)
                || isCustomBrewableBank(level, pos, 3)) {
            return true;
        }
        PotionBrewing brewing = level.potionBrewing();
        ItemStack ingredientStack = items.get(INGREDIENT_SLOT);
        for (int slot = 0; slot < POTION_SLOTS; slot++) {
            if (brewing.hasMix(ingredientStack, items.get(slot))) {
                return true;
            }
        }
        return false;
    }

    private boolean isCustomBrewableBank(
            Level level,
            BlockPos pos,
            int firstSlot
    ) {
        NonNullList<ItemStack> bank = createBank(firstSlot);
        return AlchemyEvents.isCustomBrewable(bank, level, pos);
    }

    private void doBrew(Level level, BlockPos pos) {
        boolean experimentalFirstBank = AlchemyEvents.isExperimentalBrewable(
                createBank(0),
                level,
                pos
        );
        boolean experimentalSecondBank = AlchemyEvents.isExperimentalBrewable(
                createBank(3),
                level,
                pos
        );
        boolean changed;
        if (experimentalFirstBank || experimentalSecondBank) {
            changed = applyCustomBank(
                    level,
                    pos,
                    experimentalFirstBank ? 0 : 3
            );
        } else {
            changed = applyCustomBank(level, pos, 0);
            changed = applyCustomBank(level, pos, 3) || changed;
        }
        if (changed) {
            consumeIngredient();
            AlchemyEvents.awardNearbyAlchemyXp(level, pos);
            level.playSound(
                    null,
                    pos,
                    SoundEvents.BREWING_STAND_BREW,
                    SoundSource.BLOCKS,
                    0.6f,
                    1.1f
            );
            return;
        }

        PotionBrewing brewing = level.potionBrewing();
        ItemStack ingredientStack = items.get(INGREDIENT_SLOT);
        for (int slot = 0; slot < POTION_SLOTS; slot++) {
            items.set(slot, brewing.mix(ingredientStack, items.get(slot)));
        }
        consumeIngredient();
        AlchemyEvents.maybeUpgradeBrewedPotionPotency(level, pos, items);
        AlchemyEvents.awardNearbyAlchemyXp(level, pos);
        level.playSound(
                null,
                pos,
                SoundEvents.BREWING_STAND_BREW,
                SoundSource.BLOCKS,
                0.6f,
                1.0f
        );
    }

    private boolean applyCustomBank(Level level, BlockPos pos, int firstSlot) {
        NonNullList<ItemStack> bank = createBank(firstSlot);
        if (!AlchemyEvents.applyCustomBrewResults(level, pos, bank)) {
            return false;
        }
        for (int slot = 0; slot < 3; slot++) {
            items.set(firstSlot + slot, bank.get(slot));
        }
        return true;
    }

    private NonNullList<ItemStack> createBank(int firstSlot) {
        NonNullList<ItemStack> bank =
                NonNullList.withSize(5, ItemStack.EMPTY);
        for (int slot = 0; slot < 3; slot++) {
            bank.set(slot, items.get(firstSlot + slot).copy());
        }
        bank.set(INGREDIENT_SLOT - 3, items.get(INGREDIENT_SLOT).copy());
        bank.set(4, items.get(FUEL_SLOT).copy());
        return bank;
    }

    private void consumeIngredient() {
        ItemStack stack = items.get(INGREDIENT_SLOT);
        Item remainder = stack.getItem().getCraftingRemainingItem();
        stack.shrink(1);
        if (stack.isEmpty()) {
            items.set(
                    INGREDIENT_SLOT,
                    remainder == null ? ItemStack.EMPTY : new ItemStack(remainder)
            );
        }
    }

    public static boolean isPotionSlotItem(ItemStack stack) {
        return stack.is(Items.POTION)
                || stack.is(Items.SPLASH_POTION)
                || stack.is(Items.LINGERING_POTION)
                || stack.is(Items.GLASS_BOTTLE)
                || AlchemyEvents.isXpElixir(stack)
                || AlchemyEvents.isOilBase(stack);
    }

    public static boolean isIngredientItem(Level level, ItemStack stack) {
        PotionBrewing brewing =
                level == null ? PotionBrewing.EMPTY : level.potionBrewing();
        return brewing.isIngredient(stack)
                || AlchemyEvents.isCustomBrewingIngredient(stack);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(
                "block.proficiency.proficient_brew_stand"
        );
    }

    @Override
    public AbstractContainerMenu createMenu(
            int containerId,
            Inventory playerInventory,
            Player player
    ) {
        return new ProficientBrewStandMenu(
                containerId,
                playerInventory,
                this,
                data
        );
    }

    @Override
    public int getContainerSize() {
        return SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack removed = ContainerHelper.removeItem(items, slot, amount);
        if (!removed.isEmpty()) {
            setChanged();
        }
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot < POTION_SLOTS) {
            return isPotionSlotItem(stack);
        }
        if (slot == INGREDIENT_SLOT) {
            return level != null && isIngredientItem(level, stack);
        }
        return slot == FUEL_SLOT && stack.is(Items.BLAZE_POWDER);
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
        setChanged();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.UP) {
            return TOP_SLOTS;
        }
        return side == Direction.DOWN ? BOTTOM_SLOTS : SIDE_SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(
            int slot,
            ItemStack stack,
            Direction direction
    ) {
        return canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(
            int slot,
            ItemStack stack,
            Direction direction
    ) {
        return slot != INGREDIENT_SLOT || stack.is(Items.GLASS_BOTTLE);
    }

    @Override
    protected void saveAdditional(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
        tag.putShort("BrewTime", (short) brewTime);
        tag.putByte("Fuel", (byte) fuel);
    }

    @Override
    protected void loadAdditional(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        super.loadAdditional(tag, registries);
        items.clear();
        ContainerHelper.loadAllItems(tag, items, registries);
        brewTime = tag.getShort("BrewTime");
        fuel = tag.getByte("Fuel");
        if (brewTime > 0) {
            ingredient = items.get(INGREDIENT_SLOT).getItem();
        }
    }
}
