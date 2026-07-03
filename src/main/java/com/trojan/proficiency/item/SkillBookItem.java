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

public class SkillBookItem extends Item {

    private static final int SKILL_XP = 100;
    private final SkillType skillType;

    public SkillBookItem(SkillType skillType, Properties properties) {
        super(properties);
        this.skillType = skillType;
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

        switch (skillType) {
            case MINING -> SkillManager.addMiningXp(serverPlayer, SKILL_XP);
            case WOODCUTTING -> SkillManager.addWoodcuttingXp(serverPlayer, SKILL_XP);
            case FARMING -> SkillManager.addFarmingXp(serverPlayer, SKILL_XP);
        }

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        player.sendSystemMessage(Component.literal(
                "\u00A7aThe book grants 100 "
                        + skillType.getDisplayName()
                        + " XP."
        ));
        return InteractionResultHolder.consume(stack);
    }
}
