package com.trojan.proficiency.item;

import com.trojan.proficiency.block.ModBlocks;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class AlchemyJournalRegistry {

    private static final List<RecipeEntry> RECIPES = createRecipes();

    private AlchemyJournalRegistry() {
    }

    private static List<RecipeEntry> createRecipes() {
        List<RecipeEntry> recipes = new ArrayList<>();

        add(recipes, "Potion Recipes", "Awkward Potion", "Water Bottle",
                "Nether Wart", "Awkward Potion", "Nether Wart");
        add(recipes, "Potion Recipes", "Swiftness", "Awkward Potion",
                "Sugar", "Potion of Swiftness", "Sugar");
        add(recipes, "Potion Recipes", "Leaping", "Awkward Potion",
                "Rabbit Foot", "Potion of Leaping", "Rabbit Foot");
        add(recipes, "Potion Recipes", "Healing", "Awkward Potion",
                "Glistering Melon Slice", "Potion of Healing",
                "Glistering Melon");
        add(recipes, "Potion Recipes", "Poison", "Awkward Potion",
                "Spider Eye", "Potion of Poison", "Spider Eye");
        add(recipes, "Potion Recipes", "Water Breathing", "Awkward Potion",
                "Pufferfish", "Potion of Water Breathing", "Pufferfish");
        add(recipes, "Potion Recipes", "Fire Resistance", "Awkward Potion",
                "Magma Cream", "Potion of Fire Resistance", "Magma Cream");
        add(recipes, "Potion Recipes", "Night Vision", "Awkward Potion",
                "Golden Carrot", "Potion of Night Vision", "Golden Carrot");
        add(recipes, "Potion Recipes", "Strength", "Awkward Potion",
                "Blaze Powder", "Potion of Strength", "Blaze Powder");
        add(recipes, "Potion Recipes", "Regeneration", "Awkward Potion",
                "Ghast Tear", "Potion of Regeneration", "Ghast Tear");
        add(recipes, "Potion Recipes", "Slow Falling", "Awkward Potion",
                "Phantom Membrane", "Potion of Slow Falling",
                "Phantom Membrane");
        add(recipes, "Potion Recipes", "Turtle Master", "Awkward Potion",
                "Turtle Helmet", "Potion of the Turtle Master",
                "Turtle Helmet");
        add(recipes, "Potion Recipes", "Weakness", "Water Bottle",
                "Fermented Spider Eye", "Potion of Weakness",
                "Fermented Spider Eye");

        add(recipes, "Fermented Spider Eye Conversions",
                "Night Vision -> Invisibility", "Night Vision",
                "Fermented Spider Eye", "Invisibility",
                "Fermented Spider Eye");
        add(recipes, "Fermented Spider Eye Conversions",
                "Healing -> Harming", "Healing",
                "Fermented Spider Eye", "Harming",
                "Fermented Spider Eye");
        add(recipes, "Fermented Spider Eye Conversions",
                "Poison -> Harming", "Poison",
                "Fermented Spider Eye", "Harming",
                "Fermented Spider Eye");
        add(recipes, "Fermented Spider Eye Conversions",
                "Swiftness -> Slowness", "Swiftness",
                "Fermented Spider Eye", "Slowness",
                "Fermented Spider Eye");
        add(recipes, "Fermented Spider Eye Conversions",
                "Leaping -> Slowness", "Leaping",
                "Fermented Spider Eye", "Slowness",
                "Fermented Spider Eye");

        add(recipes, "Potion Modifiers", "Long Duration", "Eligible Potion",
                "Redstone Dust", "Long Potion", "Redstone",
                "Works on most timed potions.");
        add(recipes, "Potion Modifiers", "Greater Potency",
                "Eligible Potion", "Glowstone Dust", "Stronger Potion",
                "Glowstone Dust", "Works on potions with stronger variants.");
        add(recipes, "Potion Modifiers", "Splash Potion", "Any Potion",
                "Gunpowder", "Splash Potion", "Gunpowder");
        add(recipes, "Potion Modifiers", "Lingering Potion", "Splash Potion",
                "Dragon's Breath", "Lingering Potion", "Dragon's Breath");

        add(recipes, "Splash Potions", "Splash Swiftness", "Swiftness",
                "Gunpowder", "Splash Potion of Swiftness", "Gunpowder");
        add(recipes, "Splash Potions", "Splash Strength", "Strength",
                "Gunpowder", "Splash Potion of Strength", "Gunpowder");
        add(recipes, "Splash Potions", "Splash Healing", "Healing",
                "Gunpowder", "Splash Potion of Healing", "Gunpowder");
        add(recipes, "Splash Potions", "Splash Regeneration",
                "Regeneration", "Gunpowder",
                "Splash Potion of Regeneration", "Gunpowder");
        add(recipes, "Splash Potions", "Splash Fire Resistance",
                "Fire Resistance", "Gunpowder",
                "Splash Potion of Fire Resistance", "Gunpowder");
        add(recipes, "Splash Potions", "Splash Night Vision",
                "Night Vision", "Gunpowder",
                "Splash Potion of Night Vision", "Gunpowder");
        add(recipes, "Splash Potions", "Splash Water Breathing",
                "Water Breathing", "Gunpowder",
                "Splash Potion of Water Breathing", "Gunpowder");
        add(recipes, "Splash Potions", "Splash Leaping", "Leaping",
                "Gunpowder", "Splash Potion of Leaping", "Gunpowder");
        add(recipes, "Splash Potions", "Splash Poison", "Poison",
                "Gunpowder", "Splash Potion of Poison", "Gunpowder");
        add(recipes, "Splash Potions", "Splash Slow Falling",
                "Slow Falling", "Gunpowder",
                "Splash Potion of Slow Falling", "Gunpowder");
        add(recipes, "Splash Potions", "Splash Turtle Master",
                "Turtle Master", "Gunpowder",
                "Splash Potion of the Turtle Master", "Gunpowder");
        add(recipes, "Splash Potions", "Splash Weakness", "Weakness",
                "Gunpowder", "Splash Potion of Weakness", "Gunpowder");
        add(recipes, "Splash Potions", "Splash Invisibility",
                "Invisibility", "Gunpowder",
                "Splash Potion of Invisibility", "Gunpowder");
        add(recipes, "Splash Potions", "Splash Harming", "Harming",
                "Gunpowder", "Splash Potion of Harming", "Gunpowder");
        add(recipes, "Splash Potions", "Splash Slowness", "Slowness",
                "Gunpowder", "Splash Potion of Slowness", "Gunpowder");

        add(recipes, "Lingering Potions", "Lingering Swiftness",
                "Splash Swiftness", "Dragon's Breath",
                "Lingering Potion of Swiftness", "Dragon's Breath");
        add(recipes, "Lingering Potions", "Lingering Strength",
                "Splash Strength", "Dragon's Breath",
                "Lingering Potion of Strength", "Dragon's Breath");
        add(recipes, "Lingering Potions", "Lingering Healing",
                "Splash Healing", "Dragon's Breath",
                "Lingering Potion of Healing", "Dragon's Breath");
        add(recipes, "Lingering Potions", "Lingering Regeneration",
                "Splash Regeneration", "Dragon's Breath",
                "Lingering Potion of Regeneration", "Dragon's Breath");
        add(recipes, "Lingering Potions", "Lingering Fire Resistance",
                "Splash Fire Resistance", "Dragon's Breath",
                "Lingering Potion of Fire Resistance", "Dragon's Breath");
        add(recipes, "Lingering Potions", "Lingering Night Vision",
                "Splash Night Vision", "Dragon's Breath",
                "Lingering Potion of Night Vision", "Dragon's Breath");
        add(recipes, "Lingering Potions", "Lingering Water Breathing",
                "Splash Water Breathing", "Dragon's Breath",
                "Lingering Potion of Water Breathing", "Dragon's Breath");
        add(recipes, "Lingering Potions", "Lingering Leaping",
                "Splash Leaping", "Dragon's Breath",
                "Lingering Potion of Leaping", "Dragon's Breath");
        add(recipes, "Lingering Potions", "Lingering Poison",
                "Splash Poison", "Dragon's Breath",
                "Lingering Potion of Poison", "Dragon's Breath");
        add(recipes, "Lingering Potions", "Lingering Slow Falling",
                "Splash Slow Falling", "Dragon's Breath",
                "Lingering Potion of Slow Falling", "Dragon's Breath");
        add(recipes, "Lingering Potions", "Lingering Turtle Master",
                "Splash Turtle Master", "Dragon's Breath",
                "Lingering Potion of the Turtle Master", "Dragon's Breath");
        add(recipes, "Lingering Potions", "Lingering Weakness",
                "Splash Weakness", "Dragon's Breath",
                "Lingering Potion of Weakness", "Dragon's Breath");
        add(recipes, "Lingering Potions", "Lingering Invisibility",
                "Splash Invisibility", "Dragon's Breath",
                "Lingering Potion of Invisibility", "Dragon's Breath");
        add(recipes, "Lingering Potions", "Lingering Harming",
                "Splash Harming", "Dragon's Breath",
                "Lingering Potion of Harming", "Dragon's Breath");
        add(recipes, "Lingering Potions", "Lingering Slowness",
                "Splash Slowness", "Dragon's Breath",
                "Lingering Potion of Slowness", "Dragon's Breath");

        add(recipes, "XP Elixirs", "Honeyed Base", "Awkward Potion",
                "Honeycomb", "Honeyed Base", "Honeycomb");
        add(recipes, "XP Elixirs", "Experience Elixir", "Honeyed Base",
                "Bone Meal", "Experience Elixir", "Bone Meal");
        add(recipes, "XP Elixirs", "Greater Experience Elixir",
                "Experience Elixir", "Ender Pearl",
                "Greater Experience Elixir", "Ender Pearl");
        add(recipes, "XP Elixirs", "Honey Duration Extension",
                "Experience Elixir or Greater Experience Elixir",
                "Honey Bottle", "Longer Elixir", "Honey Bottle",
                "Adds +1 minute up to your unlocked cap.");

        add(recipes, "Oil Recipes", "Oil Base", "Water Bottle",
                "Camellia Flower", "Oil Base",
                key(ModBlocks.CAMELLIA_FLOWER.asItem()));
        add(recipes, "Oil Recipes", "Camellia Oil", "Oil Base",
                "Camellia Flower", "Camellia Oil",
                key(ModBlocks.CAMELLIA_FLOWER.asItem()), "camellia_press",
                "");
        add(recipes, "Oil Recipes", "Fire Oil", "Oil Base",
                "Blaze Powder", "Fire Oil", "Blaze Powder", "oilers_touch",
                "");
        add(recipes, "Oil Recipes", "Frost Oil", "Oil Base",
                "Snowball", "Frost Oil", "Snowball", "polished_edge", "");
        add(recipes, "Oil Recipes", "Miner's Oil", "Oil Base",
                "Redstone Dust", "Miner's Oil", "Redstone",
                "everlasting_sheen", "");
        add(recipes, "Oil Recipes", "Lumber Oil", "Oil Base",
                "Oak Sapling", "Lumber Oil", "Oak Sapling",
                "everlasting_sheen", "");

        recipes.add(new RecipeEntry(
                id("Ancient Recipes", "Philosopher's Stone"),
                "Ancient Recipes",
                "Philosopher's Stone",
                "Ancient components",
                "Crafting Grid",
                "Philosopher's Stone",
                "Transmutation unlock required.",
                List.of(),
                "transmutation",
                null,
                true
        ));

        for (ExperimentalAlchemyRegistry.Entry recipe
                : ExperimentalAlchemyRegistry.recipes()) {
            recipes.add(new RecipeEntry(
                    id("Experimental Brewing", recipe.displayName()),
                    "Experimental Brewing",
                    recipe.displayName(),
                    recipe.displayName().split(" \\+ ")[0],
                    recipe.displayName().split(" \\+ ")[1],
                    "Experimental Brew: " + recipe.displayName(),
                    "Requires Alchemy Prestige II.",
                    List.of(),
                    null,
                    recipe.discoveryKey(),
                    true
            ));
        }

        return List.copyOf(recipes);
    }

    public static List<RecipeEntry> recipes() {
        return RECIPES;
    }

    public static RecipeEntry get(String id) {
        for (RecipeEntry recipe : RECIPES) {
            if (recipe.id().equals(id)) {
                return recipe;
            }
        }
        return null;
    }

    private static void add(
            List<RecipeEntry> recipes,
            String category,
            String name,
            String base,
            String ingredient,
            String result,
            String discoveryIngredient
    ) {
        add(recipes, category, name, base, ingredient, result,
                discoveryIngredient, null, "");
    }

    private static void add(
            List<RecipeEntry> recipes,
            String category,
            String name,
            String base,
            String ingredient,
            String result,
            String discoveryIngredient,
            String notes
    ) {
        add(recipes, category, name, base, ingredient, result,
                discoveryIngredient, null, notes);
    }

    private static void add(
            List<RecipeEntry> recipes,
            String category,
            String name,
            String base,
            String ingredient,
            String result,
            String discoveryIngredient,
            String requiredPerkId,
            String notes
    ) {
        recipes.add(new RecipeEntry(
                id(category, name),
                category,
                name,
                base,
                ingredient,
                result,
                notes,
                requiredKeys(base, discoveryIngredient),
                requiredPerkId,
                recipeDiscoveryKey(category, name),
                false
        ));
    }

    private static List<String> requiredKeys(
            String base,
            String discoveryIngredient
    ) {
        Set<String> keys = new LinkedHashSet<>();
        keys.addAll(baseKeys(base));
        String ingredientKey = key(discoveryIngredient);
        if (!ingredientKey.isBlank()) {
            keys.add(ingredientKey);
        }
        return List.copyOf(keys);
    }

    private static List<String> baseKeys(String base) {
        return switch (base) {
            case "Awkward Potion" -> List.of(key(Items.NETHER_WART));
            case "Honeyed Base" -> List.of(
                    key(Items.NETHER_WART),
                    key(Items.HONEYCOMB)
            );
            case "Experience Elixir",
                 "Experience Elixir or Greater Experience Elixir" -> List.of(
                    key(Items.HONEYCOMB),
                    key(Items.BONE_MEAL)
            );
            case "Oil Base" -> List.of(key(ModBlocks.CAMELLIA_FLOWER.asItem()));
            case "Swiftness", "Splash Swiftness" ->
                    List.of(key(Items.SUGAR));
            case "Strength", "Splash Strength" ->
                    List.of(key(Items.BLAZE_POWDER));
            case "Healing", "Splash Healing" ->
                    List.of(key(Items.GLISTERING_MELON_SLICE));
            case "Regeneration", "Splash Regeneration" ->
                    List.of(key(Items.GHAST_TEAR));
            case "Fire Resistance", "Splash Fire Resistance" ->
                    List.of(key(Items.MAGMA_CREAM));
            case "Night Vision", "Splash Night Vision" ->
                    List.of(key(Items.GOLDEN_CARROT));
            case "Water Breathing", "Splash Water Breathing" ->
                    List.of(key(Items.PUFFERFISH));
            case "Leaping", "Splash Leaping" ->
                    List.of(key(Items.RABBIT_FOOT));
            case "Poison", "Splash Poison" ->
                    List.of(key(Items.SPIDER_EYE));
            case "Slow Falling", "Splash Slow Falling" ->
                    List.of(key(Items.PHANTOM_MEMBRANE));
            case "Turtle Master", "Splash Turtle Master" ->
                    List.of(key(Items.TURTLE_HELMET));
            case "Weakness", "Splash Weakness" ->
                    List.of(key(Items.FERMENTED_SPIDER_EYE));
            case "Invisibility", "Splash Invisibility" -> List.of(
                    key(Items.GOLDEN_CARROT),
                    key(Items.FERMENTED_SPIDER_EYE)
            );
            case "Harming", "Splash Harming" -> List.of(
                    key(Items.GLISTERING_MELON_SLICE),
                    key(Items.SPIDER_EYE),
                    key(Items.FERMENTED_SPIDER_EYE)
            );
            case "Slowness", "Splash Slowness" -> List.of(
                    key(Items.SUGAR),
                    key(Items.RABBIT_FOOT),
                    key(Items.FERMENTED_SPIDER_EYE)
            );
            default -> List.of();
        };
    }

    private static String key(String itemName) {
        return switch (itemName) {
            case "Nether Wart" -> key(Items.NETHER_WART);
            case "Sugar" -> key(Items.SUGAR);
            case "Rabbit Foot" -> key(Items.RABBIT_FOOT);
            case "Glistering Melon", "Glistering Melon Slice" ->
                    key(Items.GLISTERING_MELON_SLICE);
            case "Spider Eye" -> key(Items.SPIDER_EYE);
            case "Pufferfish" -> key(Items.PUFFERFISH);
            case "Magma Cream" -> key(Items.MAGMA_CREAM);
            case "Golden Carrot" -> key(Items.GOLDEN_CARROT);
            case "Blaze Powder" -> key(Items.BLAZE_POWDER);
            case "Ghast Tear" -> key(Items.GHAST_TEAR);
            case "Phantom Membrane" -> key(Items.PHANTOM_MEMBRANE);
            case "Turtle Helmet" -> key(Items.TURTLE_HELMET);
            case "Fermented Spider Eye" -> key(Items.FERMENTED_SPIDER_EYE);
            case "Redstone", "Redstone Dust" -> key(Items.REDSTONE);
            case "Glowstone Dust" -> key(Items.GLOWSTONE_DUST);
            case "Gunpowder" -> key(Items.GUNPOWDER);
            case "Dragon's Breath" -> key(Items.DRAGON_BREATH);
            case "Honeycomb" -> key(Items.HONEYCOMB);
            case "Bone Meal" -> key(Items.BONE_MEAL);
            case "Ender Pearl" -> key(Items.ENDER_PEARL);
            case "Honey Bottle" -> key(Items.HONEY_BOTTLE);
            case "Snowball" -> key(Items.SNOWBALL);
            case "Oak Sapling" -> key(Items.OAK_SAPLING);
            default -> itemName;
        };
    }

    private static String key(net.minecraft.world.item.Item item) {
        return AlchemyIngredientRegistry.key(item);
    }

    private static String id(String category, String name) {
        return (category + ":" + name)
                .toLowerCase(java.util.Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_+|_+$", "");
    }

    public static String recipeDiscoveryKey(String category, String name) {
        return "recipe:" + id(category, name);
    }

    public record RecipeEntry(
            String id,
            String category,
            String name,
            String base,
            String ingredient,
            String result,
            String notes,
            List<String> ingredientKeys,
            String requiredPerkId,
            String discoveryKey,
            boolean hiddenUntilUnlocked
    ) {
    }
}
