package dev.hybridlabs.gradle

/**
 * A mod built from a checkout inside the mod that depends on it, such as a git submodule, in place
 * of its published jars. The checkouts are listed in the local_mods property.
 */
class LocalMod {
    final File dir
    private final Properties gradleProperties = new Properties()

    private LocalMod(File dir) {
        this.dir = dir
        new File(dir, 'gradle.properties').withInputStream { gradleProperties.load(it) }
    }

    /** A listed directory that holds no build, such as a submodule that is not checked out, is skipped. */
    static List<LocalMod> listedIn(String localMods, File rootDir) {
        (localMods ?: '').tokenize(', ')
                .collect { new File(rootDir, it) }
                .findAll { new File(it, 'settings.gradle').exists() }
                .collect { new LocalMod(it) }
    }

    /** Whether the checkout's settings.gradle includes the loader: a leftover directory does not count. */
    boolean has(String loader) {
        new File(dir, 'settings.gradle').text =~ /(?m)^\s*include\W+${loader}\b/
    }

    /** The coordinates dev.hybridlabs.base publishes a loader's jar under, without the version. */
    String module(String loader) {
        "${gradleProperties.group}:${gradleProperties.mod_id}-${loader}-${gradleProperties.minecraft_version}"
    }
}
