package me.hapyl.fight.filter;

import com.google.common.collect.Sets;
import me.hapyl.fight.Main;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

public final class ProfanityFilter {

    public static final String CENSOR_CHAR = "🖤";

    private static final Set<String> profanityWords = Sets.newHashSet();
    private static Main plugin;

    private ProfanityFilter() {
    }

    public static void instantiate(Main plugin) {
        if (ProfanityFilter.plugin != null) {
            throw new IllegalStateException("Already instantiated!");
        }

        ProfanityFilter.plugin = plugin;

        // Load profane words
        new BukkitRunnable() {
            @Override
            public void run() {
                final InputStream resource = plugin.getResource("profanity.json");

                if (resource == null) {
                    profanityWords.add("fuck");
                    return;
                }

                try {
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(resource));
                    String line;

                    while ((line = reader.readLine()) != null) {
                        final String stripped = stripLine(line);
                        profanityWords.add(stripped);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private String stripLine(String string) {
                if (string == null) {
                    return "";
                }

                string = string.trim();

                if (string.contains("[") || string.contains("]")) {
                    return "";
                }

                string = string.replace("\"", "");
                string = string.replace(",", "");

                return string;
            }
        }.runTaskLaterAsynchronously(plugin, 5);
    }

    public static boolean isProfane(@Nonnull String string) {
        return profanityWords.contains(string.toLowerCase());
    }

    public static boolean isInstantiated() {
        return plugin != null;
    }

    @Nonnull
    public static String replaceProfane(@Nonnull String message) {
        final StringBuilder messageBuilder = new StringBuilder();
        final char[] chars = (message + "\n").toCharArray();

        StringBuilder builder = new StringBuilder();

        for (final char c : chars) {
            if (c == ' ' || c == '\n') {
                messageBuilder.append(builder.toString().trim()).append(" ");
                builder = new StringBuilder();
                continue;
            }

            builder.append(c);

            final String string = builder.toString().trim();

            if (isProfane(string)) {
                final int stringLength = string.length();

                builder.replace(0, stringLength, CENSOR_CHAR.repeat(Math.min(stringLength, 6)));
            }

        }

        return messageBuilder.toString();
    }
}
