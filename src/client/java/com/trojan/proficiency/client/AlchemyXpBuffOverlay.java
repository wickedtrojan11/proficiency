package com.trojan.proficiency.client;

import com.trojan.proficiency.network.AlchemyXpBuffPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class AlchemyXpBuffOverlay {

    private static final int BOX_WIDTH = 138;
    private static final int BOX_HEIGHT = 22;
    private static int multiplierAtSync;
    private static int remainingTicksAtSync;
    private static long syncGameTime;

    private AlchemyXpBuffOverlay() {
    }

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(
                AlchemyXpBuffPayload.TYPE,
                (payload, context) ->
                        context.client().execute(
                                () -> synchronize(
                                        payload.multiplier(),
                                        payload.remainingTicks()
                                )
                        )
        );

        HudRenderCallback.EVENT.register(AlchemyXpBuffOverlay::render);

        ClientPlayConnectionEvents.DISCONNECT.register(
                (handler, client) -> clear()
        );
    }

    public static int getNotificationStartY() {
        return isActive()
                ? getBoxY() + BOX_HEIGHT + 8
                : WellRestedOverlay.getNotificationStartY();
    }

    private static void synchronize(
            int multiplier,
            int remainingTicks
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        multiplierAtSync = multiplier;
        remainingTicksAtSync = remainingTicks;
        syncGameTime = minecraft.level == null
                ? 0
                : minecraft.level.getGameTime();
    }

    private static void clear() {
        multiplierAtSync = 0;
        remainingTicksAtSync = 0;
        syncGameTime = 0;
    }

    private static boolean isActive() {
        return getRemainingTicks() > 0 && multiplierAtSync > 1;
    }

    private static int getRemainingTicks() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || remainingTicksAtSync <= 0) {
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
        int outline = multiplierAtSync >= 3 ? 0xAAFF77FF : 0xAAFFD24A;
        int textColor = multiplierAtSync >= 3 ? 0xFFFFCCFF : 0xFFFFF0AA;
        String label = multiplierAtSync >= 3
                ? "Greater Elixir "
                : "XP Elixir ";

        graphics.fill(x, y, x + BOX_WIDTH, y + BOX_HEIGHT, 0xAA111111);
        graphics.renderOutline(x, y, BOX_WIDTH, BOX_HEIGHT, outline);
        graphics.renderItem(new ItemStack(Items.EXPERIENCE_BOTTLE), x + 3, y + 3);
        graphics.drawString(
                minecraft.font,
                label + formatTime(remainingTicks),
                x + 23,
                y + 7,
                textColor,
                true
        );
    }

    private static int getBoxY() {
        return WellRestedOverlay.isActive()
                ? WellRestedOverlay.getBoxYForStacking()
                + WellRestedOverlay.getBoxHeightForStacking()
                + 4
                : baseBoxY();
    }

    private static int baseBoxY() {
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
        return String.format("%d:%02d", minutes, seconds);
    }
}
