package com.trojan.proficiency.perk;

import java.util.List;
import java.util.Map;

public final class OneHandedPerks {

    public static final SkillPerk OFFHAND_STRIKE = perk("offhand_strike", "Bloodlust", "A weapon in each hand quickens your pulse.", "Gain attack speed and right-click to strike with your offhand weapon while dual wielding.", 5, 125, 325, null);
    public static final SkillPerk TWIN_BLADES = perk("twin_blades", "Reckless Assault", "Pain only makes you hit harder.", "Deal 25% more damage while below half health.", 12, 125, 265, "offhand_strike");
    public static final SkillPerk BERSERKERS_RHYTHM = perk("berserkers_rhythm", "Adrenaline Rush", "Every wound drives you forward.", "Taking damage grants 20% attack speed for 5 seconds.", 25, 125, 205, "twin_blades");
    public static final SkillPerk BLOOD_FRENZY = perk("blood_frenzy", "Blood Frenzy", "Victory feeds the fury.", "Killing a hostile mob restores 1-2 hearts and refreshes Adrenaline Rush.", 35, 125, 145, "berserkers_rhythm");
    public static final SkillPerk LAST_STAND = perk("last_stand", "Last Stand", "Death itself must win the toss.", "At one heart, incoming damage may trigger 5 seconds of doubled damage and 50% attack speed.", 40, 125, 95, "blood_frenzy");

    public static final SkillPerk DUELISTS_FOCUS = perk("duelists_focus", "Duelist's Focus", "A lone weapon leaves no room for wasted motion.", "Gain 0.5 melee damage with a supported weapon and empty offhand.", 5, 220, 325, null);
    public static final SkillPerk PARRY = perk("parry", "Parry", "Timing turns an enemy's strength aside.", "Right-click to briefly parry an incoming melee attack.", 12, 220, 265, "duelists_focus");
    public static final SkillPerk RIPOSTE = perk("riposte", "Riposte", "Every opening invites an answer.", "After a successful parry, your next one-handed hit deals bonus damage.", 25, 220, 205, "parry");
    public static final SkillPerk PERFECT_TIMING = perk("perfect_timing", "Projectile Parry", "Even a flying arrow leaves an opening.", "Reflect incoming projectiles while your Parry window is active.", 40, 220, 95, "riposte");

    public static final SkillPerk SHIELD_TRAINING = perk("shield_training", "Shield Training", "Weapon and shield move in practiced balance.", "Gain 2 armor and sometimes prevent shield durability loss.", 5, 315, 325, null);
    public static final SkillPerk GUARDED_STRIKE = perk("guarded_strike", "Guarded Strike", "Defense and offense share the same motion.", "After blocking, your next supported one-handed hit deals 1 bonus damage.", 12, 315, 265, "shield_training");
    public static final SkillPerk SHIELD_BASH = perk("shield_bash", "Shield Bash", "Your shield can make its own opening.", "Right-click to stagger and knock back a nearby enemy.", 25, 315, 205, "guarded_strike");
    public static final SkillPerk GUARDIANS_RESOLVE = perk("guardians_resolve", "Guardian's Resolve", "Every impact strengthens your resolve.", "Blocking several quick hits grants brief Resistance I.", 30, 335, 145, "shield_bash");
    public static final SkillPerk BULWARK = perk("bulwark", "Bulwark", "You hold the line when others cannot.", "Gain knockback resistance while actively blocking.", 40, 315, 95, "shield_bash");

    public static final SkillPerk BLADE_TRAINING = perk("blade_training", "Blade Training", "Every one-handed weapon teaches the same fundamentals.", "Gain 0.5 melee damage with supported one-handed weapons.", 5, 410, 325, null);
    public static final SkillPerk PRECISE_STRIKES = perk("precise_strikes", "Precise Strikes", "Accuracy matters more than force.", "Critical effects reserved for a future update.", 12, 410, 265, "blade_training");
    public static final SkillPerk MONSTER_HUNTER = perk("monster_hunter", "Monster Hunter", "You know where every creature is vulnerable.", "Monster-hunting effects reserved for a future update.", 25, 410, 205, "precise_strikes");
    public static final SkillPerk TROPHY_COLLECTOR = perk("trophy_collector", "Trophy Collector", "Every victory leaves a story behind.", "Loot capstone reserved for a future update.", 40, 410, 95, "monster_hunter");

    public static final List<SkillPerk> ALL_PERKS = List.of(
            OFFHAND_STRIKE, TWIN_BLADES, BERSERKERS_RHYTHM, BLOOD_FRENZY, LAST_STAND,
            DUELISTS_FOCUS, PARRY, RIPOSTE, PERFECT_TIMING,
            SHIELD_TRAINING, GUARDED_STRIKE, SHIELD_BASH, GUARDIANS_RESOLVE, BULWARK,
            BLADE_TRAINING, PRECISE_STRIKES, MONSTER_HUNTER, TROPHY_COLLECTOR
    );

    public static final Map<String, SkillPerk> PERKS_BY_ID =
            SkillPerk.indexById(ALL_PERKS);

    private OneHandedPerks() {
    }

    public static SkillPerk getById(String perkId) {
        return PERKS_BY_ID.get(perkId);
    }

    private static SkillPerk perk(String id, String name, String description,
            String effect, int level, int x, int y, String parentId) {
        return new SkillPerk(id, name, description, effect, level, x, y, parentId);
    }
}
