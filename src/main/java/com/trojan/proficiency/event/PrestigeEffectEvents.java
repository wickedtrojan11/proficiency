package com.trojan.proficiency.event;

import com.trojan.proficiency.ProficiencyMod;
import com.trojan.proficiency.SkillManager;
import com.trojan.proficiency.skill.SkillType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.PickaxeItem;

public final class PrestigeEffectEvents {

    private static final ResourceLocation BREAK_SPEED_ID =
            ResourceLocation.fromNamespaceAndPath(
                    ProficiencyMod.MOD_ID,
                    "prestige_break_speed"
            );

    private PrestigeEffectEvents() {
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                updateBreakSpeed(player);
            }
        });
    }

    private static void updateBreakSpeed(ServerPlayer player) {
        double amount = getMiningBreakSpeedBonus(player)
                + getWoodcuttingBreakSpeedBonus(player);
        AttributeInstance attribute = player.getAttribute(
                Attributes.BLOCK_BREAK_SPEED
        );

        if (attribute == null) {
            return;
        }

        AttributeModifier current = attribute.getModifier(BREAK_SPEED_ID);
        if (current != null && Math.abs(current.amount() - amount) < 0.000001) {
            return;
        }

        if (current != null) {
            attribute.removeModifier(BREAK_SPEED_ID);
        }

        if (amount > 0.0) {
            attribute.addTransientModifier(new AttributeModifier(
                    BREAK_SPEED_ID,
                    amount,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            ));
        }
    }

    private static double getMiningBreakSpeedBonus(ServerPlayer player) {
        if (!(player.getMainHandItem().getItem() instanceof PickaxeItem)) {
            return 0.0;
        }

        double basePercent = SkillManager.hasMiningPerk(
                player.getUUID(),
                "stonecutter"
        ) ? 15.0 : 0.0;
        int streak = SkillManager.getMiningStreak(player.getUUID());

        if (SkillManager.hasMiningPerk(player.getUUID(), "miners_momentum")) {
            basePercent = Math.max(
                    basePercent,
                    streak >= 50 ? 45.0 : streak >= 30 ? 30.0 : streak >= 10 ? 15.0 : 0.0
            );
        }

        return prestigeShare(player, SkillType.MINING, basePercent);
    }

    private static double getWoodcuttingBreakSpeedBonus(ServerPlayer player) {
        if (!(player.getMainHandItem().getItem() instanceof AxeItem)) {
            return 0.0;
        }

        double basePercent = 0.0;
        if (SkillManager.hasWoodcuttingPerk(player.getUUID(), "rhythm_of_the_forest")) {
            basePercent = 50.0;
        } else if (SkillManager.hasWoodcuttingPerk(player.getUUID(), "felling_momentum")) {
            basePercent = 35.0;
        } else if (SkillManager.hasWoodcuttingPerk(player.getUUID(), "clean_swing")) {
            basePercent = 25.0;
        } else if (SkillManager.hasWoodcuttingPerk(player.getUUID(), "lumberjacks_stance")) {
            basePercent = 15.0;
        } else if (SkillManager.hasWoodcuttingPerk(player.getUUID(), "timber_training")) {
            basePercent = 10.0;
        }

        return prestigeShare(player, SkillType.WOODCUTTING, basePercent);
    }

    private static double prestigeShare(
            ServerPlayer player,
            SkillType skillType,
            double basePercent
    ) {
        return basePercent / 100.0
                * SkillManager.getPrestige(player.getUUID(), skillType)
                / 100.0;
    }
}
