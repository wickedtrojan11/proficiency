package com.trojan.proficiency.event;

import com.trojan.proficiency.item.ModItems;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Set;

public final class SkillBookEvents {

    private static final float HOSTILE_DROP_CHANCE = 0.001f;
    private static final float CHEST_BOOK_CHANCE = 0.04f;
    private static final Set<ResourceKey<LootTable>> SKILL_BOOK_CHESTS = Set.of(
            BuiltInLootTables.SIMPLE_DUNGEON,
            BuiltInLootTables.ABANDONED_MINESHAFT,
            BuiltInLootTables.STRONGHOLD_CORRIDOR,
            BuiltInLootTables.NETHER_BRIDGE,
            BuiltInLootTables.BASTION_TREASURE,
            BuiltInLootTables.ANCIENT_CITY,
            BuiltInLootTables.VILLAGE_PLAINS_HOUSE,
            BuiltInLootTables.VILLAGE_TAIGA_HOUSE,
            BuiltInLootTables.VILLAGE_SNOWY_HOUSE,
            BuiltInLootTables.VILLAGE_SAVANNA_HOUSE,
            BuiltInLootTables.VILLAGE_DESERT_HOUSE
    );

    private SkillBookEvents() {
    }

    public static void register() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (!source.isBuiltin() || !SKILL_BOOK_CHESTS.contains(key)) {
                return;
            }

            LootPool.Builder pool = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0f))
                    .when(LootItemRandomChanceCondition.randomChance(
                            CHEST_BOOK_CHANCE
                    ))
                    .add(LootItem.lootTableItem(ModItems.MINING_SKILL_BOOK).setWeight(1))
                    .add(LootItem.lootTableItem(ModItems.WOODCUTTING_SKILL_BOOK).setWeight(1))
                    .add(LootItem.lootTableItem(ModItems.FARMING_SKILL_BOOK).setWeight(1));
            tableBuilder.withPool(pool);
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (
                    !(entity instanceof Enemy)
                            || !(source.getEntity() instanceof ServerPlayer player)
                            || player.getRandom().nextFloat() >= HOSTILE_DROP_CHANCE
            ) {
                return;
            }

            Item book = switch (player.getRandom().nextInt(3)) {
                case 0 -> ModItems.MINING_SKILL_BOOK;
                case 1 -> ModItems.WOODCUTTING_SKILL_BOOK;
                default -> ModItems.FARMING_SKILL_BOOK;
            };
            entity.spawnAtLocation(new ItemStack(book));
        });
    }
}
