package com.sloth.client;

import com.sloth.client.config.ConfigManager;
import com.sloth.client.hud.HudRenderer;
import com.sloth.client.module.Module;
import com.sloth.client.module.ModuleManager;
import com.sloth.client.ui.ModMenuScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlothClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("sloth");

    private static KeyBinding menuKey;
    private static KeyBinding zoomKey;
    private static Integer savedFov = null;

    @Override
    public void onInitializeClient() {
        ConfigManager.load();

        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.sloth.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "category.sloth"));
        zoomKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.sloth.zoom", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C, "category.sloth"));

        ClientTickEvents.END_CLIENT_TICK.register(SlothClient::tick);
        HudRenderCallback.EVENT.register(HudRenderer::render);

        ScreenEvents.AFTER_INIT.register((client, screen, w, h) -> {
            // Right Shift also opens the menu from the title and pause screens
            if (screen instanceof TitleScreen || screen instanceof GameMenuScreen) {
                ScreenKeyboardEvents.afterKeyPress(screen).register((s, key, scancode, mods) -> {
                    if (key == GLFW.GLFW_KEY_RIGHT_SHIFT) client.setScreen(new ModMenuScreen(s));
                });
            }
            if (screen instanceof TitleScreen) {
                ScreenEvents.afterRender(screen).register((s, ctx, mx, my, delta) ->
                        HudRenderer.renderTitleBranding(ctx, s.width, s.height));
            }
        });

        ClientLifecycleEvents.CLIENT_STOPPING.register(mc -> {
            restoreFov(mc);
            ConfigManager.save();
        });

        LOGGER.info("Sloth Client loaded");
    }

    private static void tick(MinecraftClient mc) {
        while (menuKey.wasPressed()) {
            if (mc.currentScreen == null) mc.setScreen(new ModMenuScreen(null));
        }

        Module zoom = ModuleManager.get("Zoom");
        boolean want = zoom.enabled && mc.currentScreen == null && zoomKey.isPressed();
        if (want) {
            if (savedFov == null) savedFov = mc.options.getFov().getValue();
            mc.options.getFov().setValue((int) zoom.value("Zoom FOV"));
        } else {
            restoreFov(mc);
        }
    }

    private static void restoreFov(MinecraftClient mc) {
        if (savedFov != null) {
            mc.options.getFov().setValue(savedFov);
            savedFov = null;
        }
    }
}
