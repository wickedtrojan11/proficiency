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
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;
import com.trojan.proficiency.network.ParryVisualPayload;

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
    private static final int GUARDED_STRIKE_TIMEOUT_TICKS = 100;
    private static final float GUARDED_STRIKE_BONUS_DAMAGE = 1.0f;
    private static final int SHIELD_BASH_COOLDOWN_TICKS = 60;
    private static final double SHIELD_BASH_RANGE = 3.0;
    private static final float SHIELD_TRAINING_SAVE_CHANCE = 0.20f;
    private static final int RESOLVE_BLOCK_WINDOW_TICKS = 80;
    private static final int RESOLVE_REQUIRED_BLOCKS = 3;
    private static final int RESOLVE_DURATION_TICKS = 60;
    private static final float RESOLVE_HEAL_AMOUNT = 1.0f;
    private static final int ADRENALINE_DURATION_TICKS = 100;
    private static final double BLOODLUST_SPEED_BONUS = 0.05;
    private static final double RECKLESS_DAMAGE_BONUS = 0.25;
    private static final double ADRENALINE_SPEED_BONUS = 0.20;
    private static final float BLOOD_FRENZY_MIN_HEAL = 2.0f;
    private static final float BLOOD_FRENZY_MAX_HEAL = 4.0f;
    private static final float LAST_STAND_HEALTH_THRESHOLD = 2.0f;
    private static final float LAST_STAND_SAVE_CHANCE = 0.50f;
    private static final int BERSERK_DURATION_TICKS = 100;
    private static final double BERSERK_DAMAGE_BONUS = 1.0;
    private static final double BERSERK_SPEED_BONUS = 0.50;
    private static final double OFFHAND_STRIKE_RANGE = 3.0;
    private static final double OFFHAND_DAMAGE_MULTIPLIER = 0.70;
    private static final int MIN_OFFHAND_COOLDOWN_TICKS = 6;
    private static final double MASTERY_SPEED_BONUS = 0.05;
    private static final double ADVANCED_MASTERY_SPEED_BONUS = 0.05;
    private static final float MASTERY_DURABILITY_CHANCE = 0.15f;
    private static final float ADVANCED_DURABILITY_CHANCE = 0.25f;
    private static final ResourceLocation DAMAGE_MODIFIER_ID = id("one_handed_damage");
    private static final ResourceLocation SPEED_MODIFIER_ID = id("one_handed_speed");
    private static final ResourceLocation DEFENSE_MODIFIER_ID = id("one_handed_defense");
    private static final ResourceLocation BULWARK_MODIFIER_ID = id("one_handed_bulwark");
    private static final Map<UUID, Integer> PARRY_WINDOWS = new HashMap<>();
    private static final Map<UUID, Integer> PARRY_COOLDOWNS = new HashMap<>();
    private static final Map<UUID, Integer> RIPOSTE_WINDOWS = new HashMap<>();
    private static final Map<UUID, Integer> GUARDED_STRIKE_WINDOWS = new HashMap<>();
    private static final Map<UUID, Integer> SHIELD_BASH_COOLDOWNS = new HashMap<>();
    private static final Map<UUID, Integer> RESOLVE_BLOCK_COUNTS = new HashMap<>();
    private static final Map<UUID, Integer> RESOLVE_WINDOWS = new HashMap<>();
    private static final Map<UUID, Integer> ADRENALINE_WINDOWS = new HashMap<>();
    private static final Map<UUID, Integer> BERSERK_WINDOWS = new HashMap<>();
    private static final Map<UUID, Integer> OFFHAND_STRIKE_COOLDOWNS = new HashMap<>();
    private static final Map<UUID, PendingDurabilityRefund> PENDING_DURABILITY_REFUNDS =
            new HashMap<>();
    private static boolean applyingRiposteDamage;
    private static boolean applyingGuardedStrikeDamage;
    private static boolean applyingOffhandStrikeDamage;

    private OneHandedEvents() {
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                applyPendingDurabilityRefund(player);
                updateLoadoutEffects(player);
            }
            int tick = server.getTickCount();
            PARRY_WINDOWS.entrySet().removeIf(entry -> entry.getValue() < tick);
            PARRY_COOLDOWNS.entrySet().removeIf(entry -> entry.getValue() < tick);
            RIPOSTE_WINDOWS.entrySet().removeIf(entry -> entry.getValue() < tick);
            GUARDED_STRIKE_WINDOWS.entrySet().removeIf(entry -> entry.getValue() < tick);
            SHIELD_BASH_COOLDOWNS.entrySet().removeIf(entry -> entry.getValue() < tick);
            RESOLVE_WINDOWS.entrySet().removeIf(entry -> entry.getValue() < tick);
            ADRENALINE_WINDOWS.entrySet().removeIf(entry -> entry.getValue() < tick);
            BERSERK_WINDOWS.entrySet().removeIf(entry -> entry.getValue() < tick);
            OFFHAND_STRIKE_COOLDOWNS.entrySet().removeIf(
                    entry -> entry.getValue() < tick
            );
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (!(player instanceof ServerPlayer serverPlayer)) {
                return InteractionResultHolder.pass(stack);
            }

            if (tryActivateParry(serverPlayer, hand)) {
                return InteractionResultHolder.success(stack);
            }
            if (tryShieldBash(serverPlayer)) {
                return InteractionResultHolder.success(stack);
            }
            return InteractionResultHolder.pass(stack);
        });

        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) ->
                !tryParryDamage(entity, source)
                        && !tryLastStand(entity)
        );

        ServerLivingEntityEvents.AFTER_DAMAGE.register(
                (entity, source, baseDamage, damageTaken, blocked) -> {
                    if (entity instanceof ServerPlayer defender && blocked) {
                        handleGuardianBlock(defender);
                    }
                    if (entity instanceof ServerPlayer defender
                            && !blocked
                            && damageTaken > 0.0f) {
                        activateAdrenaline(defender);
                    }
                    if (applyingRiposteDamage || applyingGuardedStrikeDamage) {
                        return;
                    }
                    ServerPlayer attacker = getOneHandedAttacker(source);
                    if (attacker != null && !blocked && damageTaken > 0.0f) {
                        if (!applyingOffhandStrikeDamage) {
                            scheduleDurabilityRefund(attacker);
                        }
                        tryApplyRiposte(attacker, entity);
                        tryApplyGuardedStrike(attacker, entity);
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
                GUARDED_STRIKE_WINDOWS.remove(attacker.getUUID());
                handleBloodFrenzy(attacker, entity);
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
        ParryVisualPayload.send(player, PARRY_WINDOW_TICKS);
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

    public static boolean tryProjectileParry(
            Projectile projectile,
            ServerPlayer player
    ) {
        if (projectile.level().isClientSide
                || !hasDuelistLoadout(player)
                || !SkillManager.hasOneHandedPerk(
                player.getUUID(),
                "perfect_timing"
        )
                || !isToggleEnabled(player, "parry")) {
            return false;
        }

        int tick = player.getServer().getTickCount();
        if (PARRY_WINDOWS.getOrDefault(player.getUUID(), 0) < tick) {
            return false;
        }

        PARRY_WINDOWS.remove(player.getUUID());
        Vec3 incoming = projectile.getDeltaMovement();
        double speed = Math.max(0.5, incoming.length());
        Entity attacker = projectile.getOwner();
        Vec3 reflectedDirection;
        if (attacker != null
                && attacker != player
                && attacker != projectile) {
            reflectedDirection = attacker.getEyePosition()
                    .subtract(projectile.position())
                    .normalize();
        } else if (incoming.lengthSqr() > 0.0001) {
            reflectedDirection = incoming.normalize().scale(-1.0);
        } else {
            reflectedDirection = player.getLookAngle();
        }

        projectile.deflect(
                ProjectileDeflection.REVERSE,
                player,
                player,
                true
        );
        projectile.setDeltaMovement(reflectedDirection.scale(speed));
        projectile.setPos(
                player.getX() + reflectedDirection.x * 0.8,
                player.getEyeY() - 0.15 + reflectedDirection.y * 0.8,
                player.getZ() + reflectedDirection.z * 0.8
        );

        player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.SHIELD_BLOCK,
                SoundSource.PLAYERS,
                0.7f,
                1.8f
        );
        player.serverLevel().sendParticles(
                ParticleTypes.CRIT,
                projectile.getX(),
                projectile.getY(),
                projectile.getZ(),
                12,
                0.25,
                0.25,
                0.25,
                0.08
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

    private static boolean hasGuardianLoadout(ServerPlayer player) {
        return OneHandedWeapons.isSupported(player.getMainHandItem())
                && player.getOffhandItem().getItem() instanceof ShieldItem;
    }

    private static boolean hasBerserkerLoadout(ServerPlayer player) {
        return OneHandedWeapons.isSupported(player.getMainHandItem())
                && OneHandedWeapons.isSupported(player.getOffhandItem())
                && isToggleEnabled(player, "dual_wield");
    }

    public static boolean tryOffhandStrike(ServerPlayer player) {
        if (!hasBerserkerLoadout(player)
                || !SkillManager.hasOneHandedPerk(
                player.getUUID(),
                "offhand_strike"
        )) {
            return false;
        }

        int tick = player.getServer().getTickCount();
        if (OFFHAND_STRIKE_COOLDOWNS.getOrDefault(
                player.getUUID(),
                0
        ) > tick) {
            return false;
        }

        double attackSpeed = Math.max(
                0.5,
                player.getAttributeValue(Attributes.ATTACK_SPEED)
        );
        int cooldownTicks = Math.max(
                MIN_OFFHAND_COOLDOWN_TICKS,
                (int) Math.ceil(20.0 / attackSpeed)
        );
        OFFHAND_STRIKE_COOLDOWNS.put(
                player.getUUID(),
                tick + cooldownTicks
        );
        player.swing(InteractionHand.OFF_HAND, true);

        LivingEntity target = findOffhandStrikeTarget(player);
        if (target == null) {
            player.level().playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.PLAYER_ATTACK_NODAMAGE,
                    SoundSource.PLAYERS,
                    0.35f,
                    1.1f
            );
            return true;
        }

        float damage = (float) Math.max(
                1.0,
                player.getAttributeValue(Attributes.ATTACK_DAMAGE)
                        * OFFHAND_DAMAGE_MULTIPLIER
        );
        applyingOffhandStrikeDamage = true;
        boolean hit;
        try {
            hit = target.hurt(
                    player.damageSources().playerAttack(player),
                    damage
            );
        } finally {
            applyingOffhandStrikeDamage = false;
        }
        if (!hit) {
            return true;
        }

        if (!shouldPreserveWeaponDurability(player)) {
            player.getOffhandItem().hurtAndBreak(
                    1,
                    player,
                    EquipmentSlot.OFFHAND
            );
        }
        player.level().playSound(
                null,
                target.blockPosition(),
                SoundEvents.PLAYER_ATTACK_SWEEP,
                SoundSource.PLAYERS,
                0.55f,
                1.25f
        );
        player.serverLevel().sendParticles(
                ParticleTypes.SWEEP_ATTACK,
                target.getX(),
                target.getY() + target.getBbHeight() * 0.5,
                target.getZ(),
                1,
                0.0,
                0.0,
                0.0,
                0.0
        );
        return true;
    }

    private static LivingEntity findOffhandStrikeTarget(ServerPlayer player) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        LivingEntity bestTarget = null;
        double bestDistance = OFFHAND_STRIKE_RANGE * OFFHAND_STRIKE_RANGE;

        for (LivingEntity target : player.serverLevel().getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(OFFHAND_STRIKE_RANGE),
                target -> target != player
                        && target.isAlive()
                        && player.canAttack(target)
        )) {
            Vec3 toTarget = target.getEyePosition().subtract(eye);
            double distance = toTarget.lengthSqr();
            if (distance < bestDistance
                    && look.dot(toTarget.normalize()) > 0.75
                    && player.hasLineOfSight(target)) {
                bestTarget = target;
                bestDistance = distance;
            }
        }
        return bestTarget;
    }

    private static void scheduleDurabilityRefund(ServerPlayer player) {
        if (!shouldPreserveWeaponDurability(player)) {
            return;
        }
        ItemStack weapon = player.getMainHandItem();
        PENDING_DURABILITY_REFUNDS.put(
                player.getUUID(),
                new PendingDurabilityRefund(
                        weapon,
                        weapon.getDamageValue()
                )
        );
    }

    private static void applyPendingDurabilityRefund(ServerPlayer player) {
        PendingDurabilityRefund refund = PENDING_DURABILITY_REFUNDS.remove(
                player.getUUID()
        );
        if (refund == null || refund.weapon().isEmpty()) {
            return;
        }
        int currentDamage = refund.weapon().getDamageValue();
        if (currentDamage > refund.damageBefore()) {
            refund.weapon().setDamageValue(currentDamage - 1);
        }
    }

    private static boolean shouldPreserveWeaponDurability(
            ServerPlayer player
    ) {
        float chance;
        if (SkillManager.hasOneHandedPerk(
                player.getUUID(),
                "monster_hunter"
        )) {
            chance = ADVANCED_DURABILITY_CHANCE;
        } else if (SkillManager.hasOneHandedPerk(
                player.getUUID(),
                "precise_strikes"
        )) {
            chance = MASTERY_DURABILITY_CHANCE;
        } else {
            return false;
        }
        return player.getRandom().nextFloat()
                < SkillManager.scalePerkChance(
                player.getUUID(),
                SkillType.ONE_HANDED,
                chance
        );
    }

    private static void activateAdrenaline(ServerPlayer player) {
        if (!hasBerserkerLoadout(player)
                || !SkillManager.hasOneHandedPerk(
                player.getUUID(),
                "berserkers_rhythm"
        )) {
            return;
        }
        ADRENALINE_WINDOWS.put(
                player.getUUID(),
                player.getServer().getTickCount() + ADRENALINE_DURATION_TICKS
        );
    }

    private static void handleBloodFrenzy(
            ServerPlayer player,
            LivingEntity defeated
    ) {
        if (!(defeated instanceof Enemy)
                || !hasBerserkerLoadout(player)
                || !SkillManager.hasOneHandedPerk(
                player.getUUID(),
                "blood_frenzy"
        )) {
            return;
        }

        float heal = BLOOD_FRENZY_MIN_HEAL
                + player.getRandom().nextFloat()
                * (BLOOD_FRENZY_MAX_HEAL - BLOOD_FRENZY_MIN_HEAL);
        player.heal((float) scale(player, heal));
        ADRENALINE_WINDOWS.put(
                player.getUUID(),
                player.getServer().getTickCount() + ADRENALINE_DURATION_TICKS
        );
        player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.EXPERIENCE_ORB_PICKUP,
                SoundSource.PLAYERS,
                0.25f,
                0.7f
        );
        player.serverLevel().sendParticles(
                ParticleTypes.HEART,
                player.getX(),
                player.getY() + 1.0,
                player.getZ(),
                3,
                0.3,
                0.4,
                0.3,
                0.02
        );
    }

    private static boolean tryLastStand(LivingEntity target) {
        if (!(target instanceof ServerPlayer player)
                || player.getHealth() > LAST_STAND_HEALTH_THRESHOLD
                || !hasBerserkerLoadout(player)
                || !SkillManager.hasOneHandedPerk(
                player.getUUID(),
                "last_stand"
        )
                || player.getRandom().nextFloat()
                >= SkillManager.scalePerkChance(
                player.getUUID(),
                SkillType.ONE_HANDED,
                LAST_STAND_SAVE_CHANCE
        )) {
            return false;
        }

        int tick = player.getServer().getTickCount();
        BERSERK_WINDOWS.put(
                player.getUUID(),
                tick + BERSERK_DURATION_TICKS
        );
        player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.WARDEN_HEARTBEAT,
                SoundSource.PLAYERS,
                0.65f,
                1.15f
        );
        player.serverLevel().sendParticles(
                ParticleTypes.DAMAGE_INDICATOR,
                player.getX(),
                player.getY() + 1.0,
                player.getZ(),
                14,
                0.45,
                0.6,
                0.45,
                0.08
        );
        return true;
    }

    private static void handleGuardianBlock(ServerPlayer player) {
        if (!hasGuardianLoadout(player)
                || !isToggleEnabled(player, "shield_effects")) {
            return;
        }

        int tick = player.getServer().getTickCount();
        if (SkillManager.hasOneHandedPerk(player.getUUID(), "guarded_strike")) {
            GUARDED_STRIKE_WINDOWS.put(
                    player.getUUID(),
                    tick + GUARDED_STRIKE_TIMEOUT_TICKS
            );
        }

        if (
                SkillManager.hasOneHandedPerk(player.getUUID(), "shield_training")
                        && player.getRandom().nextFloat()
                        < SkillManager.scalePerkChance(
                        player.getUUID(),
                        SkillType.ONE_HANDED,
                        SHIELD_TRAINING_SAVE_CHANCE
                )
        ) {
            ItemStack shield = player.getOffhandItem();
            if (shield.getDamageValue() > 0) {
                shield.setDamageValue(shield.getDamageValue() - 1);
            }
        }

        if (!SkillManager.hasOneHandedPerk(
                player.getUUID(),
                "guardians_resolve"
        )) {
            return;
        }

        if (RESOLVE_WINDOWS.getOrDefault(player.getUUID(), 0) < tick) {
            RESOLVE_BLOCK_COUNTS.put(player.getUUID(), 0);
        }
        int blocks = RESOLVE_BLOCK_COUNTS.getOrDefault(
                player.getUUID(),
                0
        ) + 1;
        RESOLVE_BLOCK_COUNTS.put(player.getUUID(), blocks);
        RESOLVE_WINDOWS.put(
                player.getUUID(),
                tick + RESOLVE_BLOCK_WINDOW_TICKS
        );

        if (blocks >= RESOLVE_REQUIRED_BLOCKS) {
            RESOLVE_BLOCK_COUNTS.put(player.getUUID(), 0);
            player.heal((float) scale(player, RESOLVE_HEAL_AMOUNT));
            player.addEffect(new MobEffectInstance(
                    MobEffects.DAMAGE_RESISTANCE,
                    RESOLVE_DURATION_TICKS,
                    0,
                    false,
                    false,
                    true
            ));
            player.level().playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.ANVIL_LAND,
                    SoundSource.PLAYERS,
                    0.2f,
                    1.8f
            );
            player.serverLevel().sendParticles(
                    ParticleTypes.ENCHANT,
                    player.getX(),
                    player.getY() + 1.0,
                    player.getZ(),
                    8,
                    0.35,
                    0.5,
                    0.35,
                    0.02
            );
        }
    }

    private static boolean tryShieldBash(ServerPlayer player) {
        if (
                !hasGuardianLoadout(player)
                        || !SkillManager.hasOneHandedPerk(
                        player.getUUID(),
                        "shield_bash"
                )
                        || !isToggleEnabled(player, "shield_effects")
        ) {
            return false;
        }

        int tick = player.getServer().getTickCount();
        if (SHIELD_BASH_COOLDOWNS.getOrDefault(player.getUUID(), 0) > tick) {
            return false;
        }

        LivingEntity target = findShieldBashTarget(player);
        if (target == null) {
            return false;
        }

        SHIELD_BASH_COOLDOWNS.put(
                player.getUUID(),
                tick + SHIELD_BASH_COOLDOWN_TICKS
        );
        target.knockback(
                0.8,
                player.getX() - target.getX(),
                player.getZ() - target.getZ()
        );
        target.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SLOWDOWN,
                20,
                0,
                false,
                false,
                true
        ));
        player.level().playSound(
                null,
                target.blockPosition(),
                SoundEvents.SHIELD_BLOCK,
                SoundSource.PLAYERS,
                0.6f,
                0.8f
        );
        player.serverLevel().sendParticles(
                ParticleTypes.CRIT,
                target.getX(),
                target.getY() + target.getBbHeight() * 0.5,
                target.getZ(),
                6,
                0.25,
                0.3,
                0.25,
                0.08
        );
        return true;
    }

    private static LivingEntity findShieldBashTarget(ServerPlayer player) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        LivingEntity bestTarget = null;
        double bestDistance = SHIELD_BASH_RANGE * SHIELD_BASH_RANGE;

        for (LivingEntity target : player.serverLevel().getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(SHIELD_BASH_RANGE),
                target -> target instanceof Enemy && target.isAlive()
        )) {
            Vec3 toTarget = target.getEyePosition().subtract(eye);
            double distance = toTarget.lengthSqr();
            if (
                    distance < bestDistance
                            && look.dot(toTarget.normalize()) > 0.65
                            && player.hasLineOfSight(target)
            ) {
                bestTarget = target;
                bestDistance = distance;
            }
        }
        return bestTarget;
    }

    private static void tryApplyGuardedStrike(
            ServerPlayer player,
            LivingEntity target
    ) {
        int tick = player.getServer().getTickCount();
        if (
                GUARDED_STRIKE_WINDOWS.getOrDefault(player.getUUID(), 0) < tick
                        || !OneHandedWeapons.isSupported(player.getMainHandItem())
        ) {
            return;
        }

        GUARDED_STRIKE_WINDOWS.remove(player.getUUID());
        applyingGuardedStrikeDamage = true;
        try {
            target.hurt(
                    player.damageSources().playerAttack(player),
                    (float) scale(player, GUARDED_STRIKE_BONUS_DAMAGE)
            );
        } finally {
            applyingGuardedStrikeDamage = false;
        }
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
        boolean activelyBlocking = weaponAndShield
                && player.isUsingItem()
                && player.getUseItem().getItem() instanceof ShieldItem;
        int tick = player.getServer().getTickCount();
        boolean berserkerActive = dualWield
                && isToggleEnabled(player, "dual_wield");
        boolean adrenalineActive = berserkerActive
                && ADRENALINE_WINDOWS.getOrDefault(
                player.getUUID(),
                0
        ) >= tick;
        boolean berserkActive = berserkerActive
                && BERSERK_WINDOWS.getOrDefault(
                player.getUUID(),
                0
        ) >= tick;

        if (berserkActive && tick % 20 == 0) {
            emitBerserkFeedback(player, tick);
        }

        double damageBonus = 0.0;
        double speedBonus = 0.0;
        double defenseBonus = 0.0;

        if (supportedMainHand
                && SkillManager.hasOneHandedPerk(
                player.getUUID(),
                "blade_training"
        )) {
            speedBonus += MASTERY_SPEED_BONUS;
        }

        if (supportedMainHand
                && SkillManager.hasOneHandedPerk(
                player.getUUID(),
                "monster_hunter"
        )) {
            speedBonus += ADVANCED_MASTERY_SPEED_BONUS;
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
                berserkerActive
                        && SkillManager.hasOneHandedPerk(
                        player.getUUID(),
                        "offhand_strike"
                )
                        && isToggleEnabled(player, "dual_wield")
        ) {
            speedBonus += BLOODLUST_SPEED_BONUS;
        }

        if (berserkerActive
                && player.getHealth() < player.getMaxHealth() * 0.5f
                && SkillManager.hasOneHandedPerk(
                player.getUUID(),
                "twin_blades"
        )) {
            damageBonus += RECKLESS_DAMAGE_BONUS;
        }

        if (adrenalineActive) {
            speedBonus += ADRENALINE_SPEED_BONUS;
        }

        if (berserkActive) {
            damageBonus += BERSERK_DAMAGE_BONUS;
            speedBonus += BERSERK_SPEED_BONUS;
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
        updateModifier(
                player.getAttribute(Attributes.KNOCKBACK_RESISTANCE),
                BULWARK_MODIFIER_ID,
                activelyBlocking
                        && SkillManager.hasOneHandedPerk(
                        player.getUUID(),
                        "bulwark"
                )
                        && isToggleEnabled(player, "shield_effects")
                        ? scale(player, 0.10)
                        : 0.0,
                AttributeModifier.Operation.ADD_VALUE
        );
    }

    private static void emitBerserkFeedback(ServerPlayer player, int tick) {
        player.serverLevel().sendParticles(
                ParticleTypes.DAMAGE_INDICATOR,
                player.getX(),
                player.getY() + 1.0,
                player.getZ(),
                2,
                0.3,
                0.45,
                0.3,
                0.02
        );
        if (tick % 40 == 0) {
            player.level().playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.WARDEN_HEARTBEAT,
                    SoundSource.PLAYERS,
                    0.25f,
                    1.25f
            );
        }
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
                (!(target instanceof Enemy)
                        && !(target instanceof NeutralMob))
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

    private record PendingDurabilityRefund(
            ItemStack weapon,
            int damageBefore
    ) {
    }
}
