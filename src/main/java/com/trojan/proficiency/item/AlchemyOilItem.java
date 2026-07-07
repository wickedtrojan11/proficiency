package com.trojan.proficiency.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class AlchemyOilItem extends Item {

    private final String oilId;

    public AlchemyOilItem(Properties properties, String oilId) {
        super(properties);
        this.oilId = oilId;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        ItemStack oilStack = player.getItemInHand(hand);
        ItemStack target = hand == InteractionHand.MAIN_HAND
                ? player.getOffhandItem()
                : player.getMainHandItem();
        OilRegistry.Entry oil = OilRegistry.get(oilId);

        if (oil == null || !oil.canApplyTo(target)) {
            return InteractionResultHolder.fail(oilStack);
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.sidedSuccess(oilStack, true);
        }

        if (!OilRegistry.isOilUnlocked(serverPlayer, oil)) {
            serverPlayer.sendSystemMessage(Component.literal(
                    "\u00A7cUnlock " + oil.displayName()
                            + " in Alchemy before using it."
            ));
            return InteractionResultHolder.fail(oilStack);
        }

        if (!OilRegistry.applyOil(serverPlayer, target, oil)) {
            return InteractionResultHolder.fail(oilStack);
        }

        if (!serverPlayer.getAbilities().instabuild) {
            oilStack.shrink(1);
        }
        level.playSound(
                null,
                serverPlayer.blockPosition(),
                SoundEvents.HONEY_BLOCK_PLACE,
                SoundSource.PLAYERS,
                0.45f,
                1.35f
        );
        serverPlayer.sendSystemMessage(Component.literal(
                "\u00A7a" + oil.displayName() + " coats your "
                        + target.getHoverName().getString()
                        + "."
        ));
        return InteractionResultHolder.consume(oilStack);
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltip,
            TooltipFlag flag
    ) {
        OilRegistry.Entry oil = OilRegistry.get(oilId);
        if (oil == null) {
            return;
        }

        tooltip.add(Component.literal("Alchemy oil coating.")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal(
                "Use while holding target gear in the opposite hand."
        ).withStyle(ChatFormatting.DARK_GRAY));
    }
}
