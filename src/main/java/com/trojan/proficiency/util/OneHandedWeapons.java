package com.trojan.proficiency.util;

import com.trojan.proficiency.ProficiencyMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;

public final class OneHandedWeapons {

    public static final TagKey<Item> ONE_HANDED_WEAPONS = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(
                    ProficiencyMod.MOD_ID,
                    "one_handed_weapons"
            )
    );

    private OneHandedWeapons() {
    }

    public static boolean isSupported(ItemStack stack) {
        return stack.is(ONE_HANDED_WEAPONS)
                || stack.getItem() instanceof SwordItem
                || stack.getItem() instanceof AxeItem;
    }
}
