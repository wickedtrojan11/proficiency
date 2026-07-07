package com.trojan.proficiency.client;

import com.trojan.proficiency.item.ModItems;
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
}
