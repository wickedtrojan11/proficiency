package com.trojan.proficiency.perk;

import java.util.List;
import java.util.Map;

public final class AlchemyPerks {

    public static final SkillPerk QUICK_STIR =
            perk("quick_stir", "Quick Stir",
                    "Your hands learn the rhythm of the stand.",
                    "Brewing stands work 10% faster.", 5, 125, 325, null);
    public static final SkillPerk HEATED_REACTION =
            perk("heated_reaction", "Heated Reaction",
                    "Every bubble rises a little sooner.",
                    "Brewing stands work 20% faster.", 12, 125, 265,
                    "quick_stir");
    public static final SkillPerk RAPID_INFUSION =
            perk("rapid_infusion", "Rapid Infusion",
                    "The mixture knows where it is going.",
                    "Brewing stands work 35% faster.", 25, 125, 205,
                    "heated_reaction");
    public static final SkillPerk PROFICIENT_BREW_STAND =
            perk("proficient_brew_stand", "Proficient Brew Stand",
                    "A master brewer never settles for a single batch.",
                    "Unlocks a future double-output brewing stand.", 40, 125,
                    95, "rapid_infusion");

    public static final SkillPerk CAREFUL_MEASURE =
            perk("careful_measure", "Careful Measure",
                    "A steady hand wastes fewer rare ingredients.",
                    "Ingredients have a 5% chance to be refunded.", 5, 220,
                    325, null);
    public static final SkillPerk GLASS_SCRAPER =
            perk("glass_scraper", "Glass Scraper",
                    "You recover what lesser brewers leave behind.",
                    "Ingredient refund chance improves to 10%.", 12, 220,
                    265, "careful_measure");
    public static final SkillPerk ALCHEMICAL_RECLAIMING =
            perk("alchemical_reclaiming", "Alchemical Reclaiming",
                    "Even failed residue has a use.",
                    "Ingredient refund chance improves to 18%.", 25, 220,
                    205, "glass_scraper");
    public static final SkillPerk NOTHING_WASTED =
            perk("nothing_wasted", "Nothing Wasted",
                    "Every pinch lands where it belongs.",
                    "Ingredient refund chance improves to 25%.", 40, 220,
                    95, "alchemical_reclaiming");

    public static final SkillPerk SWEETENED_STABILITY =
            perk("sweetened_stability", "Sweetened Stability",
                    "Honey helps fragile mixtures hold together.",
                    "Honey Bottle extensions can add up to 2 minutes.", 5,
                    315, 325, null);
    public static final SkillPerk LONG_STEEP =
            perk("long_steep", "Long Steep",
                    "Patience gives every potion a longer tail.",
                    "Honey Bottle extensions can add up to 4 minutes.", 12,
                    315, 265, "sweetened_stability");
    public static final SkillPerk DEEP_BINDING =
            perk("deep_binding", "Deep Binding",
                    "The effect clings to the bottle like amber.",
                    "Honey Bottle extensions can add up to 6 minutes.", 25,
                    315, 205, "long_steep");
    public static final SkillPerk PERFECT_SUSPENSION =
            perk("perfect_suspension", "Perfect Suspension",
                    "Nothing separates until you say it does.",
                    "Honey Bottle extensions can add up to 8 minutes.", 35,
                    315, 145, "deep_binding");
    public static final SkillPerk ETERNAL_DRAUGHT =
            perk("eternal_draught", "Eternal Draught",
                    "You brew minutes into the mixture itself.",
                    "Honey Bottle extensions can add up to 10 minutes.", 40,
                    315, 95, "perfect_suspension");

    public static final SkillPerk CAMELLIA_PRESS =
            perk("camellia_press", "Camellia Oil",
                    "Camellia petals yield a protective oil.",
                    "Unlocks Camellia Oil for tools and weapons.", 5, 410,
                    325, null);
    public static final SkillPerk OILERS_TOUCH =
            perk("oilers_touch", "Fire Oil",
                    "A volatile coating catches on a clean strike.",
                    "Unlocks Fire Oil for weapons.", 10, 410,
                    280, "camellia_press");
    public static final SkillPerk POLISHED_EDGE =
            perk("polished_edge", "Frost Oil",
                    "Winter settles into the edge of the blade.",
                    "Unlocks Frost Oil for weapons.", 15, 410, 235,
                    "oilers_touch");
    public static final SkillPerk EVERLASTING_SHEEN =
            perk("everlasting_sheen", "Master Craftsman",
                    "You tailor coatings to the tool in your hand.",
                    "Unlocks Miner's Oil and Lumber Oil.", 20,
                    410, 190, "polished_edge");
    public static final SkillPerk PERFECT_COATING =
            perk("perfect_coating", "Perfect Coating",
                    "No drop is wasted, and no edge rejects a second finish.",
                    "Oil charges are doubled and two oils can coat one item.",
                    25, 410, 145, "everlasting_sheen");

    public static final SkillPerk REFINED_MIXTURE =
            perk("refined_mixture", "Refined Mixture",
                    "Your mixtures sometimes emerge sharper than expected.",
                    "Eligible brewed potions have a 10% chance to gain one amplifier level.",
                    5, 505, 325, null);
    public static final SkillPerk CONCENTRATED_ESSENCE =
            perk("concentrated_essence", "Concentrated Essence",
                    "You draw more strength from the same ingredients.",
                    "Potion potency upgrade chance improves to 20%.", 12,
                    505, 265, "refined_mixture");
    public static final SkillPerk DISTILLED_PERFECTION =
            perk("distilled_perfection", "Distilled Perfection",
                    "A cleaner distillation leaves fewer weak brews.",
                    "Potion potency upgrade chance improves to 35%.", 25,
                    505, 205, "concentrated_essence");
    public static final SkillPerk MASTERS_FORMULA =
            perk("masters_formula", "Master's Formula",
                    "Your formula has become a brewer's signature.",
                    "Potion potency upgrade chance improves to 50%.", 35,
                    505, 145, "distilled_perfection");
    public static final SkillPerk PHILOSOPHERS_BREW =
            perk("philosophers_brew", "Philosopher's Brew",
                    "Every eligible brew reaches its natural peak.",
                    "Eligible brewed potions always gain one amplifier level.",
                    40, 505, 95, "masters_formula");

    public static final List<SkillPerk> ALL_PERKS = List.of(
            QUICK_STIR, HEATED_REACTION, RAPID_INFUSION,
            PROFICIENT_BREW_STAND,
            CAREFUL_MEASURE, GLASS_SCRAPER, ALCHEMICAL_RECLAIMING,
            NOTHING_WASTED,
            SWEETENED_STABILITY, LONG_STEEP, DEEP_BINDING,
            PERFECT_SUSPENSION, ETERNAL_DRAUGHT,
            CAMELLIA_PRESS, OILERS_TOUCH, POLISHED_EDGE,
            EVERLASTING_SHEEN, PERFECT_COATING,
            REFINED_MIXTURE, CONCENTRATED_ESSENCE,
            DISTILLED_PERFECTION, MASTERS_FORMULA, PHILOSOPHERS_BREW
    );

    public static final Map<String, SkillPerk> PERKS_BY_ID =
            SkillPerk.indexById(ALL_PERKS);

    private AlchemyPerks() {
    }

    public static SkillPerk getById(String perkId) {
        return PERKS_BY_ID.get(perkId);
    }

    private static SkillPerk perk(
            String id,
            String name,
            String description,
            String effect,
            int level,
            int x,
            int y,
            String parentId
    ) {
        return new SkillPerk(
                id,
                name,
                description,
                effect,
                level,
                x,
                y,
                parentId
        );
    }
}
