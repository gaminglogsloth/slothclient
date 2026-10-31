package com.sloth.client.hud;

import com.sloth.client.Theme;
import com.sloth.client.module.Module;
import com.sloth.client.module.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.math.Direction;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class HudRenderer {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm");

    private HudRenderer() {}

    public static void render(DrawContext c, RenderTickCounter tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options.hudHidden) return;

        TextRenderer tr = mc.textRenderer;
        int accent = Theme.accent();
        int w = c.getScaledWindowWidth();
        int h = c.getScaledWindowHeight();

        // Screen glow (top and bottom edges)
        Module glow = ModuleManager.get("Screen Glow");
        if (glow.enabled) {
            int a = (int) (glow.value("Intensity") / 100.0 * 0x70);
            if (a > 0) {
                c.fillGradient(0, 0, w, h / 5, Theme.alpha(accent, a), Theme.alpha(accent, 0));
                c.fillGradient(0, h - h / 5, w, h, Theme.alpha(accent, 0), Theme.alpha(accent, a));
            }
        }

        // Info panel
        Module info = ModuleManager.get("Info Panel");
        if (info.enabled) {
            Direction f = mc.player.getHorizontalFacing();
            String facing = f.asString();
            facing = Character.toUpperCase(facing.charAt(0)) + facing.substring(1);
            String[] lines = {
                    mc.getCurrentFps() + " FPS",
                    String.format("XYZ %.1f / %.1f / %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ()),
                    "Facing " + facing
            };
            int tw = tr.getWidth("SLOTH");
            for (String l : lines) tw = Math.max(tw, tr.getWidth(l));
            int bw = tw + 16;
            int bh = lines.length * 11 + 22;
            int a = (int) (info.value("Opacity") / 100.0 * 0xDD);
            panel(c, 6, 6, bw, bh, a, accent);
            c.drawText(tr, "SLOTH", 14, 12, accent, true);
            for (int i = 0; i < lines.length; i++) {
                c.drawText(tr, lines[i], 14, 25 + i * 11, 0xFFE8E8F0, true);
            }
        }

        // Clock
        Module clock = ModuleManager.get("Clock");
        if (clock.enabled) {
            String t = LocalTime.now().format(FMT);
            int bw = tr.getWidth(t) + 16;
            panel(c, w - bw - 6, 6, bw, 20, 0xAA, accent);
            c.drawText(tr, t, w - bw - 6 + 10, 12, 0xFFE8E8F0, true);
        }

        // Keystrokes
        Module keys = ModuleManager.get("Keystrokes");
        if (keys.enabled) {
            int x = 8, y = h - 70;
            key(c, tr, x + 22, y, 20, 20, "W", mc.options.forwardKey.isPressed(), accent);
            key(c, tr, x, y + 22, 20, 20, "A", mc.options.leftKey.isPressed(), accent);
            key(c, tr, x + 22, y + 22, 20, 20, "S", mc.options.backKey.isPressed(), accent);
            key(c, tr, x + 44, y + 22, 20, 20, "D", mc.options.rightKey.isPressed(), accent);
            key(c, tr, x, y + 44, 31, 16, "LMB", mc.options.attackKey.isPressed(), accent);
            key(c, tr, x + 33, y + 44, 31, 16, "RMB", mc.options.useKey.isPressed(), accent);
        }
    }

    /** Drawn on top of the title screen. */
    public static void renderTitleBranding(DrawContext c, int w, int h) {
        Module m = ModuleManager.get("Menu Branding");
        if (!m.enabled) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        TextRenderer tr = mc.textRenderer;
        int accent = Theme.accent();
        c.fillGradient(0, 0, w, h / 3, Theme.alpha(accent, 0x38), Theme.alpha(accent, 0));
        c.fillGradient(0, h - h / 4, w, h, Theme.alpha(accent, 0), Theme.alpha(accent, 0x38));
        c.fill(8, 8, 10, 30, accent);
        c.drawText(tr, "SLOTH CLIENT", 15, 9, accent, true);
        c.drawText(tr, "Right Shift  -  Mod Menu", 15, 20, 0xFFB8B8C8, true);
    }

    private static void panel(DrawContext c, int x, int y, int w, int h, int a, int accent) {
        c.fill(x, y, x + w, y + h, ((a & 0xFF) << 24) | 0x101016);
        c.fill(x, y, x + 2, y + h, accent);
    }

    private static void key(DrawContext c, TextRenderer tr, int x, int y, int w, int h,
                            String label, boolean pressed, int accent) {
        int bg = pressed ? Theme.alpha(accent, 0xC0) : 0x99101016;
        c.fill(x, y, x + w, y + h, bg);
        c.fill(x, y + h - 1, x + w, y + h, pressed ? accent : 0x66FFFFFF);
        c.drawText(tr, label, x + (w - tr.getWidth(label)) / 2, y + (h - 8) / 2, 0xFFFFFFFF, true);
    }
}
