package com.trojan.proficiency.network;

import com.trojan.proficiency.ProficiencyMod;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record AlchemyXpBuffPayload(
        int multiplier,
        int remainingTicks
) implements CustomPacketPayload {

    public static final Type<AlchemyXpBuffPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            ProficiencyMod.MOD_ID,
                            "alchemy_xp_buff"
                    )
            );

    public static final StreamCodec<RegistryFriendlyByteBuf,
            AlchemyXpBuffPayload> STREAM_CODEC = StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    AlchemyXpBuffPayload::multiplier,
                    ByteBufCodecs.VAR_INT,
                    AlchemyXpBuffPayload::remainingTicks,
                    AlchemyXpBuffPayload::new
            );

    public static void send(
            ServerPlayer player,
            int multiplier,
            int remainingTicks
    ) {
        if (ServerPlayNetworking.canSend(player, TYPE)) {
            ServerPlayNetworking.send(
                    player,
                    new AlchemyXpBuffPayload(multiplier, remainingTicks)
            );
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
