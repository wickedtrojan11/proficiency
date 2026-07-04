package com.trojan.proficiency.client;

import com.trojan.proficiency.util.OneHandedWeapons;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;

public final class OneHandedTooltip {

    private OneHandedTooltip() {
    }

    public static void register() {
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player == null) {
                return;
            }

            ItemStack mainHand = minecraft.player.getMainHandItem();
            ItemStack offHand = minecraft.player.getOffhandItem();
            boolean supportedItem = OneHandedWeapons.isSupported(stack);
            boolean supportedMainHand = OneHandedWeapons.isSupported(mainHand);
            boolean dualWield = supportedMainHand
                    && OneHandedWeapons.isSupported(offHand);
            boolean weaponAndShield = supportedMainHand
                    && offHand.getItem() instanceof ShieldItem;
            boolean heldItem = stack == mainHand || stack == offHand;
            boolean shieldItem = stack.getItem() instanceof ShieldItem;

            if (!supportedItem && !(shieldItem && weaponAndShield && heldItem)) {
                return;
            }

            boolean addedBonus = false;

            if (supportedItem
                    && ClientSkillState.hasOneHandedPerk(
                    minecraft.player.getUUID(),
                    "blade_training"
            )) {
                int masterySpeed = ClientSkillState.hasOneHandedPerk(
                        minecraft.player.getUUID(),
                        "monster_hunter"
                ) ? 10 : 5;
                addLine(lines, "Weapon Mastery: +" + masterySpeed + "% Attack Speed", ChatFormatting.GREEN);
                addedBonus = true;
            }

            if (supportedItem
                    && ClientSkillState.hasOneHandedPerk(
                    minecraft.player.getUUID(),
                    "precise_strikes"
            )) {
                int durability = ClientSkillState.hasOneHandedPerk(
                        minecraft.player.getUUID(),
                        "monster_hunter"
                ) ? 25 : 15;
                addLine(lines, "Weapon Durability: +" + durability + "% preservation", ChatFormatting.GRAY);
                addedBonus = true;
            }

            if (supportedItem
                    && ClientSkillState.hasOneHandedPerk(
                    minecraft.player.getUUID(),
                    "trophy_collector"
            )) {
                addLine(lines, "Weapon Mastery: +1 Looting", ChatFormatting.GOLD);
                addedBonus = true;
            }

            if (supportedItem
                    && offHand.isEmpty()
                    && ClientSkillState.hasOneHandedPerk(
                    minecraft.player.getUUID(),
                    "perfect_timing"
            )
                    && ClientSkillState.isOneHandedToggleEnabled("parry")) {
                addLine(lines, "Projectile Parry: Reflect during Parry", ChatFormatting.AQUA);
                addedBonus = true;
            }

            if (
                    supportedItem
                            && offHand.isEmpty()
                            && ClientSkillState.hasOneHandedPerk(
                            minecraft.player.getUUID(),
                            "duelists_focus"
                    )
            ) {
                addLine(lines, "Duelist: +0.5 Damage", ChatFormatting.GOLD);
                addedBonus = true;
            }

            if (
                    supportedItem
                            && offHand.isEmpty()
                            && ClientSkillState.hasOneHandedPerk(
                            minecraft.player.getUUID(),
                            "parry"
                    )
                            && ClientSkillState.isOneHandedToggleEnabled("parry")
            ) {
                addLine(lines, "Parry: +1 Armor", ChatFormatting.YELLOW);
                addedBonus = true;
            }

            if (
                    supportedItem
                            && dualWield
                            && heldItem
                            && ClientSkillState.hasOneHandedPerk(
                            minecraft.player.getUUID(),
                            "offhand_strike"
                    )
                            && ClientSkillState.isOneHandedToggleEnabled("dual_wield")
            ) {
                addLine(lines, "Bloodlust: +5% Attack Speed", ChatFormatting.LIGHT_PURPLE);
                addLine(lines, "Offhand Strike: Right-click to attack", ChatFormatting.LIGHT_PURPLE);
                addedBonus = true;
            }

            if (supportedItem
                    && dualWield
                    && heldItem
                    && ClientSkillState.isOneHandedToggleEnabled("dual_wield")) {
                if (ClientSkillState.hasOneHandedPerk(
                        minecraft.player.getUUID(),
                        "twin_blades"
                )) {
                    addLine(lines, "Reckless Assault: +25% Damage below half health", ChatFormatting.DARK_RED);
                    addedBonus = true;
                }
                if (ClientSkillState.hasOneHandedPerk(
                        minecraft.player.getUUID(),
                        "berserkers_rhythm"
                )) {
                    addLine(lines, "Adrenaline Rush: +20% Attack Speed after damage", ChatFormatting.RED);
                    addedBonus = true;
                }
                if (ClientSkillState.hasOneHandedPerk(
                        minecraft.player.getUUID(),
                        "blood_frenzy"
                )) {
                    addLine(lines, "Blood Frenzy: Heal 1-2 hearts on hostile kills", ChatFormatting.RED);
                    addedBonus = true;
                }
                if (ClientSkillState.hasOneHandedPerk(
                        minecraft.player.getUUID(),
                        "last_stand"
                )) {
                    addLine(lines, "Last Stand: 50% save chance at one heart", ChatFormatting.DARK_RED);
                    addedBonus = true;
                }
            }

            if (
                    weaponAndShield
                            && heldItem
                            && ClientSkillState.hasOneHandedPerk(
                            minecraft.player.getUUID(),
                            "shield_training"
                    )
                            && ClientSkillState.isOneHandedToggleEnabled("shield_effects")
            ) {
                addLine(lines, "Guardian: +2 Armor", ChatFormatting.AQUA);
                addedBonus = true;
            }

            if (
                    weaponAndShield
                            && heldItem
                            && ClientSkillState.isOneHandedToggleEnabled("shield_effects")
            ) {
                if (ClientSkillState.hasOneHandedPerk(
                        minecraft.player.getUUID(),
                        "guarded_strike"
                )) {
                    addLine(lines, "Guarded Strike: +1 Damage after blocking", ChatFormatting.BLUE);
                    addedBonus = true;
                }
                if (ClientSkillState.hasOneHandedPerk(
                        minecraft.player.getUUID(),
                        "shield_bash"
                )) {
                    addLine(lines, "Shield Bash: Right-click nearby enemies", ChatFormatting.BLUE);
                    addedBonus = true;
                }
                if (ClientSkillState.hasOneHandedPerk(
                        minecraft.player.getUUID(),
                        "bulwark"
                )) {
                    addLine(lines, "Bulwark: Knockback resistance while blocking", ChatFormatting.BLUE);
                    addedBonus = true;
                }
                if (ClientSkillState.hasOneHandedPerk(
                        minecraft.player.getUUID(),
                        "guardians_resolve"
                )) {
                    addLine(lines, "Guardian's Resolve: Heal and Resistance after repeated blocks", ChatFormatting.BLUE);
                    addedBonus = true;
                }
            }

            int prestige = ClientSkillState.getOneHandedPrestige(
                    minecraft.player.getUUID()
            );
            if (addedBonus && prestige > 0) {
                addLine(
                        lines,
                        "Prestige Bonus: +" + prestige + "%",
                        ChatFormatting.GREEN
                );
            }
        });
    }

    private static void addLine(
            java.util.List<Component> lines,
            String text,
            ChatFormatting color
    ) {
        lines.add(Component.literal(text).withStyle(color));
    }
}
