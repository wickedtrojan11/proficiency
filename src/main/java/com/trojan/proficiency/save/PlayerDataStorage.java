package com.trojan.proficiency.save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.trojan.proficiency.player.PlayerData;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import java.util.UUID;

public class PlayerDataStorage {

    private static final Gson GSON =
            new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

    private static final File SAVE_FOLDER =
            new File("config/proficiency");

    static {

        if (!SAVE_FOLDER.exists()) {

            SAVE_FOLDER.mkdirs();
        }
    }

    public static void savePlayer(
            UUID playerId,
            PlayerData data
    ) {

        try {

            File playerFile =
                    new File(
                            SAVE_FOLDER,
                            "dev_player.json"
                    );

            FileWriter writer =
                    new FileWriter(playerFile);

            GSON.toJson(data, writer);

            writer.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public static PlayerData loadPlayer(
            UUID playerId
    ) {

        try {

            File playerFile =
                    new File(
                            SAVE_FOLDER,
                            "dev_player.json"
                    );

            if (!playerFile.exists()) {

                return new PlayerData();
            }

            FileReader reader =
                    new FileReader(playerFile);

            PlayerData data =
                    GSON.fromJson(
                            reader,
                            PlayerData.class
                    );

            reader.close();

            return data;

        } catch (Exception e) {

            e.printStackTrace();
        }

        return new PlayerData();
    }
}