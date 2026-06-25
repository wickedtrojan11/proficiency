package com.trojan.proficiency.mixin;

import com.trojan.proficiency.SkillManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Animal.class)
public abstract class AnimalBreedingMixin {

    @Unique
    private ServerPlayer proficiency$breeder;

    @Inject(
            method = "finalizeSpawnChildFromBreeding",
            at = @At("HEAD")
    )
    private void proficiency$captureBreeder(
            ServerLevel level,
            Animal partner,
            AgeableMob child,
            CallbackInfo callbackInfo
    ) {

        Animal animal =
                (Animal) (Object) this;

        proficiency$breeder =
                animal.getLoveCause();

        if (proficiency$breeder == null) {

            proficiency$breeder =
                    partner.getLoveCause();
        }
    }

    @Inject(
            method = "finalizeSpawnChildFromBreeding",
            at = @At("TAIL")
    )
    private void proficiency$applyFarmingBreedingEffects(
            ServerLevel level,
            Animal partner,
            AgeableMob child,
            CallbackInfo callbackInfo
    ) {

        Animal animal =
                (Animal) (Object) this;

        ServerPlayer breeder =
                proficiency$breeder;

        proficiency$breeder = null;

        if (breeder != null) {

            SkillManager.addFarmingXp(
                    breeder,
                    3
            );

            if (SkillManager.hasFarmingPerk(
                    breeder.getUUID(),
                    "experienced_breeder"
            )) {

                animal.setAge(4500);
                partner.setAge(4500);
            }
        }
    }
}
