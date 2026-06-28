package com.trojan.proficiency.network;

import com.trojan.proficiency.ProficiencyMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PerkUnlockRequestPayload(
        String skillId,
        String perkId
) implements CustomPacketPayload {

    public static final Type<PerkUnlockRequestPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            ProficiencyMod.MOD_ID,
                            "perk_unlock_request"
                    )
            );

    public static final StreamCodec<RegistryFriendlyByteBuf,
            PerkUnlockRequestPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    PerkUnlockRequestPayload::skillId,
                    ByteBufCodecs.STRING_UTF8,
                    PerkUnlockRequestPayload::perkId,
                    PerkUnlockRequestPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
