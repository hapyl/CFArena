package me.hapyl.fight.translate;

import com.google.common.collect.Maps;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.game.Debug;
import me.hapyl.spigotutils.module.util.BFormat;
import me.hapyl.spigotutils.module.util.DependencyInjector;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Translate extends DependencyInjector<Main> {

    private static final Pattern formatPattern = Pattern.compile("<(.*?)>");
    private final Map<Language, TranslateMap> translateMap = Maps.newHashMap();

    public Translate(Main plugin) {
        super(plugin);

        // Load language files
        load(false);
    }

    public void load(boolean replace) {
        translateMap.clear();
        final Main plugin = getPlugin();

        for (Language language : Language.values()) {
            final String fileName = "lang/" + language.getFileName();
            final InputStream resource = plugin.getResource(fileName);

            if (resource == null) {
                Debug.warn("Missing translation file for '%s'!", language.name());
                continue;
            }

            // Save default resource
            plugin.saveResource(fileName, replace);

            // Load resource
            final TranslateMap translateMap = new TranslateMap(language);
            final YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/" + fileName));

            yaml.getValues(true).forEach((key, value) -> {
                if (!(value instanceof String stringValue)) {
                    return;
                }

                translateMap.put(key, stringValue);
            });

            this.translateMap.put(language, translateMap);
        }
    }

    // *=* Static members *=*

    @Nonnull
    public static String getFormatted(@Nonnull Language language, @Nonnull String message, @Nullable Object... format) {
        final Matcher matcher = formatPattern.matcher(message);
        final StringBuilder builder = new StringBuilder();

        while (matcher.find()) {
            final String key = matcher.group(1);
            final String translated = getFormatted(language, getTranslated(language, key));

            matcher.appendReplacement(builder, translated);
        }

        matcher.appendTail(builder);

        final String string = builder.toString();
        return format != null ? BFormat.format(string, format) : string;
    }

    @Nonnull
    public static String getFormatted(@Nonnull CommandSender sender, @Nonnull String message, @Nullable Object... format) {
        return getFormatted(getLanguage(sender), message, format);
    }

    @Nonnull
    public static String getTranslated(@Nonnull CommandSender sender, @Nonnull String key) {
        return getTranslated(getLanguage(sender), key);
    }

    @Nonnull
    public static String getTranslated(@Nonnull Language language, @Nonnull String key) {
        return getTranslated(language, new TranslateKey(key));
    }

    @Nonnull
    public static String getTranslated(@Nonnull Language language, @Nonnull TranslateKey key) {
        final TranslateMap translateMap = current().translateMap.get(language);

        return translateMap != null ? translateMap.getString(key) : key.toString();
    }

    @Nonnull
    public static Translate current() {
        return Main.getPlugin().getTranslate();
    }

    private static Language getLanguage(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            return Language.ENGLISH;
        }

        return PlayerDatabase.getDatabase(player).getLanguage();
    }

}
