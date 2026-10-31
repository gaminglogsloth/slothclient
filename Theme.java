package com.sloth.client;

public final class Theme {
    public static final String[] NAMES = {"Violet", "Cyan", "Rose", "Emerald", "Amber", "Sky", "Pink", "Lime"};
    public static final int[] COLORS = {0x8B5CF6, 0x22D3EE, 0xF43F5E, 0x10B981, 0xF59E0B, 0x3B82F6, 0xEC4899, 0x84CC16};
    public static int index = 0;

    private Theme() {}

    /** Current accent as opaque ARGB. */
    public static int accent() {
        return 0xFF000000 | COLORS[index];
    }

    /** Applies an alpha (0-255) to an RGB color. */
    public static int alpha(int rgb, int a) {
        return ((a & 0xFF) << 24) | (rgb & 0xFFFFFF);
    }
}
