package com.trojan.proficiency.network;

import com.trojan.proficiency.ProficiencyMod;
import com.trojan.proficiency.skill.SkillType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record XpGainPayload(
        int skillOrdinal,
        int amount
) implements CustomPacketPayload {

    public static final Type<XpGainPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            ProficiencyMod.MOD_ID,
                            "xp_gain"
                    )
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, XpGainPayload>
            STREAM_CODEC = StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    XpGainPayload::skillOrdinal,
                    ByteBufCodecs.VAR_INT,
                    XpGainPayload::amount,
                    XpGainPayload::new
            );

    public static void send(
            ServerPlayer player,
            SkillType skillType,
            int amount
    ) {

        if (ServerPlayNetworking.canSend(player, TYPE)) {

            ServerPlayNetworking.send(
                    player,
                    new XpGainPayload(
                            skillType.ordinal(),
                            amount
                    )
            );
        }
    }

    public SkillType skillType() {

        SkillType[] skillTypes = SkillType.values();

        if (
                skillOrdinal < 0
                        || skillOrdinal >= skillTypes.length
        ) {

            return SkillType.MINING;
        }

        return skillTypes[skillOrdinal];
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
