package com.trojan.proficiency.event;

import com.trojan.proficiency.item.SkillBookRegistry;
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

    private static final float HOSTILE_DROP_CHANCE = 0.03f;
    private static final float VILLAGE_CHEST_CHANCE = 0.07f;
    private static final float MINESHAFT_CHEST_CHANCE = 0.09f;
    private static final float STRONGHOLD_CHEST_CHANCE = 0.10f;
    private static final float ANCIENT_CITY_CHEST_CHANCE = 0.125f;
    private static final float NETHER_CHEST_CHANCE = 0.075f;
    private static final float DUNGEON_CHEST_CHANCE = 0.08f;
    private static final Set<ResourceKey<LootTable>> SKILL_BOOK_CHESTS = Set.of(
            BuiltInLootTables.SIMPLE_DUNGEON,
            BuiltInLootTables.ABANDONED_MINESHAFT,
            BuiltInLootTables.STRONGHOLD_CORRIDOR,
            BuiltInLootTables.STRONGHOLD_CROSSING,
            BuiltInLootTables.STRONGHOLD_LIBRARY,
            BuiltInLootTables.NETHER_BRIDGE,
            BuiltInLootTables.BASTION_TREASURE,
            BuiltInLootTables.BASTION_BRIDGE,
            BuiltInLootTables.BASTION_HOGLIN_STABLE,
            BuiltInLootTables.BASTION_OTHER,
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
        SkillBookRegistry.validate();

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (!source.isBuiltin() || !SKILL_BOOK_CHESTS.contains(key)) {
                return;
            }

            LootPool.Builder pool = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0f))
                    .when(LootItemRandomChanceCondition.randomChance(
                            getChestBookChance(key)
                    ));

            for (SkillBookRegistry.Entry skillBook
                    : SkillBookRegistry.entries()) {
                pool.add(
                        LootItem.lootTableItem(skillBook.item())
                                .setWeight(1)
                );
            }
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

            Item book = SkillBookRegistry.getRandomBook(
                    player.getRandom()
            );
            entity.spawnAtLocation(new ItemStack(book));
        });
    }

    private static float getChestBookChance(
            ResourceKey<LootTable> lootTable
    ) {
        if (lootTable.equals(BuiltInLootTables.ABANDONED_MINESHAFT)) {
            return MINESHAFT_CHEST_CHANCE;
        }
        if (
                lootTable.equals(BuiltInLootTables.STRONGHOLD_CORRIDOR)
                        || lootTable.equals(BuiltInLootTables.STRONGHOLD_CROSSING)
                        || lootTable.equals(BuiltInLootTables.STRONGHOLD_LIBRARY)
        ) {
            return STRONGHOLD_CHEST_CHANCE;
        }
        if (lootTable.equals(BuiltInLootTables.ANCIENT_CITY)) {
            return ANCIENT_CITY_CHEST_CHANCE;
        }
        if (
                lootTable.equals(BuiltInLootTables.NETHER_BRIDGE)
                        || lootTable.equals(BuiltInLootTables.BASTION_TREASURE)
                        || lootTable.equals(BuiltInLootTables.BASTION_BRIDGE)
                        || lootTable.equals(BuiltInLootTables.BASTION_HOGLIN_STABLE)
                        || lootTable.equals(BuiltInLootTables.BASTION_OTHER)
        ) {
            return NETHER_CHEST_CHANCE;
        }
        if (lootTable.equals(BuiltInLootTables.SIMPLE_DUNGEON)) {
            return DUNGEON_CHEST_CHANCE;
        }
        return VILLAGE_CHEST_CHANCE;
    }
}
