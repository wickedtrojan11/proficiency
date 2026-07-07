package com.trojan.proficiency.event;

import com.trojan.proficiency.block.ModBlocks;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public final class CamelliaBonemealEvents {

    private static final Map<ResourceKey<Biome>, Integer> BIOME_CHANCES =
            Map.of(
                    Biomes.FLOWER_FOREST, 10,
                    Biomes.FOREST, 18,
                    Biomes.BIRCH_FOREST, 18,
                    Biomes.OLD_GROWTH_BIRCH_FOREST, 16,
                    Biomes.MEADOW, 14,
                    Biomes.PLAINS, 32
            );

    private CamelliaBonemealEvents() {
    }

    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!(world instanceof ServerLevel serverLevel)
                    || !player.getItemInHand(hand).is(Items.BONE_MEAL)
                    || !serverLevel.getBlockState(hitResult.getBlockPos())
                    .is(Blocks.GRASS_BLOCK)) {
                return InteractionResult.PASS;
            }

            ResourceKey<Biome> biomeKey = serverLevel
                    .registryAccess()
                    .registryOrThrow(Registries.BIOME)
                    .getResourceKey(serverLevel.getBiome(hitResult.getBlockPos()).value())
                    .orElse(null);
            int chance = BIOME_CHANCES.getOrDefault(biomeKey, 0);
            if (chance <= 0 || serverLevel.random.nextInt(chance) != 0) {
                return InteractionResult.PASS;
            }

            for (int attempt = 0; attempt < 6; attempt++) {
                BlockPos placePos = hitResult.getBlockPos().above().offset(
                        serverLevel.random.nextInt(7) - 3,
                        0,
                        serverLevel.random.nextInt(7) - 3
                );
                BlockState flower = ModBlocks.CAMELLIA_FLOWER.defaultBlockState();
                if (serverLevel.isEmptyBlock(placePos)
                        && flower.canSurvive(serverLevel, placePos)) {
                    serverLevel.setBlock(placePos, flower, 3);
                    break;
                }
            }

            return InteractionResult.PASS;
        });
    }
}
