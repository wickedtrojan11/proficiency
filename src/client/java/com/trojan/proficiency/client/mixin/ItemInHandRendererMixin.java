package com.trojan.proficiency.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.trojan.proficiency.client.ParryVisualState;
import com.trojan.proficiency.util.OneHandedWeapons;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

    @Inject(method = "renderArmWithItem", at = @At("HEAD"))
    private void proficiency$applyParryPose(
            AbstractClientPlayer player,
            float partialTick,
            float pitch,
            InteractionHand hand,
            float swingProgress,
            ItemStack stack,
            float equippedProgress,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int combinedLight,
            CallbackInfo callbackInfo
    ) {
        if (
                !ParryVisualState.isActive()
                        || hand != InteractionHand.MAIN_HAND
                        || !OneHandedWeapons.isSupported(stack)
                        || !player.getOffhandItem().isEmpty()
        ) {
            return;
        }

        poseStack.translate(-0.18, 0.08, -0.12);
        poseStack.mulPose(Axis.XP.rotationDegrees(-20.0f));
        poseStack.mulPose(Axis.YP.rotationDegrees(-25.0f));
        poseStack.mulPose(Axis.ZP.rotationDegrees(38.0f));
    }
}
