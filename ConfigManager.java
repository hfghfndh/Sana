package com.sansmod.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sansmod.SansMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path path;
    private static ModConfig config = new ModConfig();

    private ConfigManager() {}

    public static ModConfig get() {
        return config;
    }

    public static void load() {
        path = FabricLoader.getInstance().getConfigDir().resolve("sansmod.json");
        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path)) {
                ModConfig loaded = GSON.fromJson(reader, ModConfig.class);
                if (loaded != null) config = loaded;
            } catch (Exception e) {
                SansMod.LOGGER.warn("Could not read config, using defaults", e);
            }
        }
        save();
    }

    public static void save() {
        if (path == null) return;
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(config));
        } catch (IOException e) {
            SansMod.LOGGER.warn("Could not save config", e);
        }
    }
}
