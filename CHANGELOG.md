<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Encore IntelliJ Plugin Changelog

## [Unreleased]
### Added
- From Encore v1.9.3 services databases can be automatically detected and configured within the IDE.
  This will add both the local database used by `encore run` and the test database used by `encore test`
- New settings panel which allows the plugin to be configured to use a different encore binary (useful when testing features) 

## [0.0.3]
### Bugs
- Fix support for directory tests (previously only file and function level tests worked).
- Changed the `newapi` live template to fix a typo and renamed the parameters prop to `p`

## [0.0.2]
### Added
- Added support for running unit tests of Encore applications in GoLand
- Added a live template `newapi` for creating a new API easily within GoLand
- Add syntax highlighting and icon for `encore.app` files
