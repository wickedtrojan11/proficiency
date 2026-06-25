package com.trojan.proficiency.mixin;

import com.trojan.proficiency.SkillManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Sheep.class)
public abstract class SheepShearingMixin {

    @Unique
    private boolean proficiency$wasReadyForShearing;

    @Inject(
            method = "mobInteract",
            at = @At("HEAD")
    )
    private void proficiency$captureShearing(
            Player player,
            InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> callbackInfo
    ) {

        Sheep sheep = (Sheep) (Object) this;

        proficiency$wasReadyForShearing =
                !sheep.level().isClientSide
                        && player.getItemInHand(hand).is(Items.SHEARS)
                        && sheep.readyForShearing();
    }

    @Inject(
            method = "mobInteract",
            at = @At("RETURN")
    )
    private void proficiency$dropExtraWool(
            Player player,
            InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> callbackInfo
    ) {

        Sheep sheep = (Sheep) (Object) this;

        if (
                !proficiency$wasReadyForShearing
                        || !(player instanceof ServerPlayer serverPlayer)
                        || !sheep.isSheared()
                        || !SkillManager.hasFarmingPerk(
                        serverPlayer.getUUID(),
                        "extra_wool"
                )
                        || sheep.getRandom().nextFloat() >= 0.10f
        ) {
            return;
        }

        sheep.spawnAtLocation(
                new ItemStack(
                        proficiency$getWoolItem(sheep)
                ),
                1.0f
        );
    }

    @Unique
    private static Item proficiency$getWoolItem(
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
