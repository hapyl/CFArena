package me.hapyl.fight.command;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.translate.Language;
import me.hapyl.fight.ux.Message;
import me.hapyl.spigotutils.module.command.SimplePlayerCommand;
import me.hapyl.spigotutils.module.util.Enums;
import org.bukkit.entity.Player;

public class LanguageCommand extends SimplePlayerCommand {
    /**
     * Creates a new simple command
     *
     * @param name - Name of the command.
     */
    public LanguageCommand(String name) {
        super(name);

        addCompleterValues(1, Language.values());
    }

    @Override
    protected void execute(Player player, String[] args) {
        final PlayerDatabase database = PlayerDatabase.getDatabase(player);
        final String string = getArgument(args, 0).toString();
        final Language language = Enums.byName(Language.class, string);

        if (language == null) {
            Message.error(player, "<error.invalid_argument>");
            return;
        }

        database.setLanguage(language);
        Message.success(player, "<language.selected>");
    }
}
