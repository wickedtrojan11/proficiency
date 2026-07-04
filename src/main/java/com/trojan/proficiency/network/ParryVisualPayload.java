package com.trojan.proficiency.network;

import com.trojan.proficiency.ProficiencyMod;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record ParryVisualPayload(int durationTicks)
        implements CustomPacketPayload {

    public static final Type<ParryVisualPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    ProficiencyMod.MOD_ID,
                    "parry_visual"
            ));

    public static final StreamCodec<RegistryFriendlyByteBuf,
            ParryVisualPayload> STREAM_CODEC = StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    ParryVisualPayload::durationTicks,
                    ParryVisualPayload::new
            );

    public static void send(ServerPlayer player, int durationTicks) {
        if (ServerPlayNetworking.canSend(player, TYPE)) {
            ServerPlayNetworking.send(
                    player,
                    new ParryVisualPayload(durationTicks)
            );
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
