package com.trojan.proficiency.item;

import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class ExperimentalAlchemyRegistry {

    public static final String DISCOVERY_PREFIX = "experimental:";
    private static final List<Entry> RECIPES = List.of(
            new Entry(
                    "strength_speed",
                    "Strength + Speed",
                    MobEffects.DAMAGE_BOOST,
                    MobEffects.MOVEMENT_SPEED
            ),
            new Entry(
                    "fire_resistance_regeneration",
                    "Fire Resistance + Regeneration",
                    MobEffects.FIRE_RESISTANCE,
                    MobEffects.REGENERATION
            ),
            new Entry(
                    "night_vision_water_breathing",
                    "Night Vision + Water Breathing",
                    MobEffects.NIGHT_VISION,
                    MobEffects.WATER_BREATHING
            ),
            new Entry(
                    "resistance_jump_boost",
                    "Resistance + Jump Boost",
                    MobEffects.DAMAGE_RESISTANCE,
                    MobEffects.JUMP
            )
    );

    private ExperimentalAlchemyRegistry() {
    }

    public static List<Entry> recipes() {
        return RECIPES;
    }

    public static Entry findRecipe(
            ItemStack basePotion,
            ItemStack ingredientPotion
    ) {
        if (!isCompletedPotion(basePotion)
                || !isCompletedPotion(ingredientPotion)) {
            return null;
        }

        for (Entry recipe : RECIPES) {
            if (recipe.matches(basePotion, ingredientPotion)) {
                return recipe;
            }
        }
        return null;
    }

    public static Entry randomUnknown(
            Set<String> discoveries,
            RandomSource random
    ) {
        List<Entry> unknown = new ArrayList<>();
        for (Entry recipe : RECIPES) {
            if (!discoveries.contains(recipe.discoveryKey())) {
                unknown.add(recipe);
            }
        }
        if (unknown.isEmpty()) {
            return null;
        }
        return unknown.get(random.nextInt(unknown.size()));
    }

    private static boolean isCompletedPotion(ItemStack stack) {
        if (!stack.is(Items.POTION)
                && !stack.is(Items.SPLASH_POTION)
                && !stack.is(Items.LINGERING_POTION)) {
            return false;
        }
        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        return contents != null && contents.hasEffects();
    }

    public record Entry(
            String id,
            String displayName,
            Holder<MobEffect> firstEffect,
            Holder<MobEffect> secondEffect
    ) {
        public String discoveryKey() {
            return DISCOVERY_PREFIX + id;
        }

        private boolean matches(
                ItemStack basePotion,
                ItemStack ingredientPotion
        ) {
            return (hasEffect(basePotion, firstEffect)
                    && hasEffect(ingredientPotion, secondEffect))
                    || (hasEffect(basePotion, secondEffect)
                    && hasEffect(ingredientPotion, firstEffect));
        }

        private boolean hasEffect(
                ItemStack stack,
                Holder<MobEffect> effect
        ) {
            PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
            if (contents == null) {
                return false;
            }
            for (net.minecraft.world.effect.MobEffectInstance instance
                    : contents.getAllEffects()) {
                if (instance.is(effect)) {
                    return true;
                }
            }
            return false;
        }
    }
}
