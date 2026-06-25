package com.trojan.proficiency.mixin;

import com.trojan.proficiency.entity.SyncedAnimalAge;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.AgeableMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AgeableMob.class)
public abstract class AgeableMobAgeMixin
        implements SyncedAnimalAge {

    @Unique
    private static final EntityDataAccessor<Integer>
            PROFICIENCY_SYNCED_AGE =
            SynchedEntityData.defineId(
                    AgeableMob.class,
                    EntityDataSerializers.INT
            );

    @Inject(
            method = "defineSynchedData",
            at = @At("TAIL")
    )
    private void proficiency$defineSyncedAge(
            SynchedEntityData.Builder builder,
            CallbackInfo callbackInfo
    ) {

        builder.define(
                PROFICIENCY_SYNCED_AGE,
                0
        );
    }

    @Inject(
            method = "aiStep",
            at = @At("TAIL")
    )
    private void proficiency$syncAge(
            CallbackInfo callbackInfo
    ) {

        AgeableMob animal =
                (AgeableMob) (Object) this;

        if (
                !animal.level().isClientSide
                        && animal.tickCount % 5 == 0
        ) {

            animal.getEntityData().set(
                    PROFICIENCY_SYNCED_AGE,
                    animal.getAge()
            );
        }
    }

    @Override
    public int proficiency$getSyncedAge() {

        AgeableMob animal =
                (AgeableMob) (Object) this;

        return animal.getEntityData().get(
                PROFICIENCY_SYNCED_AGE
        );
    }
}
