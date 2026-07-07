package com.trojan.proficiency.item;

import com.trojan.proficiency.SkillManager;
import com.trojan.proficiency.event.AlchemyEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
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
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(
            ItemStack stack,
            Level level,
            LivingEntity entity
    ) {
        int appliedDurationTicks = AlchemyEvents.getXpPotionDuration(stack);

        if (entity instanceof ServerPlayer serverPlayer) {
            SkillManager.grantAlchemyXpBuff(
                    serverPlayer,
                    multiplier,
                    appliedDurationTicks
            );
            serverPlayer.sendSystemMessage(Component.literal(
                    "\u00A7d" + getDescription().getString()
                            + " active: skill XP x" + multiplier + " for "
                            + formatDuration(appliedDurationTicks)
                            + "."
            ));
        }

        if (
                entity instanceof Player player
                        && !player.getAbilities().instabuild
        ) {
            stack.shrink(1);
            if (stack.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }
            player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
        }

        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
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
                multiplier == 3
                        ? "Triples skill XP temporarily."
                        : "Doubles skill XP temporarily."
        ));
        tooltip.add(Component.literal(
                "Duration: " + formatDuration(durationSeconds * 20)
        ));
        tooltip.add(Component.literal(
                "Honey extension: +"
                        + formatDuration(
                        AlchemyEvents.getHoneyExtensionTicks(stack)
                )
        ));
        tooltip.add(Component.literal(
                "Does not stack with weaker XP multipliers; strongest applies."
        ));
    }

    private static String formatDuration(int ticks) {
        int totalSeconds = (ticks + 19) / 20;
        return totalSeconds / 60
                + ":"
                + String.format("%02d", totalSeconds % 60);
    }
}
