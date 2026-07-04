package com.trojan.proficiency.mixin;

import com.trojan.proficiency.SkillManager;
import com.trojan.proficiency.util.OneHandedWeapons;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class OneHandedLootingMixin {

    @Unique
    private ItemStack proficiency$lootingWeapon;
    @Unique
    private ItemEnchantments proficiency$originalEnchantments;

    @Inject(method = "dropAllDeathLoot", at = @At("HEAD"))
    private void proficiency$applyMasteryLooting(
            ServerLevel level,
            DamageSource source,
            CallbackInfo callbackInfo
    ) {
        if (!(source.getEntity() instanceof ServerPlayer player)
                || source.getDirectEntity() != player
                || !OneHandedWeapons.isSupported(player.getMainHandItem())
                || !SkillManager.hasOneHandedPerk(
                player.getUUID(),
                "trophy_collector"
        )) {
            return;
        }

        ItemStack weapon = player.getMainHandItem();
        ItemEnchantments original = weapon.get(DataComponents.ENCHANTMENTS);
        Holder<Enchantment> looting = level.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(Enchantments.LOOTING);
        ItemEnchantments.Mutable augmented = new ItemEnchantments.Mutable(
                original == null ? ItemEnchantments.EMPTY : original
        );
        augmented.set(looting, augmented.getLevel(looting) + 1);

        proficiency$lootingWeapon = weapon;
        proficiency$originalEnchantments = original;
        weapon.set(DataComponents.ENCHANTMENTS, augmented.toImmutable());
    }

    @Inject(method = "dropAllDeathLoot", at = @At("RETURN"))
    private void proficiency$restoreMasteryLooting(
            ServerLevel level,
            DamageSource source,
            CallbackInfo callbackInfo
    ) {
        if (proficiency$lootingWeapon == null) {
            return;
        }
        if (proficiency$originalEnchantments == null) {
            proficiency$lootingWeapon.remove(DataComponents.ENCHANTMENTS);
        } else {
            proficiency$lootingWeapon.set(
                    DataComponents.ENCHANTMENTS,
                    proficiency$originalEnchantments
            );
        }
        proficiency$lootingWeapon = null;
        proficiency$originalEnchantments = null;
    }
}
