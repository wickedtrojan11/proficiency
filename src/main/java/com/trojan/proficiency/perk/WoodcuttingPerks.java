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
                    245,
                    215,
                    null
            );

    public static final SkillPerk PROPER_GRIP =
            new SkillPerk(
                    "proper_grip",
                    "Proper Grip",
                    "You learn to stop wasting durability on bad strikes.",
                    "Axes lose 10% less durability.",
                    7,
                    315,
                    150,
                    "splinter_fighter"
            );

    public static final SkillPerk TIMBER_TRAINING =
            new SkillPerk(
                    "timber_training",
                    "Timber Training",
                    "You start reading the grain before each swing.",
                    "+10% chopping speed.",
                    8,
                    245,
                    180,
                    "splinter_fighter"
            );

    public static final SkillPerk LUMBERJACKS_STANCE =
            new SkillPerk(
                    "lumberjacks_stance",
                    "Lumberjack's Stance",
                    "Your feet settle before the axe falls.",
                    "+15% chopping speed total.",
                    12,
                    245,
                    145,
                    "timber_training"
            );

    public static final SkillPerk REINFORCED_HAFT =
            new SkillPerk(
                    "reinforced_haft",
                    "Reinforced Haft",
                    "A stronger handle keeps every swing under control.",
                    "Axes lose 20% less durability.",
                    12,
                    315,
                    110,
                    "proper_grip"
            );

    public static final SkillPerk TWIGS_EVERYWHERE =
            new SkillPerk(
                    "twigs_everywhere",
                    "Twigs Everywhere",
                    "You start finding useful scraps in every clean chop.",
                    "Small chance to get extra sticks when breaking logs.",
                    10,
                    165,
                    320,
                    "splinter_fighter"
            );

    public static final SkillPerk GREEN_THUMB =
            new SkillPerk(
                    "green_thumb",
                    "Green Thumb",
                    "Nature rewards your careful hands with new growth.",
                    "Small chance to get extra saplings from logs or leaves.",
                    15,
                    245,
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
                    310,
                    325,
                    "green_thumb"
            );

    public static final SkillPerk NATURES_GIFT =
            new SkillPerk(
                    "natures_gift",
                    "Nature's Gift",
                    "The forest leaves small gifts in your path.",
                    "Small chance for an extra random nature reward when chopping logs.",
                    25,
                    360,
                    275,
                    "apple_picker"
            );

    public static final List<SkillPerk> ALL_PERKS =
            List.of(
                    SPLINTER_FIGHTER,
                    PROPER_GRIP,
                    TIMBER_TRAINING,
                    LUMBERJACKS_STANCE,
                    REINFORCED_HAFT,
                    TWIGS_EVERYWHERE,
                    GREEN_THUMB,
                    APPLE_PICKER,
                    NATURES_GIFT
            );
}
