package com.trojan.proficiency.event;

import com.trojan.proficiency.SkillManager;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class WellRestedEvents {

    private static final long DAY_LENGTH = 24000L;
    private static final long NIGHT_START = 12542L;
    private static final long MORNING_END = 1000L;
    private static final Map<UUID, Long> SLEEP_START_DAYS =
            new HashMap<>();

    private WellRestedEvents() {
    }

    public static void register() {

        EntitySleepEvents.START_SLEEPING.register(
                (entity, sleepingPos) -> {

                    if (
                            entity instanceof ServerPlayer player
                                    && getTimeOfDay(player)
                                    >= NIGHT_START
                    ) {

                        SLEEP_START_DAYS.put(
                                player.getUUID(),
                                player.serverLevel()
                                        .getDayTime()
                                        / DAY_LENGTH
                        );
                    }
                }
        );

        EntitySleepEvents.STOP_SLEEPING.register(
                (entity, sleepingPos) -> {

                    if (!(entity instanceof ServerPlayer player)) {

                        return;
                    }

                    Long sleepStartDay =
                            SLEEP_START_DAYS.remove(
                                    player.getUUID()
                            );

                    if (sleepStartDay == null) {
                        return;
                    }

                    long dayTime =
                            player.serverLevel().getDayTime();

                    if (
                            dayTime / DAY_LENGTH > sleepStartDay
                                    && dayTime % DAY_LENGTH < MORNING_END
                    ) {

                        SkillManager.grantWellRested(player);
                    }
                }
        );

        ServerTickEvents.END_SERVER_TICK.register(server -> {

            SkillManager.tickWellRestedTimers();
        });
    }

    private static long getTimeOfDay(
            ServerPlayer player
    ) {

        return player.serverLevel()
                .getDayTime()
                % DAY_LENGTH;
    }
}
