package com.trojan.proficiency.item;

import com.trojan.proficiency.SkillManager;
import com.trojan.proficiency.skill.SkillType;
import com.trojan.proficiency.util.OneHandedWeapons;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class OilRegistry {

    private static final String OIL_ID_KEY = "proficiency_oil_id";
    private static final String OIL_CHARGES_KEY = "proficiency_oil_charges";
    private static final int BASE_CHARGES = 250;
    private static final Map<String, Entry> BY_ID = new LinkedHashMap<>();
    private static final Map<Item, Entry> BY_ITEM = new IdentityHashMap<>();

    private OilRegistry() {
    }

    public static Entry register(
            String id,
            Item item,
            String displayName,
            Target target,
            List<Component> tooltip
    ) {
        if (
                id == null
                        || id.isBlank()
                        || item == null
                        || displayName == null
                        || displayName.isBlank()
                        || target == null
                        || BY_ID.containsKey(id)
                        || BY_ITEM.containsKey(item)
        ) {
            throw new IllegalArgumentException(
                    "Invalid or duplicate oil registration: " + id
            );
        }
        Entry entry = new Entry(id, item, displayName, target, tooltip);
        BY_ID.put(id, entry);
        BY_ITEM.put(item, entry);
        return entry;
    }

    public static Entry get(String id) {
        return BY_ID.get(id);
    }

    public static Entry getByItem(Item item) {
        return BY_ITEM.get(item);
    }

    public static Collection<Entry> entries() {
        return BY_ID.values();
    }

    public static void validate() {
        if (BY_ID.isEmpty() || BY_ID.size() != BY_ITEM.size()) {
            throw new IllegalStateException(
                    "Oil registry contains incomplete or duplicate entries"
            );
        }
    }

    public static boolean applyOil(
            ServerPlayer player,
            ItemStack targetStack,
            Entry oil
    ) {
        if (oil == null || !oil.canApplyTo(targetStack)) {
            return false;
        }
        if (!SkillManager.isAlchemyToggleEnabled(player.getUUID(), "oils")) {
            return false;
        }

        CompoundTag tag = targetStack.getOrDefault(
                DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.EMPTY
        ).copyTag();
        tag.putString(OIL_ID_KEY, oil.id());
        tag.putInt(OIL_CHARGES_KEY, getMaxCharges(player));
        targetStack.set(
                DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.of(tag)
        );
        targetStack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        return true;
    }

    public static Entry getAppliedOil(ItemStack stack) {
        String id = getAppliedOilId(stack);
        return id == null ? null : get(id);
    }

    public static String getAppliedOilId(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(
                DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.EMPTY
        ).copyTag();
        return tag.contains(OIL_ID_KEY) ? tag.getString(OIL_ID_KEY) : null;
    }

    public static int getRemainingCharges(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(
                DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.EMPTY
        ).copyTag();
        return tag.contains(OIL_CHARGES_KEY)
                ? Math.max(0, tag.getInt(OIL_CHARGES_KEY))
                : 0;
    }

    public static boolean consumeCharge(ItemStack stack) {
        Entry oil = getAppliedOil(stack);
        if (oil == null) {
            return false;
        }

        CompoundTag tag = stack.getOrDefault(
                DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.EMPTY
        ).copyTag();
        int charges = Math.max(0, tag.getInt(OIL_CHARGES_KEY)) - 1;
        if (charges <= 0) {
            tag.remove(OIL_ID_KEY);
            tag.remove(OIL_CHARGES_KEY);
            if (tag.isEmpty()) {
                stack.remove(DataComponents.CUSTOM_DATA);
            } else {
                stack.set(
                        DataComponents.CUSTOM_DATA,
                        net.minecraft.world.item.component.CustomData.of(tag)
                );
            }
            stack.remove(DataComponents.ENCHANTMENT_GLINT_OVERRIDE);
            return true;
        }

        tag.putInt(OIL_CHARGES_KEY, charges);
        stack.set(
                DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.of(tag)
        );
        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        return true;
    }

    public static boolean appendAppliedOilTooltip(
            ItemStack stack,
            List<Component> tooltip
    ) {
        Entry oil = getAppliedOil(stack);
        if (oil == null) {
            return false;
        }
        tooltip.add(Component.literal(
                "Current Oil: " + oil.displayName()
        ).withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.literal(
                "Remaining Charges: " + getRemainingCharges(stack)
        ).withStyle(ChatFormatting.GRAY));
        return true;
    }

    public static int getMaxCharges(ServerPlayer player) {
        if (SkillManager.hasAlchemyPerk(
                player.getUUID(),
                "everlasting_sheen"
        )) {
            return 400;
        }
        if (SkillManager.hasAlchemyPerk(player.getUUID(), "polished_edge")) {
            return 350;
        }
        if (SkillManager.hasAlchemyPerk(player.getUUID(), "oilers_touch")) {
            return 300;
        }
        return BASE_CHARGES;
    }

    public static float getDurabilitySaveChance(
            ServerPlayer player,
            ItemStack stack
    ) {
        Entry oil = getAppliedOil(stack);
        if (oil == null) {
            return 0.0f;
        }
        if (!SkillManager.isAlchemyToggleEnabled(player.getUUID(), "oils")) {
            return 0.0f;
        }

        float chance = switch (oil.id()) {
            case "camellia" -> 0.20f;
            case "miners", "lumber" -> 0.08f;
            default -> 0.0f;
        };
        if (chance <= 0.0f) {
            return 0.0f;
        }
        if (SkillManager.hasAlchemyPerk(player.getUUID(), "oilers_touch")) {
            chance += 0.04f;
        }
        if (SkillManager.hasAlchemyPerk(player.getUUID(), "polished_edge")) {
            chance += 0.05f;
        }
        if (SkillManager.hasAlchemyPerk(
                player.getUUID(),
                "everlasting_sheen"
        )) {
            chance += 0.06f;
        }
        return SkillManager.scalePerkChance(
                player.getUUID(),
                SkillType.ALCHEMY,
                Math.min(0.45f, chance)
        );
    }

    public static boolean tryPreserveDurability(
            ServerPlayer player,
            ItemStack stack,
            RandomSource random
    ) {
        float chance = getDurabilitySaveChance(player, stack);
        if (chance <= 0.0f || random.nextFloat() > chance) {
            return false;
        }
        int damage = stack.getDamageValue();
        if (damage > 0) {
            stack.setDamageValue(damage - 1);
        }
        consumeCharge(stack);
        return true;
    }

    public static int getFireTicks(ServerPlayer player) {
        int ticks = 60;
        if (SkillManager.hasAlchemyPerk(player.getUUID(), "oilers_touch")) {
            ticks += 20;
        }
        if (SkillManager.hasAlchemyPerk(player.getUUID(), "polished_edge")) {
            ticks += 20;
        }
        if (SkillManager.hasAlchemyPerk(
                player.getUUID(),
                "everlasting_sheen"
        )) {
            ticks += 20;
        }
        return ticks;
    }

    public static int getFrostTicks(ServerPlayer player) {
        int ticks = 50;
        if (SkillManager.hasAlchemyPerk(player.getUUID(), "oilers_touch")) {
            ticks += 15;
        }
        if (SkillManager.hasAlchemyPerk(player.getUUID(), "polished_edge")) {
            ticks += 15;
        }
        if (SkillManager.hasAlchemyPerk(
                player.getUUID(),
                "everlasting_sheen"
        )) {
            ticks += 20;
        }
        return ticks;
    }

    public enum Target {
        DAMAGEABLE {
            @Override
            public boolean matches(ItemStack stack) {
                return stack.isDamageableItem();
            }
        },
        WEAPON {
            @Override
            public boolean matches(ItemStack stack) {
                return OneHandedWeapons.isSupported(stack);
            }
        },
        PICKAXE {
            @Override
            public boolean matches(ItemStack stack) {
                return stack.getItem() instanceof PickaxeItem;
            }
        },
        AXE {
            @Override
            public boolean matches(ItemStack stack) {
                return stack.getItem() instanceof AxeItem;
            }
        };

        public abstract boolean matches(ItemStack stack);
    }

    public record Entry(
            String id,
            Item item,
            String displayName,
            Target target,
            List<Component> tooltip
    ) {
        public boolean canApplyTo(ItemStack stack) {
            return !stack.isEmpty() && target.matches(stack);
        }
    }
}
