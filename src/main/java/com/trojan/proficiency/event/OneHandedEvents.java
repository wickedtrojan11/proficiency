package com.trojan.proficiency.event;

import com.trojan.proficiency.SkillManager;
import com.trojan.proficiency.util.OneHandedWeapons;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
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

public final class OneHandedEvents {

    private static final int DAMAGE_XP = 1;
    private static final int KILL_XP = 4;
    private static final ResourceLocation DAMAGE_MODIFIER_ID = id("one_handed_damage");
    private static final ResourceLocation SPEED_MODIFIER_ID = id("one_handed_speed");
    private static final ResourceLocation DEFENSE_MODIFIER_ID = id("one_handed_defense");

    private OneHandedEvents() {
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                updateLoadoutEffects(player);
            }
        });

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
}
