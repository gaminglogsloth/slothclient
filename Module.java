package com.sloth.client.module;

import java.util.ArrayList;
import java.util.List;

public class Module {
    public final String name, description, category;
    public boolean enabled;
    public boolean expanded;
    public final List<Setting> settings = new ArrayList<>();

    public Module(String name, String description, String category, boolean enabled) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.enabled = enabled;
    }

    public Module setting(String name, double def, double min, double max) {
        settings.add(new Setting(name, def, min, max));
        return this;
    }

    public double value(String settingName) {
        for (Setting s : settings) if (s.name.equals(settingName)) return s.value;
        return 0;
    }
}
