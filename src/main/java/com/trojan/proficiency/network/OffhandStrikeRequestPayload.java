package com.trojan.proficiency.network;

import com.trojan.proficiency.ProficiencyMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record OffhandStrikeRequestPayload()
        implements CustomPacketPayload {

    public static final Type<OffhandStrikeRequestPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    ProficiencyMod.MOD_ID,
                    "offhand_strike_request"
            ));

    public static final StreamCodec<RegistryFriendlyByteBuf,
            OffhandStrikeRequestPayload> STREAM_CODEC =
            StreamCodec.unit(new OffhandStrikeRequestPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
