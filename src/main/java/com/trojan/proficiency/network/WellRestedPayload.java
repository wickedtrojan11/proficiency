package com.trojan.proficiency.network;

import com.trojan.proficiency.ProficiencyMod;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record WellRestedPayload(
        int remainingTicks
) implements CustomPacketPayload {

    public static final Type<WellRestedPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            ProficiencyMod.MOD_ID,
                            "well_rested"
                    )
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, WellRestedPayload>
            STREAM_CODEC = StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    WellRestedPayload::remainingTicks,
                    WellRestedPayload::new
            );

    public static void send(
            ServerPlayer player,
            int remainingTicks
    ) {

        if (ServerPlayNetworking.canSend(player, TYPE)) {

            ServerPlayNetworking.send(
                    player,
                    new WellRestedPayload(remainingTicks)
            );
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
