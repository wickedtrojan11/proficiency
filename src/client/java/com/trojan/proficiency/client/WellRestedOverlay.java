package com.trojan.proficiency.client;

import com.trojan.proficiency.network.WellRestedPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class WellRestedOverlay {

    private static final int BOX_WIDTH = 112;
    private static final int BOX_HEIGHT = 22;
    private static int remainingTicksAtSync;
    private static long syncGameTime;

    private WellRestedOverlay() {
    }

    public static void register() {

        ClientPlayNetworking.registerGlobalReceiver(
                WellRestedPayload.TYPE,
                (payload, context) ->
                        context.client().execute(
                                () -> synchronize(
                                        payload.remainingTicks()
                                )
                        )
        );

        HudRenderCallback.EVENT.register(
                WellRestedOverlay::render
        );

        ClientPlayConnectionEvents.DISCONNECT.register(
                (handler, client) -> clear()
        );
    }

    public static boolean isActive() {
        return getRemainingTicks() > 0;
    }

    public static int getNotificationStartY() {

        return isActive()
                ? getBoxY() + BOX_HEIGHT + 8
                : 50;
    }

    private static void synchronize(int remainingTicks) {

        Minecraft minecraft = Minecraft.getInstance();
        remainingTicksAtSync = remainingTicks;
        syncGameTime = minecraft.level == null
                ? 0
                : minecraft.level.getGameTime();
    }

    private static void clear() {
        remainingTicksAtSync = 0;
        syncGameTime = 0;
    }

    private static int getRemainingTicks() {

        Minecraft minecraft = Minecraft.getInstance();

        if (
                minecraft.level == null
                        || remainingTicksAtSync <= 0
        ) {
            return 0;
        }

        long elapsedTicks = Math.max(
                0,
                minecraft.level.getGameTime() - syncGameTime
        );

        return Math.max(
                0,
                remainingTicksAtSync - (int) elapsedTicks
        );
    }

    private static void render(
            GuiGraphics graphics,
            net.minecraft.client.DeltaTracker tickCounter
    ) {

        Minecraft minecraft = Minecraft.getInstance();
        int remainingTicks = getRemainingTicks();

        if (
                remainingTicks <= 0
                        || minecraft.player == null
                        || minecraft.options.hideGui
        ) {
            return;
        }

        int x = graphics.guiWidth() - BOX_WIDTH - 12;
        int y = getBoxY();

        graphics.fill(
                x,
                y,
                x + BOX_WIDTH,
                y + BOX_HEIGHT,
                0xAA111111
        );
        graphics.renderOutline(
                x,
                y,
                BOX_WIDTH,
                BOX_HEIGHT,
                0xAA80C8FF
        );
        graphics.renderItem(
                new ItemStack(Items.EXPERIENCE_BOTTLE),
                x + 3,
                y + 3
        );
        graphics.drawString(
                minecraft.font,
                "Well Rested "
                        + formatTime(remainingTicks),
                x + 23,
                y + 7,
                0xFFE8F7FF,
                true
        );
    }

    private static int getBoxY() {

        Minecraft minecraft = Minecraft.getInstance();

        return minecraft.player != null
                && !minecraft.player.getActiveEffects().isEmpty()
                ? 52
                : 10;
    }

    private static String formatTime(int ticks) {

        int totalSeconds = (ticks + 19) / 20;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        return String.format(
                "%d:%02d",
                minutes,
                seconds
        );
    }
}
