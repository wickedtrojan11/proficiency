package com.trojan.proficiency.item;

import com.trojan.proficiency.ProficiencyMod;
import com.trojan.proficiency.skill.SkillType;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public final class ModItems {

    public static final Item MINING_SKILL_BOOK = new SkillBookItem(
            SkillType.MINING,
            new Item.Properties().stacksTo(16)
    );
    public static final Item WOODCUTTING_SKILL_BOOK = new SkillBookItem(
            SkillType.WOODCUTTING,
            new Item.Properties().stacksTo(16)
    );
    public static final Item FARMING_SKILL_BOOK = new SkillBookItem(
            SkillType.FARMING,
            new Item.Properties().stacksTo(16)
    );

    private ModItems() {
    }

    public static void register() {
        register("mining_skill_book", MINING_SKILL_BOOK);
        register("woodcutting_skill_book", WOODCUTTING_SKILL_BOOK);
        register("farming_skill_book", FARMING_SKILL_BOOK);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS)
                .register(entries -> {
                    entries.accept(MINING_SKILL_BOOK);
                    entries.accept(WOODCUTTING_SKILL_BOOK);
                    entries.accept(FARMING_SKILL_BOOK);
                });
    }

    private static void register(String name, Item item) {
        Registry.register(
                BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(
                        ProficiencyMod.MOD_ID,
                        name
                ),
                item
        );
    }
}
