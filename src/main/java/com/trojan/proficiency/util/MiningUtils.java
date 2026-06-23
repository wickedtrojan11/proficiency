package com.trojan.proficiency.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class MiningUtils {

    public static boolean isStoneType(
            BlockState state
    ) {

        return
                state.is(Blocks.STONE)
                        || state.is(Blocks.DEEPSLATE)
                        || state.is(Blocks.COBBLED_DEEPSLATE)
                        || state.is(Blocks.ANDESITE)
                        || state.is(Blocks.GRANITE)
                        || state.is(Blocks.DIORITE);
    }

    public static boolean isPickaxe(
            ItemStack stack
    ) {

        return stack.getItem()
                instanceof PickaxeItem;
    }

    public static boolean isOre(
            BlockState state
    ) {

        return
                state.is(Blocks.COAL_ORE)
                        || state.is(Blocks.DEEPSLATE_COAL_ORE)
                        || state.is(Blocks.COPPER_ORE)
                        || state.is(Blocks.DEEPSLATE_COPPER_ORE)
                        || state.is(Blocks.IRON_ORE)
                        || state.is(Blocks.DEEPSLATE_IRON_ORE)
                        || state.is(Blocks.GOLD_ORE)
                        || state.is(Blocks.DEEPSLATE_GOLD_ORE)
                        || state.is(Blocks.REDSTONE_ORE)
                        || state.is(Blocks.DEEPSLATE_REDSTONE_ORE)
                        || state.is(Blocks.EMERALD_ORE)
                        || state.is(Blocks.DEEPSLATE_EMERALD_ORE)
                        || state.is(Blocks.LAPIS_ORE)
                        || state.is(Blocks.DEEPSLATE_LAPIS_ORE)
                        || state.is(Blocks.DIAMOND_ORE)
                        || state.is(Blocks.DEEPSLATE_DIAMOND_ORE)
                        || state.is(Blocks.NETHER_GOLD_ORE)
                        || state.is(Blocks.NETHER_QUARTZ_ORE)
                        || state.is(Blocks.ANCIENT_DEBRIS);
    }
}
