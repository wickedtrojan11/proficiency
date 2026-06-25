package com.trojan.proficiency.mixin;

import com.trojan.proficiency.SkillManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Animal.class)
public abstract class AnimalBreedingMixin {

    @Inject(
            method = "finalizeSpawnChildFromBreeding",
            at = @At("HEAD")
    )
    private void proficiency$awardFarmingXp(
            ServerLevel level,
            Animal partner,
            AgeableMob child,
            CallbackInfo callbackInfo
    ) {

        Animal animal =
                (Animal) (Object) this;

        ServerPlayer breeder =
                animal.getLoveCause();

        if (breeder == null) {

            breeder = partner.getLoveCause();
        }

        if (breeder != null) {

            SkillManager.addFarmingXp(
                    breeder,
                    3
            );
        }
    }
}
