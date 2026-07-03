package com.trojan.proficiency.network;

import com.trojan.proficiency.ProficiencyMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record PrestigeRosterPayload(Map<UUID, Integer> prestigeByPlayer)
        implements CustomPacketPayload {

    public static final Type<PrestigeRosterPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    ProficiencyMod.MOD_ID,
                    "prestige_roster"
            ));

    public static final StreamCodec<RegistryFriendlyByteBuf,
            PrestigeRosterPayload> STREAM_CODEC = StreamCodec.of(
                    PrestigeRosterPayload::encode,
                    PrestigeRosterPayload::decode
            );

    public PrestigeRosterPayload {
        prestigeByPlayer = Map.copyOf(prestigeByPlayer);
    }

    private static void encode(
            RegistryFriendlyByteBuf buffer,
            PrestigeRosterPayload payload
    ) {
        buffer.writeVarInt(payload.prestigeByPlayer.size());
        payload.prestigeByPlayer.forEach((playerId, prestige) -> {
            buffer.writeUUID(playerId);
            buffer.writeVarInt(prestige);
        });
    }

    private static PrestigeRosterPayload decode(
            RegistryFriendlyByteBuf buffer
    ) {
        int count = buffer.readVarInt();
        Map<UUID, Integer> roster = new HashMap<>();
        for (int index = 0; index < count; index++) {
            roster.put(buffer.readUUID(), buffer.readVarInt());
        }
        return new PrestigeRosterPayload(roster);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
