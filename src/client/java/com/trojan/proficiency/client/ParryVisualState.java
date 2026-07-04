package com.trojan.proficiency.client;

import com.trojan.proficiency.network.ParryVisualPayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class ParryVisualState {

    private static int remainingTicks;

    private ParryVisualState() {
    }

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(
                ParryVisualPayload.TYPE,
                (payload, context) -> context.client().execute(
                        () -> remainingTicks = Math.max(
                                0,
                                payload.durationTicks()
                        )
                )
        );
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!client.isPaused() && remainingTicks > 0) {
                remainingTicks--;
            }
        });
        ClientPlayConnectionEvents.DISCONNECT.register(
                (handler, client) -> remainingTicks = 0
        );
    }

    public static boolean isActive() {
        return remainingTicks > 0;
    }
}
