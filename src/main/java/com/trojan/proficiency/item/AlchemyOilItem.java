package com.trojan.proficiency.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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
        if (!level.isClientSide) {
            player.sendSystemMessage(Component.literal(
                    "\u00A77Apply oils to gear at a Smithing Table."
            ));
        }
        return InteractionResultHolder.pass(oilStack);
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
                "Apply to valid gear at a Smithing Table."
        ).withStyle(ChatFormatting.DARK_GRAY));
    }
}
