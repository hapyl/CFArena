package me.hapyl.fight.command;

import me.hapyl.eterna.module.command.SimplePlayerAdminCommand;
import me.hapyl.fight.Notifier;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.archer.ShockDart;
import me.hapyl.fight.registry.Keyed;
import org.bukkit.entity.Player;

import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class FixMongoDbMigrationFiles extends SimplePlayerAdminCommand {
    public FixMongoDbMigrationFiles(String name) {
        super(name);
    }

    @Override
    protected void execute(Player player, String[] args) {
        try {
            final String hostName = InetAddress.getLocalHost().getHostName();

            if (!hostName.equals("hapyl")) {
                Notifier.error(player, "This can only be used on a localhost server!");
                return;
            }

            final Path path = Path.of(getArgument(args, 0).toString());
            final List<String> lines = Files.readAllLines(path);
            int fixedCount = 0;

            for (int i = 0; i < lines.size(); i++) {
                final String original = lines.get(i);
                final String fixed = doFix(original);

                if (original.equals(fixed)) {
                    continue;
                }

                fixedCount++;
                lines.set(i, fixed);
            }

            if (fixedCount < 0) {
                Notifier.error(player, "Nothing to fix!");
                return;
            }

            Files.write(path, lines);
            Notifier.success(player, "Successfully fixed {%s} occurrences.".formatted(fixedCount));

        } catch (Exception e) {
            Notifier.error(player, "Error occurred! {%s}".formatted(e.getClass() + ":" + e.getMessage()));
        }
    }

    private String doFix(String original) {
        Debug.info(">| " + original);

        for (Talent keyed : TalentRegistry.values()) {
            original = doFix(original, keyed);
        }

        for (Hero keyed : HeroRegistry.values()) {
            original = doFix(original, keyed);
        }

        return original;
    }

    private String doFix(String original, Keyed keyed) {
        String newKey;
        String oldKey;

        // I'm fucking fixing shock_darK it's bothering me
        if (keyed instanceof ShockDart) {
            newKey = "shock_dart";
            oldKey = "SHOCK_DARK";
        }
        else {
            newKey = keyed.getKeyAsString();
            oldKey = newKey.toUpperCase();
        }

        final Pattern pattern = Pattern.compile("\\b" + Pattern.quote(oldKey) + "\\b");

        return pattern.matcher(original).replaceAll(newKey);
    }
}
