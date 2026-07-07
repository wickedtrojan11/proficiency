package com.trojan.proficiency.item;

import com.trojan.proficiency.SkillManager;
import com.trojan.proficiency.skill.SkillType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AncientAlchemyNotesItem extends Item {

    public AncientAlchemyNotesItem(Properties properties) {
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

        if (SkillManager.getPrestige(
                serverPlayer.getUUID(),
                SkillType.ALCHEMY
        ) < 2) {
            serverPlayer.sendSystemMessage(Component.literal(
                    "\u00A75The notes are too advanced. Reach Alchemy Prestige II."
            ));
            return InteractionResultHolder.fail(stack);
        }

        ExperimentalAlchemyRegistry.Entry recipe =
                ExperimentalAlchemyRegistry.randomUnknown(
                        SkillManager.getDiscoveredAlchemyKeys(
                                serverPlayer.getUUID()
                        ),
                        serverPlayer.getRandom()
                );

        if (recipe == null) {
            serverPlayer.sendSystemMessage(Component.literal(
                    "\u00A77You already understand these ancient notes."
            ));
            return InteractionResultHolder.fail(stack);
        }

        SkillManager.discoverAlchemyIngredient(
                serverPlayer,
                recipe.discoveryKey()
        );
        stack.shrink(1);
        serverPlayer.sendSystemMessage(Component.literal(
                "\u00A7dExperimental recipe discovered: "
                        + recipe.displayName()
        ));
        return InteractionResultHolder.consume(stack);
    }
}
