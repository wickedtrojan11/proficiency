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
    private static final String SECONDARY_OIL_ID_KEY =
            "proficiency_oil_id_2";
    private static final String SECONDARY_OIL_CHARGES_KEY =
            "proficiency_oil_charges_2";
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
            String requiredPerkId,
            List<Component> tooltip
    ) {
        if (
                id == null
                        || id.isBlank()
                        || item == null
                        || displayName == null
                        || displayName.isBlank()
                        || target == null
                        || requiredPerkId == null
                        || requiredPerkId.isBlank()
                        || BY_ID.containsKey(id)
                        || BY_ITEM.containsKey(item)
        ) {
            throw new IllegalArgumentException(
                    "Invalid or duplicate oil registration: " + id
            );
        }
        Entry entry = new Entry(
                id,
                item,
                displayName,
                target,
                requiredPerkId,
                tooltip
        );
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

    public static Entry getByStack(ItemStack stack) {
        return stack.isEmpty() ? null : getByItem(stack.getItem());
    }

    public static boolean isOilItem(ItemStack stack) {
        return getByStack(stack) != null;
    }

    public static boolean canAnyOilApplyTo(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return BY_ID.values().stream().anyMatch(oil -> oil.canApplyTo(stack));
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
        if (!oil.isUnlocked(player)) {
            return false;
        }

        CompoundTag tag = targetStack.getOrDefault(
                DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.EMPTY
        ).copyTag();
        if (!hasPerfectCoating(player)) {
            tag.remove(SECONDARY_OIL_ID_KEY);
            tag.remove(SECONDARY_OIL_CHARGES_KEY);
        }
        int slot = findApplicationSlot(player, tag, oil.id());
        tag.putString(oilIdKey(slot), oil.id());
        tag.putInt(oilChargesKey(slot), getMaxCharges(player));
        targetStack.set(
                DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.of(tag)
        );
        targetStack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        return true;
    }

    public static Entry getAppliedOil(ItemStack stack) {
        List<AppliedOil> oils = getAppliedOils(stack);
        return oils.isEmpty() ? null : oils.getFirst().entry();
    }

    public static String getAppliedOilId(ItemStack stack) {
        Entry oil = getAppliedOil(stack);
        return oil == null ? null : oil.id();
    }

    public static List<AppliedOil> getAppliedOils(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(
                DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.EMPTY
        ).copyTag();
        java.util.ArrayList<AppliedOil> oils = new java.util.ArrayList<>();
        addAppliedOil(oils, tag, 0);
        addAppliedOil(oils, tag, 1);
        return oils;
    }

    public static int getRemainingCharges(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(
                DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.EMPTY
        ).copyTag();
        int charges = 0;
        for (AppliedOil oil : getAppliedOils(stack)) {
            charges += oil.charges();
        }
        return charges;
    }

    public static boolean consumeCharge(ItemStack stack) {
        AppliedOil oil = getAppliedOils(stack).stream()
                .findFirst()
                .orElse(null);
        return oil != null && consumeCharge(stack, oil.entry().id());
    }

    public static boolean consumeCharge(ItemStack stack, String oilId) {
        AppliedOil oil = getAppliedOils(stack).stream()
                .filter(appliedOil -> appliedOil.entry().id().equals(oilId))
                .findFirst()
                .orElse(null);
        if (oil == null) {
            return false;
        }

        CompoundTag tag = stack.getOrDefault(
                DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.EMPTY
        ).copyTag();
        int charges = Math.max(
                0,
                tag.getInt(oilChargesKey(oil.slot()))
        ) - 1;
        if (charges <= 0) {
            tag.remove(oilIdKey(oil.slot()));
            tag.remove(oilChargesKey(oil.slot()));
            if (tag.isEmpty()) {
                stack.remove(DataComponents.CUSTOM_DATA);
            } else {
                stack.set(
                        DataComponents.CUSTOM_DATA,
                        net.minecraft.world.item.component.CustomData.of(tag)
                );
            }
            if (getAppliedOils(stack).isEmpty()) {
                stack.remove(DataComponents.ENCHANTMENT_GLINT_OVERRIDE);
            }
            return true;
        }

        tag.putInt(oilChargesKey(oil.slot()), charges);
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
        List<AppliedOil> oils = getAppliedOils(stack);
        if (oils.isEmpty()) {
            return false;
        }
        for (AppliedOil oil : oils) {
            tooltip.add(Component.literal(
                    "Current Oil: " + oil.entry().displayName()
            ).withStyle(ChatFormatting.AQUA));
            tooltip.add(Component.literal(
                    "Remaining Charges: " + oil.charges()
            ).withStyle(ChatFormatting.GRAY));
        }
        return true;
    }

    public static int getMaxCharges(ServerPlayer player) {
        return hasPerfectCoating(player) ? BASE_CHARGES * 2 : BASE_CHARGES;
    }

    public static float getDurabilitySaveChance(
            ServerPlayer player,
            ItemStack stack
    ) {
        AppliedOil oil = getAppliedOils(stack).stream()
                .filter(appliedOil -> appliedOil.entry().isDurabilityOil()
                        && appliedOil.entry().isUnlocked(player))
                .findFirst()
                .orElse(null);
        if (oil == null) {
            return 0.0f;
        }
        if (!SkillManager.isAlchemyToggleEnabled(player.getUUID(), "oils")) {
            return 0.0f;
        }

        float chance = switch (oil.entry().id()) {
            case "camellia" -> 0.20f;
            case "miners", "lumber" -> 0.08f;
            default -> 0.0f;
        };
        if (chance <= 0.0f) {
            return 0.0f;
        }
        return SkillManager.scalePerkChance(
                player.getUUID(),
                SkillType.ALCHEMY,
                chance
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
        AppliedOil oil = getAppliedOils(stack).stream()
                .filter(appliedOil -> appliedOil.entry().isDurabilityOil()
                        && appliedOil.entry().isUnlocked(player))
                .findFirst()
                .orElse(null);
        if (oil != null) {
            consumeCharge(stack, oil.entry().id());
        }
        return true;
    }

    public static int getFireTicks(ServerPlayer player) {
        return 60;
    }

    public static int getFrostTicks(ServerPlayer player) {
        return 50;
    }

    public static boolean hasOil(ItemStack stack, String oilId) {
        return getAppliedOils(stack).stream()
                .anyMatch(oil -> oil.entry().id().equals(oilId));
    }

    public static boolean hasUsableOil(
            ServerPlayer player,
            ItemStack stack,
            String oilId
    ) {
        return getAppliedOils(stack).stream()
                .anyMatch(oil -> oil.entry().id().equals(oilId)
                        && oil.entry().isUnlocked(player));
    }

    public static boolean isOilUnlocked(ServerPlayer player, Entry oil) {
        return oil != null && oil.isUnlocked(player);
    }

    private static boolean hasPerfectCoating(ServerPlayer player) {
        return SkillManager.hasAlchemyPerk(
                player.getUUID(),
                "perfect_coating"
        );
    }

    private static int findApplicationSlot(
            ServerPlayer player,
            CompoundTag tag,
            String oilId
    ) {
        if (oilId.equals(tag.getString(OIL_ID_KEY))) {
            return 0;
        }
        if (oilId.equals(tag.getString(SECONDARY_OIL_ID_KEY))) {
            return 1;
        }
        if (!tag.contains(OIL_ID_KEY)) {
            return 0;
        }
        if (hasPerfectCoating(player) && !tag.contains(SECONDARY_OIL_ID_KEY)) {
            return 1;
        }
        return 0;
    }

    private static void addAppliedOil(
            List<AppliedOil> oils,
            CompoundTag tag,
            int slot
    ) {
        String idKey = oilIdKey(slot);
        String chargesKey = oilChargesKey(slot);
        if (!tag.contains(idKey)) {
            return;
        }
        Entry entry = get(tag.getString(idKey));
        int charges = Math.max(0, tag.getInt(chargesKey));
        if (entry != null && charges > 0) {
            oils.add(new AppliedOil(entry, slot, charges));
        }
    }

    private static String oilIdKey(int slot) {
        return slot == 0 ? OIL_ID_KEY : SECONDARY_OIL_ID_KEY;
    }

    private static String oilChargesKey(int slot) {
        return slot == 0 ? OIL_CHARGES_KEY : SECONDARY_OIL_CHARGES_KEY;
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
            String requiredPerkId,
            List<Component> tooltip
    ) {
        public boolean canApplyTo(ItemStack stack) {
            return !stack.isEmpty() && target.matches(stack);
        }

        public boolean isUnlocked(ServerPlayer player) {
            return SkillManager.hasAlchemyPerk(
                    player.getUUID(),
                    requiredPerkId
            );
        }

        public boolean isDurabilityOil() {
            return "camellia".equals(id)
                    || "miners".equals(id)
                    || "lumber".equals(id);
        }
    }

    public record AppliedOil(Entry entry, int slot, int charges) {
    }
}
