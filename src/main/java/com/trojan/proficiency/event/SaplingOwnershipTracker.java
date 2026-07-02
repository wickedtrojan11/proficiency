package com.trojan.proficiency.event;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.SaplingBlock;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SaplingOwnershipTracker {

    private static final Map<ServerLevel, Map<BlockPos, UUID>> OWNERS =
            new HashMap<>();

    private SaplingOwnershipTracker() {
    }

    public static void register() {

        PlayerBlockBreakEvents.AFTER.register(
                (level, player, pos, state, blockEntity) -> {

                    if (
                            level instanceof ServerLevel serverLevel
                                    && state.getBlock()
                                    instanceof SaplingBlock
                    ) {
                        remove(serverLevel, pos);
                    }
                }
        );
    }

    public static void clear() {
        OWNERS.clear();
    }

    public static void record(
            ServerLevel level,
            BlockPos pos,
            UUID playerId
    ) {

        OWNERS.computeIfAbsent(
                level,
                ignored -> new HashMap<>()
        ).put(pos.immutable(), playerId);
    }

    public static UUID takeOwner(
            ServerLevel level,
            BlockPos pos
    ) {

        Map<BlockPos, UUID> owners = OWNERS.get(level);

        if (owners == null) {
            return null;
        }

        UUID owner = owners.get(pos);

        for (BlockPos nearby
                : BlockPos.betweenClosed(
                pos.offset(-1, 0, -1),
                pos.offset(1, 0, 1)
        )) {
            owners.remove(nearby);
        }

        if (owners.isEmpty()) {
            OWNERS.remove(level);
        }

        return owner;
    }

    private static void remove(
            ServerLevel level,
            BlockPos pos
    ) {

        Map<BlockPos, UUID> owners = OWNERS.get(level);

        if (owners == null) {
            return;
        }

        owners.remove(pos);

        if (owners.isEmpty()) {
            OWNERS.remove(level);
        }
    }
}
