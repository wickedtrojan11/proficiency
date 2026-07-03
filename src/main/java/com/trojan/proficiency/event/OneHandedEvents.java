package com.trojan.proficiency.event;

import com.trojan.proficiency.SkillManager;
import com.trojan.proficiency.util.OneHandedWeapons;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;

public final class OneHandedEvents {

    private static final int DAMAGE_XP = 1;
    private static final int KILL_XP = 4;

    private OneHandedEvents() {
    }

    public static void register() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register(
                (entity, source, baseDamage, damageTaken, blocked) -> {
                    ServerPlayer player = getEligiblePlayer(entity, source);
                    if (player != null && !blocked && damageTaken > 0.0f) {
                        SkillManager.addOneHandedXp(player, DAMAGE_XP);
                    }
                }
        );

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            ServerPlayer player = getEligiblePlayer(entity, source);
            if (player != null) {
                SkillManager.addOneHandedXp(player, KILL_XP);
            }
        });
    }

    private static ServerPlayer getEligiblePlayer(
            LivingEntity target,
            DamageSource source
    ) {
        if (
                !(target instanceof Enemy)
                        || !(source.getEntity() instanceof ServerPlayer player)
                        || source.getDirectEntity() != player
                        || !OneHandedWeapons.isSupported(
                        player.getMainHandItem()
                )
        ) {
            return null;
        }
        return player;
    }
}
