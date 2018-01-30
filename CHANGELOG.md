# Changelog

## [Unreleased]
### Added
- Server compatibility checks for future elements
- CPS counter
- Block viewer recognises custom inventory names
- Block viewer is smarter with special blocks
- New positioning system which accepts an anchor, an offset and an alignment
- XP viewer can show both total XP and XP since death
### Changed
- Code refactoring, easier to work with
- Much smaller code
- Multiple choice settings now save their names rather than indices
- Config uses Forge's system
- Some settings show different text
- Language file made easier to view
### Removed
- Text mode for sign viewer
### Fixed
- Absolute positions are correctly anchored
- No longer corrupts the game's OpenGL state manager, so most render bugs should be fixed
- Arrow count works correctly if the player is left-handed and has a bow in their offhand

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
