package com.trojan.proficiency.mixin;

import com.trojan.proficiency.event.SaplingOwnershipTracker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.SaplingBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class SaplingPlacementMixin {

    @Inject(
            method = "place",
            at = @At("RETURN")
    )
    private void proficiency$recordSaplingOwner(
            BlockPlaceContext context,
            CallbackInfoReturnable<InteractionResult> callbackInfo
    ) {

        BlockItem blockItem = (BlockItem) (Object) this;

        if (
                callbackInfo.getReturnValue().consumesAction()
                        && blockItem.getBlock() instanceof SaplingBlock
                        && context.getLevel()
                        instanceof ServerLevel serverLevel
                        && context.getPlayer()
                        instanceof ServerPlayer player
        ) {

            SaplingOwnershipTracker.record(
                    serverLevel,
                    context.getClickedPos(),
                    player.getUUID()
            );
        }
    }
}
