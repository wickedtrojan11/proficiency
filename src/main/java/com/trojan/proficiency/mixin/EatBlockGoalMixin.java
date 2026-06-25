package com.trojan.proficiency.mixin;

import com.trojan.proficiency.event.FarmingAnimalEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.entity.animal.Sheep;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EatBlockGoal.class)
public abstract class EatBlockGoalMixin {

    @Shadow
    @Final
    private Mob mob;

    @ModifyConstant(
            method = "canUse",
            constant = @Constant(intValue = 1000)
    )
    private int proficiency$accelerateWoolRegrowth(
            int originalChance
    ) {

        if (!(mob instanceof Sheep sheep) || !sheep.isSheared()) {
            return originalChance;
        }

        int bonusPercent =
                FarmingAnimalEffects
                        .getNearbyWoolRegrowthBonusPercent(
                                sheep
                        );

        if (bonusPercent <= 0) {
            return originalChance;
        }

        return Math.max(
                1,
                Math.round(
                        originalChance
                                / (1.0f + bonusPercent / 100.0f)
                )
        );
    }
}
