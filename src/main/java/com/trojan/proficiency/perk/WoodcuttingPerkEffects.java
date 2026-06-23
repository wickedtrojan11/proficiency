package com.trojan.proficiency.perk;

import com.trojan.proficiency.SkillManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;

public class WoodcuttingPerkEffects {

    public static void register() {

        ServerTickEvents.END_SERVER_TICK.register(server -> {

            for (ServerPlayer player
                    : server.getPlayerList().getPlayers()) {

                ItemStack heldItem =
                        player.getMainHandItem();

                boolean holdingAxe =
                        heldItem.getItem()
                                instanceof AxeItem;

                if (
                        holdingAxe
                                && SkillManager.hasWoodcuttingPerk(
                                        player.getUUID(),
                                        "splinter_fighter"
                                )
                ) {

                    player.addEffect(
                            new MobEffectInstance(
                                    MobEffects.DAMAGE_BOOST,
                                    40,
                                    0,
                                    false,
                                    false,
                                    true
                            )
                    );
                }

                if (
                        holdingAxe
                                && (
                                SkillManager.hasWoodcuttingPerk(
                                        player.getUUID(),
                                        "timber_training"
                                )
                                        || SkillManager.hasWoodcuttingPerk(
                                        player.getUUID(),
                                        "lumberjacks_stance"
                                )
                        )
                ) {

                    player.addEffect(
                            new MobEffectInstance(
                                    MobEffects.DIG_SPEED,
                                    40,
                                    0,
                                    false,
                                    false,
                                    true
                            )
                    );
                }
            }
        });
    }
}
