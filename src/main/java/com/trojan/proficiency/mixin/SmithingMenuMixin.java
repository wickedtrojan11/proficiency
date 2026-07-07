package com.trojan.proficiency.mixin;

import com.trojan.proficiency.item.OilRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SmithingTemplateItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SmithingMenu.class)
public abstract class SmithingMenuMixin extends ItemCombinerMenu {

    protected SmithingMenuMixin(
            MenuType<?> menuType,
            int containerId,
            Inventory inventory,
            ContainerLevelAccess access
    ) {
        super(menuType, containerId, inventory, access);
    }

    @Inject(
            method = "createInputSlotDefinitions",
            at = @At("HEAD"),
            cancellable = true
    )
    private void proficiency$allowOilInputs(
            CallbackInfoReturnable<ItemCombinerMenuSlotDefinition> callbackInfo
    ) {
        callbackInfo.setReturnValue(ItemCombinerMenuSlotDefinition.create()
                .withSlot(
                        0,
                        8,
                        48,
                        stack -> stack.getItem() instanceof SmithingTemplateItem
                )
                .withSlot(1, 26, 48, stack -> true)
                .withSlot(2, 44, 48, stack -> true)
                .withResultSlot(3, 98, 48)
                .build());
    }

    @Inject(
            method = "createResult",
            at = @At("TAIL")
    )
    private void proficiency$createOilResult(CallbackInfo callbackInfo) {
        if (!this.resultSlots.getItem(0).isEmpty()) {
            return;
        }
        if (!(this.player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        ItemStack result = proficiency$createOilApplicationResult(serverPlayer);
        if (!result.isEmpty()) {
            this.resultSlots.setItem(0, result);
        }
    }

    @Inject(
            method = "mayPickup",
            at = @At("HEAD"),
            cancellable = true
    )
    private void proficiency$mayPickupOilResult(
            Player player,
            boolean hasStack,
            CallbackInfoReturnable<Boolean> callbackInfo
    ) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!proficiency$createOilApplicationResult(serverPlayer).isEmpty()) {
            callbackInfo.setReturnValue(true);
        }
    }

    @Inject(
            method = "onTake",
            at = @At("HEAD"),
            cancellable = true
    )
    private void proficiency$takeOilResult(
            Player player,
            ItemStack stack,
            CallbackInfo callbackInfo
    ) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (proficiency$createOilApplicationResult(serverPlayer).isEmpty()) {
            return;
        }

        stack.onCraftedBy(player.level(), player, stack.getCount());
        this.inputSlots.removeItem(1, 1);
        this.inputSlots.removeItem(2, 1);
        this.resultSlots.setItem(0, ItemStack.EMPTY);
        this.access.execute((level, pos) -> level.playSound(
                null,
                pos,
                SoundEvents.SMITHING_TABLE_USE,
                SoundSource.BLOCKS,
                1.0f,
                1.0f
        ));
        callbackInfo.cancel();
    }

    @Unique
    private ItemStack proficiency$createOilApplicationResult(
            ServerPlayer player
    ) {
        ItemStack base = this.inputSlots.getItem(1);
        OilRegistry.Entry oil = OilRegistry.getByStack(this.inputSlots.getItem(2));
        if (base.isEmpty()
                || oil == null
                || !oil.canApplyTo(base)
                || !OilRegistry.isOilUnlocked(player, oil)) {
            return ItemStack.EMPTY;
        }

        ItemStack result = base.copyWithCount(1);
        return OilRegistry.applyOil(player, result, oil)
                ? result
                : ItemStack.EMPTY;
    }
}
