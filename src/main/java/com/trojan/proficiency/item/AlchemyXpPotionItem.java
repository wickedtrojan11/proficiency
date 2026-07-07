package com.trojan.proficiency.item;

import com.trojan.proficiency.SkillManager;
import com.trojan.proficiency.event.AlchemyEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class AlchemyXpPotionItem extends Item {

    private final int multiplier;
    private final int durationTicks;

    public AlchemyXpPotionItem(
            Properties properties,
            int multiplier,
            int durationTicks
    ) {
        super(properties);
        this.multiplier = multiplier;
        this.durationTicks = durationTicks;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.sidedSuccess(stack, true);
        }

        SkillManager.grantAlchemyXpBuff(
                serverPlayer,
                multiplier,
                AlchemyEvents.getXpPotionDuration(stack)
        );

        if (!serverPlayer.getAbilities().instabuild) {
            stack.shrink(1);
            if (stack.isEmpty()) {
                return InteractionResultHolder.consume(
                        new ItemStack(Items.GLASS_BOTTLE)
                );
            }
            serverPlayer.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
        }

        serverPlayer.sendSystemMessage(Component.literal(
                "\u00A7dSkill XP x" + multiplier + " for "
                        + durationTicks / 20 / 60 + " minutes."
        ));

        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltip,
            TooltipFlag flag
    ) {
        int durationSeconds = AlchemyEvents.getXpPotionDuration(stack) / 20;
        tooltip.add(Component.literal(
                "Skill XP x" + multiplier + " for "
                        + durationSeconds / 60 + ":"
                        + String.format("%02d", durationSeconds % 60)
        ));
        tooltip.add(Component.literal("Strongest active XP multiplier wins."));
    }
}
