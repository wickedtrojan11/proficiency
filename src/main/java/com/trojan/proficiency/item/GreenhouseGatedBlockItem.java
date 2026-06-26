package com.trojan.proficiency.item;

import com.trojan.proficiency.SkillManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;

public class GreenhouseGatedBlockItem extends BlockItem {

    public GreenhouseGatedBlockItem(
            Block block,
            Properties properties
    ) {

        super(block, properties);
    }

    @Override
    public InteractionResult place(
            BlockPlaceContext context
    ) {

        if (
                context.getPlayer()
                        instanceof ServerPlayer player
                        && !SkillManager.hasFarmingPerk(
                        player.getUUID(),
                        "greenhouse_genius"
                )
        ) {

            player.displayClientMessage(
                    Component.literal(
                            "Requires Greenhouse Genius."
                    ).withStyle(ChatFormatting.RED),
                    true
            );

            return InteractionResult.FAIL;
        }

        return super.place(context);
    }
}
