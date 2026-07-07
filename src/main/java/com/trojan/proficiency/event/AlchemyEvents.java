package com.trojan.proficiency.event;

import com.trojan.proficiency.SkillManager;
import com.trojan.proficiency.item.ModItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class AlchemyEvents {

    public static final String CUSTOM_DATA_STAGE = "proficiency_alchemy_stage";
    public static final String CUSTOM_DATA_EXTRA_DURATION =
            "proficiency_extra_duration";
    private static final String STAGE_HONEYED_BASE = "honeyed_base";
    private static final int BREW_XP = 5;
    private static final int EXTENSION_XP = 1;
    private static final int BASE_XP_POTION_DURATION_TICKS = 5 * 60 * 20;
    private static final int HONEY_EXTENSION_TICKS = 60 * 20;

    private AlchemyEvents() {
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(
                server -> SkillManager.tickAlchemyXpBuffs()
        );
    }

    public static boolean isCustomBrewable(
            NonNullList<ItemStack> items
    ) {
        return isCustomBrewable(items, null, null);
    }

    public static boolean isCustomBrewable(
            NonNullList<ItemStack> items,
            Level level,
            BlockPos pos
    ) {
        ItemStack ingredient = items.get(3);

        if (ingredient.is(Items.HONEYCOMB)) {
            return hasPotion(items, AlchemyEvents::isAwkwardPotion);
        }
        if (ingredient.is(Items.BONE_MEAL)) {
            return hasPotion(items, AlchemyEvents::isHoneyedBase);
        }
        if (ingredient.is(Items.ENDER_PEARL)) {
            return hasPotion(
                    items,
                    stack -> stack.is(ModItems.DOUBLE_XP_POTION)
            );
        }
        if (ingredient.is(Items.HONEY_BOTTLE)) {
            if (level != null && pos != null) {
                int maxExtraTicks = getNearbyHoneyExtensionCap(level, pos);
                return hasPotion(
                        items,
                        stack -> canExtendXpPotion(stack, maxExtraTicks)
                );
            }
            return hasPotion(items, AlchemyEvents::canExtendXpPotion);
        }

        return false;
    }

    public static boolean isCustomBrewingIngredient(ItemStack stack) {
        return stack.is(Items.HONEYCOMB)
                || stack.is(Items.BONE_MEAL)
                || stack.is(Items.ENDER_PEARL)
                || stack.is(Items.HONEY_BOTTLE);
    }

    public static boolean isXpElixir(ItemStack stack) {
        return stack.is(ModItems.DOUBLE_XP_POTION)
                || stack.is(ModItems.TRIPLE_XP_POTION);
    }

    public static boolean handleCustomBrew(
            Level level,
            BlockPos pos,
            NonNullList<ItemStack> items
    ) {
        ItemStack ingredient = items.get(3);
        boolean changed = false;
        int xpAward = BREW_XP;

        if (ingredient.is(Items.HONEYCOMB)) {
            changed = replaceMatching(
                    items,
                    AlchemyEvents::isAwkwardPotion,
                    AlchemyEvents::honeyedBase
            );
        } else if (ingredient.is(Items.BONE_MEAL)) {
            changed = replaceMatching(
                    items,
                    AlchemyEvents::isHoneyedBase,
                    stack -> xpPotion(ModItems.DOUBLE_XP_POTION, 0)
            );
        } else if (ingredient.is(Items.ENDER_PEARL)) {
            changed = replaceMatching(
                    items,
                    stack -> stack.is(ModItems.DOUBLE_XP_POTION),
                    stack -> xpPotion(
                            ModItems.TRIPLE_XP_POTION,
                            getExtraDuration(stack)
                    )
            );
        } else if (ingredient.is(Items.HONEY_BOTTLE)) {
            int maxExtraTicks = getNearbyHoneyExtensionCap(level, pos);
            changed = extendMatchingXpPotions(items, maxExtraTicks);
            xpAward = EXTENSION_XP;
        }

        if (!changed) {
            return false;
        }

        consumeIngredient(level, pos, items);
        awardNearbyAlchemyXp(level, pos, xpAward);
        if (ingredient.is(Items.HONEY_BOTTLE)) {
            playHoneyExtensionFeedback(level, pos);
        }
        level.playSound(
                null,
                pos,
                SoundEvents.BREWING_STAND_BREW,
                SoundSource.BLOCKS,
                0.6f,
                1.1f
        );
        return true;
    }

    public static void awardNearbyAlchemyXp(Level level, BlockPos pos) {
        awardNearbyAlchemyXp(level, pos, BREW_XP);
    }

    private static void awardNearbyAlchemyXp(
            Level level,
            BlockPos pos,
            int amount
    ) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        serverLevel.getPlayers(
                        player -> player.distanceToSqr(
                                pos.getX() + 0.5,
                                pos.getY() + 0.5,
                                pos.getZ() + 0.5
                        ) <= 8.0 * 8.0
                )
                .stream()
                .min(Comparator.comparingDouble(
                        player -> player.distanceToSqr(
                                pos.getX() + 0.5,
                                pos.getY() + 0.5,
                                pos.getZ() + 0.5
                        )
                ))
                .ifPresent(player -> SkillManager.addAlchemyXp(player, amount));
    }

    public static void maybeRefundVanillaIngredient(
            Level level,
            BlockPos pos,
            NonNullList<ItemStack> items,
            ItemStack ingredient
    ) {
        if (ingredient.isEmpty() || !shouldPreserveIngredient(level, pos)) {
            return;
        }

        ItemStack ingredientSlot = items.get(3);
        if (ingredientSlot.isEmpty()) {
            items.set(3, ingredient.copyWithCount(1));
            return;
        }
        if (
                ItemStack.isSameItemSameComponents(
                        ingredientSlot,
                        ingredient
                )
                        && ingredientSlot.getCount()
                        < ingredientSlot.getMaxStackSize()
        ) {
            ingredientSlot.grow(1);
            return;
        }

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.addFreshEntity(new ItemEntity(
                    serverLevel,
                    pos.getX() + 0.5,
                    pos.getY() + 1.0,
                    pos.getZ() + 0.5,
                    ingredient.copyWithCount(1)
            ));
        }
    }

    public static void maybeUpgradeBrewedPotionPotency(
            Level level,
            BlockPos pos,
            NonNullList<ItemStack> items
    ) {
        float chance = getNearbyPotencyChance(level, pos);
        if (
                chance <= 0.0f
                        || !(level instanceof ServerLevel serverLevel)
                        || serverLevel.random.nextFloat() >= chance
        ) {
            return;
        }

        boolean upgradedAny = false;
        for (int slot = 0; slot < 3; slot++) {
            ItemStack stack = items.get(slot);
            if (!isPotionContainer(stack)) {
                continue;
            }
            ItemStack upgraded = upgradePotionPotency(stack);
            if (!ItemStack.matches(stack, upgraded)) {
                items.set(slot, upgraded);
                upgradedAny = true;
            }
        }

        if (upgradedAny) {
            level.playSound(
                    null,
                    pos,
                    SoundEvents.ENCHANTMENT_TABLE_USE,
                    SoundSource.BLOCKS,
                    0.35f,
                    1.6f
            );
        }
    }

    public static int getNearbyBrewingSpeedBonusPercent(
            Level level,
            BlockPos pos
    ) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return 0;
        }

        return serverLevel.getPlayers(
                        player -> player.distanceToSqr(
                                pos.getX() + 0.5,
                                pos.getY() + 0.5,
                                pos.getZ() + 0.5
                        ) <= 8.0 * 8.0
                )
                .stream()
                .filter(player -> SkillManager.isAlchemyToggleEnabled(
                        player.getUUID(),
                        "brewing_speed"
                ))
                .map(ServerPlayer::getUUID)
                .mapToInt(AlchemyEvents::getBrewingSpeedBonusPercent)
                .max()
                .orElse(0);
    }

    public static int getPotionPotencyChancePercent(UUID playerId) {
        if (SkillManager.hasAlchemyPerk(playerId, "philosophers_brew")) {
            return 100;
        }
        if (SkillManager.hasAlchemyPerk(playerId, "masters_formula")) {
            return 50;
        }
        if (SkillManager.hasAlchemyPerk(playerId, "distilled_perfection")) {
            return 35;
        }
        if (SkillManager.hasAlchemyPerk(playerId, "concentrated_essence")) {
            return 20;
        }
        if (SkillManager.hasAlchemyPerk(playerId, "refined_mixture")) {
            return 10;
        }
        return 0;
    }

    public static int getXpPotionDuration(ItemStack stack) {
        return BASE_XP_POTION_DURATION_TICKS + getExtraDuration(stack);
    }

    public static int getHoneyExtensionTicks(ItemStack stack) {
        return getExtraDuration(stack);
    }

    private static boolean hasPotion(
            NonNullList<ItemStack> items,
            StackPredicate predicate
    ) {
        for (int slot = 0; slot < 3; slot++) {
            if (predicate.test(items.get(slot))) {
                return true;
            }
        }
        return false;
    }

    private static boolean replaceMatching(
            NonNullList<ItemStack> items,
            StackPredicate predicate,
            StackTransformer transformer
    ) {
        boolean changed = false;
        for (int slot = 0; slot < 3; slot++) {
            ItemStack stack = items.get(slot);
            if (predicate.test(stack)) {
                items.set(slot, transformer.transform(stack));
                changed = true;
            }
        }
        return changed;
    }

    private static boolean isAwkwardPotion(ItemStack stack) {
        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        return stack.is(Items.POTION)
                && contents != null
                && contents.is(Potions.AWKWARD);
    }

    private static boolean isPotionContainer(ItemStack stack) {
        return stack.is(Items.POTION)
                || stack.is(Items.SPLASH_POTION)
                || stack.is(Items.LINGERING_POTION);
    }

    private static ItemStack upgradePotionPotency(ItemStack stack) {
        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        if (contents == null || !contents.hasEffects()) {
            return stack;
        }

        List<MobEffectInstance> upgradedEffects = new ArrayList<>();
        boolean changed = false;

        for (MobEffectInstance effect : contents.getAllEffects()) {
            if (canUpgradeEffect(effect)) {
                upgradedEffects.add(upgradeEffect(effect));
                changed = true;
            } else {
                upgradedEffects.add(new MobEffectInstance(effect));
            }
        }

        if (!changed) {
            return stack;
        }

        ItemStack upgraded = stack.copy();
        upgraded.set(
                DataComponents.POTION_CONTENTS,
                new PotionContents(
                        Optional.empty(),
                        contents.customColor(),
                        upgradedEffects
                )
        );
        return upgraded;
    }

    private static boolean canUpgradeEffect(MobEffectInstance effect) {
        int nextAmplifier = effect.getAmplifier() + 1;
        if (
                effect.getAmplifier() != 0
                        || nextAmplifier > 1
                        || !effect.getEffect().value().isBeneficial()
        ) {
            return false;
        }

        return hasRegisteredAmplifierProgression(
                effect.getEffect(),
                nextAmplifier
        );
    }

    private static MobEffectInstance upgradeEffect(MobEffectInstance effect) {
        return new MobEffectInstance(
                effect.getEffect(),
                effect.getDuration(),
                effect.getAmplifier() + 1,
                effect.isAmbient(),
                effect.isVisible(),
                effect.showIcon()
        );
    }

    private static boolean hasRegisteredAmplifierProgression(
            Holder<MobEffect> effect,
            int amplifier
    ) {
        for (Potion potion : BuiltInRegistries.POTION) {
            for (MobEffectInstance potionEffect : potion.getEffects()) {
                if (
                        potionEffect.is(effect)
                                && potionEffect.getAmplifier() == amplifier
                ) {
                    return true;
                }
            }
        }

        return false;
    }

    private static ItemStack honeyedBase(ItemStack ignored) {
        ItemStack stack = PotionContents.createItemStack(
                Items.POTION,
                Potions.AWKWARD
        );
        CompoundTag tag = new CompoundTag();
        tag.putString(CUSTOM_DATA_STAGE, STAGE_HONEYED_BASE);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        stack.set(
                DataComponents.CUSTOM_NAME,
                Component.literal("Honeyed Awkward Potion")
        );
        return stack;
    }

    private static boolean isHoneyedBase(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null
                && STAGE_HONEYED_BASE.equals(
                data.copyTag().getString(CUSTOM_DATA_STAGE)
        );
    }

    private static ItemStack xpPotion(
            net.minecraft.world.item.Item item,
            int extraDurationTicks
    ) {
        ItemStack stack = new ItemStack(item);
        if (extraDurationTicks > 0) {
            setExtraDuration(stack, extraDurationTicks);
        }
        return stack;
    }

    private static boolean canExtendXpPotion(ItemStack stack) {
        return (stack.is(ModItems.DOUBLE_XP_POTION)
                || stack.is(ModItems.TRIPLE_XP_POTION))
                && getExtraDuration(stack) < 10 * 60 * 20;
    }

    private static boolean canExtendXpPotion(
            ItemStack stack,
            int maxExtraTicks
    ) {
        return (stack.is(ModItems.DOUBLE_XP_POTION)
                || stack.is(ModItems.TRIPLE_XP_POTION))
                && maxExtraTicks > 0
                && getExtraDuration(stack) < maxExtraTicks;
    }

    private static ItemStack extendXpPotion(
            ItemStack stack,
            int maxExtraTicks
    ) {
        ItemStack result = stack.copy();
        int extra = Math.min(
                maxExtraTicks,
                getExtraDuration(result) + HONEY_EXTENSION_TICKS
        );
        setExtraDuration(result, extra);
        return result;
    }

    private static boolean extendMatchingXpPotions(
            NonNullList<ItemStack> items,
            int maxExtraTicks
    ) {
        boolean changed = false;

        for (int slot = 0; slot < 3; slot++) {
            ItemStack stack = items.get(slot);
            if (
                    !stack.is(ModItems.DOUBLE_XP_POTION)
                            && !stack.is(ModItems.TRIPLE_XP_POTION)
            ) {
                continue;
            }

            int currentExtra = getExtraDuration(stack);
            if (currentExtra >= maxExtraTicks) {
                continue;
            }

            items.set(slot, extendXpPotion(stack, maxExtraTicks));
            changed = true;
        }

        return changed;
    }

    private static void playHoneyExtensionFeedback(Level level, BlockPos pos) {
        level.playSound(
                null,
                pos,
                SoundEvents.HONEY_BLOCK_PLACE,
                SoundSource.BLOCKS,
                0.3f,
                1.4f
        );

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.HAPPY_VILLAGER,
                    pos.getX() + 0.5,
                    pos.getY() + 0.8,
                    pos.getZ() + 0.5,
                    3,
                    0.2,
                    0.15,
                    0.2,
                    0.0
            );
        }
    }

    private static int getExtraDuration(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            return 0;
        }
        return data.copyTag().getInt(CUSTOM_DATA_EXTRA_DURATION);
    }

    private static void setExtraDuration(ItemStack stack, int ticks) {
        CompoundTag tag = new CompoundTag();
        tag.putInt(CUSTOM_DATA_EXTRA_DURATION, ticks);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    private static int getNearbyHoneyExtensionCap(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return 0;
        }

        return serverLevel.getPlayers(
                        player -> player.distanceToSqr(
                                pos.getX() + 0.5,
                                pos.getY() + 0.5,
                                pos.getZ() + 0.5
                        ) <= 8.0 * 8.0
                )
                .stream()
                .map(ServerPlayer::getUUID)
                .mapToInt(AlchemyEvents::getHoneyExtensionCap)
                .max()
                .orElse(0);
    }

    private static float getNearbyPotencyChance(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return 0.0f;
        }

        return serverLevel.getPlayers(
                        player -> player.distanceToSqr(
                                pos.getX() + 0.5,
                                pos.getY() + 0.5,
                                pos.getZ() + 0.5
                        ) <= 8.0 * 8.0
                )
                .stream()
                .map(ServerPlayer::getUUID)
                .mapToInt(AlchemyEvents::getPotionPotencyChancePercent)
                .max()
                .orElse(0) / 100.0f;
    }

    private static int getHoneyExtensionCap(UUID playerId) {
        if (SkillManager.hasAlchemyPerk(playerId, "eternal_draught")) {
            return 10 * 60 * 20;
        }
        if (SkillManager.hasAlchemyPerk(playerId, "perfect_suspension")) {
            return 8 * 60 * 20;
        }
        if (SkillManager.hasAlchemyPerk(playerId, "deep_binding")) {
            return 6 * 60 * 20;
        }
        if (SkillManager.hasAlchemyPerk(playerId, "long_steep")) {
            return 4 * 60 * 20;
        }
        if (SkillManager.hasAlchemyPerk(playerId, "sweetened_stability")) {
            return 2 * 60 * 20;
        }
        return 0;
    }

    private static void consumeIngredient(
            Level level,
            BlockPos pos,
            NonNullList<ItemStack> items
    ) {
        ItemStack ingredient = items.get(3);
        if (shouldPreserveIngredient(level, pos)) {
            return;
        }
        Item remainderItem = ingredient.getItem().getCraftingRemainingItem();
        ingredient.shrink(1);
        if (ingredient.isEmpty()) {
            items.set(
                    3,
                    remainderItem == null
                            ? ItemStack.EMPTY
                            : new ItemStack(remainderItem)
            );
        }
    }

    private static boolean shouldPreserveIngredient(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        float chance = serverLevel.getPlayers(
                        player -> player.distanceToSqr(
                                pos.getX() + 0.5,
                                pos.getY() + 0.5,
                                pos.getZ() + 0.5
                        ) <= 8.0 * 8.0
                )
                .stream()
                .filter(player -> SkillManager.isAlchemyToggleEnabled(
                        player.getUUID(),
                        "ingredient_efficiency"
                ))
                .map(ServerPlayer::getUUID)
                .map(AlchemyEvents::getIngredientRefundChance)
                .max(Float::compare)
                .orElse(0.0f);

        return chance > 0.0f && serverLevel.random.nextFloat() < chance;
    }

    private static float getIngredientRefundChance(UUID playerId) {
        if (SkillManager.hasAlchemyPerk(playerId, "nothing_wasted")) {
            return 0.25f;
        }
        if (SkillManager.hasAlchemyPerk(playerId, "alchemical_reclaiming")) {
            return 0.18f;
        }
        if (SkillManager.hasAlchemyPerk(playerId, "glass_scraper")) {
            return 0.10f;
        }
        if (SkillManager.hasAlchemyPerk(playerId, "careful_measure")) {
            return 0.05f;
        }
        return 0.0f;
    }

    private static int getBrewingSpeedBonusPercent(UUID playerId) {
        if (SkillManager.hasAlchemyPerk(playerId, "rapid_infusion")) {
            return 35;
        }
        if (SkillManager.hasAlchemyPerk(playerId, "heated_reaction")) {
            return 20;
        }
        if (SkillManager.hasAlchemyPerk(playerId, "quick_stir")) {
            return 10;
        }
        return 0;
    }

    private interface StackPredicate {
        boolean test(ItemStack stack);
    }

    private interface StackTransformer {
        ItemStack transform(ItemStack stack);
    }
}
