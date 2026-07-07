package com.trojan.proficiency.mixin;

import com.trojan.proficiency.event.AlchemyEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.BrewingStandMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.BrewingStandMenu$PotionSlot")
public abstract class BrewingStandPotionSlotMixin {

    @Inject(
            method = "mayPlaceItem",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void proficiency$mayPlaceXpElixir(
            ItemStack stack,
            CallbackInfoReturnable<Boolean> callbackInfo
    ) {
        if (AlchemyEvents.isXpElixir(stack)) {
            callbackInfo.setReturnValue(true);
        }
    }
}
