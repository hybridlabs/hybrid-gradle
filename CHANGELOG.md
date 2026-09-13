# Changelog for [hybrid-gradle](https://github.com/hybridlabs/hybrid-gradle)

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.1] 2026-09-13

- Fail the build when a `mods.toml` dependency has no `versionRange`, which Forge 1.20.1 treats as
  matching no version

## [1.0.0] 2026-09-13

- The shared Gradle setup of the HybridLabs multiloader mods, moved out of each mod's `buildSrc`
