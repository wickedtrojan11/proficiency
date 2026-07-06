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
    public static final Item RARE_MINING_SKILL_BOOK = new SkillBookItem(
            new Item.Properties().stacksTo(16)
    );
    public static final Item ANCIENT_MINING_SKILL_TOME = new SkillBookItem(
            new Item.Properties().stacksTo(16)
    );
    public static final Item WOODCUTTING_SKILL_BOOK = new SkillBookItem(
            new Item.Properties().stacksTo(16)
    );
    public static final Item RARE_WOODCUTTING_SKILL_BOOK = new SkillBookItem(
            new Item.Properties().stacksTo(16)
    );
    public static final Item ANCIENT_WOODCUTTING_SKILL_TOME = new SkillBookItem(
            new Item.Properties().stacksTo(16)
    );
    public static final Item FARMING_SKILL_BOOK = new SkillBookItem(
            new Item.Properties().stacksTo(16)
    );
    public static final Item RARE_FARMING_SKILL_BOOK = new SkillBookItem(
            new Item.Properties().stacksTo(16)
    );
    public static final Item ANCIENT_FARMING_SKILL_TOME = new SkillBookItem(
            new Item.Properties().stacksTo(16)
    );
    public static final Item ONE_HANDED_SKILL_BOOK = new SkillBookItem(
            new Item.Properties().stacksTo(16)
    );
    public static final Item RARE_ONE_HANDED_SKILL_BOOK = new SkillBookItem(
            new Item.Properties().stacksTo(16)
    );
    public static final Item ANCIENT_ONE_HANDED_SKILL_TOME = new SkillBookItem(
            new Item.Properties().stacksTo(16)
    );

    private ModItems() {
    }

    public static void register() {
        register("mining_skill_book", MINING_SKILL_BOOK);
        register("rare_mining_skill_book", RARE_MINING_SKILL_BOOK);
        register("ancient_mining_skill_tome", ANCIENT_MINING_SKILL_TOME);
        register("woodcutting_skill_book", WOODCUTTING_SKILL_BOOK);
        register("rare_woodcutting_skill_book", RARE_WOODCUTTING_SKILL_BOOK);
        register(
                "ancient_woodcutting_skill_tome",
                ANCIENT_WOODCUTTING_SKILL_TOME
        );
        register("farming_skill_book", FARMING_SKILL_BOOK);
        register("rare_farming_skill_book", RARE_FARMING_SKILL_BOOK);
        register("ancient_farming_skill_tome", ANCIENT_FARMING_SKILL_TOME);
        register("one_handed_skill_book", ONE_HANDED_SKILL_BOOK);
        register("rare_one_handed_skill_book", RARE_ONE_HANDED_SKILL_BOOK);
        register(
                "ancient_one_handed_skill_tome",
                ANCIENT_ONE_HANDED_SKILL_TOME
        );

        SkillBookRegistry.registerSkillBooks(
                "mining",
                MINING_SKILL_BOOK,
                RARE_MINING_SKILL_BOOK,
                ANCIENT_MINING_SKILL_TOME,
                "Mining"
        );
        SkillBookRegistry.registerSkillBooks(
                "woodcutting",
                WOODCUTTING_SKILL_BOOK,
                RARE_WOODCUTTING_SKILL_BOOK,
                ANCIENT_WOODCUTTING_SKILL_TOME,
                "Woodcutting"
        );
        SkillBookRegistry.registerSkillBooks(
                "farming",
                FARMING_SKILL_BOOK,
                RARE_FARMING_SKILL_BOOK,
                ANCIENT_FARMING_SKILL_TOME,
                "Farming"
        );
        SkillBookRegistry.registerSkillBooks(
                "one_handed",
                ONE_HANDED_SKILL_BOOK,
                RARE_ONE_HANDED_SKILL_BOOK,
                ANCIENT_ONE_HANDED_SKILL_TOME,
                "One-Handed"
        );
        SkillBookRegistry.validate();

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS)
                .register(entries -> {
                    entries.accept(MINING_SKILL_BOOK);
                    entries.accept(RARE_MINING_SKILL_BOOK);
                    entries.accept(ANCIENT_MINING_SKILL_TOME);
                    entries.accept(WOODCUTTING_SKILL_BOOK);
                    entries.accept(RARE_WOODCUTTING_SKILL_BOOK);
                    entries.accept(ANCIENT_WOODCUTTING_SKILL_TOME);
                    entries.accept(FARMING_SKILL_BOOK);
                    entries.accept(RARE_FARMING_SKILL_BOOK);
                    entries.accept(ANCIENT_FARMING_SKILL_TOME);
                    entries.accept(ONE_HANDED_SKILL_BOOK);
                    entries.accept(RARE_ONE_HANDED_SKILL_BOOK);
                    entries.accept(ANCIENT_ONE_HANDED_SKILL_TOME);
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
