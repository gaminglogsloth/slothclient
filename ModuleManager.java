package com.sloth.client.module;

import java.util.ArrayList;
import java.util.List;

public final class ModuleManager {
    private static final List<Module> MODULES = new ArrayList<>();

    static {
        MODULES.add(new Module("Info Panel", "FPS, coordinates and facing direction", "HUD", true)
                .setting("Opacity", 65, 0, 100));
        MODULES.add(new Module("Keystrokes", "WASD and mouse button display", "HUD", true));
        MODULES.add(new Module("Clock", "Real-time clock in the corner", "HUD", true));
        MODULES.add(new Module("Screen Glow", "Accent colored glow along the screen edges", "Visual", true)
                .setting("Intensity", 40, 0, 100));
        MODULES.add(new Module("Zoom", "Hold C to zoom in", "Visual", true)
                .setting("Zoom FOV", 30, 30, 70));
        MODULES.add(new Module("Menu Branding", "Themed overlay on the title screen", "Visual", true));
    }

    private ModuleManager() {}

    public static List<Module> all() {
        return MODULES;
    }

    public static Module get(String name) {
        for (Module m : MODULES) if (m.name.equals(name)) return m;
        throw new IllegalArgumentException("Unknown module: " + name);
    }

    public static List<Module> inCategory(String category) {
        List<Module> out = new ArrayList<>();
        for (Module m : MODULES) if (m.category.equals(category)) out.add(m);
        return out;
    }
}
