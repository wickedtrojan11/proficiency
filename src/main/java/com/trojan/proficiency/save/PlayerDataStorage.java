package com.trojan.proficiency.save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

            if (!playerFile.exists()) {

                ProficiencyMod.LOGGER.info(
                        "No proficiency player data file found for {}; using defaults",
                        playerId
                );

                return new PlayerData();
            }

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

                return new PlayerData();
            }

            return data;

        } catch (Exception e) {

            e.printStackTrace();
        }

        return new PlayerData();
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
