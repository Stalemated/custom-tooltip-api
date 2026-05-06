# Custom Tooltip API

**Custom Tooltip API** is a powerful client-side tool and library designed for developers, modpack creators, and players who want absolute control over item tooltips. Add custom lore, dynamic gradients, and advanced positioning to any item without the need for complex code.

***

### ✨ Key Features

* **Dynamic Detection:** Target item groups via Tags (e.g., `#c:swords`) or specific IDs (e.g., `minecraft:diamond_sword`).
* **Advanced Targeting:** Apply tooltips to entire mods via Namespaces (`minecraft:*`), use Regex (`regex:.*_sword`), or target all items (`*`).
* **Dynamic Placeholders:** Embed real-time item data directly into your text using variables like `%max_durability%`, `%weapon_damage%`, or `%enchantments%`.
* **Visual Effects:** Support for Rainbow, Slide Gradients, Breathing Gradients, Solid Gradients and Solid Colors by using hex codes (e.g. #RRGGBB), legacy codes (&d) or Minecraft color names (blue).
* **Advanced Positioning:** Choose between `Top`, `Bottom`, `Append`, `Prepend`, `Replace Name`, or `Replace All`.
* **In-Game GUI:** Full integration with YACL and ModMenu for real-time editing. Hold `CTRL` in the edit screen for an instant **Live Preview** of your tooltip!

---

### 🛠️ Developer API

Developers can easily register custom and dynamic tooltips via code using the new Builder API:

```java
// Example: A dynamic tooltip that reads NBT/State in real-time
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

// Example: Registering a custom placeholder for your mod
CustomTooltipApi.registerPlaceholder("mana", stack -> getManaAmount(stack));
```

These entries merge with user-defined JSON configs.

More examples can be found in the `com.stalemated.customtooltips.test.CustomTooltipApiTest` class.

---

### 🏗️ How to Build

If you want to compile the project yourself, follow these steps:

#### 1. Clone the Repository:
```bash
  git clone https://github.com/Stalemated/custom-tooltip-api.git
  cd custom-tooltip-api
```

#### 2. Build the Project:
   Use the included Gradle wrapper to compile the mod:

- Windows: `gradlew.bat build`

- Linux/macOS: `./gradlew build`

Once finished, you will find the compiled file in:
`build/libs/custom-tooltip-api-<version>.jar`

---

### 📦 Dependencies
Custom Tooltip API has the following dependencies:

* [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api)
* [YACL](https://www.curseforge.com/minecraft/mc-mods/yacl)
* [ModMenu](https://www.curseforge.com/minecraft/mc-mods/modmenu)

### 📄 License
This project is licensed under the MIT License.
