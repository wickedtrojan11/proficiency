package com.trojan.proficiency.item;

import com.trojan.proficiency.SkillManager;
import com.trojan.proficiency.skill.SkillType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SkillBookRegistry {

    private static final Map<String, Map<Tier, Entry>> BY_SKILL_ID =
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
        return register(skillId, Tier.COMMON, item, displayName, xpAmount);
    }

    public static void registerSkillBooks(
            String skillId,
            Item commonBook,
            Item rareBook,
            Item ancientTome,
            String displayNamePrefix
    ) {
        register(
                skillId,
                Tier.COMMON,
                commonBook,
                displayNamePrefix + " Skill Book",
                Tier.COMMON.xpAmount()
        );
        register(
                skillId,
                Tier.RARE,
                rareBook,
                "Rare " + displayNamePrefix + " Skill Book",
                Tier.RARE.xpAmount()
        );
        register(
                skillId,
                Tier.ANCIENT,
                ancientTome,
                "Ancient " + displayNamePrefix + " Skill Tome",
                Tier.ANCIENT.xpAmount()
        );
    }

    public static Entry register(
            String skillId,
            Tier tier,
            Item item,
            String displayName,
            int xpAmount
    ) {
        if (
                skillId == null
                        || skillId.isBlank()
                        || tier == null
                        || item == null
                        || displayName == null
                        || displayName.isBlank()
                        || xpAmount <= 0
                        || BY_ITEM.containsKey(item)
                        || BY_SKILL_ID
                        .computeIfAbsent(skillId, id -> new LinkedHashMap<>())
                        .containsKey(tier)
        ) {
            throw new IllegalArgumentException(
                    "Invalid or duplicate skill book registration: "
                            + skillId + " / " + tier
            );
        }

        Entry entry = new Entry(skillId, tier, item, displayName, xpAmount);
        BY_SKILL_ID.get(skillId).put(tier, entry);
        BY_ITEM.put(item, entry);
        return entry;
    }

    public static Entry getBySkillId(String skillId) {
        return getBySkillId(skillId, Tier.COMMON);
    }

    public static Entry getBySkillId(String skillId, Tier tier) {
        Map<Tier, Entry> entries = BY_SKILL_ID.get(skillId);
        return entries == null ? null : entries.get(tier);
    }

    public static Entry getByItem(Item item) {
        return BY_ITEM.get(item);
    }

    public static List<Entry> entries() {
        return Collections.unmodifiableList(
                BY_SKILL_ID.values().stream()
                        .flatMap(entries -> entries.values().stream())
                        .toList()
        );
    }

    public static Item getRandomBook(RandomSource random) {
        return getRandomBook(random, LootProfile.HOSTILE).item();
    }

    public static Entry getRandomBook(
            RandomSource random,
            LootProfile profile
    ) {
        Tier tier = profile.randomTier(random);
        List<Entry> entries = BY_SKILL_ID.values().stream()
                .map(skillEntries -> skillEntries.get(tier))
                .filter(entry -> entry != null)
                .toList();
        if (entries.isEmpty()) {
            throw new IllegalStateException("No skill books are registered");
        }
        return entries.get(random.nextInt(entries.size()));
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
        List<Entry> entries = entries();
        if (
                BY_SKILL_ID.isEmpty()
                        || entries.size() != BY_ITEM.size()
                        || entries.stream()
                        .map(Entry::item)
                        .distinct()
                        .count() != entries.size()
        ) {
            throw new IllegalStateException(
                    "Skill book registry contains incomplete or duplicate entries"
            );
        }

        for (Map.Entry<String, Map<Tier, Entry>> skillEntry
                : BY_SKILL_ID.entrySet()) {
            for (Tier tier : Tier.values()) {
                if (!skillEntry.getValue().containsKey(tier)) {
                    throw new IllegalStateException(
                            "Skill book registry is missing "
                                    + tier + " book for "
                                    + skillEntry.getKey()
                    );
                }
            }
        }
    }

    public enum Tier {
        COMMON(100),
        RARE(500),
        ANCIENT(2500);

        private final int xpAmount;

        Tier(int xpAmount) {
            this.xpAmount = xpAmount;
        }

        public int xpAmount() {
            return xpAmount;
        }
    }

    public enum LootProfile {
        HOSTILE(82, 15, 3),
        STANDARD_CHEST(82, 15, 3),
        DANGEROUS_CHEST(65, 25, 10);

        private final int commonWeight;
        private final int rareWeight;
        private final int ancientWeight;

        LootProfile(
                int commonWeight,
                int rareWeight,
                int ancientWeight
        ) {
            this.commonWeight = commonWeight;
            this.rareWeight = rareWeight;
            this.ancientWeight = ancientWeight;
        }

        public Tier randomTier(RandomSource random) {
            int total = commonWeight + rareWeight + ancientWeight;
            int roll = random.nextInt(total);
            if (roll < commonWeight) {
                return Tier.COMMON;
            }
            if (roll < commonWeight + rareWeight) {
                return Tier.RARE;
            }
            return Tier.ANCIENT;
        }

        public int weight(Tier tier) {
            return switch (tier) {
                case COMMON -> commonWeight;
                case RARE -> rareWeight;
                case ANCIENT -> ancientWeight;
            };
        }
    }

    public record Entry(
            String skillId,
            Tier tier,
            Item item,
            String displayName,
            int xpAmount
    ) {
    }
}
