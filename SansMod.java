package com.sansmod;

import com.sansmod.config.ConfigManager;
import com.sansmod.config.ModConfig;
import com.sansmod.gui.MainScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SansMod implements ClientModInitializer {
    public static final String MOD_ID = "sansmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        ConfigManager.load();
        Keybinds.register();
        ClientTickEvents.END_CLIENT_TICK.register(SansMod::onTick);
        LOGGER.info("Sans Mod stage 1 loaded");
    }

    private static void onTick(MinecraftClient client) {
        while (Keybinds.OPEN_GUI.wasPressed()) {
            if (client.currentScreen == null) client.setScreen(new MainScreen());
        }
        while (Keybinds.TOGGLE_DODGE.wasPressed()) {
            ModConfig c = ConfigManager.get();
            c.autoDodge = !c.autoDodge;
            ConfigManager.save();
            if (client.player != null) {
                client.player.sendMessage(Text.literal("Auto Dodge: " + (c.autoDodge ? "ON" : "OFF")), true);
            }
        }
        // Stage 1: these keys are registered but their abilities arrive in later stages.
        while (Keybinds.GASTER.wasPressed()) { }
        while (Keybinds.BONE.wasPressed()) { }
        while (Keybinds.TELEPORT.wasPressed()) { }
        while (Keybinds.CYCLE_TARGET.wasPressed()) { }
    }
}
