package com.trojan.proficiency.item;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

import java.util.List;

public class CamelliaOilItem extends Item {

    public CamelliaOilItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        ItemStack oil = player.getItemInHand(hand);
        ItemStack target = hand == InteractionHand.MAIN_HAND
                ? player.getOffhandItem()
                : player.getMainHandItem();

        if (target.isEmpty() || !target.isDamageableItem()) {
            return InteractionResultHolder.fail(oil);
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.sidedSuccess(oil, true);
        }

        Holder<Enchantment> unbreaking =
                serverPlayer.registryAccess()
                        .registryOrThrow(Registries.ENCHANTMENT)
                        .getHolderOrThrow(Enchantments.UNBREAKING);
        ItemEnchantments current =
                target.getOrDefault(
                        DataComponents.ENCHANTMENTS,
                        ItemEnchantments.EMPTY
                );
        ItemEnchantments.Mutable mutable =
                new ItemEnchantments.Mutable(current);
        mutable.set(unbreaking, Math.max(1, mutable.getLevel(unbreaking)));
        target.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());

        if (!serverPlayer.getAbilities().instabuild) {
            oil.shrink(1);
        }

        serverPlayer.sendSystemMessage(Component.literal(
                "\u00A7aCamellia Oil protects your "
                        + target.getHoverName().getString()
                        + "."
        ));

        return InteractionResultHolder.consume(oil);
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltip,
            TooltipFlag flag
    ) {
        tooltip.add(Component.literal(
                "Apply to a held tool or weapon to add a protective gleam."
        ));
        tooltip.add(Component.literal(
                "Adds an Unbreaking-style durability benefit."
        ));
    }
}
