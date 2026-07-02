package com.trojan.proficiency.perk;

import java.util.List;
import java.util.Map;

public class FarmingPerks {

    public static final SkillPerk CULTIVATION_FASTER_GROWTH =
            perk(
                    "cultivation_faster_growth",
                    "Faster Growth",
                    "Your crops respond to careful cultivation.",
                    "Vanilla crops grow 10% faster.",
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
                    "Vanilla crops grow 25% faster in total.",
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
                    "Vanilla crops grow 40% faster in total.",
                    30,
                    145,
                    145,
                    "auto_replant"
            );

    public static final SkillPerk GREENHOUSE_GENIUS =
            perk(
                    "greenhouse_genius",
                    "Greenhouse Genius",
                    "Unlocks the Proficient Pot and Auto Composter for automated farming.",
                    "Vanilla crops grow 50% faster in total.",
                    40,
                    125,
                    95,
                    "rapid_growth"
            );

    public static final SkillPerk ANIMAL_FASTER_GROWTH =
            perk(
                    "animal_faster_growth",
                    "Animal Care",
                    "Young animals thrive under your care.",
                    "Nearby baby animals grow 25% faster.",
                    5,
                    220,
                    325,
                    null
            );

    public static final SkillPerk EXPERIENCED_BREEDER =
            perk(
                    "experienced_breeder",
                    "Experienced Breeder",
                    "You know how to keep breeding animals healthy and comfortable.",
                    "Breeding cooldowns are reduced by 25%.",
                    8,
                    200,
                    295,
                    "animal_faster_growth"
            );

    public static final SkillPerk EXTRA_WOOL =
            perk(
                    "extra_wool",
                    "Expert Shearer",
                    "A well-kept flock grows a fuller coat.",
                    "Shearing has a 10% chance to produce one extra wool.",
                    12,
                    220,
                    265,
                    "experienced_breeder"
            );

    public static final SkillPerk HEALTHY_FLOCKS =
            perk(
                    "healthy_flocks",
                    "Healthy Flocks",
                    "Healthy sheep replace their coats more quickly.",
                    "Nearby sheep regrow wool 25% faster.",
                    20,
                    200,
                    235,
                    "extra_wool"
            );

    public static final SkillPerk HERD_INSTINCT =
            perk(
                    "herd_instinct",
                    "Herd Instinct",
                    "Your animals move together with practiced ease.",
                    "Nearby farm animals follow without requiring food.",
                    25,
                    220,
                    205,
                    "healthy_flocks"
            );

    public static final SkillPerk SHEPHERDS_TOUCH =
            perk(
                    "shepherds_touch",
                    "Shepherd's Touch",
                    "Your flock flourishes under an expert hand.",
                    "Baby animals grow 50% faster and sheep regrow wool 50% faster in total.",
                    30,
                    200,
                    175,
                    "herd_instinct"
            );

    public static final SkillPerk SHEPHERDS_CALL =
            perk(
                    "shepherds_call",
                    "Shepherd's Call",
                    "Your care helps every newborn thrive.",
                    "Baby animals grow 75% faster in total.",
                    40,
                    220,
                    95,
                    "shepherds_touch"
            );

    public static final SkillPerk HEALTHY_STOCK =
            perk(
                    "healthy_stock",
                    "Healthy Stock",
                    "Well-raised livestock provide a little more.",
                    "Player-killed farm animals have a 10% chance to drop one extra meat.",
                    15,
                    250,
                    250,
                    "extra_wool"
            );

    public static final SkillPerk PRIME_CUTS =
            perk(
                    "prime_cuts",
                    "Prime Cuts",
                    "Careful husbandry produces better cuts.",
                    "The extra meat chance improves to 18%.",
                    22,
                    250,
                    215,
                    "healthy_stock"
            );

    public static final SkillPerk EFFICIENT_RANCHER =
            perk(
                    "efficient_rancher",
                    "Efficient Rancher",
                    "You make use of every part of your livestock.",
                    "Player kills sometimes produce an extra secondary animal drop.",
                    30,
                    250,
                    170,
                    "prime_cuts"
            );

    public static final SkillPerk BOUNTIFUL_HERDS =
            perk(
                    "bountiful_herds",
                    "Bountiful Herds",
                    "Generations of careful ranching show in every animal.",
                    "Extra meat chance reaches 25% and secondary drop chance reaches 12%.",
                    40,
                    250,
                    95,
                    "efficient_rancher"
            );

    public static final SkillPerk MUSHROOM_EXPERT =
            perk(
                    "mushroom_expert",
                    "Mushroom Expert",
                    "You recognize the best places for fungi to flourish.",
                    "Nearby mushrooms spread faster and sometimes yield an extra mushroom.",
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
                    "Sweet berry bushes grow faster and sometimes yield extra berries.",
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
                    "Ready hives have a 15% chance to yield one extra honeycomb.",
                    25,
                    410,
                    205,
                    "pollination_expert"
            );

    public static final SkillPerk BOUNTIFUL_HARVEST =
            perk(
                    "bountiful_harvest",
                    "Bountiful Harvest",
                    "The land offers its finest rewards to you.",
                    "Harvesting a mature crop also harvests adjacent mature crops.",
                    40,
                    315,
                    95,
                    "berry_harvester"
            );

    public static final SkillPerk BUSY_BEES =
            perk(
                    "busy_bees",
                    "Busy Bees",
                    "Your hives hum with tireless activity.",
                    "Active nearby bees occasionally help crops grow.",
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
                    "Bee-assisted crop growth reaches farther and happens more often.",
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
                    "Nearby full hives produce honey slightly faster over time.",
                    25,
                    410,
                    155,
                    "honey_gatherer"
            );

    public static final SkillPerk MASTER_BEEKEEPER =
            perk(
                    "master_beekeeper",
                    "Master Beekeeper",
                    "The apiary thrives under your watch.",
                    "Harvesting ready hives can produce extra honey, and bees are calmer.",
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
                    EXPERIENCED_BREEDER,
                    EXTRA_WOOL,
                    HEALTHY_FLOCKS,
                    HERD_INSTINCT,
                    SHEPHERDS_TOUCH,
                    SHEPHERDS_CALL,
                    HEALTHY_STOCK,
                    PRIME_CUTS,
                    EFFICIENT_RANCHER,
                    BOUNTIFUL_HERDS,
                    MUSHROOM_EXPERT,
                    BERRY_HARVESTER,
                    HONEY_GATHERER,
                    BOUNTIFUL_HARVEST,
                    BUSY_BEES,
                    POLLINATION_EXPERT,
                    HONEY_MASTERY,
                    MASTER_BEEKEEPER
            );

    public static final Map<String, SkillPerk> PERKS_BY_ID =
            SkillPerk.indexById(ALL_PERKS);

    public static SkillPerk getById(String perkId) {
        return PERKS_BY_ID.get(perkId);
    }

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
