package me.hapyl.fight.command;

import com.google.common.collect.Sets;
import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.talents.*;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.displayfield.DisplayFieldSerializer;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.command.SimpleAdminCommand;
import me.hapyl.eterna.module.util.BukkitUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class DumpHeroData extends SimpleAdminCommand {
    public DumpHeroData(String name) {
        super(name);

        addCompleterValues(1, HeroRegistry.keys());
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        final Hero hero = HeroRegistry.ofStringOrNull(getArgument(args, 0).toString());

        if (hero == null) {
            Chat.sendMessage(sender, "&cInvalid hero!");
            return;
        }

        final String argList = getArgument(args, 1).toString();
        final StringArgumentMatcher argMatcher = new StringArgumentMatcher(argList);

        boolean stripColor = !argMatcher.match('c');

        final File file = new HeroDumper(hero, stripColor).dump();

        Chat.sendMessage(sender, "&aDumped into '%s'!".formatted(file.getAbsolutePath()));
    }

    private static class StringArgumentMatcher {

        private final Set<Character> args;

        private StringArgumentMatcher(String string) {
            this.args = Sets.newHashSet();

            if (string.startsWith("-")) {
                final char[] chars = string.substring(1).toCharArray();

                for (char c : chars) {
                    if (Character.isWhitespace(c) || c == ' ' || !Character.isLetter(c)) {
                        continue;
                    }

                    args.add(Character.toLowerCase(c));
                }
            }
        }

        public boolean match(char c) {
            return args.contains(Character.toLowerCase(c));
        }

    }

    private static class HeroDumper {

        private final Hero hero;
        private final boolean stripColor;
        private final File path;
        private final File file;

        public HeroDumper(Hero hero, boolean stripColor) {
            this.hero = hero;
            this.stripColor = stripColor;

            path = new File(Main.getPlugin().getDataFolder() + "/hero_dumps/");
            file = new File(path, hero.getKeyAsString() + ".md");
        }

        public File dump() {
            if (!path.exists()) {
                path.mkdirs();
            }

            final LocalDate now = LocalDate.now();
            final HeroProfile profile = hero.getProfile();

            try (MdFileWriter writer = new MdFileWriter(this)) {
                writer.comment("%s ; v%s ; %s".formatted(hero.getKey(), CF.getVersionNoSnapshot(), now.toString()));

                writer.header("Name:");
                writer.append(hero.getName());

                writer.header("Description:");
                writer.append(hero.getDescription());

                writer.header("Archetype");
                writer.append(profile.getArchetypes().toString());

                final Affiliation affiliation = profile.getAffiliation();
                final Race race = profile.getRace();
                final Gender gender = profile.getGender();

                if (affiliation != Affiliation.NOT_SET) {
                    writer.header("Affiliation");
                    writer.append(affiliation.getName());
                }

                if (race != Race.UNKNOWN) {
                    writer.header("Race");
                    writer.append(race.toString());
                }

                if (gender != Gender.UNKNOWN) {
                    writer.header("Gender");
                    writer.append(gender.toString());
                }

                // Attributes
                final HeroAttributes attributes = hero.getAttributes();

                writer.nl();
                writer.header("Attributes");

                attributes.forEachMandatoryAndNonDefault((type, value) -> {
                    writer.append(" %s: %s".formatted(type.getName(), type.getFormatted(attributes)));
                });

                // Weapon
                writer.nl();
                writer.header("Weapon");

                final Weapon weapon = hero.getWeapon();

                writer.append(weapon.getName());
                writer.append(weapon.getDescription());

                // Talents
                writer.nl();
                writer.header("Talents");

                hero.getTalentsSorted().forEach(talent -> {
                    if (talent == null) {
                        return;
                    }

                    writer.append("#" + talent.getName());
                    writer.append(talent.getTypeFormattedWithClassType());
                    writer.nl();

                    final String description = StaticFormat.formatTalent(talent.getDescription(), talent);

                    writer.append(description);

                    // Details

                    writer.append("| Details");
                    writer.append("| ");

                    final TalentType type = talent.getType();

                    writer.append("| " + type.getName());
                    writer.append("| " + type.getDescription());
                    writer.append("| ");

                    final int cd = talent.getCooldown();

                    if (cd > 0) {
                        writer.append("| Cooldown%s: %ss".formatted(
                                talent instanceof ChargedTalent ? " between charges" : "",
                                BukkitUtils.roundTick(cd)
                        ));
                    }
                    else if (cd <= -1) {
                        writer.append("| Cooldown: Dynamic");
                    }

                    final int duration = talent.getDuration();

                    if (duration > 0) {
                        writer.append("| Duration: %ss".formatted(BukkitUtils.roundTick(duration)));
                    }

                    final int point = talent.getPoint();

                    if (point > 0 && !(talent instanceof PassiveTalent || talent instanceof UltimateTalent)) {
                        writer.append("| Point%s Generation: %s".formatted(point == 1 ? "" : "s", point));
                    }

                    final List<String> fields = DisplayFieldSerializer.serialize(talent, DisplayFieldSerializer.DEFAULT_FORMATTER);

                    fields.forEach(field -> writer.append("| " + field));

                    writer.append("---------------------------");
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

            return file;
        }
    }

    @SuppressWarnings("all")
    private static class MdFileWriter extends FileWriter {

        private final HeroDumper dumper;

        public MdFileWriter(HeroDumper dumper) throws IOException {
            super(dumper.file, StandardCharsets.UTF_8);

            this.dumper = dumper;
        }

        public MdFileWriter comment(String comment) {
            try {
                append("<!-- %s -->".formatted(comment));
            } finally {
                return this;
            }
        }

        public MdFileWriter header(String string) {
            try {
                append("### " + string);
            } finally {
                return this;
            }
        }

        @Override
        public MdFileWriter append(CharSequence csq) {
            try {
                super.append(workColor(csq));

                this.nl();
            } finally {
                return this;
            }
        }

        private String workColor(CharSequence csq) {
            String string = csq.toString();

            string = string.replace('&', ChatColor.COLOR_CHAR);
            string = string.replace(";;", ""); // text block wrap artifact

            if (dumper.stripColor) {
                string = ChatColor.stripColor(string);
            }

            return string;
        }

        private MdFileWriter nl() {
            try {
                super.append("\n");
            } finally {
                return this;
            }
        }

    }
}
