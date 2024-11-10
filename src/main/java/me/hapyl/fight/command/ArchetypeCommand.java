package me.hapyl.fight.command;

import me.hapyl.eterna.module.command.SimplePlayerCommand;
import me.hapyl.fight.Notifier;
import me.hapyl.fight.game.heroes.Archetype;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class ArchetypeCommand extends SimplePlayerCommand {
    public ArchetypeCommand(@Nonnull String name) {
        super(name);

        addCompleterValues(0, Archetype.values());
    }

    @Override
    protected void execute(Player player, String[] strings) {
        final Archetype archetype = getArgument(strings, 0).toEnum(Archetype.class);

        if (archetype == null) {
            Notifier.error(player, "Invalid archetype!");
            return;
        }

        Notifier.info(player, "About %s archetype:");
        Notifier.info(player, archetype.getDescription());
    }

}
