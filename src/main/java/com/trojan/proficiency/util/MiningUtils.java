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
}