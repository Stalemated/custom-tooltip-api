# Changelog

## 2.0.0
**Big update, especially for the config screen!
This completely removes Cloth Config as dependency in favor of YACL (Yet Another Config Lib) and implements a lot of QOL fixes.**

### BREAKING: YOUR OLD CONFIG WILL NOT WORK ANYMORE!!!

### Changes:
- Added YACL as the config manager and completely rewrote the tooltip config screen, as Cloth Config will no longer be used
- The config changed location, from `/config/custom_tooltips.json5`
  to `/config/custom_tooltip_api/config.json5`
- The `&` character can be used in the custom text field to allow for advanced formatting with legacy color codes (only with Solid style)
- Added better descriptions for several entries and added some color to them
- Implemented a keybind to open the config (unbound by default)
    - A toast pops up in the mod's config to show the player a config key exists (shows only once per session, and only if the key is unbound)
- Added a search bar to the Tooltip List screen
- Added a small horizontal text scrolling feature for item ids/tags that are too long to be displayed in the config menu
- Added a Double click to select all text in a textbox feature, can be toggled on/off in the config
- Added a proper custom text list that can be reordered instead of the old one
- Added 4 color pickers, 2 visual and 2 advanced: visual ones use a visual color picker, while advanced uses the old logic of either legacy codes, minecraft color names and hex codes, this overrides the visual picker
- Changed default primary color from gray to `#FFFFFF` (white)
- Loosened structure of color hex codes, now accepts prefixes (`0x`, `0X`, `x`, `X`, `#` and none)

### Fixes:
- The config gets automatically regenerated in case the json5 file exists but is of length 0 (nothing inside of it)
- The mod automatically handles an edge case where users may have malformed the json5 file (removing a bracket for example) by making a config backup in `/config/custom_tooltip_api/config_backup.json5`

## 1.2.2

### Additions:

- Added compatibility with Eldritch End's icons
- Removed the default tooltip as I figured people might not like it / find it annoying

## 1.2.1

### Fixes:

- There is now larger Unicode compat so more icon packs will work with the mod

- Now supports formatted icons (icons that are assigned a different color than white)

- Fixed tooltips in the same line as icons not having animation when using modes prepend and append

## 1.2.0

**This update focuses on improving performance, and adding a new option to align tooltip icons! (Thanks Afterlyte for requesting this!)**

### Additions:

- Implemented logic to align tooltip icons from resource packs to the start of the line so they can stay aligned
Added a small tooltip to indicate the player to press shift when a tooltip is available
- 
### Upgrades:

- Completely rewrote most of the internal logic to improve performance
- Implemented heavy caching to improve performance further 

### Fixes:

- Fixed bug that made custom tooltips show up above the hotbar with position modes APPEND and PREPEND and line offsets != 0

## 1.1.0

**This update adds an API for developers, some QOL additions and an important bug fix.**

### Additions:

- Added an actual API for developers, no need to edit tooltips in-game or by using the JSON5 file anymore

- The custom tooltip now shows up above your hotbar if it replaces the item's name

- Added a confirmation screen to the delete tooltip button

- Added a duplicate tooltip button

### Fixes:

- Default config (the sword tooltip) does not regenerate anymore if all entries are deleted, only if file is deleted

## 1.0.0

### Additions:

- **Advanced Text Rendering:** Integrated Necronomicon TextAPI to handle advanced gradients, custom text positioning, and all vanilla in-game text formatting types.

- **Full Mod Menu Integration:** Complete in-game configuration support via ModMenu, including support for localization/language files.

- **Live Tooltip Previews:** Added the actual rendered tooltip inside the tooltip selection screen for faster and easier selection.

- **More Placement Options:** The mod supports adding a tooltip at the top, bottom, prepend, append, replace the item's name, or entirely replace the item's original tooltip.

- **User-Friendly Configs:** Added detailed config descriptions, helpful hints, and a default tooltip example for first-time users.