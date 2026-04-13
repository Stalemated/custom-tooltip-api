# ✦ Custom Tooltip API ✦

**Custom Tooltip API** is a powerful client-side tool and library designed for developers, modpack creators, and players who want absolute control over item tooltips. Add custom lore, dynamic gradients, and advanced positioning to any item without the need for complex code.

***

### ✨ Key Features

* **Dynamic Detection:** Target item groups via Tags (e.g., `#c:swords`) or specific IDs (e.g., `minecraft:diamond_sword`).
* **Visual Effects:** Support for Rainbow, Slide Gradients, Breathing Gradients, and Hex codes (#RRGGBB).
* **Advanced Positioning:** Choose between `Top`, `Bottom`, `Append`, `Prepend`, `Replace Name`, or `Replace All`.
* **In-Game GUI:** Full integration with ModMenu and Cloth Config for real-time editing with live previews.

---

### 🛠️ Developer API

Developers can register tooltips via code by adding this mod as a dependency and using the following logic:

```java
TooltipEntry myEntry = new TooltipEntry(
    "minecrafct:apple", // Item ID or tag
    new ArrayList<>(Arrays.asList("Magic Apple")), // Custom text
    TooltipEntry.TooltipStyle.SOLID, // Style
    new ArrayList<>(Arrays.asList("red")), // Colors
    false, false, false, false, false, // Formatting
    false, false, // Shift requirement, empty line before
    TooltipEntry.TooltipPosition.TOP, 0, // Position, line offset
    0, 1L // Animation offset, tickrate
);

CustomTooltipApi.registerTooltip(myEntry);
```

These entries merge with user-defined JSON configs.

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
`build/libs/custom-tooltip-api-1.1.0.jar`

---

### 📦 Dependencies
Custom Tooltip API has the following dependencies:

* [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api)
* [Necronomicon API](https://www.curseforge.com/minecraft/mc-mods/necronomicon)
* [Cloth Config API](https://www.curseforge.com/minecraft/mc-mods/cloth-config)
* [ModMenu](https://www.curseforge.com/minecraft/mc-mods/modmenu)

### 📄 License
This project is licensed under the MIT License.
