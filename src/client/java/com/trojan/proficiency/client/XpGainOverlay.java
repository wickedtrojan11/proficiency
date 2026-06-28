package com.trojan.proficiency.client;

import com.trojan.proficiency.network.XpGainPayload;
import com.trojan.proficiency.skill.SkillType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

public final class XpGainOverlay {

    private static final int DISPLAY_TICKS = 60;
    private static final int MERGE_TICKS = 12;
    private static final int MAX_NOTIFICATIONS = 5;
    private static final float TEXT_SCALE = 0.8f;
    private static final List<Notification> NOTIFICATIONS =
            new ArrayList<>();

    private XpGainOverlay() {
    }

    public static void register() {

        ClientPlayNetworking.registerGlobalReceiver(
                XpGainPayload.TYPE,
                (payload, context) ->
                        context.client().execute(
                                () -> add(
                                        payload.skillType(),
                                        payload.amount()
                                )
                        )
        );

        HudRenderCallback.EVENT.register(
                XpGainOverlay::render
        );

        ClientPlayConnectionEvents.DISCONNECT.register(
                (handler, client) -> NOTIFICATIONS.clear()
        );
    }

    private static void add(
            SkillType skillType,
            int amount
    ) {

        Minecraft minecraft = Minecraft.getInstance();
        long gameTime = minecraft.level == null
                ? 0
                : minecraft.level.getGameTime();

        for (Notification notification : NOTIFICATIONS) {

            if (
                    notification.skillType == skillType
                            && gameTime - notification.updatedAt
                            <= MERGE_TICKS
            ) {

                notification.amount += amount;
                notification.updatedAt = gameTime;
                return;
            }
        }

        NOTIFICATIONS.add(
                0,
                new Notification(
                        skillType,
                        amount,
                        gameTime
                )
        );

        while (NOTIFICATIONS.size() > MAX_NOTIFICATIONS) {
            NOTIFICATIONS.remove(NOTIFICATIONS.size() - 1);
        }
    }

    private static void render(
            GuiGraphics graphics,
            net.minecraft.client.DeltaTracker tickCounter
    ) {

        Minecraft minecraft = Minecraft.getInstance();

        if (
                minecraft.level == null
                        || minecraft.options.hideGui
        ) {
            return;
        }

        long gameTime = minecraft.level.getGameTime();

        NOTIFICATIONS.removeIf(
                notification ->
                        gameTime - notification.updatedAt
                                > DISPLAY_TICKS
        );

        int startY = WellRestedOverlay.getNotificationStartY();

        for (int index = 0;
             index < NOTIFICATIONS.size();
             index++) {

            Notification notification =
                    NOTIFICATIONS.get(index);

            String text =
                    "+"
                            + notification.amount
                            + " "
                            + notification.skillType.getDisplayName()
                            + " XP";

            int x = graphics.guiWidth()
                    - Math.round(
                    minecraft.font.width(text) * TEXT_SCALE
            )
                    - 14;
            int y = startY + index * 10;

            graphics.pose().pushPose();
            graphics.pose().scale(
                    TEXT_SCALE,
                    TEXT_SCALE,
                    1.0f
            );

            graphics.drawString(
                    minecraft.font,
                    text,
                    Math.round(x / TEXT_SCALE),
                    Math.round(y / TEXT_SCALE),
                    getColor(notification.skillType),
                    true
            );
            graphics.pose().popPose();
        }
    }

    private static int getColor(SkillType skillType) {

        return switch (skillType) {
            case MINING -> 0xFFFFD24A;
            case WOODCUTTING -> 0xFF55FF55;
            case FARMING -> 0xFFFFCC55;
        };
    }

    private static final class Notification {
        private final SkillType skillType;
        private int amount;
        private long updatedAt;

        private Notification(
                SkillType skillType,
                int amount,
                long updatedAt
        ) {
            this.skillType = skillType;
            this.amount = amount;
            this.updatedAt = updatedAt;
        }
    }
}
