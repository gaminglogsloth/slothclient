# Sloth Client

A client-side Fabric mod that overhauls Minecraft Java's visuals and adds a mod menu on **Right Shift**.

## Features
- **Mod menu (Right Shift)** - tabbed UI with toggle switches, sliders and 8 accent themes. Also opens from the title and pause screens.
- **Info Panel** - FPS, coordinates, facing direction
- **Keystrokes** - WASD + mouse buttons
- **Clock**
- **Screen Glow** - accent-colored edge glow
- **Zoom** - hold C
- **Menu Branding** - themed title-screen overlay
- Settings are saved to `config/sloth.json`

## Build
Requires JDK 21 and Gradle 8.10+ (no wrapper is bundled).

    gradle wrapper      # once
    ./gradlew build     # Windows: gradlew.bat build

The jar lands in `build/libs/sloth-client-1.0.0.jar`.

## Install
1. Install Fabric Loader for Minecraft 1.21.4
2. Put Fabric API and `sloth-client-1.0.0.jar` in `.minecraft/mods`

Targets 1.21.4. To use another version, change the four version lines in `gradle.properties`
(values at https://fabricmc.net/develop). Small API changes may need code tweaks.

Note: this project intentionally has no `.gitignore`.

## Building on GitHub
A workflow is included at `.github/workflows/build.yml`. Push the project to a GitHub repo, open the
**Actions** tab, and download the `sloth-client` artifact from the finished run. No local setup needed.
