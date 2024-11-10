package me.hapyl.fight.util.strict;

import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.talents.Talent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import javax.annotation.Nonnull;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class StrictValidator {

    public static void validateAll(@Nonnull Main caller) {
        if (caller != CF.getPlugin()) {
            throw new IllegalArgumentException("Cannot validate outside the Main class!");
        }

        validatePackages();
    }

    public static void validatePackages() {
        validatePackage(Talent.class);
        validatePackage(Hero.class);
    }

    public static void validateClassName(@Nonnull Class<?> clazz, @Nonnull String stringPattern) {
        final Pattern pattern = Pattern.compile(stringPattern);
        final String className = clazz.getSimpleName();

        if (!pattern.matcher(className).matches()) {
            throw disablePlugin("Class name '%s' violates the pattern '%s'!".formatted(className, pattern));
        }
    }

    private static void validatePackage(Class<?> clazz) {
        if (clazz == null) {
            throw disablePlugin("Cannot validate null class!");
        }

        final StrictPackage annotation = clazz.getAnnotation(StrictPackage.class);
        final String className = clazz.getSimpleName();

        if (annotation == null) {
            throw disablePlugin("Cannot validate class '%s' because it isn't annotated with '%s'!".formatted(
                    className,
                    StrictPackage.class
            ));
        }

        final String packageName = annotation.value();

        if (packageName.isEmpty() || packageName.isBlank()) {
            throw disablePlugin("Package name cannot be empty!");
        }

        final String actualPackageName = clazz.getPackage().getName();

        if (!packageName.equals(actualPackageName)) {
            throw disablePlugin("Class '%s' must be in package '%s', not '%s'!".formatted(className, packageName, actualPackageName));
        }
    }

    private static IllegalArgumentException disablePlugin(String errorMessage) {
        final PluginManager pluginManager = Bukkit.getPluginManager();
        final Main plugin = Main.getPlugin();

        final Logger logger = plugin.getLogger();

        logger.severe("");
        logger.severe("** Strict check failed, disabling plugin!");
        logger.severe("** Resolve the following issues:");
        logger.severe(errorMessage);
        logger.severe("");

        pluginManager.disablePlugin(plugin);

        return new IllegalArgumentException("Strict check failed! " + errorMessage);
    }

}
