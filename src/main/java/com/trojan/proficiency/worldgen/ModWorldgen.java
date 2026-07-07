package com.trojan.proficiency.worldgen;

import com.trojan.proficiency.ProficiencyMod;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public final class ModWorldgen {

    private static final ResourceKey<PlacedFeature> CAMELLIA_PATCH =
            ResourceKey.create(
                    Registries.PLACED_FEATURE,
                    ResourceLocation.fromNamespaceAndPath(
                            ProficiencyMod.MOD_ID,
                            "camellia_flower_patch"
                    )
            );

    private ModWorldgen() {
    }

    public static void register() {
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(
                        Biomes.FLOWER_FOREST,
                        Biomes.FOREST,
                        Biomes.BIRCH_FOREST,
                        Biomes.OLD_GROWTH_BIRCH_FOREST,
                        Biomes.MEADOW,
                        Biomes.PLAINS
                ),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                CAMELLIA_PATCH
        );
    }
}
