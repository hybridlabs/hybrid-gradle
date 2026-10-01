# hybrid-gradle

The Gradle setup shared by the HybridLabs multiloader mods, published to
<https://maven.dvitski.cc/releases>. A mod's build scripts only list what is specific to that mod:
its dependencies, its data generation settings and its Modrinth/CurseForge relations.

It also pins Loom, ModDevGradle and mod-publish-plugin, so updating them for every mod means
releasing a new version of this plugin and bumping the version in each mod's `settings.gradle`.

## Usage

`settings.gradle`:

```groovy
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            name = 'Fabric'
            url = 'https://maven.fabricmc.net/'
        }
        maven {
            name = 'Hybrid Labs'
            url = 'https://maven.dvitski.cc/releases'
        }
    }
}

plugins {
    id 'dev.hybridlabs.multiloader' version '1.0.2'
}

rootProject.name = 'hybrid-example'
include 'common'
include 'fabric'
include 'neoforge'
```

No root `build.gradle` or `buildSrc` is needed. Each subproject applies its plugin, plus Kotlin
(versioned by `kotlin_version`):

```groovy
plugins {
    id 'dev.hybridlabs.fabric'
    id 'org.jetbrains.kotlin.jvm'
}
```

## Plugins

| Plugin | Applied to | Provides |
| --- | --- | --- |
| `dev.hybridlabs.multiloader` | `settings.gradle` | JDK provisioning, Kotlin version, the other plugins |
| `dev.hybridlabs.common` | `common` | NeoForm (1.21) or MCP (1.20.1), mixin annotations, sources for the loaders |
| `dev.hybridlabs.fabric` | `fabric` | Loom, Minecraft, mappings, Fabric API, runs |
| `dev.hybridlabs.neoforge` | `neoforge` | ModDevGradle, NeoForge, runs, Kotlin for Forge |
| `dev.hybridlabs.forge` | `forge` | ModDevGradle legacy Forge, mixin refmap, MixinExtras, runs, Kotlin for Forge |

The project plugins also set up the repositories, the jar, metadata expansion and Maven publishing.
The loader plugins compile `common` into their jar and configure `publishMods`.

Fabric data generation is left to the mod, since Loom only allows configuring it once:

```groovy
fabricApi {
    configureDataGeneration {
        modId = mod_id
        outputDirectory = file('src/generated/resources')
    }
}
```

## Properties

Read from the mod's `gradle.properties`. Every key is also available as a placeholder in
`fabric.mod.json`, the `mods.toml` and the mixin configs.

Every `mods.toml` dependency needs a `versionRange`, `"[0,)"` for any version. Forge 1.20.1 reads a
missing one as a range nothing satisfies, so the build fails without it.

| Property | Used by |
| --- | --- |
| `mod_id`, `mod_name`, `mod_author`, `version`, `group` | all |
| `minecraft_version`, `java_version`, `parchment_version` | all |
| `parchment_minecraft` | all, optional: defaults to `minecraft_version` |
| `kotlin_version` | Kotlin |
| `neo_form_version` | `common` on 1.21; without it, `common` uses legacy Forge |
| `fabric_loader_version`, `fabric_version`, `fabric_kotlin_version` | `fabric` |
| `neoforge_version`, `kotlin_for_forge_version` | `neoforge` |
| `forge_version`, `kotlin_for_forge_version` | `forge` |
| `modrinth_id`, `curseforge_id` | publishing |
| `local_mods` | optional: see [Building a dependency from source](#building-a-dependency-from-source) |

## Building a dependency from source

A mod can build another of these mods from a checkout inside it, so a change to the dependency is
tested without publishing it. Add the dependency as a git submodule, and list its directory in the
mod's `gradle.properties`, separated by commas if there are several:

```properties
local_mods=hybrid-api
```

The dependencies stay declared by their published coordinates. The checkout replaces them whatever
version they ask for, as an [included build](https://docs.gradle.org/current/userguide/composite_builds.html)
with its own `gradle.properties`, so it has to use version 1.0.2 or newer of this plugin as well.

A directory that holds no build is skipped and the published jars are used, which is what a clone
without `--recurse-submodules` and the CI get. `git submodule update --init` checks the submodule
out, `git submodule deinit <directory>` or `-Plocal_mods=` goes back to the published jars.

On Fabric the checkout brings no dependencies of its own, so the mod has to declare the ones it
compiles against.

## Publishing a mod

1. Bump `version` in `gradle.properties`.
2. Add a `## [<version>]` section to `CHANGELOG.md`. Publishing fails without one.
3. `./gradlew publishMods -PdryRun` to check, then `./gradlew publishMods`.

List each loader's dependencies by the slug they share on both sites:

```groovy
publishMods.platforms.configureEach {
    requires 'fabric-api', 'fabric-language-kotlin'
}
```

Tokens are read from `modrinthToken` / `curseforgeToken` in `~/.gradle/gradle.properties`, or the
`MODRINTH_TOKEN` / `CURSEFORGE_TOKEN` environment variables.

## Releasing this plugin

1. Make the change, and bump `version` in `gradle.properties`.
2. Add a `## [<version>]` section to `CHANGELOG.md`.
3. `./gradlew publish`. Credentials are `hybridlabsMavenUser` / `hybridlabsMavenPassword` in
   `~/.gradle/gradle.properties`, or `HYBRIDLABS_MAVEN_USER` / `HYBRIDLABS_MAVEN_PASSWORD`.
4. Bump the version in each mod's `settings.gradle`.

To try a change on a mod before releasing, run `./gradlew publishToMavenLocal` here and add
`mavenLocal()` to the mod's `pluginManagement.repositories`.
