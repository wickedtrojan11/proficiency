package com.trojan.proficiency.event;

import com.trojan.proficiency.SkillManager;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;

import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;

import java.util.Random;
import net.minecraft.network.chat.Component;
public class MiningDurabilityEvents {

    private static final Random RANDOM =
            new Random();

    public static void register() {

        PlayerBlockBreakEvents.AFTER.register((
                world,
                player,
                pos,
                state,
                blockEntity
        ) -> {

            if (!(player instanceof ServerPlayer serverPlayer)) {
                return;
            }

            ItemStack heldItem =
                    serverPlayer.getMainHandItem();

            if (!(heldItem.getItem()
                    instanceof PickaxeItem)) {
                return;
            }

            float saveChance = 0.0f;


// =========================
// NEARLY INDESTRUCTIBLE
// =========================

            if (
                    SkillManager.hasMiningPerk(
                            serverPlayer.getUUID(),
                            "nearly_indestructible"
                    )
            ) {

                saveChance = 0.75f;
            }

// =========================
// TEMPERED TOOLS
// =========================

            else if (
                    SkillManager.hasMiningPerk(
                            serverPlayer.getUUID(),
                            "tempered_tools"
                    )
            ) {

                saveChance = 0.40f;
            }

// =========================
// REINFORCED GRIP
// =========================

            else if (
                    SkillManager.hasMiningPerk(
                            serverPlayer.getUUID(),
                            "reinforced_grip"
                    )
            ) {

                saveChance = 0.25f;
            }

// =========================
// BETTER HANDLING
// =========================

            else if (
                    SkillManager.hasMiningPerk(
                            serverPlayer.getUUID(),
                            "better_handling"
                    )
            ) {

                saveChance = 0.10f;
            }

            // =========================
            // APPLY DURABILITY SAVE
            // =========================

            if (saveChance > 0.0f) {

                if (RANDOM.nextFloat()
                        <= saveChance) {
                    serverPlayer.serverLevel().sendParticles(
                            net.minecraft.core.particles.ParticleTypes.ENCHANT,
                            serverPlayer.getX(),
                            serverPlayer.getY() + 1.0,
                            serverPlayer.getZ(),
                            4,
                            0.2,
                            0.3,
                            0.2,
                            0.01
                    );
                    int damage =
                            heldItem.getDamageValue();

                    if (damage > 0) {

                        heldItem.setDamageValue(
                                damage - 1
                        );
                    }
                }
            }
        });
    }
}