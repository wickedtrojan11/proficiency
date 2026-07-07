package com.trojan.proficiency.client;

import com.trojan.proficiency.item.ModItems;
import com.trojan.proficiency.item.AlchemyIngredientRegistry;
import com.trojan.proficiency.item.OilRegistry;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class AlchemyTooltip {

    private AlchemyTooltip() {
    }

    public static void register() {
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            OilRegistry.appendAppliedOilTooltip(stack, lines);
            appendOilItemUnlockTooltip(stack, lines);
            appendIngredientKnowledge(stack, lines);

            Minecraft minecraft = Minecraft.getInstance();
            if (
                    minecraft.player == null
                            || (
                            !stack.is(ModItems.DOUBLE_XP_POTION)
                                    && !stack.is(ModItems.TRIPLE_XP_POTION)
                    )
            ) {
                return;
            }

            lines.add(Component.literal(
                    "Your honey extension cap: +"
                            + getUnlockedExtensionMinutes()
                            + " minutes"
            ).withStyle(ChatFormatting.GOLD));
        });
    }

    private static int getUnlockedExtensionMinutes() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return 0;
        }

        if (ClientSkillState.hasAlchemyPerk(
                minecraft.player.getUUID(),
                "eternal_draught"
        )) {
            return 10;
        }
        if (ClientSkillState.hasAlchemyPerk(
                minecraft.player.getUUID(),
                "perfect_suspension"
        )) {
            return 8;
        }
        if (ClientSkillState.hasAlchemyPerk(
                minecraft.player.getUUID(),
                "deep_binding"
        )) {
            return 6;
        }
        if (ClientSkillState.hasAlchemyPerk(
                minecraft.player.getUUID(),
                "long_steep"
        )) {
            return 4;
        }
        if (ClientSkillState.hasAlchemyPerk(
                minecraft.player.getUUID(),
                "sweetened_stability"
        )) {
            return 2;
        }

        return 0;
    }

    private static void appendIngredientKnowledge(
            ItemStack stack,
            java.util.List<Component> lines
    ) {
        AlchemyIngredientRegistry.Entry ingredient =
                AlchemyIngredientRegistry.get(stack);
        if (ingredient == null) {
            return;
        }

        if (ClientSkillState.hasDiscoveredAlchemyIngredient(
                ingredient.key()
        )) {
            lines.add(Component.literal("Known effect: ")
                    .withStyle(ChatFormatting.LIGHT_PURPLE)
                    .append(ingredient.knownEffect().copy()
                            .withStyle(ChatFormatting.GRAY)));
            for (String knownUse : ingredient.knownUses()) {
                lines.add(Component.literal("Known use: ")
                        .withStyle(ChatFormatting.LIGHT_PURPLE)
                        .append(Component.literal(knownUse)
                                .withStyle(ChatFormatting.GRAY)));
            }
            return;
        }

        lines.add(Component.literal(
                "Unknown alchemical properties"
        ).withStyle(ChatFormatting.DARK_PURPLE));
    }

    private static void appendOilItemUnlockTooltip(
            ItemStack stack,
            java.util.List<Component> lines
    ) {
        OilRegistry.Entry oil = OilRegistry.getByItem(stack.getItem());
        if (oil == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        if (!ClientSkillState.hasAlchemyPerk(
                minecraft.player.getUUID(),
                oil.requiredPerkId()
        )) {
            lines.add(Component.literal(
                    "Locked: unlock " + oil.displayName() + " in Alchemy."
            ).withStyle(ChatFormatting.DARK_RED));
            return;
        }

        lines.add(Component.literal(
                "Charges: " + (ClientSkillState.hasAlchemyPerk(
                        minecraft.player.getUUID(),
                        "perfect_coating"
                ) ? "500" : "250")
        ).withStyle(ChatFormatting.GOLD));
        lines.addAll(oil.tooltip());
    }
}
