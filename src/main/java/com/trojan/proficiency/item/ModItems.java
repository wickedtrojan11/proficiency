package com.trojan.proficiency.item;

import com.trojan.proficiency.ProficiencyMod;
import com.trojan.proficiency.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
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
    public static final Item ALCHEMY_SKILL_BOOK = new SkillBookItem(
            new Item.Properties().stacksTo(16)
    );
    public static final Item RARE_ALCHEMY_SKILL_BOOK = new SkillBookItem(
            new Item.Properties().stacksTo(16)
    );
    public static final Item ANCIENT_ALCHEMY_SKILL_TOME = new SkillBookItem(
            new Item.Properties().stacksTo(16)
    );
    public static final Item DOUBLE_XP_POTION = new AlchemyXpPotionItem(
            new Item.Properties().stacksTo(1),
            2,
            5 * 60 * 20
    );
    public static final Item TRIPLE_XP_POTION = new AlchemyXpPotionItem(
            new Item.Properties().stacksTo(1),
            3,
            5 * 60 * 20
    );
    public static final Item OIL_BASE = new Item(
            new Item.Properties().stacksTo(16)
    );
    public static final Item CAMELLIA_OIL = oil("camellia");
    public static final Item FIRE_OIL = oil("fire");
    public static final Item FROST_OIL = oil("frost");
    public static final Item MINERS_OIL = oil("miners");
    public static final Item LUMBER_OIL = oil("lumber");
    public static final Item PHILOSOPHERS_STONE =
            new PhilosophersStoneItem(new Item.Properties().stacksTo(1));
    public static final Item ANCIENT_ALCHEMY_NOTES =
            new AncientAlchemyNotesItem(new Item.Properties().stacksTo(16));

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
        register("alchemy_skill_book", ALCHEMY_SKILL_BOOK);
        register("rare_alchemy_skill_book", RARE_ALCHEMY_SKILL_BOOK);
        register("ancient_alchemy_skill_tome", ANCIENT_ALCHEMY_SKILL_TOME);
        register("double_xp_potion", DOUBLE_XP_POTION);
        register("triple_xp_potion", TRIPLE_XP_POTION);
        register("oil_base", OIL_BASE);
        register("camellia_oil", CAMELLIA_OIL);
        register("fire_oil", FIRE_OIL);
        register("frost_oil", FROST_OIL);
        register("miners_oil", MINERS_OIL);
        register("lumber_oil", LUMBER_OIL);
        register("philosophers_stone", PHILOSOPHERS_STONE);
        register("ancient_alchemy_notes", ANCIENT_ALCHEMY_NOTES);

        OilRegistry.register(
                "camellia",
                CAMELLIA_OIL,
                "Camellia Oil",
                OilRegistry.Target.DAMAGEABLE,
                "camellia_press",
                java.util.List.of(
                        Component.literal(
                                "Unbreaking-style durability preservation."
                        )
                )
        );
        OilRegistry.register(
                "fire",
                FIRE_OIL,
                "Fire Oil",
                OilRegistry.Target.WEAPON,
                "oilers_touch",
                java.util.List.of(
                        Component.literal("Weapons briefly burn enemies.")
                )
        );
        OilRegistry.register(
                "frost",
                FROST_OIL,
                "Frost Oil",
                OilRegistry.Target.WEAPON,
                "polished_edge",
                java.util.List.of(
                        Component.literal("Weapons briefly slow enemies.")
                )
        );
        OilRegistry.register(
                "miners",
                MINERS_OIL,
                "Miner's Oil",
                OilRegistry.Target.PICKAXE,
                "everlasting_sheen",
                java.util.List.of(
                        Component.literal("Pickaxes mine slightly faster."),
                        Component.literal("Small durability preservation.")
                )
        );
        OilRegistry.register(
                "lumber",
                LUMBER_OIL,
                "Lumber Oil",
                OilRegistry.Target.AXE,
                "everlasting_sheen",
                java.util.List.of(
                        Component.literal("Axes chop slightly faster."),
                        Component.literal("Small durability preservation.")
                )
        );
        OilRegistry.validate();

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
        SkillBookRegistry.registerSkillBooks(
                "alchemy",
                ALCHEMY_SKILL_BOOK,
                RARE_ALCHEMY_SKILL_BOOK,
                ANCIENT_ALCHEMY_SKILL_TOME,
                "Alchemy"
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
                    entries.accept(ALCHEMY_SKILL_BOOK);
                    entries.accept(RARE_ALCHEMY_SKILL_BOOK);
                    entries.accept(ANCIENT_ALCHEMY_SKILL_TOME);
                    entries.accept(DOUBLE_XP_POTION);
                    entries.accept(TRIPLE_XP_POTION);
                    entries.accept(OIL_BASE);
                    entries.accept(ModBlocks.CAMELLIA_FLOWER.asItem());
                    entries.accept(CAMELLIA_OIL);
                    entries.accept(FIRE_OIL);
                    entries.accept(FROST_OIL);
                    entries.accept(MINERS_OIL);
                    entries.accept(LUMBER_OIL);
                    entries.accept(PHILOSOPHERS_STONE);
                    entries.accept(ANCIENT_ALCHEMY_NOTES);
                    entries.accept(ModBlocks.PROFICIENT_BREW_STAND.asItem());
                });
    }

    private static Item oil(String oilId) {
        return new AlchemyOilItem(new Item.Properties().stacksTo(16), oilId);
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
