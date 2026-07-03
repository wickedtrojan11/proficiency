package com.trojan.proficiency.perk;

import com.trojan.proficiency.SkillManager;
import com.trojan.proficiency.skill.SkillType;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WoodcuttingPerkEffects {

    private static final float AXE_TRAINING_BONUS_DAMAGE = 1.0f;
    private static final float BATTLE_AXE_MASTERY_BONUS_DAMAGE = 2.0f;
    private static final float HEAVY_CHOP_CHANCE = 0.15f;
    private static final int HEAVY_CHOP_DURATION = 60;
    private static final float CLEAVING_SWING_CHANCE = 0.10f;
    private static final float CLEAVING_SWING_DAMAGE = 1.0f;
    private static final double CLEAVING_SWING_RANGE = 3.0;
    private static final float DECAPITATION_CHANCE = 0.25f;
    private static final int FELLING_MOMENTUM_DURATION_TICKS = 100;
    private static final double CLEAN_FOREST_FLOOR_RADIUS = 7.0;
    private static final double CLEAN_FOREST_FLOOR_PULL_SPEED = 0.08;
    private static final ResourceLocation QUICK_HATCHET_ATTACK_SPEED_ID =
            ResourceLocation.fromNamespaceAndPath(
                    "proficiency",
                    "quick_hatchet_attack_speed"
            );

    private static final AttributeModifier QUICK_HATCHET_ATTACK_SPEED =
            new AttributeModifier(
                    QUICK_HATCHET_ATTACK_SPEED_ID,
                    0.20,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            );

    private static final ResourceLocation FELLING_MOMENTUM_EFFICIENCY_ID =
            ResourceLocation.fromNamespaceAndPath(
                    "proficiency",
                    "felling_momentum_efficiency"
            );

    private static final AttributeModifier FELLING_MOMENTUM_EFFICIENCY =
            new AttributeModifier(
                    FELLING_MOMENTUM_EFFICIENCY_ID,
                    1.0,
                    AttributeModifier.Operation.ADD_VALUE
            );

    private static final Map<UUID, Integer> FELLING_MOMENTUM_EXPIRY_TICKS =
            new HashMap<>();

    private static boolean applyingAxeCombatDamage;
    private static boolean applyingCleavingDamage;

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
                                        "splinter_fighter_2"
                                )
                ) {

                    player.addEffect(
                            new MobEffectInstance(
                                    MobEffects.DAMAGE_BOOST,
                                    40,
                                    1,
                                    false,
                                    false,
                                    true
                            )
                    );

                } else if (
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
                                        "clean_swing"
                                )
                                        || SkillManager.hasWoodcuttingPerk(
                                        player.getUUID(),
                                        "rhythm_of_the_forest"
                                )
                        )
                ) {

                    player.addEffect(
                            new MobEffectInstance(
                                    MobEffects.DIG_SPEED,
                                    40,
                                    1,
                                    false,
                                    false,
                                    true
                            )
                    );

                } else if (
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

                updateQuickHatchetAttackSpeed(
                        player,
                        holdingAxe
                );

                updateFellingMomentumEfficiency(
                        player,
                        holdingAxe,
                        server.getTickCount()
                );

                attractTreeDrops(player);
            }
        });

        ServerLivingEntityEvents.AFTER_DAMAGE.register(
                (entity, source, baseDamageTaken, damageTaken, blocked) -> {

                    if (!blocked && damageTaken > 0.0f) {

                        applyAxeCombatEffects(
                                entity,
                                source
                        );
                    }
                }
        );

        ServerLivingEntityEvents.AFTER_DEATH.register(
                (entity, source) -> {

                    tryDropMobHead(
                            entity,
                            source
                    );

                    applyAxeCombatEffects(
                                entity,
                                source
                        );
                }
        );
    }

    private static void attractTreeDrops(
            ServerPlayer player
    ) {

        if (
                !SkillManager.hasWoodcuttingPerk(
                        player.getUUID(),
                        "clean_forest_floor"
                )
                        || !SkillManager.isWoodcuttingCleanFloorEnabled(
                        player.getUUID()
                )
        ) {

            return;
        }

        Vec3 target =
                player.position()
                        .add(
                                0.0,
                                0.75,
                                0.0
                        );

        for (ItemEntity itemEntity
                : player.serverLevel()
                .getEntitiesOfClass(
                        ItemEntity.class,
                        player.getBoundingBox()
                                .inflate(
                                        CLEAN_FOREST_FLOOR_RADIUS
                                ),
                        WoodcuttingPerkEffects::isTreeRelatedDrop
                )) {

            Vec3 pullDirection =
                    target.subtract(
                            itemEntity.position()
                    );

            if (pullDirection.lengthSqr() < 0.25) {

                continue;
            }

            Vec3 pullVelocity =
                    pullDirection.normalize()
                            .scale(
                                    CLEAN_FOREST_FLOOR_PULL_SPEED
                            );

            itemEntity.setDeltaMovement(
                    itemEntity.getDeltaMovement()
                            .scale(0.85)
                            .add(pullVelocity)
            );
        }
    }

    private static boolean isTreeRelatedDrop(
            ItemEntity itemEntity
    ) {

        ItemStack stack =
                itemEntity.getItem();

        return stack.is(ItemTags.SAPLINGS)
                || stack.is(ItemTags.LOGS)
                || stack.is(ItemTags.LEAVES)
                || stack.is(Items.STICK)
                || stack.is(Items.APPLE)
                || stack.is(Items.HONEYCOMB)
                || stack.is(Items.HONEY_BOTTLE)
                || stack.is(Items.CHARCOAL);
    }

    public static void activateFellingMomentum(
            ServerPlayer player
    ) {

        FELLING_MOMENTUM_EXPIRY_TICKS.put(
                player.getUUID(),
                player.server.getTickCount()
                        + FELLING_MOMENTUM_DURATION_TICKS
        );
    }

    private static void updateFellingMomentumEfficiency(
            ServerPlayer player,
            boolean holdingAxe,
            int currentTick
    ) {

        AttributeInstance miningEfficiency =
                player.getAttribute(
                        Attributes.MINING_EFFICIENCY
                );

        if (miningEfficiency == null) {

            return;
        }

        Integer expiryTick =
                FELLING_MOMENTUM_EXPIRY_TICKS.get(
                        player.getUUID()
                );

        boolean shouldApply =
                holdingAxe
                        && expiryTick != null
                        && currentTick < expiryTick;

        if (
                shouldApply
                        && !miningEfficiency.hasModifier(
                        FELLING_MOMENTUM_EFFICIENCY_ID
                )
        ) {

            miningEfficiency.addTransientModifier(
                    FELLING_MOMENTUM_EFFICIENCY
            );

        } else if (
                !shouldApply
                        && miningEfficiency.hasModifier(
                        FELLING_MOMENTUM_EFFICIENCY_ID
                )
        ) {

            miningEfficiency.removeModifier(
                    FELLING_MOMENTUM_EFFICIENCY_ID
            );
        }

        if (
                expiryTick != null
                        && currentTick >= expiryTick
        ) {

            FELLING_MOMENTUM_EXPIRY_TICKS.remove(
                    player.getUUID()
            );
        }
    }

    private static void applyAxeCombatEffects(
            LivingEntity target,
            DamageSource source
    ) {

        if (
                applyingAxeCombatDamage
                        || !(source.getEntity()
                        instanceof ServerPlayer player)
                        || source.getDirectEntity() != player
                        || !(player.getMainHandItem().getItem()
                        instanceof AxeItem)
        ) {

            return;
        }

        if (
                target.isAlive()
                        && SkillManager.hasWoodcuttingPerk(
                                player.getUUID(),
                                "battle_axe_mastery"
                        )
        ) {

            applyBonusDamage(
                    target,
                    player,
                    (float) SkillManager.scalePerkValue(
                            player.getUUID(),
                            SkillType.WOODCUTTING,
                            AXE_TRAINING_BONUS_DAMAGE
                                    + BATTLE_AXE_MASTERY_BONUS_DAMAGE
                    )
            );

        } else if (
                target.isAlive()
                        && SkillManager.hasWoodcuttingPerk(
                        player.getUUID(),
                        "axe_training"
                )
        ) {

            applyBonusDamage(
                    target,
                    player,
                    (float) SkillManager.scalePerkValue(
                            player.getUUID(),
                            SkillType.WOODCUTTING,
                            AXE_TRAINING_BONUS_DAMAGE
                    )
            );
        }

        if (
                target.isAlive()
                        && SkillManager.hasWoodcuttingPerk(
                                player.getUUID(),
                                "heavy_chop"
                        )
                        && player.getRandom().nextFloat()
                        < SkillManager.scalePerkChance(player.getUUID(), SkillType.WOODCUTTING, HEAVY_CHOP_CHANCE)
        ) {

            target.addEffect(
                    new MobEffectInstance(
                            MobEffects.MOVEMENT_SLOWDOWN,
                            HEAVY_CHOP_DURATION,
                            0
                    )
            );
        }

        if (
                SkillManager.hasWoodcuttingPerk(
                        player.getUUID(),
                        "cleaving_swing"
                )
                        && player.getRandom().nextFloat()
                        < SkillManager.scalePerkChance(player.getUUID(), SkillType.WOODCUTTING, CLEAVING_SWING_CHANCE)
        ) {

            applyingAxeCombatDamage = true;
            applyingCleavingDamage = true;

            try {

                for (LivingEntity nearby
                        : target.level().getEntitiesOfClass(
                        LivingEntity.class,
                        target.getBoundingBox().inflate(
                                CLEAVING_SWING_RANGE
                        ),
                        entity ->
                                entity != target
                                        && entity instanceof Enemy
                                        && entity.isAlive()
                )) {

                    nearby.hurt(
                            player.damageSources().playerAttack(player),
                            CLEAVING_SWING_DAMAGE
                    );
                }

            } finally {

                applyingCleavingDamage = false;
                applyingAxeCombatDamage = false;
            }
        }
    }

    private static void updateQuickHatchetAttackSpeed(
            ServerPlayer player,
            boolean holdingAxe
    ) {

        AttributeInstance attackSpeed =
                player.getAttribute(
                        Attributes.ATTACK_SPEED
                );

        if (attackSpeed == null) {

            return;
        }

        boolean shouldApply =
                holdingAxe
                        && SkillManager.hasWoodcuttingPerk(
                        player.getUUID(),
                        "quick_hatchet"
                );

        if (
                shouldApply
                        && !attackSpeed.hasModifier(
                        QUICK_HATCHET_ATTACK_SPEED_ID
                )
        ) {

            attackSpeed.addTransientModifier(
                    QUICK_HATCHET_ATTACK_SPEED
            );

        } else if (
                !shouldApply
                        && attackSpeed.hasModifier(
                        QUICK_HATCHET_ATTACK_SPEED_ID
                )
        ) {

            attackSpeed.removeModifier(
                    QUICK_HATCHET_ATTACK_SPEED_ID
            );
        }
    }

    private static void tryDropMobHead(
            LivingEntity target,
            DamageSource source
    ) {

        if (
                applyingCleavingDamage
                        || !(source.getEntity()
                        instanceof ServerPlayer player)
                        || source.getDirectEntity() != player
                        || !(player.getMainHandItem().getItem()
                        instanceof AxeItem)
                        || !SkillManager.hasWoodcuttingPerk(
                        player.getUUID(),
                        "decapitation_chance"
                )
                        || player.getRandom().nextFloat()
                        >= SkillManager.scalePerkChance(player.getUUID(), SkillType.WOODCUTTING, DECAPITATION_CHANCE)
        ) {

            return;
        }

        Item headItem =
                getMatchingHeadItem(target);

        if (headItem != null) {

            target.spawnAtLocation(
                    new ItemStack(headItem)
            );
        }
    }

    private static Item getMatchingHeadItem(
            LivingEntity target
    ) {

        if (target.getType() == EntityType.ZOMBIE) {

            return Items.ZOMBIE_HEAD;
        }

        if (target.getType() == EntityType.SKELETON) {

            return Items.SKELETON_SKULL;
        }

        if (target.getType() == EntityType.WITHER_SKELETON) {

            return Items.WITHER_SKELETON_SKULL;
        }

        if (target.getType() == EntityType.CREEPER) {

            return Items.CREEPER_HEAD;
        }

        if (target.getType() == EntityType.PIGLIN) {

            return Items.PIGLIN_HEAD;
        }

        if (target.getType() == EntityType.ENDER_DRAGON) {

            return Items.DRAGON_HEAD;
        }

        return null;
    }

    private static void applyBonusDamage(
            LivingEntity target,
            ServerPlayer player,
            float damage
    ) {

        int previousInvulnerableTime =
                target.invulnerableTime;

        applyingAxeCombatDamage = true;
        target.invulnerableTime = 0;

        try {

            target.hurt(
                    player.damageSources().playerAttack(player),
                    damage
            );

        } finally {

            target.invulnerableTime =
                    Math.max(
                            target.invulnerableTime,
                            previousInvulnerableTime
                    );

            applyingAxeCombatDamage = false;
        }
    }
}
