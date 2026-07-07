package com.trojan.proficiency.network;

import com.trojan.proficiency.ProficiencyMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record SkillStatePayload(
        UUID playerId,
        SkillState mining,
        SkillState woodcutting,
        SkillState farming,
        SkillState oneHanded,
        SkillState alchemy,
        int miningStreak
) implements CustomPacketPayload {

    public static final Type<SkillStatePayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            ProficiencyMod.MOD_ID,
                            "skill_state"
                    )
            );

    public static final StreamCodec<RegistryFriendlyByteBuf,
            SkillStatePayload> STREAM_CODEC =
            StreamCodec.of(
                    SkillStatePayload::encode,
                    SkillStatePayload::decode
            );

    private static void encode(
            RegistryFriendlyByteBuf buffer,
            SkillStatePayload payload
    ) {

        buffer.writeUUID(payload.playerId);
        writeSkillState(buffer, payload.mining);
        writeSkillState(buffer, payload.woodcutting);
        writeSkillState(buffer, payload.farming);
        writeSkillState(buffer, payload.oneHanded);
        writeSkillState(buffer, payload.alchemy);
        buffer.writeVarInt(payload.miningStreak);
    }

    private static SkillStatePayload decode(
            RegistryFriendlyByteBuf buffer
    ) {

        return new SkillStatePayload(
                buffer.readUUID(),
                readSkillState(buffer),
                readSkillState(buffer),
                readSkillState(buffer),
                readSkillState(buffer),
                readSkillState(buffer),
                buffer.readVarInt()
        );
    }

    public static void send(
            ServerPlayer player,
            SkillStatePayload payload
    ) {

        if (ServerPlayNetworking.canSend(player, TYPE)) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    private static void writeSkillState(
            RegistryFriendlyByteBuf buffer,
            SkillState state
    ) {

        buffer.writeVarInt(state.level);
        buffer.writeVarInt(state.xp);
        buffer.writeVarInt(state.requiredXp);
        buffer.writeVarInt(state.perkPoints);
        buffer.writeVarInt(state.prestige);

        buffer.writeVarInt(state.unlockedPerks.size());

        for (String perkId : state.unlockedPerks) {
            buffer.writeUtf(perkId);
        }

        buffer.writeVarInt(state.toggles.size());

        for (Map.Entry<String, Boolean> toggle
                : state.toggles.entrySet()) {

            buffer.writeUtf(toggle.getKey());
            buffer.writeBoolean(toggle.getValue());
        }
    }

    private static SkillState readSkillState(
            RegistryFriendlyByteBuf buffer
    ) {

        int level = buffer.readVarInt();
        int xp = buffer.readVarInt();
        int requiredXp = buffer.readVarInt();
        int perkPoints = buffer.readVarInt();
        int prestige = buffer.readVarInt();

        int perkCount = buffer.readVarInt();
        Set<String> unlockedPerks = new HashSet<>();

        for (int index = 0; index < perkCount; index++) {
            unlockedPerks.add(buffer.readUtf());
        }

        int toggleCount = buffer.readVarInt();
        Map<String, Boolean> toggles = new HashMap<>();

        for (int index = 0; index < toggleCount; index++) {

            toggles.put(
                    buffer.readUtf(),
                    buffer.readBoolean()
            );
        }

        return new SkillState(
                level,
                xp,
                requiredXp,
                perkPoints,
                prestige,
                unlockedPerks,
                toggles
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public record SkillState(
            int level,
            int xp,
            int requiredXp,
            int perkPoints,
            int prestige,
            Set<String> unlockedPerks,
            Map<String, Boolean> toggles
    ) {

        public SkillState {
            unlockedPerks = Set.copyOf(unlockedPerks);
            toggles = Map.copyOf(toggles);
        }
    }
}
