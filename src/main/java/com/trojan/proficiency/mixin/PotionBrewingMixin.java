package com.trojan.proficiency.mixin;

import com.trojan.proficiency.event.AlchemyEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionBrewing.class)
public abstract class PotionBrewingMixin {

    @Inject(
            method = "isIngredient",
            at = @At("HEAD"),
            cancellable = true
    )
    private void proficiency$isCustomAlchemyIngredient(
            ItemStack stack,
            CallbackInfoReturnable<Boolean> callbackInfo
    ) {
        if (AlchemyEvents.isCustomBrewingIngredient(stack)) {
            callbackInfo.setReturnValue(true);
        }
    }
}
