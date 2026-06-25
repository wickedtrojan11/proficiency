package com.trojan.proficiency.perk;

import java.util.List;

public class FarmingPerks {

    public static final SkillPerk CULTIVATION_FASTER_GROWTH =
            perk(
                    "cultivation_faster_growth",
                    "Faster Growth",
                    "Your crops respond to careful cultivation.",
                    "Normal crops grow 10% faster.",
                    5,
                    125,
                    325,
                    null
            );

    public static final SkillPerk BETTER_YIELDS =
            perk(
                    "better_yields",
                    "Better Yields",
                    "Healthy soil rewards a patient farmer.",
                    "Mature crops have a 10% chance to drop one extra crop item.",
                    12,
                    125,
                    265,
                    "cultivation_faster_growth"
            );

    public static final SkillPerk AUTO_REPLANT =
            perk(
                    "auto_replant",
                    "Auto Replant",
                    "Every harvest leaves the next crop ready to begin.",
                    "Mature crops are replanted when matching seeds are available.",
                    25,
                    125,
                    205,
                    "improved_growth"
            );

    public static final SkillPerk IMPROVED_GROWTH =
            perk(
                    "improved_growth",
                    "Improved Growth",
                    "Your fields respond to increasingly careful cultivation.",
                    "Normal crops grow 25% faster in total.",
                    18,
                    105,
                    235,
                    "better_yields"
            );

    public static final SkillPerk RAPID_GROWTH =
            perk(
                    "rapid_growth",
                    "Rapid Growth",
                    "Your crops waste no time reaching the harvest.",
                    "Normal crops grow 40% faster in total.",
                    30,
                    145,
                    145,
                    "auto_replant"
            );

    public static final SkillPerk GREENHOUSE_GENIUS =
            perk(
                    "greenhouse_genius",
                    "Greenhouse Genius",
                    "You can coax abundance from any patch of soil.",
                    "Normal crops grow 50% faster in total.",
                    40,
                    125,
                    95,
                    "rapid_growth"
            );

    public static final SkillPerk ANIMAL_FASTER_GROWTH =
            perk(
                    "animal_faster_growth",
                    "Faster Growth",
                    "Young animals thrive under your care.",
                    5,
                    220,
                    325,
                    null
            );

    public static final SkillPerk EXTRA_WOOL =
            perk(
                    "extra_wool",
                    "Extra Wool",
                    "A well-kept flock grows a fuller coat.",
                    12,
                    220,
                    265,
                    "animal_faster_growth"
            );

    public static final SkillPerk HERD_INSTINCT =
            perk(
                    "herd_instinct",
                    "Herd Instinct",
                    "Your animals move together with practiced ease.",
                    25,
                    220,
                    205,
                    "extra_wool"
            );

    public static final SkillPerk SHEPHERDS_CALL =
            perk(
                    "shepherds_call",
                    "Shepherd's Call",
                    "Every animal on the farm knows your voice.",
                    40,
                    220,
                    95,
                    "herd_instinct"
            );

    public static final SkillPerk MUSHROOM_EXPERT =
            perk(
                    "mushroom_expert",
                    "Mushroom Expert",
                    "You recognize the best places for fungi to flourish.",
                    5,
                    315,
                    325,
                    null
            );

    public static final SkillPerk BERRY_HARVESTER =
            perk(
                    "berry_harvester",
                    "Berry Harvester",
                    "Not a single ripe berry escapes your eye.",
                    12,
                    315,
                    265,
                    "mushroom_expert"
            );

    public static final SkillPerk HONEY_GATHERER =
            perk(
                    "honey_gatherer",
                    "Honey Gatherer",
                    "You gather from the hive with a steady hand.",
                    25,
                    315,
                    205,
                    "berry_harvester"
            );

    public static final SkillPerk BOUNTIFUL_HARVEST =
            perk(
                    "bountiful_harvest",
                    "Bountiful Harvest",
                    "The land offers its finest rewards to you.",
                    40,
                    315,
                    95,
                    "honey_gatherer"
            );

    public static final SkillPerk BUSY_BEES =
            perk(
                    "busy_bees",
                    "Busy Bees",
                    "Your hives hum with tireless activity.",
                    5,
                    410,
                    325,
                    null
            );

    public static final SkillPerk POLLINATION_EXPERT =
            perk(
                    "pollination_expert",
                    "Pollination Expert",
                    "You understand how flowers and bees strengthen a farm.",
                    12,
                    410,
                    265,
                    "busy_bees"
            );

    public static final SkillPerk HONEY_MASTERY =
            perk(
                    "honey_mastery",
                    "Honey Mastery",
                    "Every hive is managed with practiced precision.",
                    25,
                    410,
                    205,
                    "pollination_expert"
            );

    public static final SkillPerk MASTER_BEEKEEPER =
            perk(
                    "master_beekeeper",
                    "Master Beekeeper",
                    "The apiary thrives under your watch.",
                    40,
                    410,
                    95,
                    "honey_mastery"
            );

    public static final List<SkillPerk> ALL_PERKS =
            List.of(
                    CULTIVATION_FASTER_GROWTH,
                    BETTER_YIELDS,
                    IMPROVED_GROWTH,
                    AUTO_REPLANT,
                    RAPID_GROWTH,
                    GREENHOUSE_GENIUS,
                    ANIMAL_FASTER_GROWTH,
                    EXTRA_WOOL,
                    HERD_INSTINCT,
                    SHEPHERDS_CALL,
                    MUSHROOM_EXPERT,
                    BERRY_HARVESTER,
                    HONEY_GATHERER,
                    BOUNTIFUL_HARVEST,
                    BUSY_BEES,
                    POLLINATION_EXPERT,
                    HONEY_MASTERY,
                    MASTER_BEEKEEPER
            );

    private static SkillPerk perk(
            String id,
            String name,
            String description,
            String effectText,
            int requiredLevel,
            int x,
            int y,
            String parentId
    ) {

        return new SkillPerk(
                id,
                name,
                description,
                effectText,
                requiredLevel,
                x,
                y,
                parentId
        );
    }

    private static SkillPerk perk(
            String id,
            String name,
            String description,
            int requiredLevel,
            int x,
            int y,
            String parentId
    ) {

        return perk(
                id,
                name,
                description,
                "Effect not implemented yet.",
                requiredLevel,
                x,
                y,
                parentId
        );
    }
}
