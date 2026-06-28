package com.trojan.proficiency.perk;

import com.trojan.proficiency.SkillManager;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LightLayer;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class MiningPerkEffects {

    public static void register() {

        ServerTickEvents.END_SERVER_TICK.register(server -> {

            for (ServerPlayer player
                    : server.getPlayerList().getPlayers()) {

                ItemStack heldItem =
                        player.getMainHandItem();

                boolean holdingPickaxe =
                        heldItem.getItem()
                                instanceof PickaxeItem;

                // =========================
                // ITS A.. WEAPON?
                // =========================

                boolean hasWeaponPerk =
                        SkillManager.hasMiningPerk(
                                player.getUUID(),
                                "its_a_weapon"
                        );

                if (holdingPickaxe && hasWeaponPerk) {

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

                // =========================
                // STONECUTTER
                // =========================

                boolean hasStonecutter =
                        SkillManager.hasMiningPerk(
                                player.getUUID(),
                                "stonecutter"
                        );

                if (holdingPickaxe && hasStonecutter) {

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

                // =========================
                // DEEP DELVER
                // =========================

                boolean hasDeepDelver =
                        SkillManager.hasMiningPerk(
                                player.getUUID(),
                                "deep_delver"
                        );

                if (
                        hasDeepDelver
                                && isInsideCave(player)
                ) {

                    player.addEffect(
                            new MobEffectInstance(
                                    MobEffects.NIGHT_VISION,
                                    260,
                                    0,
                                    false,
                                    false,
                                    true
                            )
                    );
                }

                if (
                        !holdingPickaxe
                                && SkillManager.getMiningStreak(
                                player.getUUID()
                        ) > 0
                ) {

                    SkillManager.resetMiningStreak(
                            player.getUUID()
                    );
                    SkillManager.sendSkillState(player);
                }
            }

        });

    }

    private static boolean isInsideCave(
            ServerPlayer player
    ) {

        int skyLight =
                player.serverLevel()
                        .getBrightness(
                                LightLayer.SKY,
                                player.blockPosition()
                        );

        boolean belowSeaLevel =
                player.getY()
                        < player.serverLevel()
                                .getSeaLevel();

        return belowSeaLevel
                && skyLight <= 4;
    }

}
