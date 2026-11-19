package com.sansmod;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public final class Keybinds {
    public static final KeyBinding.Category CATEGORY =
            KeyBinding.Category.create(Identifier.of(SansMod.MOD_ID, "keys"));

    public static KeyBinding OPEN_GUI;
    public static KeyBinding GASTER;
    public static KeyBinding BONE;
    public static KeyBinding TELEPORT;
    public static KeyBinding CYCLE_TARGET;
    public static KeyBinding TOGGLE_DODGE;

    private Keybinds() {}

    private static KeyBinding reg(String id, int key) {
        return KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.sansmod." + id, InputUtil.Type.KEYSYM, key, CATEGORY));
    }

    public static void register() {
        OPEN_GUI = reg("open_gui", GLFW.GLFW_KEY_RIGHT_SHIFT);
        GASTER = reg("gaster", GLFW.GLFW_KEY_G);
        BONE = reg("bone", GLFW.GLFW_KEY_B);
        TELEPORT = reg("teleport", GLFW.GLFW_KEY_V);
        CYCLE_TARGET = reg("cycle_target", GLFW.GLFW_KEY_H);
        TOGGLE_DODGE = reg("toggle_dodge", GLFW.GLFW_KEY_J);
    }
}
