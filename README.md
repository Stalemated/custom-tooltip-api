# Custom Tooltip API

**Custom Tooltip API** is a powerful client-side Minecraft mod and developer library that gives you full control over item tooltips. Add custom lore, dynamic real-time data, animated gradients, and custom backgrounds, all configurable in-game or through code.

No need to code for basic usage, as it features a powerful in-game interface and full JSON support. However, it also provides a robust **Developer API** for those who want to inject tooltips via code.

***

## Features

*   **Icon Alignment:** Align tooltip icons from external resource packs to the start of the line so the position stays consistent!

*   **Advanced Targeting:** Target specific item IDs (`minecraft:diamond_sword`), tag groups (`#c:swords`), entire mod namespaces (`minecraft:*`), Regex patterns (`regex:.*_sword`), or every item at once (`*`).

*   **Dynamic Placeholders:** Embed real-time item data directly into your text using variables like `%durability%`, `%max_durability%`, `%weapon_damage%`, or `%enchantments%`. You can also embed keybind hints using the format `<key:key.jump>` -> `[Space]`.

*   **Multi-line Support (Lore):** Add entire paragraphs of lore or skill descriptions.

*   **Visual Effects & Gradients:**

    *   **Rainbow:** Animated rainbow effect.
    *   **Slide Gradient:** Two-color gradients that flow through the text.
    *   **Breathing Gradient:** A smooth pulse between two colors.
    *   **Static Gradient & Solid:** Fixed gradients or traditional solid text.
    *   **Colors:** Hex codes (e.g. `#RRGGBB`), legacy codes (e.g. `&d`) or Minecraft color names (e.g. `blue`).

*   **Custom Backgrounds & Borders:** Solid, Gradient, Texture (Stretch), or Texture (Framed / Nine-Slice) modes with full opacity control. Drop any background image into `config/custom-tooltip-api/backgrounds/` and enable it per tooltip via the dynamically generated resource pack. Supports full compatibility with **Tierify** custom rendering.

*   **Custom Fonts:** Drop any font into `config/custom-tooltip-api/fonts/` and enable it per tooltip via the dynamically generated resource pack.

*   **Positioning:** Decide exactly where your text appears:

    *   **Top:** Right below the item's name.
    *   **Bottom:** At the very end of all descriptions.
    *   **Prepend:** At the start of a specific line.
    *   **Append:** At the end of a specific line.
    *   **Replace Name:** Swap the item's default name for your custom text.
    *   **Replace Line:** Replace a specific line with your custom text.
    *   **Replace All:** Completely clear the original tooltip and show only yours.

*   **Visibility Conditions:** Show a tooltip only when an item is Damaged, Enchanted, or Unbreakable. Use **Require Keybind** mode to hide tooltips until the player holds a configurable key.

*   **Text Modifiers:** Full support for **Bold**, _Italic_, Underlined, ~~Strikethrough~~, and O̷͍̞͐͑b̷̻̌f̶̯̈u̸̲͗͐s̶̩̬͝c̵͎̊a̸̹̟̎ẗ̴͖̿e̴̟̪͊̈́ď̴̹̪ (obfuscated) text.

***

## In-Game Interface (GUI)

*   Manage all your tooltips directly from the **Mods Menu**. No need to manually edit files unless you want to.

*   Hold `CTRL` in the edit screen for an instant **Live Preview** of your tooltip.

*   Copy/paste entries to clipboard, reorder, duplicate, enable/disable, and sort by creation date or alphabetically.

***

## For Modpack Creators

The mod generates a `custom_tooltip_api/config.json5` file in your `config` folder. It fully supports code comments, making it incredibly easy to document your modpack's systems for your development team or community.

Drop custom backgrounds into `config/custom-tooltip-api/backgrounds/` and custom fonts into `config/custom-tooltip-api/fonts/`. Enable the dynamically generated resource pack to activate them.

---

## Developer API

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

## Building from Source

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

## Platform Support

| Platform | Versions       |
|----------|----------------|
| Fabric   | 1.20.1, 1.21.1 |
| Forge    | 1.20.1         |
| NeoForge | 1.21.1         |

---

## Dependencies

- [S-Lib](https://github.com/Stalemated/s-lib)
- [YACL](https://www.curseforge.com/minecraft/mc-mods/yacl)
#### Fabric only
- [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api)
- [ModMenu](https://www.curseforge.com/minecraft/mc-mods/modmenu)
