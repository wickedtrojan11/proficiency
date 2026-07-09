package com.trojan.proficiency.item;

import com.trojan.proficiency.block.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Collection;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

public final class AlchemyIngredientRegistry {

    private static final Map<String, Entry> BY_KEY = new LinkedHashMap<>();
    private static final Map<Item, Entry> BY_ITEM = new LinkedHashMap<>();

    private AlchemyIngredientRegistry() {
    }

    public static void registerDefaults() {
        register(Items.NETHER_WART, "Potion Base ingredient",
                MobEffects.REGENERATION, 0, 10 * 20,
                List.of("Awkward Potion", "Potion Recipes"));
        register(Items.SUGAR, "Speed Potion ingredient",
                MobEffects.MOVEMENT_SPEED, 0, 10 * 20,
                List.of("Swiftness"));
        register(Items.RABBIT_FOOT, "Leaping Potion ingredient",
                MobEffects.JUMP, 0, 10 * 20,
                List.of("Leaping"));
        register(Items.GLISTERING_MELON_SLICE, "Healing Potion ingredient",
                MobEffects.HEAL, 0, 1,
                List.of("Healing"));
        register(Items.SPIDER_EYE, "Poison Potion ingredient",
                MobEffects.POISON, 0, 6 * 20,
                List.of("Poison"));
        register(Items.PUFFERFISH, "Water Breathing Potion ingredient",
                MobEffects.WATER_BREATHING, 0, 10 * 20,
                List.of("Water Breathing"));
        register(Items.MAGMA_CREAM, "Fire Resistance Potion ingredient",
                MobEffects.FIRE_RESISTANCE, 0, 10 * 20,
                List.of("Fire Resistance"));
        register(Items.GOLDEN_CARROT, "Night Vision Potion ingredient",
                MobEffects.NIGHT_VISION, 0, 10 * 20,
                List.of("Night Vision"));
        register(Items.GHAST_TEAR, "Regeneration Potion ingredient",
                MobEffects.REGENERATION, 0, 10 * 20,
                List.of("Regeneration"));
        register(Items.PHANTOM_MEMBRANE, "Slow Falling Potion ingredient",
                MobEffects.SLOW_FALLING, 0, 10 * 20,
                List.of("Slow Falling"));
        register(Items.TURTLE_HELMET, "Turtle Master Potion ingredient",
                MobEffects.DAMAGE_RESISTANCE, 0, 10 * 20,
                List.of("Turtle Master"));
        register(Items.HONEYCOMB, "Experience Elixir ingredient",
                MobEffects.ABSORPTION, 0, 12 * 20,
                List.of("Honeyed Base", "Experience Elixir"));
        register(Items.BONE_MEAL, "Experience Elixir catalyst",
                MobEffects.DIG_SPEED, 0, 10 * 20,
                List.of("Experience Elixir"));
        register(Items.ENDER_PEARL, "Greater Experience Elixir ingredient",
                MobEffects.MOVEMENT_SPEED, 0, 10 * 20,
                List.of("Greater Experience Elixir"));
        register(Items.HONEY_BOTTLE, "Potion Duration ingredient",
                MobEffects.REGENERATION, 0, 10 * 20,
                List.of("Potion Duration Extension"));
        register(ModBlocks.CAMELLIA_FLOWER.asItem(), "Camellia Oil ingredient",
                MobEffects.DAMAGE_RESISTANCE, 0, 10 * 20,
                List.of("Oil Base", "Camellia Oil"));
        register(Items.BLAZE_POWDER, "Fire Oil ingredient",
                MobEffects.FIRE_RESISTANCE, 0, 12 * 20,
                List.of("Strength", "Fire Oil"));
        register(Items.SNOWBALL, "Frost Oil ingredient",
                MobEffects.MOVEMENT_SLOWDOWN, 0, 8 * 20,
                List.of("Frost Oil"));
        register(Items.REDSTONE, "Miner's Oil ingredient",
                MobEffects.DIG_SPEED, 0, 12 * 20,
                List.of("Duration Upgrade", "Miner's Oil"));
        register(Items.GLOWSTONE_DUST, "Potion Potency modifier",
                MobEffects.GLOWING, 0, 8 * 20,
                List.of("Potency Upgrade"));
        register(Items.FERMENTED_SPIDER_EYE, "Corruption modifier",
                MobEffects.WEAKNESS, 0, 8 * 20,
                List.of("Fermented Spider Eye Conversions"));
        register(Items.GUNPOWDER, "Splash Potion modifier",
                MobEffects.MOVEMENT_SPEED, 0, 8 * 20,
                List.of("Splash Potions"));
        register(Items.DRAGON_BREATH, "Lingering Potion modifier",
                MobEffects.SLOW_FALLING, 0, 8 * 20,
                List.of("Lingering Potions"));
        register(Items.OAK_SAPLING, "Lumber Oil ingredient",
                MobEffects.DIG_SPEED, 0, 10 * 20,
                List.of("Lumber Oil"));
    }

    public static Entry register(
            Item item,
            String knownEffect,
            Holder<MobEffect> sampleEffect,
            int amplifier,
            int durationTicks,
            List<String> knownUses
    ) {
        String key = key(item);
        if (BY_KEY.containsKey(key) || BY_ITEM.containsKey(item)) {
            return BY_KEY.get(key);
        }
        Entry entry = new Entry(
                key,
                item,
                Component.literal(knownEffect),
                sampleEffect,
                amplifier,
                durationTicks,
                List.copyOf(knownUses)
        );
        BY_KEY.put(key, entry);
        BY_ITEM.put(item, entry);
        return entry;
    }

    public static Entry get(ItemStack stack) {
        return stack.isEmpty() ? null : BY_ITEM.get(stack.getItem());
    }

    public static Entry get(String key) {
        return BY_KEY.get(key);
    }

    public static Collection<Entry> entries() {
        return BY_KEY.values();
    }

    public static String key(Item item) {
        ResourceLocation location = BuiltInRegistries.ITEM.getKey(item);
        return location.toString();
    }

    public record Entry(
            String key,
            Item item,
            Component knownEffect,
            Holder<MobEffect> sampleEffect,
            int amplifier,
            int durationTicks,
            List<String> knownUses
    ) {
    }
}
