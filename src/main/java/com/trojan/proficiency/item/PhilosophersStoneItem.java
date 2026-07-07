package com.trojan.proficiency.item;

import com.trojan.proficiency.SkillManager;
import com.trojan.proficiency.event.AlchemyEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PhilosophersStoneItem extends Item {

    private static final String OWNER_ID = "Owner";
    private static final String OWNER_NAME = "OwnerName";
    private static final String BOUND_EFFECTS = "BoundEffects";
    private static final String XP_ELIXIR = "proficiency:experience_elixir";
    private static final int PERMANENT_TICKS = 20 * 60 * 60 * 24;

    public PhilosophersStoneItem(Properties properties) {
        super(properties);
    }

    @Override
    public void onCraftedBy(
            ItemStack stack,
            Level level,
            Player player
    ) {
        if (player instanceof ServerPlayer serverPlayer && !hasOwner(stack)) {
            setOwner(stack, serverPlayer);
            SkillManager.markOwnedPhilosophersStone(serverPlayer.getUUID());
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        if (!isOwner(stack, serverPlayer)) {
            serverPlayer.displayClientMessage(
                    Component.literal("This Philosopher's Stone does not answer to you.")
                            .withStyle(ChatFormatting.RED),
                    true
            );
            return InteractionResultHolder.fail(stack);
        }

        ItemStack other = player.getItemInHand(
                hand == InteractionHand.MAIN_HAND
                        ? InteractionHand.OFF_HAND
                        : InteractionHand.MAIN_HAND
        );
        if (player.isShiftKeyDown() && !other.isEmpty()) {
            return bindFromItem(serverPlayer, stack, other);
        }

        activate(serverPlayer, stack);
        return InteractionResultHolder.consume(stack);
    }

    private InteractionResultHolder<ItemStack> bindFromItem(
            ServerPlayer player,
            ItemStack stone,
            ItemStack source
    ) {
        String effectId = getBindableEffectId(source);
        if (effectId == null) {
            player.displayClientMessage(
                    Component.literal("That effect cannot be bound.")
                            .withStyle(ChatFormatting.RED),
                    true
            );
            return InteractionResultHolder.fail(stone);
        }

        int capacity = getCapacity(player);
        if (capacity <= 0) {
            player.displayClientMessage(
                    Component.literal("Unlock Eternal Catalyst first.")
                            .withStyle(ChatFormatting.RED),
                    true
            );
            return InteractionResultHolder.fail(stone);
        }

        ListTag effects = getEffects(stone);
        for (int index = 0; index < effects.size(); index++) {
            if (effectId.equals(effects.getString(index))) {
                player.displayClientMessage(
                        Component.literal("That effect is already bound.")
                                .withStyle(ChatFormatting.YELLOW),
                        true
                );
                return InteractionResultHolder.fail(stone);
            }
        }
        if (effects.size() >= capacity) {
            player.displayClientMessage(
                    Component.literal("The Stone cannot hold another effect yet.")
                            .withStyle(ChatFormatting.RED),
                    true
            );
            return InteractionResultHolder.fail(stone);
        }

        effects.add(StringTag.valueOf(effectId));
        setEffects(stone, effects);
        if (!player.getAbilities().instabuild) {
            source.shrink(1);
        }
        player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.ENCHANTMENT_TABLE_USE,
                SoundSource.PLAYERS,
                0.7f,
                1.35f
        );
        player.displayClientMessage(
                Component.literal("Effect bound to the Philosopher's Stone.")
                        .withStyle(ChatFormatting.LIGHT_PURPLE),
                true
        );
        return InteractionResultHolder.consume(stone);
    }

    private void activate(ServerPlayer player, ItemStack stone) {
        ListTag effects = getEffects(stone);
        if (effects.isEmpty()) {
            player.displayClientMessage(
                    Component.literal("No effects are bound.")
                            .withStyle(ChatFormatting.GRAY),
                    true
            );
            return;
        }
        for (int index = 0; index < effects.size(); index++) {
            applyEffect(player, effects.getString(index));
        }
        player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.BEACON_ACTIVATE,
                SoundSource.PLAYERS,
                0.6f,
                1.45f
        );
    }

    public static void clearDeathBoundEffects(ServerPlayer player) {
        for (Holder<MobEffect> effect : allowedEffects()) {
            player.removeEffect(effect);
        }
        SkillManager.setPhilosopherStoneXpBoost(player.getUUID(), false);
    }

    public static boolean isSoulboundOwnedStone(
            ItemStack stack,
            ServerPlayer player
    ) {
        return stack.is(ModItems.PHILOSOPHERS_STONE)
                && isOwner(stack, player)
                && SkillManager.hasAlchemyPerk(player.getUUID(), "magnum_opus");
    }

    private static void applyEffect(ServerPlayer player, String effectId) {
        if (XP_ELIXIR.equals(effectId)) {
            SkillManager.setPhilosopherStoneXpBoost(player.getUUID(), true);
            return;
        }
        Optional<Holder.Reference<MobEffect>> effect =
                BuiltInRegistries.MOB_EFFECT.getHolder(
                        ResourceLocation.parse(effectId)
                );
        if (effect.isEmpty() || !isAllowed(effect.get())) {
            return;
        }
        int amplifier = SkillManager.hasAlchemyPerk(
                player.getUUID(),
                "magnum_opus"
        ) && effect.get().is(MobEffects.MOVEMENT_SPEED) ? 1 : 0;
        player.addEffect(new MobEffectInstance(
                effect.get(),
                PERMANENT_TICKS,
                amplifier,
                true,
                false,
                true
        ));
    }

    private static String getBindableEffectId(ItemStack source) {
        if (source.is(ModItems.DOUBLE_XP_POTION)) {
            return XP_ELIXIR;
        }
        if (source.is(ModItems.TRIPLE_XP_POTION)) {
            return null;
        }
        PotionContents contents = source.get(DataComponents.POTION_CONTENTS);
        if (contents == null || !contents.hasEffects()) {
            return null;
        }
        for (MobEffectInstance effect : contents.getAllEffects()) {
            if (effect.getAmplifier() <= 1 && isAllowed(effect.getEffect())) {
                return BuiltInRegistries.MOB_EFFECT
                        .getKey(effect.getEffect().value())
                        .toString();
            }
        }
        return null;
    }

    private static boolean isAllowed(Holder<MobEffect> effect) {
        for (Holder<MobEffect> allowed : allowedEffects()) {
            if (effect.is(allowed)) {
                return true;
            }
        }
        return false;
    }

    private static List<Holder<MobEffect>> allowedEffects() {
        return List.of(
                MobEffects.MOVEMENT_SPEED,
                MobEffects.DAMAGE_BOOST,
                MobEffects.FIRE_RESISTANCE,
                MobEffects.WATER_BREATHING,
                MobEffects.NIGHT_VISION,
                MobEffects.DAMAGE_RESISTANCE,
                MobEffects.DIG_SPEED,
                MobEffects.JUMP,
                MobEffects.SLOW_FALLING
        );
    }

    private static int getCapacity(ServerPlayer player) {
        if (SkillManager.hasAlchemyPerk(player.getUUID(), "master_infusion")) {
            return 3;
        }
        if (SkillManager.hasAlchemyPerk(player.getUUID(), "perfect_harmony")) {
            return 2;
        }
        if (SkillManager.hasAlchemyPerk(player.getUUID(), "eternal_catalyst")) {
            return 1;
        }
        return 0;
    }

    private static boolean hasOwner(ItemStack stack) {
        return getTag(stack).hasUUID(OWNER_ID);
    }

    private static boolean isOwner(ItemStack stack, ServerPlayer player) {
        CompoundTag tag = getTag(stack);
        return tag.hasUUID(OWNER_ID)
                && tag.getUUID(OWNER_ID).equals(player.getUUID());
    }

    private static void setOwner(ItemStack stack, ServerPlayer player) {
        CompoundTag tag = getTag(stack);
        tag.putUUID(OWNER_ID, player.getUUID());
        tag.putString(OWNER_NAME, player.getGameProfile().getName());
        setTag(stack, tag);
    }

    private static ListTag getEffects(ItemStack stack) {
        return getTag(stack).getList(BOUND_EFFECTS, 8).copy();
    }

    private static void setEffects(ItemStack stack, ListTag effects) {
        CompoundTag tag = getTag(stack);
        tag.put(BOUND_EFFECTS, effects);
        setTag(stack, tag);
    }

    private static CompoundTag getTag(ItemStack stack) {
        return stack.getOrDefault(
                DataComponents.CUSTOM_DATA,
                CustomData.EMPTY
        ).copyTag();
    }

    private static void setTag(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltip,
            TooltipFlag flag
    ) {
        CompoundTag tag = getTag(stack);
        tooltip.add(Component.literal("A personal legendary artifact.")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        if (tag.contains(OWNER_NAME)) {
            tooltip.add(Component.literal("Owner: " + tag.getString(OWNER_NAME))
                    .withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.literal("Owner: Unbound")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
        ListTag effects = getEffects(stack);
        tooltip.add(Component.literal("Bound Effects: " + effects.size())
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Sneak-use with a potion to bind.")
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}
