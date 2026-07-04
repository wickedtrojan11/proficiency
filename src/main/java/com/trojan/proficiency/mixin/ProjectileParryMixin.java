package com.trojan.proficiency.mixin;

import com.trojan.proficiency.event.OneHandedEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Projectile.class)
public abstract class ProjectileParryMixin {

    @Inject(method = "onHit", at = @At("HEAD"), cancellable = true)
    private void proficiency$reflectParriedProjectile(
            HitResult hitResult,
            CallbackInfo callbackInfo
    ) {
        if (hitResult instanceof EntityHitResult entityHit
                && entityHit.getEntity() instanceof ServerPlayer player
                && OneHandedEvents.tryProjectileParry(
                (Projectile) (Object) this,
                player
        )) {
            callbackInfo.cancel();
        }
    }
}
