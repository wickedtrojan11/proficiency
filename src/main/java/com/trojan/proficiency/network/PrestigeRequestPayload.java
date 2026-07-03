package com.trojan.proficiency.network;

import com.trojan.proficiency.ProficiencyMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PrestigeRequestPayload(String skillId)
        implements CustomPacketPayload {

    public static final Type<PrestigeRequestPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    ProficiencyMod.MOD_ID,
                    "prestige_request"
            ));

    public static final StreamCodec<RegistryFriendlyByteBuf,
            PrestigeRequestPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    PrestigeRequestPayload::skillId,
                    PrestigeRequestPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
