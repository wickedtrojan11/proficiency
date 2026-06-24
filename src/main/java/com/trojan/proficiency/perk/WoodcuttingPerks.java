package com.trojan.proficiency.perk;

import java.util.List;

public class WoodcuttingPerks {

    public static final SkillPerk SPLINTER_FIGHTER =
            new SkillPerk(
                    "splinter_fighter",
                    "Splinter Fighter",
                    "You have learned that an axe is not just for trees.",
                    "Gain Strength I while holding an axe.",
                    3,
                    280,
                    220,
                    null
            );

    public static final SkillPerk PROPER_GRIP =
            new SkillPerk(
                    "proper_grip",
                    "Proper Grip",
                    "You learn to stop wasting durability on bad strikes.",
                    "Axes lose 10% less durability.",
                    7,
                    345,
                    123,
                    "splinter_fighter"
            );

    public static final SkillPerk TIMBER_TRAINING =
            new SkillPerk(
                    "timber_training",
                    "Timber Training",
                    "You start reading the grain before each swing.",
                    "+10% chopping speed.",
                    8,
                    300,
                    200,
                    "splinter_fighter"
            );

    public static final SkillPerk LUMBERJACKS_STANCE =
            new SkillPerk(
                    "lumberjacks_stance",
                    "Lumberjack's Stance",
                    "Your feet settle before the axe falls.",
                    "+15% chopping speed total.",
                    12,
                    340,
                    140,
                    "timber_training"
            );

    public static final SkillPerk FRICTION_FIRE =
            new SkillPerk(
                    "friction_fire",
                    "Friction Fire",
                    "You hit the wood so hard and fast that it sometimes sparks into charcoal.",
                    "Logs sometimes drop charcoal when chopped with an axe.",
                    25,
                    370,
                    125,
                    "lumberjacks_stance"
            );

    public static final SkillPerk CLEAN_SWING =
            new SkillPerk(
                    "clean_swing",
                    "Clean Swing",
                    "Every cut lands cleanly through the grain.",
                    "Gain an additional chopping speed bonus.",
                    18,
                    385,
                    145,
                    "lumberjacks_stance"
            );

    public static final SkillPerk FELLING_MOMENTUM =
            new SkillPerk(
                    "felling_momentum",
                    "Felling Momentum",
                    "One clean cut makes the next swing come easier.",
                    "Briefly gain an Efficiency-style chopping boost after cutting a log.",
                    28,
                    400,
                    165,
                    "clean_swing"
            );

    public static final SkillPerk RHYTHM_OF_THE_FOREST =
            new SkillPerk(
                    "rhythm_of_the_forest",
                    "Rhythm of the Forest",
                    "Each falling log pulls you deeper into the rhythm.",
                    "Continuously chopping logs builds additional chop speed.",
                    30,
                    415,
                    190,
                    "felling_momentum"
            );

    public static final SkillPerk MASTER_ARBORIST =
            new SkillPerk(
                    "master_arborist",
                    "Master Arborist",
                    "You know every tree by heart.",
                    "Breaking a tree's base log with an axe fells the connected tree.",
                    40,
                    430,
                    215,
                    "rhythm_of_the_forest"
            );

    public static final SkillPerk REINFORCED_HAFT =
            new SkillPerk(
                    "reinforced_haft",
                    "Reinforced Haft",
                    "A stronger handle keeps every swing under control.",
                    "Axes lose 20% less durability.",
                    12,
                    290,
                    170,
                    "proper_grip"
            );

    public static final SkillPerk CALLUSED_HANDS =
            new SkillPerk(
                    "callused_hands",
                    "Callused Hands",
                    "Long days with an axe teach your hands to absorb every impact.",
                    "Axes lose 35% less durability.",
                    18,
                    305,
                    155,
                    "reinforced_haft"
            );

    public static final SkillPerk SEASONED_HAFT =
            new SkillPerk(
                    "seasoned_haft",
                    "Seasoned Haft",
                    "You know how to keep a trusted handle working for years.",
                    "Axes lose 50% less durability.",
                    25,
                    320,
                    140,
                    "callused_hands"
            );

    public static final SkillPerk VETERAN_WOODSMAN =
            new SkillPerk(
                    "veteran_woodsman",
                    "Veteran Woodsman",
                    "Your tools endure because every strike is measured.",
                    "Axes lose 75% less durability.",
                    35,
                    335,
                    125,
                    "seasoned_haft"
            );

    public static final SkillPerk AXE_TRAINING =
            new SkillPerk(
                    "axe_training",
                    "Axe Training",
                    "Every swing teaches you where an axe hits hardest.",
                    "Deal a small amount of bonus melee damage with axes.",
                    8,
                    270,
                    200,
                    "splinter_fighter"
            );

    public static final SkillPerk HEAVY_CHOP =
            new SkillPerk(
                    "heavy_chop",
                    "Heavy Chop",
                    "A well-placed blow leaves your target struggling to move.",
                    "Axe hits sometimes apply brief Slowness.",
                    12,
                    255,
                    185,
                    "axe_training"
            );

    public static final SkillPerk CLEAVING_SWING =
            new SkillPerk(
                    "cleaving_swing",
                    "Cleaving Swing",
                    "Your axe carries enough force to threaten everything nearby.",
                    "Axe hits sometimes damage nearby hostile mobs.",
                    20,
                    270,
                    170,
                    "heavy_chop"
            );

    public static final SkillPerk DECAPITATION_CHANCE =
            new SkillPerk(
                    "decapitation_chance",
                    "Decapitation Chance",
                    "A perfect finishing blow can leave behind a grim trophy.",
                    "Axe kills have a 25% chance to drop a matching mob head.",
                    40,
                    250,
                    150,
                    "cleaving_swing"
            );

    public static final SkillPerk SPLINTER_FIGHTER_II =
            new SkillPerk(
                    "splinter_fighter_2",
                    "Splinter Fighter II",
                    "An axe feels as natural in battle as it does in the forest.",
                    "Gain Strength II while holding an axe.",
                    25,
                    235,
                    170,
                    "cleaving_swing"
            );

    public static final SkillPerk QUICK_HATCHET =
            new SkillPerk(
                    "quick_hatchet",
                    "Quick Hatchet",
                    "Your axe is back in position before your foe can recover.",
                    "Recover 20% faster between axe attacks.",
                    30,
                    220,
                    150,
                    "splinter_fighter_2"
            );

    public static final SkillPerk BATTLE_AXE_MASTERY =
            new SkillPerk(
                    "battle_axe_mastery",
                    "Battle Axe Mastery",
                    "Every edge, angle, and opening has become familiar.",
                    "Deal additional melee damage with axes.",
                    40,
                    205,
                    130,
                    "quick_hatchet"
            );

    public static final SkillPerk TWIGS_EVERYWHERE =
            new SkillPerk(
                    "twigs_everywhere",
                    "Twigs Everywhere",
                    "You start finding useful scraps in every clean chop.",
                    "Small chance to get extra sticks when breaking logs.",
                    10,
                    185,
                    325,
                    "splinter_fighter"
            );

    public static final SkillPerk GREEN_THUMB =
            new SkillPerk(
                    "green_thumb",
                    "Green Thumb",
                    "Nature rewards your careful hands with new growth.",
                    "Small chance to get extra saplings from logs or leaves.",
                    15,
                    265,
                    335,
                    "twigs_everywhere"
            );

    public static final SkillPerk APPLE_PICKER =
            new SkillPerk(
                    "apple_picker",
                    "Apple Picker",
                    "You know where the good branches hide their fruit.",
                    "Small chance for extra apples from oak and dark oak.",
                    12,
                    350,
                    325,
                    "green_thumb"
            );

    public static final SkillPerk NATURES_GIFT =
            new SkillPerk(
                    "natures_gift",
                    "Nature's Gift",
                    "The forest leaves small gifts in your path.",
                    "Small chance for an extra random nature reward when chopping logs.",
                    15,
                    420,
                    315,
                    "apple_picker"
            );

    public static final List<SkillPerk> ALL_PERKS =
            List.of(
                    SPLINTER_FIGHTER,
                    PROPER_GRIP,
                    TIMBER_TRAINING,
                    LUMBERJACKS_STANCE,
                    FRICTION_FIRE,
                    CLEAN_SWING,
                    FELLING_MOMENTUM,
                    RHYTHM_OF_THE_FOREST,
                    MASTER_ARBORIST,
                    REINFORCED_HAFT,
                    CALLUSED_HANDS,
                    SEASONED_HAFT,
                    VETERAN_WOODSMAN,
                    AXE_TRAINING,
                    HEAVY_CHOP,
                    CLEAVING_SWING,
                    DECAPITATION_CHANCE,
                    SPLINTER_FIGHTER_II,
                    QUICK_HATCHET,
                    BATTLE_AXE_MASTERY,
                    TWIGS_EVERYWHERE,
                    GREEN_THUMB,
                    APPLE_PICKER,
                    NATURES_GIFT
            );
}
