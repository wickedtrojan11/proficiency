package com.trojan.proficiency.event;

import com.trojan.proficiency.item.ModItems;
import com.trojan.proficiency.item.PhilosophersStoneItem;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class AlchemyPhilosopherEvents {

    private AlchemyPhilosopherEvents() {
    }

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (entity instanceof ServerPlayer player) {
                PhilosophersStoneItem.clearDeathBoundEffects(player);
            }
        });

        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            if (alive) {
                return;
            }
            for (int slot = 0; slot < oldPlayer.getInventory().getContainerSize(); slot++) {
                ItemStack stack = oldPlayer.getInventory().getItem(slot);
                if (PhilosophersStoneItem.isSoulboundOwnedStone(stack, oldPlayer)
                        && !newPlayer.getInventory().contains(new ItemStack(ModItems.PHILOSOPHERS_STONE))) {
                    newPlayer.getInventory().add(stack.copy());
                }
            }
        });
    }
}
