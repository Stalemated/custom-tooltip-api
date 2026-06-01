# Custom Tooltip API

**Custom Tooltip API** is a powerful client-side Minecraft mod and developer library that gives you full control over item tooltips. Add custom lore, dynamic real-time data, animated gradients, resizable scrollable panels, and custom backgrounds, all configurable in-game or through code.

> **Latest:** 3.3.0: Tooltips are now resizable and scrollable!

---

## ✨ Key Features

### 🎨 Rich Visual Styling
- **Resizable & scrollable tooltips:** Cap tooltips to a percentage of your screen. If they overflow, scroll through them with your mouse wheel.
- **Animated gradients:** Rainbow, Slide, Breathing, and Solid Gradient styles
- **Solid colors** via hex codes (`#RRGGBB`), legacy codes (`&d`), or Minecraft color names (`blue`)
- **Custom backgrounds and borders:** Solid, Gradient, Texture (Stretch), or Texture (Framed) modes with full opacity control. Drop any background into `config/custom-tooltip-api/backgrounds/` and enable it per tooltip.
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

### 📌 Flexible Positioning
Place your tooltip exactly where it belongs: `Top`, `Bottom`, `Append`, `Prepend`, `Replace Name`, or `Replace All` (blank canvas mode).

### 🖥️ In-Game GUI
Full **YACL + ModMenu** integration with a completely redesigned config screen:
- **Live Preview:** Hold `CTRL` in the Edit Screen to instantly see your tooltip
- Copy/paste entries to clipboard, reorder, duplicate, enable/disable, and sort tooltips
- Dedicated **Scroll & Custom Tooltip Dimensions** screen (v3.3.0+)
- Toggleable visibility conditions: show only when an item is Damaged, Enchanted, or Unbreakable
- "Require Keybind" mode: show tooltips when the player holds a configurable key

---

## 🛠️ Developer API

Register tooltips and custom placeholders directly from code using the fluent Builder API:

```java
// Dynamic tooltip that reads live NBT/state every frame
TooltipEntry.builder("minecraft:diamond_sword")
    .style(TooltipEntry.TooltipStyle.BREATHING_GRADIENT)
    .colors("0x00FF00", "red")
    .bold(true)
    .position(TooltipEntry.TooltipPosition.TOP)
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

#### 1. Clone the repository
```bash
git clone https://github.com/Stalemated/custom-tooltip-api.git
cd custom-tooltip-api
```

#### 2. Build with Gradle
```bash
# Windows
gradlew.bat build

# Linux / macOS
./gradlew build
```

Output: `build/libs/custom-tooltip-api-<version>.jar`

---

## 🌍 Platform Support

| Platform | Versions |
|---|---|
| Fabric | 1.20.1, 1.21.1 |
| Forge | 1.20.1 |
| NeoForge | 1.21.1 |

---

## 📦 Dependencies

- [YACL](https://www.curseforge.com/minecraft/mc-mods/yacl)
#### Fabric only
- [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api)
- [ModMenu](https://www.curseforge.com/minecraft/mc-mods/modmenu)

---

## 📄 License

This project is licensed under the **MIT License**.