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

            if (
                    supportedItem
                            && ClientSkillState.hasOneHandedPerk(
                            minecraft.player.getUUID(),
                            "blade_training"
                    )
            ) {
                addLine(lines, "One-Handed: +0.5 Damage", ChatFormatting.RED);
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
                addLine(lines, "Berserker: +5% Attack Speed", ChatFormatting.LIGHT_PURPLE);
                addedBonus = true;
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
