package com.trojan.proficiency.perk;
import java.util.HashMap;
import java.util.UUID;
import com.trojan.proficiency.SkillManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import java.util.Set;
import net.minecraft.core.BlockPos;

import net.minecraft.core.particles.ParticleTypes;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.level.block.Blocks;

public class OreSenseEffects {
    private static final HashMap<UUID, Integer>
            scanCooldowns = new HashMap<>();

    public static void register() {

        ServerTickEvents.END_SERVER_TICK.register(server -> {

            for (ServerPlayer player
                    : server.getPlayerList().getPlayers()) {

                if (
                        !SkillManager.hasMiningPerk(
                                player.getUUID(),
                                "they_have_a_scent"
                        )
                ) {
                    return;
                }

                ServerLevel level =
                        player.serverLevel();

                BlockPos playerPos =
                        player.blockPosition();

                int radius = 5;
                UUID playerId =
                        player.getUUID();

                int cooldown =
                        scanCooldowns.getOrDefault(
                                playerId,
                                0
                        );

                if (cooldown > 0) {

                    scanCooldowns.put(
                            playerId,
                            cooldown - 1
                    );

                    continue;
                }

                scanCooldowns.put(
                        playerId,
                        20
                );
                for (int x = -radius; x <= radius; x++) {

                    for (int y = -radius; y <= radius; y++) {

                        for (int z = -radius; z <= radius; z++) {

                            BlockPos checkPos =
                                    playerPos.offset(
                                            x,
                                            y,
                                            z
                                    );
                            BlockState state =
                                    level.getBlockState(checkPos);
                            boolean canSenseCoal =
                                    SkillManager.hasMiningPerk(
                                            player.getUUID(),
                                            "they_have_a_scent"
                                    );

                            boolean canSenseMidOres =
                                    SkillManager.hasMiningPerk(
                                            player.getUUID(),
                                            "it_smells_2"
                                    );

                            boolean canSenseRareOres =
                                    SkillManager.hasMiningPerk(
                                            player.getUUID(),
                                            "it_smells_3"
                                    );

                            boolean canSenseAncientDebris =
                                    SkillManager.hasMiningPerk(
                                            player.getUUID(),
                                            "it_smells_4"
                                    );


                            boolean shouldHighlight = false;

// =========================
// COAL + REDSTONE
// =========================

                            if (
                                    canSenseCoal
                                            &&
                                            SkillManager.isOreSelected(
                                                    player.getUUID(),
                                                    "coal"
                                            )
                                            &&
                                            (
                                                    state.is(Blocks.COAL_ORE)
                                                            || state.is(Blocks.DEEPSLATE_COAL_ORE)
                                                            || state.is(Blocks.REDSTONE_ORE)
                                                            || state.is(Blocks.DEEPSLATE_REDSTONE_ORE)
                                            )
                            ) {

                                shouldHighlight = true;
                            }

// =========================
// MID ORES
// =========================

                            else if (
                                    canSenseMidOres
                                            &&
                                            (
                                                    SkillManager.isOreSelected(
                                                            player.getUUID(),
                                                            "iron"
                                                    )
                                                            &&
                                                            (
                                                                    state.is(Blocks.IRON_ORE)
                                                                            || state.is(Blocks.DEEPSLATE_IRON_ORE)
                                                            )

                                                            ||

                                                            SkillManager.isOreSelected(
                                                                    player.getUUID(),
                                                                    "copper"
                                                            )
                                                                    &&
                                                                    (
                                                                            state.is(Blocks.COPPER_ORE)
                                                                                    || state.is(Blocks.DEEPSLATE_COPPER_ORE)
                                                                    )

                                                            ||

                                                            SkillManager.isOreSelected(
                                                                    player.getUUID(),
                                                                    "lapis"
                                                            )
                                                                    &&
                                                                    (
                                                                            state.is(Blocks.LAPIS_ORE)
                                                                                    || state.is(Blocks.DEEPSLATE_LAPIS_ORE)
                                                                    )
                                            )
                            ) {

                                shouldHighlight = true;
                            }

// =========================
// RARE ORES
// =========================

                            else if (
                                    canSenseRareOres
                                            &&
                                            (
                                                    SkillManager.isOreSelected(
                                                            player.getUUID(),
                                                            "diamond"
                                                    )
                                                            &&
                                                            (
                                                                    state.is(Blocks.DIAMOND_ORE)
                                                                            || state.is(Blocks.DEEPSLATE_DIAMOND_ORE)
                                                            )

                                                            ||

                                                            SkillManager.isOreSelected(
                                                                    player.getUUID(),
                                                                    "gold"
                                                            )
                                                                    &&
                                                                    (
                                                                            state.is(Blocks.GOLD_ORE)
                                                                                    || state.is(Blocks.DEEPSLATE_GOLD_ORE)
                                                                                    || state.is(Blocks.NETHER_GOLD_ORE)
                                                                    )

                                                            ||

                                                            SkillManager.isOreSelected(
                                                                    player.getUUID(),
                                                                    "emerald"
                                                            )
                                                                    &&
                                                                    (
                                                                            state.is(Blocks.EMERALD_ORE)
                                                                                    || state.is(Blocks.DEEPSLATE_EMERALD_ORE)
                                                                    )
                                            )
                            ) {

                                shouldHighlight = true;
                            }

// =========================
// ANCIENT DEBRIS
// =========================

                            else if (
                                    canSenseAncientDebris
                                            &&
                                            SkillManager.isOreSelected(
                                                    player.getUUID(),
                                                    "ancient_debris"
                                            )
                                            &&
                                            state.is(Blocks.ANCIENT_DEBRIS)
                            ) {

                                shouldHighlight = true;
                            }
                            if (shouldHighlight) {

                                if (level.random.nextInt(8) != 0) {
                                    continue;
                                }

                                level.sendParticles(
                                        ParticleTypes.GLOW,
                                        checkPos.getX() + 0.5,
                                        checkPos.getY() + 0.5,
                                        checkPos.getZ() + 0.5,
                                        2,
                                        1.5,
                                        1.5,
                                        1.5,
                                        0.001
                                );

                                level.sendParticles(
                                        ParticleTypes.END_ROD,
                                        checkPos.getX() + 0.5,
                                        checkPos.getY() + 0.5,
                                        checkPos.getZ() + 0.5,
                                        1,
                                        1.0,
                                        1.0,
                                        1.0,
                                        0.0
                                );
                            }
                        }
                    }
                }
            }
        });
    }
}