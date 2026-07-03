package com.trojan.proficiency.network;

import com.trojan.proficiency.ProficiencyMod;
import com.trojan.proficiency.SkillManager;
import com.trojan.proficiency.perk.PerkUnlockResult;
import com.trojan.proficiency.skill.SkillType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class SkillNetworking {

    private static final int MAX_ID_LENGTH = 64;

    private SkillNetworking() {
    }

    public static void registerServerHandlers() {

        ServerPlayNetworking.registerGlobalReceiver(
                PerkUnlockRequestPayload.TYPE,
                SkillNetworking::handlePerkUnlock
        );
        ServerPlayNetworking.registerGlobalReceiver(
                ToggleChangeRequestPayload.TYPE,
                SkillNetworking::handleToggleChange
        );
        ServerPlayNetworking.registerGlobalReceiver(
                PrestigeRequestPayload.TYPE,
                SkillNetworking::handlePrestige
        );
    }

    private static void handlePerkUnlock(
            PerkUnlockRequestPayload payload,
            ServerPlayNetworking.Context context
    ) {

        ServerPlayer player = context.player();
        SkillType skillType =
                getValidSkillType(payload.skillId());

        if (
                skillType == null
                        || !isValidId(payload.perkId())
        ) {

            ProficiencyMod.LOGGER.warn(
                    "Rejected invalid perk unlock request from {}",
                    player.getGameProfile().getName()
            );
            SkillManager.sendSkillState(player);
            return;
        }

        PerkUnlockResult result =
                SkillManager.unlockPerk(
                        player.getUUID(),
                        skillType,
                        payload.perkId()
                );

        player.sendSystemMessage(
                Component.literal(
                        (result.success() ? "\u00A76" : "\u00A7c")
                                + result.message()
                )
        );
        SkillManager.sendSkillState(player);
    }

    private static void handleToggleChange(
            ToggleChangeRequestPayload payload,
            ServerPlayNetworking.Context context
    ) {

        ServerPlayer player = context.player();
        SkillType skillType =
                getValidSkillType(payload.skillId());

        if (
                skillType == null
                        || !isValidId(payload.toggleId())
                        || !SkillManager.setToggle(
                        player.getUUID(),
                        skillType,
                        payload.toggleId(),
                        payload.desiredState()
                )
        ) {

            ProficiencyMod.LOGGER.warn(
                    "Rejected invalid toggle request from {}",
                    player.getGameProfile().getName()
            );
        }

        SkillManager.sendSkillState(player);
    }

    private static void handlePrestige(
            PrestigeRequestPayload payload,
            ServerPlayNetworking.Context context
    ) {

        ServerPlayer player = context.player();
        SkillType skillType = getValidSkillType(payload.skillId());

        if (
                skillType == null
                        || !SkillManager.prestigeSkill(player, skillType)
        ) {
            player.sendSystemMessage(Component.literal(
                    "\u00A7cThat skill is not ready to prestige."
            ));
            SkillManager.sendSkillState(player);
        }
    }

    private static SkillType getValidSkillType(String skillId) {

        if (!isValidId(skillId)) {
            return null;
        }

        return SkillType.fromId(skillId);
    }

    private static boolean isValidId(String id) {

        return id != null
                && !id.isBlank()
                && id.length() <= MAX_ID_LENGTH;
    }
}
