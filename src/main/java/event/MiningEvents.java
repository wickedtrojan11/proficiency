package com.trojan.proficiency.event;

import com.trojan.proficiency.SkillManager;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Blocks;

public class MiningEvents {

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (state.is(Blocks.STONE)) {

                boolean leveledUp =
                        SkillManager.addMiningXp(player.getUUID(), 1);

                int xp =
                        SkillManager.getMiningXp(player.getUUID());

                int level =
                        SkillManager.getMiningLevel(player.getUUID());

                if (leveledUp) {

                    player.sendSystemMessage(
                            Component.literal(
                                    "§6Mining Level Up! → Level " + level
                            )
                    );
                }

                // Mining bonus
                if (level >= 5) {

                    player.addEffect(
                            new MobEffectInstance(
                                    MobEffects.DIG_SPEED,
                                    40,
                                    0,
                                    false,
                                    false
                            )
                    );
                }
            }
        });
    }
}