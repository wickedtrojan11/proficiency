package com.trojan.proficiency.client;

import com.trojan.proficiency.entity.SyncedAnimalAge;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Rabbit;

import java.util.ArrayList;
import java.util.List;

public final class AnimalHusbandryOverlay {

    private static final int BABY_START_AGE = -24000;

    private AnimalHusbandryOverlay() {
    }

    public static void register() {

        HudRenderCallback.EVENT.register(
                AnimalHusbandryOverlay::render
        );
    }

    private static void render(
            GuiGraphics graphics,
            net.minecraft.client.DeltaTracker tickCounter
    ) {

        Minecraft minecraft = Minecraft.getInstance();

        if (
                minecraft.player == null
                        || minecraft.level == null
                        || minecraft.options.hideGui
                        || !(minecraft.crosshairPickEntity
                        instanceof Animal animal)
                        || !isFarmAnimal(animal)
                        || !ClientSkillState
                        .isFarmingAnimalOverlayEnabled(
                                minecraft.player.getUUID()
                        )
        ) {
            return;
        }

        List<String> lines = getStatusLines(animal);

        if (lines.isEmpty()) {
            return;
        }

        int centerX = graphics.guiWidth() / 2;
        int startY = graphics.guiHeight() / 2 + 14;

        for (int index = 0; index < lines.size(); index++) {

            graphics.drawCenteredString(
                    minecraft.font,
                    lines.get(index),
                    centerX,
                    startY + index * 10,
                    0xFFE6D68A
            );
        }
    }

    private static boolean isFarmAnimal(Animal animal) {

        return animal instanceof Cow
                || animal instanceof Chicken
                || animal instanceof Sheep
                || animal instanceof Pig
                || animal instanceof Rabbit
                || animal instanceof Bee;
    }

    private static List<String> getStatusLines(
            Animal animal
    ) {

        List<String> lines = new ArrayList<>();

        if (animal.isBaby()) {

            lines.add(
                    "Growth: "
                            + getBabyGrowthPercent(animal)
                            + "%"
            );
            return lines;
        }

        int breedingCooldown =
                getSyncedAge(animal);

        if (breedingCooldown <= 0) {
            lines.add("Ready to Breed");
        } else {
            lines.add(
                    "Breeding Cooldown: "
                            + formatDuration(breedingCooldown)
            );
        }

        if (animal instanceof Sheep sheep) {

            lines.add(
                    sheep.isSheared()
                            ? "Wool Regrowing"
                            : "Wool Ready"
            );
        }

        return lines;
    }

    private static int getBabyGrowthPercent(
            AgeableMob animal
    ) {

        int syncedAge =
                getSyncedAge(animal);

        return Math.clamp(
                Math.round(
                        (syncedAge - BABY_START_AGE)
                                * 100.0f
                                / -BABY_START_AGE
                ),
                0,
                100
        );
    }

    private static int getSyncedAge(
            AgeableMob animal
    ) {

        return ((SyncedAnimalAge) animal)
                .proficiency$getSyncedAge();
    }

    private static String formatDuration(
            int ticks
    ) {

        int seconds = (ticks + 19) / 20;
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;

        if (minutes <= 0) {
            return remainingSeconds + "s";
        }

        return String.format(
                "%d:%02d",
                minutes,
                remainingSeconds
        );
    }
}
