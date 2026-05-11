package com.trojan.proficiency.event;

import com.trojan.proficiency.SkillManager;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;

import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;

public class WoodcuttingEvents {

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (state.is(BlockTags.LOGS)) {

                boolean leveledUp =
                        SkillManager.addWoodcuttingXp(player.getUUID(), 1);

                int xp =
                        SkillManager.getWoodcuttingXp(player.getUUID());

                int level =
                        SkillManager.getWoodcuttingLevel(player.getUUID());

                if (leveledUp) {

                    player.sendSystemMessage(
                            Component.literal(
                                    "§2Woodcutting Level Up! → Level " + level
                            )
                    );
                }
            }
        });
    }
}