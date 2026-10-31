package com.sloth.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sloth.client.SlothClient;
import com.sloth.client.Theme;
import com.sloth.client.module.Module;
import com.sloth.client.module.ModuleManager;
import com.sloth.client.module.Setting;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private ConfigManager() {}

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("sloth.json");
    }

    public static void load() {
        Path f = file();
        if (!Files.exists(f)) return;
        try (Reader r = Files.newBufferedReader(f)) {
            JsonObject root = GSON.fromJson(r, JsonObject.class);
            if (root == null) return;
            if (root.has("theme")) {
                Theme.index = Math.floorMod(root.get("theme").getAsInt(), Theme.COLORS.length);
            }
            JsonObject mods = root.getAsJsonObject("modules");
            if (mods == null) return;
            for (Module m : ModuleManager.all()) {
                JsonObject mo = mods.getAsJsonObject(m.name);
                if (mo == null) continue;
                if (mo.has("enabled")) m.enabled = mo.get("enabled").getAsBoolean();
                for (Setting s : m.settings) {
                    if (mo.has(s.name)) {
                        s.value = Math.max(s.min, Math.min(s.max, mo.get(s.name).getAsDouble()));
                    }
                }
            }
        } catch (Exception e) {
            SlothClient.LOGGER.warn("Could not read sloth.json, using defaults", e);
        }
    }

    public static void save() {
        JsonObject root = new JsonObject();
        root.addProperty("theme", Theme.index);
        JsonObject mods = new JsonObject();
        for (Module m : ModuleManager.all()) {
            JsonObject mo = new JsonObject();
            mo.addProperty("enabled", m.enabled);
            for (Setting s : m.settings) mo.addProperty(s.name, s.value);
            mods.add(m.name, mo);
        }
        root.add("modules", mods);
        try (Writer w = Files.newBufferedWriter(file())) {
            GSON.toJson(root, w);
        } catch (Exception e) {
            SlothClient.LOGGER.warn("Could not save sloth.json", e);
        }
    }
}
