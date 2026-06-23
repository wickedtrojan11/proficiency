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
                    260,
                    320,
                    null
            );

    public static final SkillPerk PROPER_GRIP =
            new SkillPerk(
                    "proper_grip",
                    "Proper Grip",
                    "You learn to stop wasting durability on bad strikes.",
                    "Axes lose 10% less durability.",
                    7,
                    220,
                    280,
                    "splinter_fighter"
            );

    public static final SkillPerk TWIGS_EVERYWHERE =
            new SkillPerk(
                    "twigs_everywhere",
                    "Twigs Everywhere",
                    "You start finding useful scraps in every clean chop.",
                    "Small chance to get extra sticks when breaking logs.",
                    10,
                    300,
                    280,
                    "splinter_fighter"
            );

    public static final SkillPerk GREEN_THUMB =
            new SkillPerk(
                    "green_thumb",
                    "Green Thumb",
                    "Nature rewards your careful hands with new growth.",
                    "Small chance to get extra saplings from logs or leaves.",
                    15,
                    335,
                    240,
                    "twigs_everywhere"
            );

    public static final List<SkillPerk> ALL_PERKS =
            List.of(
                    SPLINTER_FIGHTER,
                    PROPER_GRIP,
                    TWIGS_EVERYWHERE,
                    GREEN_THUMB
            );
}
