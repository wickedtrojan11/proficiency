package com.trojan.proficiency.event;

import com.trojan.proficiency.SkillManager;
import com.trojan.proficiency.skill.SkillType;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class FarmingAnimalDropEffects {

    private static final float HEALTHY_STOCK_CHANCE = 0.10f;
    private static final float PRIME_CUTS_CHANCE = 0.18f;
    private static final float BOUNTIFUL_MEAT_CHANCE = 0.25f;
    private static final float SECONDARY_DROP_CHANCE = 0.08f;
    private static final float BOUNTIFUL_SECONDARY_CHANCE = 0.12f;

    private FarmingAnimalDropEffects() {
    }

    public static void register() {

        ServerLivingEntityEvents.AFTER_DEATH.register(
                FarmingAnimalDropEffects::applyBonusDrops
        );
    }

    private static void applyBonusDrops(
            LivingEntity target,
            DamageSource source
    ) {

        if (
                !(source.getEntity()
                        instanceof ServerPlayer player)
                        || !SkillManager
                        .isFarmingAnimalDropsEnabled(
                                player.getUUID()
                        )
                        || isBaby(target)
                        || !isEligibleLivestock(target)
        ) {
            return;
        }

        Item meatItem = getMeatItem(target);
        float meatChance = SkillManager.scalePerkChance(
                player.getUUID(),
                SkillType.FARMING,
                getMeatChance(player)
        );

        if (
                meatItem != null
                        && meatChance > 0.0f
                        && player.getRandom().nextFloat()
                        < meatChance
        ) {

            target.spawnAtLocation(
                    new ItemStack(meatItem)
            );
        }

        float secondaryChance = SkillManager.scalePerkChance(
                player.getUUID(),
                SkillType.FARMING,
                getSecondaryChance(player)
        );

        if (
                secondaryChance <= 0.0f
                        || player.getRandom().nextFloat()
                        >= secondaryChance
        ) {
            return;
        }

        Item secondaryItem =
                getSecondaryItem(target);

        if (secondaryItem != null) {

            target.spawnAtLocation(
                    new ItemStack(secondaryItem)
            );
        }
    }

    private static float getMeatChance(
            ServerPlayer player
    ) {

        if (SkillManager.hasFarmingPerk(
                player.getUUID(),
                "bountiful_herds"
        )) {
            return BOUNTIFUL_MEAT_CHANCE;
        }

        if (SkillManager.hasFarmingPerk(
                player.getUUID(),
                "prime_cuts"
        )) {
            return PRIME_CUTS_CHANCE;
        }

        if (SkillManager.hasFarmingPerk(
                player.getUUID(),
                "healthy_stock"
        )) {
            return HEALTHY_STOCK_CHANCE;
        }

        return 0.0f;
    }

    private static float getSecondaryChance(
            ServerPlayer player
    ) {

        if (SkillManager.hasFarmingPerk(
                player.getUUID(),
                "bountiful_herds"
        )) {
            return BOUNTIFUL_SECONDARY_CHANCE;
        }

        if (SkillManager.hasFarmingPerk(
                player.getUUID(),
                "efficient_rancher"
        )) {
            return SECONDARY_DROP_CHANCE;
        }

        return 0.0f;
    }

    private static boolean isEligibleLivestock(
            LivingEntity target
    ) {

        return target instanceof Cow
                || target instanceof Pig
                || target instanceof Sheep
                || target instanceof Chicken
                || target instanceof Rabbit;
    }

    private static boolean isBaby(
            LivingEntity target
    ) {

        return target instanceof AgeableMob ageable
                && ageable.isBaby();
    }

    private static Item getMeatItem(
            LivingEntity target
    ) {

        boolean cooked = target.isOnFire();

        if (target instanceof Cow) {
            return cooked
                    ? Items.COOKED_BEEF
                    : Items.BEEF;
        }

        if (target instanceof Pig) {
            return cooked
                    ? Items.COOKED_PORKCHOP
                    : Items.PORKCHOP;
        }

        if (target instanceof Sheep) {
            return cooked
                    ? Items.COOKED_MUTTON
                    : Items.MUTTON;
        }

        if (target instanceof Chicken) {
            return cooked
                    ? Items.COOKED_CHICKEN
                    : Items.CHICKEN;
        }

        if (target instanceof Rabbit) {
            return cooked
                    ? Items.COOKED_RABBIT
                    : Items.RABBIT;
        }

        return null;
    }

    private static Item getSecondaryItem(
            LivingEntity target
    ) {

        if (target instanceof Cow) {
            return Items.LEATHER;
        }

        if (target instanceof Chicken) {
            return Items.FEATHER;
        }

        if (target instanceof Rabbit) {
            return Items.RABBIT_HIDE;
        }

        if (target instanceof Sheep sheep) {
            return getWoolItem(sheep);
        }

        return null;
    }

    private static Item getWoolItem(
            Sheep sheep
    ) {

        return switch (sheep.getColor()) {
            case WHITE -> Items.WHITE_WOOL;
            case ORANGE -> Items.ORANGE_WOOL;
            case MAGENTA -> Items.MAGENTA_WOOL;
            case LIGHT_BLUE -> Items.LIGHT_BLUE_WOOL;
            case YELLOW -> Items.YELLOW_WOOL;
            case LIME -> Items.LIME_WOOL;
            case PINK -> Items.PINK_WOOL;
            case GRAY -> Items.GRAY_WOOL;
            case LIGHT_GRAY -> Items.LIGHT_GRAY_WOOL;
            case CYAN -> Items.CYAN_WOOL;
            case PURPLE -> Items.PURPLE_WOOL;
            case BLUE -> Items.BLUE_WOOL;
            case BROWN -> Items.BROWN_WOOL;
            case GREEN -> Items.GREEN_WOOL;
            case RED -> Items.RED_WOOL;
            case BLACK -> Items.BLACK_WOOL;
        };
    }
}
