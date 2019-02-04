# Changelog

## [Unreleased]
### Added
- Option to show or hide durability when full for armor bars
### Changed
- License changed to MIT
### Fixed
- Block viewer bug with shaders or Optifine Fast Render (#50)
- Save list not updating when saving new configs (#49)
- Log spam in debug mode when an OpenGL inconsistency is present
- Block viewer doesn't show inventory names on 1.4-beta.X even though it's supported
- Crash using blood/water droplets (#47)

## [1.4-beta.3] - 2019-01-19
### Added
- Russian language support (ru_RU)
- Debug mode log warnings for OpenGL inconsistencies
### Changed
- Removed text shadows from scoreboard sidebar and sign reader
### Fixed
- Mod potion icons are uncentred in potion viewer
- Global settings not being saved
- Config saved multiple times unnecessarily
- Item pickup crash with Forge < 14.23.1.2557
- Crash while using potion bar
- Correct modid in `mcmod.info`
- Crash if config directory doesn't exist

## [1.4-beta.2] - 2019-01-16
### Added
- Potion bar timer display (#32)
- Option to hide HUD with debug screen open
### Changed
- Change modid from `hud` to `betterhud`
### Removed
- Keybinding (F3) to disable mod
### Fixed
- Heal indicator localization error
- Strings rendering full opacity when <4 alpha
- Experience bar default position is top left instead of bottom
- OpenGL state inconsistency with vignette
- Config files in both jar and folder show up twice
- Correct modid for resources
- Incorrect rectangle-point collision
- Potion display incorrect position/texture (#31)
- Block viewer box has flat instead of gradient shading
- OpenGL consistency improvements
- Colors have swapped alpha and blue values, incorrectly scaled
- GlStateManager blend func corruption
- Add OpenGL state inconsistency warnings
- Crash with blood/water droplets (#35)
- Some mods unable to redraw vanilla elements (#29)
- Armor bar ignores special armor values (#34)
- Unable to set parents other than the child creating a loop
- Crash with invalid ID enchantments
- Elements showing in non-alphabetical order
- Pickup count doesn't work for 1.4-beta.X even though it's supported

## [1.4-beta] - 2018-09-26
### Added
- Server compatibility checks for future elements
- CPS counter
- Block viewer recognises custom inventory names
- Block viewer is smarter with special blocks
- New positioning system which accepts an anchor, an offset and an alignment
- Elements can be anchored to a parent element instead of the whole screen
- XP viewer can show both total XP and XP since death
- Sorting on the main HUD menu
- Manually change element draw order with a new GUI
- Vanilla elements can now be moved, configured and disabled
- Better support for adding and changing translations
- Some Brazilian Portuguese support
- Option to disable compass and clock without corresponding item
- Configs can be saved and loaded from files
- Option to show undamageable items in Holding Bar
- Text mode for compass
- Support for Forge's update check
### Changed
- Multiple choice settings now save their names rather than indices
- Config brought in line with Forge
- Clarified settings text
- Entity info scale can now be configured globally
- Player info moved to its own element
- Improved ping performance, no packet spam
- Hide survival mode specific elements while in creative mode
- Mob info now shows shaking hearts when a mob's health is low
### Removed
- Text mode for sign viewer
- Hunger indicator
### Fixed
- Absolute positions are correctly anchored
- No longer corrupts the game's OpenGL state manager, so most render bugs should be fixed
- Arrow count works correctly if the player is left-handed and has a bow in their offhand
- Armor bars showing wrong warning
- Mob Info showing incorrect health bar
- Configuration not saving some values
- Player hider not turning off
- Compass directions disappearing with 0% perspective
- ConcurrentModificationException crashes
- Unsnap prompt not localized
- Health bar incorrect rounding

## [1.3.9] - 2017-07-06
### Added
- Global on/off keybinding
- Hide bar option for holding bar
- Holding bar has damage warnings, both that and armor can have warnings disabled
- Armor bars show empty slot icon if nothing is being worn in that slot
### Fixed
- Clock always reports day 2 after sleeping
- Progress bars show incorrect colors
- GUI elements changing the color of other GUIs
- Player's hand is transparent when using mob info
- Coordinates unlocalized
- Item pickup sometimes reports wrong value
- Mob info shows massive box when mobs have high health
- Config file save issues
- Config file doesn't save at appropriate times
- Mob info shows garbled text
- Full inventory indicator reports incorrectly
- Custom enchantments crash
- Mob info crash

## [1.3.8] - 2016-05-30
### Added
- Anchor elements to a corner or side so resizing works gracefully
### Fixed
- Ladder crash with block viewer
- When using overlay, total arrow count ignores offhand

## [1.3.7] - 2016-03-15
### Added
- Chinese and German translations
- Holding bar can show offhand
- Potion viewer can override vanilla version
### Changed
- Inventory full includes offhand slot
- Arrow count includes offhand slot
- By default, hunger indicator only turns on when player can eat
### Fixed
- Test rendering issue with both horse and mob info
- Several bugfixes and stability

## [1.3.6] - 2015-12-30
### Fixed
- Item pickup works on servers when installed

## [1.3.5] - 2015-11-28
### Added
- Option to change level at which hunger indicator shows up
- Option to increase or decrease precision of coordinates
### Changed
- Improved error handling with settings files
### Fixed
- Connection info not showing IP
- Sliders staying selected and sliding when not held down
- Y coordinate incorrect in 1.7.10

## [1.3.4] - 2015-11-25
### Changed
- Settings that overflow the screen will scroll

## [1.3.3] - 2015-10-29
### Fixed
- Java incompatibility crash
- Mob info and related not rendering

## [1.3.2] - 2015-10-06
### Fixed
- Two crashes
- Config file not saving water drops value
- Miscellaneous bugs
- Texts being displayed in reverse order when on bottom of screen

## [1.3.2] - 2015-10-01
### Added
- Elements snap to boundaries to align them better
- Change color of text elements, e.g. light level or FPS

## [1.3.1b] - 2015-09-28
### Added
- Block viewer fallback
- Block viewer option to show numerical ID
### Fixed
- Block viewer fixed fully

## [1.3.1a] - 2015-09-22
### Fixed
- Redstone ore crash with block viewer

## [1.3.1] - 2015-09-19
### Added
- Can alter the max distance of mob info, horse and breeding info
- Can alter the distance of block viewer much further
- Customise warnings on armor bars
- Armor bars alignment
- Block viewer displays inventory item
### Fixed
- Item rendering bug with armor bars
- Startup crashes
- Incorrect mcmod.info file (and no logo)
- Block viewer shows incorrect metadata
- Missing language file text

## [1.3] - 2015-09-06
### Added
- System clock
- Ping option for connection
- Move elements anywhere on the screen and set positions
### Changed
- Overhauled options system, more customization
- Elements now have whole pages of config rather than just 1 mode

## [1.2.2] - 2015-08-07
### Added
- Rain droplets on camera
- Items picked up counter
### Fixed
- Miscellaneous crashes

## [1.2.1] - 2015-07-30
### Added
- Full inventory indicator
- Better logging
### Changed
- Breed indicator no longer shows countdown due to technical limitations
- Improved math on compass
### Removed
- Light level (didn't work)
- Unused methods
- Unnecessary debug outputs
### Fixed
- Config file placed in root under Linux
- Black background when clock enabled and bed icon visible while in a menu
- Skybox broken during sleeping hours unless chat is open
- Crash with sign reader
- OpenGL error when an item is enchanted using player info
- Screen gets spammed with blood when /kill is used
- Blood splatters are processed even if not displayed
- Horse Info box renders completely black
- Some block names not being localized correctly
- Items display garbled in armor info
- Food indicator flickering
- Crash when looking at water from far away
- Sign viewer flashes with food indicator
- Skybox changes brightness of transparent elements
- Crash on some enchanted items

## [1.2] - 2015-06-17
### Changed
- Updated for 1.8
### Fixed
- Miscellaneous

## [1.1b] - Unknown
- Unknown

## [1.1a] - 2015-01-25
### Fixed
- Block viewer crash

## [1.1] - 2015-01-17
### Added
- Readme file
- English UK language support
- Equipped info can hide names or bars
- Equipped info split off into armor bars
- Option to move coordinates left or right
- Option to move breed indicator left or right
### Changed
- GUI sorted into pages
- Improved lok and feel of GUI
- Heal indicator on right if food and health stats is off
### Fixed
- Coordinates rounding
- Empty space if no armor equipped when armor bars is enabled
- Unlocalized coordinates
- Players info incorrect on multiplayer

## [1.0] - 2014-12-29
### Added
- Arrow counter
- Biome display
- Block viewer
- Blood splatters
- Breed indicator
- Clock
- Compass
- Connection info
- Coordinates display
- Distance indicator
- Enchant indicator (shows up when you can enchant at lvl30)
- Equipped armor durability bars
- Experience info (XP until next level)
- Food and health stats (saturation and numerical food value)
- Food indicator (flashes when you need to eat)
- FPS display
- Heal indicator (visible when the player can heal naturally)
- Hide other players
- Horse info
- Light level display
- Mob info
- Potion bar for active effects
- Sign reader
- Configuration menu
