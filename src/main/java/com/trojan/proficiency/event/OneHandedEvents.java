package com.trojan.proficiency.event;

import com.trojan.proficiency.SkillManager;
import com.trojan.proficiency.util.OneHandedWeapons;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import com.trojan.proficiency.ProficiencyMod;
import com.trojan.proficiency.skill.SkillType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.particles.ParticleTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class OneHandedEvents {

    private static final int DAMAGE_XP = 1;
    private static final int KILL_XP = 4;
    private static final int PARRY_WINDOW_TICKS = 8;
    private static final int PARRY_COOLDOWN_TICKS = 40;
    private static final int RIPOSTE_TIMEOUT_TICKS = 100;
    private static final float RIPOSTE_BONUS_DAMAGE = 2.0f;
    private static final ResourceLocation DAMAGE_MODIFIER_ID = id("one_handed_damage");
    private static final ResourceLocation SPEED_MODIFIER_ID = id("one_handed_speed");
    private static final ResourceLocation DEFENSE_MODIFIER_ID = id("one_handed_defense");
    private static final Map<UUID, Integer> PARRY_WINDOWS = new HashMap<>();
    private static final Map<UUID, Integer> PARRY_COOLDOWNS = new HashMap<>();
    private static final Map<UUID, Integer> RIPOSTE_WINDOWS = new HashMap<>();
    private static boolean applyingRiposteDamage;

    private OneHandedEvents() {
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                updateLoadoutEffects(player);
            }
            int tick = server.getTickCount();
            PARRY_WINDOWS.entrySet().removeIf(entry -> entry.getValue() < tick);
            PARRY_COOLDOWNS.entrySet().removeIf(entry -> entry.getValue() < tick);
            RIPOSTE_WINDOWS.entrySet().removeIf(entry -> entry.getValue() < tick);
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (!(player instanceof ServerPlayer serverPlayer)) {
                return InteractionResultHolder.pass(stack);
            }

            if (tryActivateParry(serverPlayer, hand)) {
                return InteractionResultHolder.success(stack);
            }
            return InteractionResultHolder.pass(stack);
        });

        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) ->
                !tryParryDamage(entity, source)
        );

        ServerLivingEntityEvents.AFTER_DAMAGE.register(
                (entity, source, baseDamage, damageTaken, blocked) -> {
                    if (applyingRiposteDamage) {
                        return;
                    }
                    ServerPlayer attacker = getOneHandedAttacker(source);
                    if (attacker != null && !blocked && damageTaken > 0.0f) {
                        tryApplyRiposte(attacker, entity);
                    }
                    ServerPlayer player = getEligiblePlayer(entity, source);
                    if (player != null && !blocked && damageTaken > 0.0f) {
                        SkillManager.addOneHandedXp(player, DAMAGE_XP);
                    }
                }
        );

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            ServerPlayer attacker = getOneHandedAttacker(source);
            if (attacker != null) {
                RIPOSTE_WINDOWS.remove(attacker.getUUID());
            }
            ServerPlayer player = getEligiblePlayer(entity, source);
            if (player != null) {
                SkillManager.addOneHandedXp(player, KILL_XP);
            }
        });
    }

    private static boolean tryActivateParry(
            ServerPlayer player,
            net.minecraft.world.InteractionHand hand
    ) {
        if (
                hand != net.minecraft.world.InteractionHand.MAIN_HAND
                        || !hasDuelistLoadout(player)
                        || !SkillManager.hasOneHandedPerk(
                        player.getUUID(),
                        "parry"
                )
                        || !isToggleEnabled(player, "parry")
        ) {
            return false;
        }

        int tick = player.getServer().getTickCount();
        if (PARRY_COOLDOWNS.getOrDefault(player.getUUID(), 0) > tick) {
            return false;
        }

        PARRY_WINDOWS.put(player.getUUID(), tick + PARRY_WINDOW_TICKS);
        PARRY_COOLDOWNS.put(player.getUUID(), tick + PARRY_COOLDOWN_TICKS);
        player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.ARMOR_EQUIP_IRON.value(),
                SoundSource.PLAYERS,
                0.3f,
                1.7f
        );
        player.serverLevel().sendParticles(
                ParticleTypes.ENCHANT,
                player.getX(),
                player.getY() + 1.0,
                player.getZ(),
                3,
                0.2,
                0.3,
                0.2,
                0.01
        );
        return true;
    }

    private static boolean tryParryDamage(
            LivingEntity target,
            DamageSource source
    ) {
        if (
                !(target instanceof ServerPlayer player)
                        || !(source.getEntity() instanceof LivingEntity attacker)
                        || source.getDirectEntity() != attacker
                        || !hasDuelistLoadout(player)
                        || !SkillManager.hasOneHandedPerk(
                        player.getUUID(),
                        "parry"
                )
                        || !isToggleEnabled(player, "parry")
        ) {
            return false;
        }

        int tick = player.getServer().getTickCount();
        if (PARRY_WINDOWS.getOrDefault(player.getUUID(), 0) < tick) {
            return false;
        }

        PARRY_WINDOWS.remove(player.getUUID());
        if (SkillManager.hasOneHandedPerk(player.getUUID(), "riposte")) {
            RIPOSTE_WINDOWS.put(
                    player.getUUID(),
                    tick + RIPOSTE_TIMEOUT_TICKS
            );
        }

        player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.SHIELD_BLOCK,
                SoundSource.PLAYERS,
                0.6f,
                1.4f
        );
        player.serverLevel().sendParticles(
                ParticleTypes.CRIT,
                player.getX(),
                player.getY() + 1.0,
                player.getZ(),
                8,
                0.35,
                0.4,
                0.35,
                0.05
        );
        return true;
    }

    private static void tryApplyRiposte(
            ServerPlayer player,
            LivingEntity target
    ) {
        int tick = player.getServer().getTickCount();
        if (
                RIPOSTE_WINDOWS.getOrDefault(player.getUUID(), 0) < tick
                        || !OneHandedWeapons.isSupported(
                        player.getMainHandItem()
                )
        ) {
            return;
        }

        RIPOSTE_WINDOWS.remove(player.getUUID());
        applyingRiposteDamage = true;
        try {
            target.hurt(
                    player.damageSources().playerAttack(player),
                    (float) scale(player, RIPOSTE_BONUS_DAMAGE)
            );
        } finally {
            applyingRiposteDamage = false;
        }

        player.level().playSound(
                null,
                target.blockPosition(),
                SoundEvents.PLAYER_ATTACK_CRIT,
                SoundSource.PLAYERS,
                0.5f,
                1.2f
        );
        player.serverLevel().sendParticles(
                ParticleTypes.CRIT,
                target.getX(),
                target.getY() + target.getBbHeight() * 0.5,
                target.getZ(),
                10,
                0.3,
                0.3,
                0.3,
                0.1
        );
    }

    private static boolean hasDuelistLoadout(ServerPlayer player) {
        return OneHandedWeapons.isSupported(player.getMainHandItem())
                && player.getOffhandItem().isEmpty();
    }

    private static void updateLoadoutEffects(ServerPlayer player) {
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        boolean supportedMainHand = OneHandedWeapons.isSupported(mainHand);
        boolean emptyOffhand = offHand.isEmpty();
        boolean dualWield = supportedMainHand
                && OneHandedWeapons.isSupported(offHand);
        boolean weaponAndShield = supportedMainHand
                && offHand.getItem() instanceof ShieldItem;

        double damageBonus = 0.0;
        double speedBonus = 0.0;
        double defenseBonus = 0.0;

        if (
                supportedMainHand
                        && SkillManager.hasOneHandedPerk(
                        player.getUUID(),
                        "blade_training"
                )
        ) {
            damageBonus += 0.5;
        }

        if (
                supportedMainHand
                        && emptyOffhand
                        && SkillManager.hasOneHandedPerk(
                        player.getUUID(),
                        "duelists_focus"
                )
        ) {
            damageBonus += 0.5;
        }

        if (
                supportedMainHand
                        && emptyOffhand
                        && SkillManager.hasOneHandedPerk(
                        player.getUUID(),
                        "parry"
                )
                        && isToggleEnabled(player, "parry")
        ) {
            defenseBonus += 1.0;
        }

        if (
                weaponAndShield
                        && SkillManager.hasOneHandedPerk(
                        player.getUUID(),
                        "shield_training"
                )
                        && isToggleEnabled(player, "shield_effects")
        ) {
            defenseBonus += 2.0;
        }

        if (
                dualWield
                        && SkillManager.hasOneHandedPerk(
                        player.getUUID(),
                        "offhand_strike"
                )
                        && isToggleEnabled(player, "dual_wield")
        ) {
            speedBonus += 0.05;
        }

        damageBonus = scale(player, damageBonus);
        speedBonus = scale(player, speedBonus);
        defenseBonus = scale(player, defenseBonus);

        updateModifier(
                player.getAttribute(Attributes.ATTACK_DAMAGE),
                DAMAGE_MODIFIER_ID,
                damageBonus,
                AttributeModifier.Operation.ADD_VALUE
        );
        updateModifier(
                player.getAttribute(Attributes.ATTACK_SPEED),
                SPEED_MODIFIER_ID,
                speedBonus,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
        updateModifier(
                player.getAttribute(Attributes.ARMOR),
                DEFENSE_MODIFIER_ID,
                defenseBonus,
                AttributeModifier.Operation.ADD_VALUE
        );
    }

    private static boolean isToggleEnabled(
            ServerPlayer player,
            String toggleId
    ) {
        return SkillManager.isOneHandedToggleEnabled(
                player.getUUID(),
                toggleId
        );
    }

    private static double scale(ServerPlayer player, double value) {
        return SkillManager.scalePerkValue(
                player.getUUID(),
                SkillType.ONE_HANDED,
                value
        );
    }

    private static void updateModifier(
            AttributeInstance attribute,
            ResourceLocation id,
            double amount,
            AttributeModifier.Operation operation
    ) {
        if (attribute == null) {
            return;
        }

        AttributeModifier current = attribute.getModifier(id);
        if (
                current != null
                        && Math.abs(current.amount() - amount) < 0.000001
        ) {
            return;
        }
        if (current != null) {
            attribute.removeModifier(id);
        }
        if (amount > 0.0) {
            attribute.addTransientModifier(
                    new AttributeModifier(id, amount, operation)
            );
        }
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(
                ProficiencyMod.MOD_ID,
                path
        );
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

    private static ServerPlayer getOneHandedAttacker(DamageSource source) {
        if (
                !(source.getEntity() instanceof ServerPlayer player)
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
