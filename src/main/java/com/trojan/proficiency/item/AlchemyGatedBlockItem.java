package com.trojan.proficiency.item;

import com.trojan.proficiency.SkillManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;

public class AlchemyGatedBlockItem extends BlockItem {

    private final String perkId;
    private final String perkName;

    public AlchemyGatedBlockItem(
            Block block,
            Properties properties,
            String perkId,
            String perkName
    ) {
        super(block, properties);
        this.perkId = perkId;
        this.perkName = perkName;
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        if (
                context.getPlayer() instanceof ServerPlayer player
                        && !SkillManager.hasAlchemyPerk(
                        player.getUUID(),
                        perkId
                )
        ) {
            player.displayClientMessage(
                    Component.literal(
                            "Requires " + perkName + "."
                    ).withStyle(ChatFormatting.RED),
                    true
            );
            return InteractionResult.FAIL;
        }

        return super.place(context);
    }
}
