package com.trojan.proficiency.perk;

import java.util.List;

public class MiningPerks {

    // HANDLE

    public static final SkillPerk ITS_A_WEAPON =
            new SkillPerk(
                    "its_a_weapon",
                    "Its a.. weapon?",
                    "After using your pick for a bit you realized.. ITS DANGEROUS!",
                    "Gain strength I while holding pickaxe.",
                    3,
                    260,
                    320,
                    null
            );

    public static final SkillPerk STONECUTTER =
            new SkillPerk(
                    "stonecutter",
                    "Stonecutter",
                    "Mine stone slightly faster.",
                    "Mine stone-type blocks faster.",
                    5,
                    260,
                    280,
                    "its_a_weapon");

    public static final SkillPerk BETTER_HANDLING =
            new SkillPerk(
                    "better_handling",
                    "Better Handling",
                    "After mining for so long you FINALLY figured out where your hands should be!",
                    "Pickaxes lose 10% less durability.",
                    7,
                    260,
                    240,
                    "stonecutter"
            );
    public static final SkillPerk REINFORCED_GRIP =
            new SkillPerk(
                    "reinforced_grip",
                    "Reinforced Grip",
                    "Your pickaxes feel sturdier in your hands.",
                    "Pickaxes lose 25% less durability.",
                    10,
                    260,
                    200,
                    "better_handling"
            );

    public static final SkillPerk MINERS_MOMENTUM =
            new SkillPerk(
                    "miners_momentum",
                    "Miner's Momentum",
                    "Once you find your rhythm, the mines begin to fear YOU.",
                    "Continuous mining grants temporary Haste II.",
                    12,
                    260,
                    160,
                    "reinforced_grip"
            );
    // LEFT HEAD

    public static final SkillPerk THEY_HAVE_A_SCENT =
            new SkillPerk(
                    "they_have_a_scent",
                    "They have a scent?",
                    "After being in the mines for so long you've either lost your mind OR you've been around some minerals so long that you can SMELL the difference!",
          "Nearby coal ore emits faint particles.",
                    15,
                    220,
                    140,
                    "miners_momentum"
            );

    public static final SkillPerk IT_SMELLS_2 =
            new SkillPerk(
                    "it_smells_2",
                    "It smells 2",
                    "Ok.. OK! They do have a smell!",
                    "Nearby iron, copper, and lapis ore emit faint particles.",
                    20,
                    185,
                    125,
                    "they_have_a_scent"
            );

    public static final SkillPerk IT_SMELLS_3 =
            new SkillPerk(
                    "it_smells_3",
                    "It smells 3",
                    "Ok.. but do they smell good?",
                    "Nearby diamond, gold, and emerald ore emit faint particles.",
                    25,
                    150,
                    140,
                    "it_smells_2"
            );

    public static final SkillPerk IT_SMELLS_4 =
            new SkillPerk(
                    "it_smells_4",
                    "It smells 4",
                    "I cant imagine ANYTHING in the nether smells good..",
                    "Nearby ancient debris emits faint particles.",
                    35,
                    115,
                    170,
                    "it_smells_3"
            );

    // RIGHT HEAD


    public static final SkillPerk HEAVY_SWINGS =
            new SkillPerk(
                    "heavy_swings",
                    "Heavy Swings",
                    "You stopped fighting the stone and started BREAKING through it.",
                    "15% chance to break blocks instantly",
                    15,
                    300,
                    140,
                    "miners_momentum"
            );

    public static final SkillPerk SWIFT_SWING =
            new SkillPerk(
                    "swift_swing",
                    "Swift Swing",
                    "You've become more efficient and require far less energy to mine.",
                    "Mining stone-type blocks consumes far less hunger exhaustion.",
                    20,
                    335,
                    125,
                    "heavy_swings"
            );

    public static final SkillPerk TEMPERED_TOOLS =
            new SkillPerk(
                    "tempered_tools",
                    "Tempered Tools",
                    "pickaxes seem to last longer when you take care of them!",
                    "Pickaxes lose 40% less durability.",
                    25,
                    370,
                    140,
                    "swift_swing"
            );

    public static final SkillPerk NEARLY_INDESTRUCTIBLE =
            new SkillPerk(
                    "nearly_indestructible",
                    "Nearly Indestructible",
                    "At this point your pickaxe refuses to break out of pure respect.",
                    "Pickaxes lose 75% less durability.",
                    35,
                    405,
                    170,
                    "tempered_tools"
            );

    public static final List<SkillPerk> ALL_PERKS =
            List.of(

                    ITS_A_WEAPON,
                    STONECUTTER,
                    BETTER_HANDLING,
                    REINFORCED_GRIP,
                    MINERS_MOMENTUM,

                    THEY_HAVE_A_SCENT,
                    IT_SMELLS_2,
                    IT_SMELLS_3,
                    IT_SMELLS_4,

                    HEAVY_SWINGS,
                    SWIFT_SWING,
                    TEMPERED_TOOLS,
                    NEARLY_INDESTRUCTIBLE
            );
}