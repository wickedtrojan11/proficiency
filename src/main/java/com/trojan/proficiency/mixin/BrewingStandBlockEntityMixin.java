package com.trojan.proficiency.mixin;

import com.trojan.proficiency.event.AlchemyEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingStandBlockEntity.class)
public abstract class BrewingStandBlockEntityMixin {

    @Shadow
    int brewTime;

    @Unique
    private static final ThreadLocal<ItemStack> proficiency$capturedIngredient =
            ThreadLocal.withInitial(() -> ItemStack.EMPTY);

    @Inject(
            method = "serverTick",
            at = @At("TAIL")
    )
    private static void proficiency$accelerateBrewing(
            Level level,
            BlockPos pos,
            net.minecraft.world.level.block.state.BlockState state,
            BrewingStandBlockEntity blockEntity,
            CallbackInfo callbackInfo
    ) {
        int bonus = AlchemyEvents.getNearbyBrewingSpeedBonusPercent(level, pos);
        if (bonus <= 0) {
            return;
        }

        BrewingStandBlockEntityMixin mixin =
                (BrewingStandBlockEntityMixin) (Object) blockEntity;
        if (mixin.brewTime > 1) {
            int extraTicks = Math.max(1, bonus / 10);
            mixin.brewTime = Math.max(1, mixin.brewTime - extraTicks);
        }
    }

    @Inject(
            method = "isBrewable",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void proficiency$isCustomBrewable(
            PotionBrewing potionBrewing,
            NonNullList<ItemStack> items,
            CallbackInfoReturnable<Boolean> callbackInfo
    ) {
        if (AlchemyEvents.isCustomBrewable(items)) {
            callbackInfo.setReturnValue(true);
        }
    }

    @Inject(
            method = "canPlaceItem",
            at = @At("HEAD"),
            cancellable = true
    )
    private void proficiency$canPlaceCustomIngredient(
            int slot,
            ItemStack stack,
            CallbackInfoReturnable<Boolean> callbackInfo
    ) {
        if (slot == 3 && AlchemyEvents.isCustomBrewingIngredient(stack)) {
            callbackInfo.setReturnValue(true);
        }
    }

    @Inject(
            method = "doBrew",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void proficiency$doCustomBrew(
            Level level,
            BlockPos pos,
            NonNullList<ItemStack> items,
            CallbackInfo callbackInfo
    ) {
        proficiency$capturedIngredient.set(items.get(3).copyWithCount(1));
        if (AlchemyEvents.handleCustomBrew(level, pos, items)) {
            proficiency$capturedIngredient.set(ItemStack.EMPTY);
            callbackInfo.cancel();
        }
    }

    @Inject(
            method = "doBrew",
            at = @At("TAIL")
    )
    private static void proficiency$awardVanillaBrewXp(
            Level level,
            BlockPos pos,
            NonNullList<ItemStack> items,
            CallbackInfo callbackInfo
    ) {
        AlchemyEvents.maybeUpgradeBrewedPotionPotency(
                level,
                pos,
                items
        );
        AlchemyEvents.maybeRefundVanillaIngredient(
                level,
                pos,
                items,
                proficiency$capturedIngredient.get()
        );
        proficiency$capturedIngredient.set(ItemStack.EMPTY);
        AlchemyEvents.awardNearbyAlchemyXp(level, pos);
    }
}
