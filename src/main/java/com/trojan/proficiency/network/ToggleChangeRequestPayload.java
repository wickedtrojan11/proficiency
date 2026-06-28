package com.trojan.proficiency.network;

import com.trojan.proficiency.ProficiencyMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ToggleChangeRequestPayload(
        String skillId,
        String toggleId,
        boolean desiredState
) implements CustomPacketPayload {

    public static final Type<ToggleChangeRequestPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            ProficiencyMod.MOD_ID,
                            "toggle_change_request"
                    )
            );

    public static final StreamCodec<RegistryFriendlyByteBuf,
            ToggleChangeRequestPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    ToggleChangeRequestPayload::skillId,
                    ByteBufCodecs.STRING_UTF8,
                    ToggleChangeRequestPayload::toggleId,
                    ByteBufCodecs.BOOL,
                    ToggleChangeRequestPayload::desiredState,
                    ToggleChangeRequestPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
