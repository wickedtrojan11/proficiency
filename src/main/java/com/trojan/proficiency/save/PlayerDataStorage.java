package com.trojan.proficiency.save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.trojan.proficiency.ProficiencyMod;
import com.trojan.proficiency.player.PlayerData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.UUID;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataStorage {

    private static final Gson GSON =
            new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

    private static final File LEGACY_SAVE_FOLDER =
            new File("config/proficiency");
    private static File saveFolder =
            LEGACY_SAVE_FOLDER;
    private static Path worldFolder;
    private static boolean singleplayer;
    private static UUID singleplayerOwnerId;
    private static final Set<UUID> LOAD_FAILURES =
            ConcurrentHashMap.newKeySet();

    static {

        if (!LEGACY_SAVE_FOLDER.exists()) {

            LEGACY_SAVE_FOLDER.mkdirs();
        }
    }

    public static void configureForServer(
            MinecraftServer server
    ) {

        worldFolder =
                server.getWorldPath(
                        LevelResource.ROOT
                );
        singleplayer =
                server.isSingleplayer();
        singleplayerOwnerId =
                server.getSingleplayerProfile() == null
                        ? null
                        : server.getSingleplayerProfile().getId();

        saveFolder =
                worldFolder
                        .resolve("proficiency")
                        .resolve("playerdata")
                        .toFile();

        if (!saveFolder.exists()) {

            saveFolder.mkdirs();
        }

        ProficiencyMod.LOGGER.info(
                "Configured proficiency player data folder: {} (singleplayer: {})",
                saveFolder.getAbsolutePath(),
                singleplayer
        );
    }

    public static void savePlayer(
            UUID playerId,
            PlayerData data
    ) {

        savePlayer(
                playerId,
                data,
                "unspecified"
        );
    }

    public static void savePlayer(
            UUID playerId,
            PlayerData data,
            String reason
    ) {

        if (LOAD_FAILURES.contains(playerId)) {
            ProficiencyMod.LOGGER.error(
                    "Refusing to overwrite proficiency data for {} after a load failure ({})",
                    playerId,
                    reason
            );
            return;
        }

        try {

            File playerFile =
                    getPlayerFile(playerId);
            File tempFile =
                    new File(
                            playerFile.getParentFile(),
                            playerFile.getName()
                                    + ".tmp"
                    );

            ProficiencyMod.LOGGER.info(
                    "Saving proficiency player data for {} to {} ({})",
                    playerId,
                    playerFile.getAbsolutePath(),
                    reason
            );

            String json =
                    GSON.toJson(data);

            FileOutputStream outputStream =
                    new FileOutputStream(tempFile);
            OutputStreamWriter writer =
                    new OutputStreamWriter(
                            outputStream,
                            StandardCharsets.UTF_8
                    );

            writer.write(json);
            writer.flush();
            outputStream.getFD().sync();

            writer.close();

            Files.move(
                    tempFile.toPath(),
                    playerFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );

            ProficiencyMod.LOGGER.info(
                    "Saved proficiency JSON for {}: {}",
                    playerId,
                    Files.readString(
                            playerFile.toPath(),
                            StandardCharsets.UTF_8
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public static PlayerData loadPlayer(
            UUID playerId
    ) {

        try {

            File playerFile =
                    getPlayerFile(playerId);

            ProficiencyMod.LOGGER.info(
                    "Loading proficiency player data for {} from {}",
                    playerId,
                    playerFile.getAbsolutePath()
            );
            ProficiencyMod.LOGGER.info(
                    "Proficiency player data exists before load for {}: {}",
                    playerId,
                    playerFile.exists()
            );

            if (!playerFile.exists()) {

                migrateLegacyPlayerFile(
                        playerId,
                        playerFile
                );
            }

            if (playerFile.exists()) {
                logPerkCounts(playerId, playerFile, "before migration");
            }

            migrateSingleplayerOwnerData(
                    playerId,
                    playerFile
            );

            if (!playerFile.exists()) {

                ProficiencyMod.LOGGER.info(
                        "No proficiency player data file found for {}; using defaults",
                        playerId
                );

                LOAD_FAILURES.remove(playerId);
                return new PlayerData();
            }

            logPerkCounts(playerId, playerFile, "after migration");

            ProficiencyMod.LOGGER.info(
                    "Loaded proficiency JSON for {}: {}",
                    playerId,
                    Files.readString(
                            playerFile.toPath(),
                            StandardCharsets.UTF_8
                    )
            );

            FileReader reader =
                    new FileReader(playerFile);

            PlayerData data =
                    GSON.fromJson(
                            reader,
                            PlayerData.class
                    );

            reader.close();

            if (data == null) {
                LOAD_FAILURES.add(playerId);
                return new PlayerData();
            }

            LOAD_FAILURES.remove(playerId);
            ProficiencyMod.LOGGER.info(
                    "Loaded proficiency perk counts for {} after migration: mining={}, woodcutting={}, farming={}, one_handed={}",
                    playerId,
                    data.getUnlockedMiningPerks().size(),
                    data.getUnlockedWoodcuttingPerks().size(),
                    data.getUnlockedFarmingPerks().size(),
                    data.getUnlockedOneHandedPerks().size()
            );

            return data;

        } catch (Exception e) {
            LOAD_FAILURES.add(playerId);
            ProficiencyMod.LOGGER.error(
                    "Failed to load proficiency player data for {}; the existing file will not be overwritten",
                    playerId,
                    e
            );
        }

        return new PlayerData();
    }

    private static void migrateSingleplayerOwnerData(
            UUID playerId,
            File playerFile
    ) {
        if (
                !singleplayer
                        || singleplayerOwnerId == null
                        || !singleplayerOwnerId.equals(playerId)
        ) {
            return;
        }

        int currentScore = getProgressScore(playerFile);
        if (currentScore > 0) {
            return;
        }

        File[] candidates = saveFolder.listFiles(
                file -> file.isFile()
                        && file.getName().endsWith(".json")
                        && !file.equals(playerFile)
        );
        if (candidates == null) {
            return;
        }

        File bestCandidate = null;
        int bestScore = 0;

        for (File candidate : candidates) {
            int candidateScore = getProgressScore(candidate);
            if (candidateScore > bestScore) {
                bestCandidate = candidate;
                bestScore = candidateScore;
            }
        }

        if (bestCandidate == null) {
            return;
        }

        try {
            ProficiencyMod.LOGGER.warn(
                    "Migrating singleplayer owner proficiency data from prior UUID file {} to {} (progress score {})",
                    bestCandidate.getAbsolutePath(),
                    playerFile.getAbsolutePath(),
                    bestScore
            );
            logPerkCounts(playerId, bestCandidate, "migration source");
            Files.copy(
                    bestCandidate.toPath(),
                    playerFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );
            logPerkCounts(playerId, playerFile, "after migration copy");
        } catch (Exception e) {
            ProficiencyMod.LOGGER.error(
                    "Unable to migrate prior singleplayer owner proficiency data",
                    e
            );
        }
    }

    private static int getProgressScore(File file) {
        if (file == null || !file.exists()) {
            return 0;
        }

        try {
            PlayerData data = GSON.fromJson(
                    Files.readString(file.toPath(), StandardCharsets.UTF_8),
                    PlayerData.class
            );
            if (data == null) {
                return 0;
            }
            return Math.max(0, data.getMiningLevel() - 1) * 100
                    + Math.max(0, data.getWoodcuttingLevel() - 1) * 100
                    + Math.max(0, data.getFarmingLevel() - 1) * 100
                    + Math.max(0, data.getOneHandedLevel() - 1) * 100
                    + data.getUnlockedMiningPerks().size() * 10
                    + data.getUnlockedWoodcuttingPerks().size() * 10
                    + data.getUnlockedFarmingPerks().size() * 10
                    + data.getUnlockedOneHandedPerks().size() * 10
                    + data.getMiningPerkPoints()
                    + data.getWoodcuttingPerkPoints()
                    + data.getFarmingPerkPoints()
                    + data.getOneHandedPerkPoints()
                    + data.getMiningPrestige() * 1000
                    + data.getWoodcuttingPrestige() * 1000
                    + data.getFarmingPrestige() * 1000
                    + data.getOneHandedPrestige() * 1000
                    + (data.getMiningXp() > 0 ? 1 : 0)
                    + (data.getWoodcuttingXp() > 0 ? 1 : 0)
                    + (data.getFarmingXp() > 0 ? 1 : 0)
                    + (data.getOneHandedXp() > 0 ? 1 : 0);
        } catch (Exception ignored) {
            return 0;
        }
    }

    private static void logPerkCounts(
            UUID playerId,
            File file,
            String stage
    ) {
        try {
            JsonObject json = JsonParser.parseString(
                    Files.readString(file.toPath(), StandardCharsets.UTF_8)
            ).getAsJsonObject();
            ProficiencyMod.LOGGER.info(
                    "Proficiency perk counts for {} {}: mining={}, woodcutting={}, farming={}, one_handed={}",
                    playerId,
                    stage,
                    getArraySize(json, "unlockedMiningPerks"),
                    getArraySize(json, "unlockedWoodcuttingPerks"),
                    getArraySize(json, "unlockedFarmingPerks"),
                    getArraySize(json, "unlockedOneHandedPerks")
            );
        } catch (Exception e) {
            ProficiencyMod.LOGGER.warn(
                    "Unable to inspect proficiency perk counts in {}",
                    file.getAbsolutePath()
            );
        }
    }

    private static int getArraySize(JsonObject json, String fieldName) {
        return json.has(fieldName) && json.get(fieldName).isJsonArray()
                ? json.getAsJsonArray(fieldName).size()
                : 0;
    }

    private static File getPlayerFile(
            UUID playerId
    ) {

        if (!saveFolder.exists()) {

            if (!saveFolder.mkdirs()) {

                ProficiencyMod.LOGGER.warn(
                        "Unable to create proficiency player data folder: {}",
                        saveFolder.getAbsolutePath()
                );
            }
        }

        return new File(
                saveFolder,
                playerId + ".json"
        );
    }

    private static void migrateLegacyPlayerFile(
            UUID playerId,
            File playerFile
    ) {

        File legacyFile =
                new File(
                        LEGACY_SAVE_FOLDER,
                        "dev_player.json"
                );

        if (
                !legacyFile.exists()
                        || !singleplayer
                        || singleplayerOwnerId == null
                        || !singleplayerOwnerId.equals(playerId)
                        || worldFolder == null
                        || isNewerThanLegacyFile(
                                worldFolder,
                                legacyFile
                        )
        ) {

            return;
        }

        try {

            Files.copy(
                    legacyFile.toPath(),
                    playerFile.toPath()
            );
            ProficiencyMod.LOGGER.info(
                    "Migrated legacy proficiency player data from {} to {}",
                    legacyFile.getAbsolutePath(),
                    playerFile.getAbsolutePath()
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private static boolean isNewerThanLegacyFile(
            Path folder,
            File legacyFile
    ) {

        try {

            BasicFileAttributes attributes =
                    Files.readAttributes(
                            folder,
                            BasicFileAttributes.class
                    );

            return attributes.creationTime()
                    .toMillis()
                    > legacyFile.lastModified();

        } catch (Exception e) {

            return true;
        }
    }
}
