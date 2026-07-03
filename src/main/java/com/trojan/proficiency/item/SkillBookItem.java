package com.trojan.proficiency.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SkillBookItem extends Item {

    public SkillBookItem(Properties properties) {
        super(properties);
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

        SkillBookRegistry.Entry entry =
                SkillBookRegistry.getByItem(this);

        if (entry == null) {
            return InteractionResultHolder.fail(stack);
        }

        SkillBookRegistry.grantXp(serverPlayer, entry);

        stack.shrink(1);

        player.sendSystemMessage(Component.literal(
                "\u00A7a" + entry.displayName()
                        + " grants " + entry.xpAmount()
                        + " XP."
        ));
        return InteractionResultHolder.consume(stack);
    }
}
