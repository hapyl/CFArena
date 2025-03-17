package me.hapyl.fight.command;

import me.hapyl.eterna.module.command.SimplePlayerAdminCommand;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.fight.Message;
import me.hapyl.fight.database.entry.DailyRewardEntry;
import me.hapyl.fight.database.entry.ExperienceEntry;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.crate.Crates;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.loadout.HotBarSlot;
import me.hapyl.fight.game.setting.EnumSetting;
import me.hapyl.fight.game.stats.StatType;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.registry.Registries;
import org.bukkit.entity.Player;

import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

public class FixMongoDbMigrationFiles extends SimplePlayerAdminCommand {
    private boolean busy;

    public FixMongoDbMigrationFiles(String name) {
        super(name);
    }

    @Override
    protected void execute(Player player, String[] args) {
        try {
            final String hostName = InetAddress.getLocalHost().getHostName();

            if (busy) {
                Message.error(player, "The fixer is currently busy!");
                return;
            }

            if (!hostName.equals("hapyl")) {
                Message.error(player, "This can only be used on a localhost server!");
                return;
            }

            busy = true;
            Message.info(player, "Working on it...");

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
                Message.error(player, "Nothing to fix!");
                return;
            }

            Files.write(path, lines);
            Message.success(player, "Successfully fixed {%s} occurrences.".formatted(fixedCount));
        } catch (Exception e) {
            Message.error(player, "Error occurred! {%s}".formatted(e.getClass() + ":" + e.getMessage()));
        } finally {
            busy = false;
        }
    }

    private String doFix(String original) {
        //Debug.info(">| " + original);

        original = doFix(original, TalentRegistry.values());
        original = doFix(original, HeroRegistry.values());
        original = doFix(original, Registries.cosmetics().values());
        original = doFix(original, ExperienceEntry.Type.values());
        original = doFix(original, EnumSetting.values());
        original = doFix(original, StatType.values());
        original = doFix(original, Type.values());
        original = doFix(original, me.hapyl.fight.game.collectible.relic.Type.values());
        original = doFix(original, DailyRewardEntry.Type.values());
        original = doFix(original, Crates.values());
        original = doFix(original, HotBarSlot.values());
        original = doFix(original, Archetype.values());

        return original;
    }

    private <K extends Keyed> String doFix(String original, K[] keyed) {
        for (K k : keyed) {
            original = doFix(original, k);
        }

        return original;
    }

    private <K extends Keyed> String doFix(String original, List<K> keyed) {
        for (K k : keyed) {
            original = doFix(original, k);
        }

        return original;
    }

    private String doFix(String original, Keyed keyed) {
        final String newKey = keyed.getKeyAsString();
        final String oldKey = newKey.toUpperCase(); // assume enum

        return Pattern.compile("\\b" + Pattern.quote(oldKey) + "\\b").matcher(original).replaceAll(newKey);
    }
}
