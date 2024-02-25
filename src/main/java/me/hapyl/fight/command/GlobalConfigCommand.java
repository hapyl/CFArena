package me.hapyl.fight.command;

import me.hapyl.fight.CF;
import me.hapyl.fight.database.collection.GlobalConfigCollection;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.globalconfig.Configuration;
import me.hapyl.fight.ux.Message;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class GlobalConfigCommand extends CFCommand {
    public GlobalConfigCommand(@Nonnull String name) {
        super(name, PlayerRank.ADMIN);

        setAliases("gc", "config");

        addCompleterValues(1, Configuration.values());
        addCompleterValues(2, "enable", "disable");
    }

    @Override
    protected void execute(@Nonnull Player player, @Nonnull String[] args, @Nonnull PlayerRank rank) {
        // globalconfig (value) (enable/disable)

        final Configuration configuration = getArgument(args, 0).toEnum(Configuration.class);
        final String argument = getArgument(args, 1).toString().toLowerCase();

        if (configuration == null) {
            Message.error(player, "Could not find configuration named {}!", getArgument(args, 0));
            return;
        }

        final GlobalConfigCollection globalConfig = CF.getDatabase().getGlobalConfig();
        final boolean enabled = globalConfig.isEnabled(configuration);
        final String configurationName = configuration.name();

        switch (argument) {
            case "enable" -> {
                if (enabled) {
                    Message.error(player, "{} is already enabled!", configurationName);
                    return;
                }

                globalConfig.setEnabled(configuration, true);
                Message.success(player, "Enabled {}!", configurationName);
            }

            case "disable" -> {
                if (!enabled) {
                    Message.error(player, "{} is already disabled!", configurationName);
                    return;
                }

                globalConfig.setEnabled(configuration, false);
                Message.success(player, "Disabled {}!", configurationName);
            }

            default -> {
                Message.error(player, "Invalid argument! Expected {}, got {}.", getCompleterValues(2), argument);
            }
        }
    }
}
