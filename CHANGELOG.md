# Changelog for [hybrid-gradle](https://github.com/hybridlabs/hybrid-gradle)

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.4+loom1.19.4+mdg2.0.148] 2026-11-11

- Picked the Loom plugin by `minecraft_version`: 26.1 or newer builds without remapping through
  `net.fabricmc.fabric-loom`, older obfuscated versions keep the remapping plugin, so 1.20.1 and
  1.21.x mods build unchanged

## [1.0.3+loom1.19.4+mdg2.0.148] 2026-11-11

- Pinned Loom 1.19.4 and ModDevGradle 2.0.148
- Gradle now has to run on Java 25 or newer, which Loom 1.19 asks for, whatever loader the mod
  builds for; the Gradle wrapper was raised to 9.9.1, which Loom 1.19 requires
- Made `parchment_version` optional: without it the official Mojang mappings are used alone
- Encoded the Loom and ModDevGradle versions in the release version, as `+loom` and `+mdg` build
  metadata, so a mod sees what is pinned in its `settings.gradle`

## [1.0.2] 2026-10-02

- Added the `local_mods` property, which builds a mod from a checkout such as a git submodule in
  place of its published jars

## [1.0.1] 2026-09-13

- Fail the build when a `mods.toml` dependency has no `versionRange`, which Forge 1.20.1 treats as
  matching no version

## [1.0.0] 2026-09-13

- The shared Gradle setup of the HybridLabs multiloader mods, moved out of each mod's `buildSrc`
