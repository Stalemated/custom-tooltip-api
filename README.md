# Custom Tooltip API

**Custom Tooltip API** is a powerful client-side Minecraft mod and developer library that gives you full control over item tooltips. Add custom lore, dynamic real-time data, animated gradients, and custom backgrounds, all configurable in-game or through code.

> **⚠️ Looking for Tooltip Scrolling or Resizing?**
> Starting with version **4.0.0**, all tooltip scrolling, resizing, and custom dimension limits have been moved to a separate standalone mod: **[Smart Tooltip Scroll (STS)](https://github.com/Stalemated/smart_tooltip_scroll)**. 
> Additionally, this mod now requires **[S-Lib](https://github.com/Stalemated/s-lib)** to run.

---

## ✨ Key Features

### 🎨 Rich Visual Styling
- **Animated gradients:** Rainbow, Slide, Breathing, and Solid Gradient styles
- **Solid colors** via hex codes (`#RRGGBB`), legacy codes (`&d`), or Minecraft color names (`blue`)
- **Custom backgrounds and borders:** Solid, Gradient, Texture (Stretch), or Texture (Framed) modes with full opacity control. Drop any background into `config/custom-tooltip-api/backgrounds/` and enable it per tooltip. **Supports fully animated textures via `.png.mcmeta` files!**
- **Custom fonts:** Drop any font into `config/custom-tooltip-api/fonts/` and enable it per tooltip

### 🎯 Advanced Targeting
Target exactly what you want, nothing more:
- Specific item IDs (`minecraft:diamond_sword`)
- Tag groups (`#c:swords`)
- Entire mod namespaces (`minecraft:*`)
- Regex patterns (`regex:.*_sword`)
- Every item at once (`*`)

### 📊 Dynamic Placeholders
Embed live item data directly in tooltip text using built-in variables like `%durability%`, `%max_durability%`, `%weapon_damage%`, and `%enchantments%`. You can also embed keybind hints using the format `<key:key.jump>` → `[Space]`.
Translations are also supported by embedding `<translation:your.translation.key.here>` and using a resourcepack to create the lang files.

### 📌 Flexible Positioning
Place your tooltip exactly where it belongs: `Top`, `Bottom`, `Append`, `Prepend`, `Replace Name`, or `Replace All` (blank canvas mode).

### 🖥️ In-Game GUI
Full **YACL + ModMenu** integration with a completely redesigned config screen:
- **Live Preview:** Hold `CTRL` in the Edit Screen to instantly see your tooltip
- Copy/paste entries to clipboard, reorder, duplicate, enable/disable, and sort tooltips
- Toggleable visibility conditions: show only when an item is Damaged, Enchanted, or Unbreakable
- "Require Keybind" mode: show tooltips when the player holds a configurable key

---

## 🛠️ Developer API

Register tooltips and custom placeholders directly from code using the fluent Builder API:

```java
// Dynamic tooltip that reads live NBT/state every frame
CustomTooltipApi.builder("minecraft:diamond_sword")
    .style(TooltipStyle.BREATHING_GRADIENT)
    .colors("0x00FF00", "red")
    .bold(true)
    .position(TooltipPosition.TOP)
    .tickrate(35)
    .requireKeybind(true)
    .dynamicText(stack -> {
        int remaining = stack.getMaxDamage() - stack.getDamage();
        return List.of("Durability: " + remaining);
    })
    .register();

// Register a custom placeholder usable in any tooltip text
CustomTooltipApi.registerPlaceholder("mana", stack -> getManaAmount(stack));
```

API entries merge seamlessly with user-defined JSON configs. See `com.stalemated.customtooltips.test.CustomTooltipApiTest` for more examples.

---

## 🏗️ Building from Source

Starting from v4.0.0, CTA depends on **S-Lib**, which must be published to your local Maven repository before compiling.

#### 1. Clone and Publish S-Lib
```bash
git clone https://github.com/Stalemated/s-lib.git
cd s-lib
# Publish to maven local
gradlew.bat publishToMavenLocal # (Windows)
./gradlew publishToMavenLocal   # (Linux/macOS)
cd ..
```

#### 2. Clone CTA and Build
```bash
git clone https://github.com/Stalemated/custom-tooltip-api.git
cd custom-tooltip-api
# Build the mod
gradlew.bat build # (Windows)
./gradlew build   # (Linux/macOS)
```

Output jars will be located in `[loader]/build/libs/` or `build/libs/` depending on the platform.

---

## 🌍 Platform Support

| Platform | Versions       |
|----------|----------------|
| Fabric   | 1.20.1, 1.21.1 |
| Forge    | 1.20.1         |
| NeoForge | 1.21.1         |

---

## 📦 Dependencies

- [S-Lib](https://github.com/Stalemated/s-lib)
- [YACL](https://www.curseforge.com/minecraft/mc-mods/yacl)
#### Fabric only
- [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api)
- [ModMenu](https://www.curseforge.com/minecraft/mc-mods/modmenu)

---

## 📄 License

This project is licensed under the **MIT License**.