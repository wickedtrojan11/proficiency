package com.trojan.proficiency.item;

import com.trojan.proficiency.block.ModBlocks;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public final class AlchemyJournalRegistry {

    private static final List<RecipeEntry> RECIPES = createRecipes();

    private static List<RecipeEntry> createRecipes() {
        List<RecipeEntry> recipes = new ArrayList<>(List.of(
                new RecipeEntry(
                    "Potion Recipes",
                    "Honeyed Base",
                    List.of(AlchemyIngredientRegistry.key(Items.HONEYCOMB)),
                    null,
                    null,
                    false
            ),
                new RecipeEntry(
                    "XP Elixirs",
                    "Experience Elixir",
                    List.of(
                            AlchemyIngredientRegistry.key(Items.HONEYCOMB),
                            AlchemyIngredientRegistry.key(Items.BONE_MEAL)
                    ),
                    null,
                    null,
                    false
            ),
                new RecipeEntry(
                    "XP Elixirs",
                    "Greater Experience Elixir",
                    List.of(AlchemyIngredientRegistry.key(Items.ENDER_PEARL)),
                    null,
                    null,
                    false
            ),
                new RecipeEntry(
                    "Potion Recipes",
                    "Honey Duration Extension",
                    List.of(AlchemyIngredientRegistry.key(Items.HONEY_BOTTLE)),
                    null,
                    null,
                    false
            ),
                new RecipeEntry(
                    "Oil Recipes",
                    "Oil Base",
                    List.of(AlchemyIngredientRegistry.key(
                            ModBlocks.CAMELLIA_FLOWER.asItem()
                    )),
                    null,
                    null,
                    false
            ),
                new RecipeEntry(
                    "Oil Recipes",
                    "Camellia Oil",
                    List.of(AlchemyIngredientRegistry.key(
                            ModBlocks.CAMELLIA_FLOWER.asItem()
                    )),
                    "camellia_press",
                    null,
                    false
            ),
                new RecipeEntry(
                    "Oil Recipes",
                    "Fire Oil",
                    List.of(AlchemyIngredientRegistry.key(Items.BLAZE_POWDER)),
                    "oilers_touch",
                    null,
                    false
            ),
                new RecipeEntry(
                    "Oil Recipes",
                    "Frost Oil",
                    List.of(AlchemyIngredientRegistry.key(Items.SNOWBALL)),
                    "polished_edge",
                    null,
                    false
            ),
                new RecipeEntry(
                    "Oil Recipes",
                    "Miner's Oil",
                    List.of(AlchemyIngredientRegistry.key(Items.REDSTONE)),
                    "everlasting_sheen",
                    null,
                    false
            ),
                new RecipeEntry(
                    "Oil Recipes",
                    "Lumber Oil",
                    List.of(AlchemyIngredientRegistry.key(Items.OAK_SAPLING)),
                    "everlasting_sheen",
                    null,
                    false
            ),
                new RecipeEntry(
                    "Ancient Recipes",
                    "Philosopher's Stone",
                    List.of(),
                    "transmutation",
                    null,
                    true
            )
        ));

        for (ExperimentalAlchemyRegistry.Entry recipe
                : ExperimentalAlchemyRegistry.recipes()) {
            recipes.add(new RecipeEntry(
                    "Experimental Brewing",
                    recipe.displayName(),
                    List.of(),
                    null,
                    recipe.discoveryKey(),
                    true
            ));
        }
        return List.copyOf(recipes);
    }

    private AlchemyJournalRegistry() {
    }

    public static List<RecipeEntry> recipes() {
        return RECIPES;
    }

    public record RecipeEntry(
            String category,
            String name,
            List<String> ingredientKeys,
            String requiredPerkId,
            String discoveryKey,
            boolean hiddenUntilUnlocked
    ) {
    }
}
