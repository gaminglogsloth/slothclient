package com.sloth.client.module;

public class Setting {
    public final String name;
    public final double min, max;
    public double value;

    public Setting(String name, double def, double min, double max) {
        this.name = name;
        this.value = def;
        this.min = min;
        this.max = max;
    }
}
