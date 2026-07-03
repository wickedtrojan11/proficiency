package com.trojan.proficiency.item;

import com.trojan.proficiency.SkillManager;
import com.trojan.proficiency.skill.SkillType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SkillBookRegistry {

    private static final Map<String, Entry> BY_SKILL_ID =
            new LinkedHashMap<>();
    private static final Map<Item, Entry> BY_ITEM =
            new IdentityHashMap<>();

    private SkillBookRegistry() {
    }

    public static Entry register(
            String skillId,
            Item item,
            String displayName,
            int xpAmount
    ) {
        if (
                skillId == null
                        || skillId.isBlank()
                        || item == null
                        || displayName == null
                        || displayName.isBlank()
                        || xpAmount <= 0
                        || BY_SKILL_ID.containsKey(skillId)
                        || BY_ITEM.containsKey(item)
        ) {
            throw new IllegalArgumentException(
                    "Invalid or duplicate skill book registration: " + skillId
            );
        }

        Entry entry = new Entry(skillId, item, displayName, xpAmount);
        BY_SKILL_ID.put(skillId, entry);
        BY_ITEM.put(item, entry);
        return entry;
    }

    public static Entry getBySkillId(String skillId) {
        return BY_SKILL_ID.get(skillId);
    }

    public static Entry getByItem(Item item) {
        return BY_ITEM.get(item);
    }

    public static List<Entry> entries() {
        return Collections.unmodifiableList(
                new ArrayList<>(BY_SKILL_ID.values())
        );
    }

    public static Item getRandomBook(RandomSource random) {
        List<Entry> entries = entries();
        if (entries.isEmpty()) {
            throw new IllegalStateException("No skill books are registered");
        }
        return entries.get(random.nextInt(entries.size())).item();
    }

    public static void grantXp(ServerPlayer player, Entry entry) {
        SkillType skillType = SkillType.fromId(entry.skillId());
        if (skillType == null) {
            throw new IllegalStateException(
                    "No skill system exists for book skill: "
                            + entry.skillId()
            );
        }

        switch (skillType) {
            case MINING -> SkillManager.addMiningXp(player, entry.xpAmount());
            case WOODCUTTING ->
                    SkillManager.addWoodcuttingXp(player, entry.xpAmount());
            case FARMING -> SkillManager.addFarmingXp(player, entry.xpAmount());
            case ONE_HANDED ->
                    SkillManager.addOneHandedXp(player, entry.xpAmount());
        }
    }

    public static void validate() {
        if (
                BY_SKILL_ID.isEmpty()
                        || BY_SKILL_ID.size() != BY_ITEM.size()
                        || BY_SKILL_ID.values().stream()
                        .map(Entry::item)
                        .distinct()
                        .count() != BY_SKILL_ID.size()
        ) {
            throw new IllegalStateException(
                    "Skill book registry contains incomplete or duplicate entries"
            );
        }
    }

    public record Entry(
            String skillId,
            Item item,
            String displayName,
            int xpAmount
    ) {
    }
}
