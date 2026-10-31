package com.sloth.client.ui;

import com.sloth.client.Theme;
import com.sloth.client.config.ConfigManager;
import com.sloth.client.module.Module;
import com.sloth.client.module.ModuleManager;
import com.sloth.client.module.Setting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ModMenuScreen extends Screen {
    private static final String[] TABS = {"HUD", "Visual", "Theme"};
    private static final int W = 440, H = 290;
    private static int tab = 0;

    private record Region(int x, int y, int w, int h, Runnable click, Setting slider) {
        boolean hit(double mx, double my) {
            return mx >= x && mx < x + w && my >= y && my < y + h;
        }
    }

    private final Screen parent;
    private final List<Region> regions = new ArrayList<>();
    private Region dragging;

    public ModMenuScreen(Screen parent) {
        super(Text.literal("Sloth"));
        this.parent = parent;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }

    @Override
    public void removed() {
        ConfigManager.save();
    }

    @Override
    public void renderBackground(DrawContext c, int mx, int my, float delta) {
        c.fillGradient(0, 0, width, height, 0xA0000000, 0xD0000000);
    }

    @Override
    public void render(DrawContext c, int mx, int my, float delta) {
        super.render(c, mx, my, delta);
        regions.clear();

        int accent = Theme.accent();
        int px = (width - W) / 2, py = (height - H) / 2;

        // Frame
        c.fill(px - 1, py - 1, px + W + 1, py + H + 1, Theme.alpha(accent, 0x90));
        c.fill(px, py, px + W, py + H, 0xF0121218);
        c.fill(px, py, px + W, py + 34, 0xFF1A1A24);
        c.fill(px, py + 34, px + W, py + 36, accent);
        c.drawText(textRenderer, "SLOTH", px + 14, py + 13, accent, true);
        c.drawText(textRenderer, "CLIENT", px + 14 + textRenderer.getWidth("SLOTH "), py + 13, 0xFFB8B8C8, true);
        String hint = "Right Shift to close";
        c.drawText(textRenderer, hint, px + W - 14 - textRenderer.getWidth(hint), py + 13, 0xFF6C6C7C, true);

        // Sidebar
        c.fill(px, py + 36, px + 100, py + H, 0xFF16161E);
        for (int i = 0; i < TABS.length; i++) {
            final int idx = i;
            int ty = py + 46 + i * 30;
            boolean sel = tab == i;
            if (sel) {
                c.fill(px + 6, ty, px + 94, ty + 24, Theme.alpha(accent, 0x40));
                c.fill(px + 6, ty, px + 8, ty + 24, accent);
            }
            c.drawText(textRenderer, TABS[i], px + 18, ty + 8, sel ? 0xFFFFFFFF : 0xFF9A9AAA, true);
            regions.add(new Region(px + 6, ty, 88, 24, () -> tab = idx, null));
        }

        int cx = px + 112, cw = W - 122, y = py + 48;

        if (tab == 2) {
            c.drawText(textRenderer, "Accent color", cx, y, 0xFFE8E8F0, true);
            c.drawText(textRenderer, "Current: " + Theme.NAMES[Theme.index], cx, y + 12, 0xFF8A8A9A, true);
            y += 32;
            for (int i = 0; i < Theme.COLORS.length; i++) {
                final int idx = i;
                int sx = cx + (i % 4) * 78;
                int sy = y + (i / 4) * 56;
                int col = 0xFF000000 | Theme.COLORS[i];
                if (Theme.index == i) c.fill(sx - 2, sy - 2, sx + 70, sy + 42, 0xFFFFFFFF);
                c.fill(sx, sy, sx + 68, sy + 40, col);
                c.drawText(textRenderer, Theme.NAMES[i], sx + 4, sy + 29, 0xFF000000, false);
                regions.add(new Region(sx, sy, 68, 40, () -> {
                    Theme.index = idx;
                    ConfigManager.save();
                }, null));
            }
            return;
        }

        for (Module m : ModuleManager.inCategory(TABS[tab])) {
            c.fill(cx, y, cx + cw, y + 28, 0xFF1C1C26);
            c.fill(cx, y, cx + 2, y + 28, m.enabled ? accent : 0xFF3A3A48);
            c.drawText(textRenderer, m.name, cx + 10, y + 5, 0xFFFFFFFF, true);
            c.drawText(textRenderer, m.description, cx + 10, y + 16, 0xFF8A8A9A, false);

            if (!m.settings.isEmpty()) {
                c.drawText(textRenderer, m.expanded ? "-" : "+", cx + cw - 54, y + 10, 0xFF9A9AAA, true);
            }
            // Toggle switch
            int tx = cx + cw - 38, ty = y + 8;
            c.fill(tx, ty, tx + 28, ty + 12, m.enabled ? accent : 0xFF3A3A48);
            int kx = m.enabled ? tx + 17 : tx + 1;
            c.fill(kx, ty + 1, kx + 10, ty + 11, 0xFFFFFFFF);

            regions.add(new Region(cx, y, cw - 44, 28, () -> m.expanded = !m.expanded, null));
            regions.add(new Region(tx - 2, ty - 2, 32, 16, () -> {
                m.enabled = !m.enabled;
                ConfigManager.save();
            }, null));
            y += 32;

            if (m.expanded) {
                for (Setting s : m.settings) {
                    c.fill(cx + 8, y, cx + cw, y + 22, 0xFF171720);
                    c.drawText(textRenderer, s.name + ": " + (int) Math.round(s.value), cx + 18, y + 7, 0xFFD8D8E4, true);
                    int sx = cx + cw - 130;
                    double t = (s.value - s.min) / (s.max - s.min);
                    c.fill(sx, y + 9, sx + 110, y + 13, 0xFF3A3A48);
                    c.fill(sx, y + 9, sx + (int) (110 * t), y + 13, accent);
                    int kx2 = sx + (int) (110 * t) - 3;
                    c.fill(kx2, y + 5, kx2 + 6, y + 17, 0xFFFFFFFF);
                    regions.add(new Region(sx - 4, y + 3, 118, 16, null, s));
                    y += 24;
                }
            }
        }
    }

    private void applySlider(Region r, double mx) {
        Setting s = r.slider();
        double t = Math.max(0, Math.min(1, (mx - (r.x() + 4)) / (r.w() - 8)));
        s.value = Math.round(s.min + t * (s.max - s.min));
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0) {
            for (int i = regions.size() - 1; i >= 0; i--) {
                Region r = regions.get(i);
                if (!r.hit(mx, my)) continue;
                if (r.slider() != null) {
                    dragging = r;
                    applySlider(r, mx);
                } else if (r.click() != null) {
                    r.click().run();
                }
                return true;
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (dragging != null) {
            applySlider(dragging, mx);
            return true;
        }
        return super.mouseDragged(mx, my, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        if (dragging != null) {
            dragging = null;
            ConfigManager.save();
        }
        return super.mouseReleased(mx, my, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
