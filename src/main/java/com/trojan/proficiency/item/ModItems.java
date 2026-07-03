package com.trojan.proficiency.item;

import com.trojan.proficiency.ProficiencyMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public final class ModItems {

    public static final Item MINING_SKILL_BOOK = new SkillBookItem(
            new Item.Properties().stacksTo(16)
    );
    public static final Item WOODCUTTING_SKILL_BOOK = new SkillBookItem(
            new Item.Properties().stacksTo(16)
    );
    public static final Item FARMING_SKILL_BOOK = new SkillBookItem(
            new Item.Properties().stacksTo(16)
    );
    public static final Item ONE_HANDED_SKILL_BOOK = new SkillBookItem(
            new Item.Properties().stacksTo(16)
    );

    private ModItems() {
    }

    public static void register() {
        register("mining_skill_book", MINING_SKILL_BOOK);
        register("woodcutting_skill_book", WOODCUTTING_SKILL_BOOK);
        register("farming_skill_book", FARMING_SKILL_BOOK);
        register("one_handed_skill_book", ONE_HANDED_SKILL_BOOK);

        SkillBookRegistry.register(
                "mining",
                MINING_SKILL_BOOK,
                "Mining Skill Book",
                100
        );
        SkillBookRegistry.register(
                "woodcutting",
                WOODCUTTING_SKILL_BOOK,
                "Woodcutting Skill Book",
                100
        );
        SkillBookRegistry.register(
                "farming",
                FARMING_SKILL_BOOK,
                "Farming Skill Book",
                100
        );
        SkillBookRegistry.register(
                "one_handed",
                ONE_HANDED_SKILL_BOOK,
                "One-Handed Skill Book",
                100
        );
        SkillBookRegistry.validate();

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS)
                .register(entries -> {
                    entries.accept(MINING_SKILL_BOOK);
                    entries.accept(WOODCUTTING_SKILL_BOOK);
                    entries.accept(FARMING_SKILL_BOOK);
                    entries.accept(ONE_HANDED_SKILL_BOOK);
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
