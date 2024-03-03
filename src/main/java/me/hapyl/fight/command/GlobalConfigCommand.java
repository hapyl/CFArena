package me.hapyl.fight.command;

import me.hapyl.fight.CF;
import me.hapyl.fight.database.collection.GlobalConfigCollection;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.globalconfig.Configuration;
import me.hapyl.fight.ux.Notifier;
import me.hapyl.spigotutils.module.util.ArgumentList;
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
    protected void execute(@Nonnull Player player, @Nonnull ArgumentList args, @Nonnull PlayerRank rank) {
        // globalconfig (value) (enable/disable)

        final Configuration configuration = args.get(0).toEnum(Configuration.class);
        final String argument = args.get(1).toString().toLowerCase();

        if (configuration == null) {
            Notifier.error(player, "Could not find configuration named {}!", args.getString(0));
            return;
        }

        final GlobalConfigCollection globalConfig = CF.getDatabase().getGlobalConfig();
        final boolean enabled = globalConfig.isEnabled(configuration);
        final String configurationName = configuration.name();

        switch (argument) {
            case "enable" -> {
                if (enabled) {
                    Notifier.error(player, "{} is already enabled!", configurationName);
                    return;
                }

                globalConfig.setEnabled(configuration, true);
                Notifier.success(player, "Enabled {}!", configurationName);
            }

            case "disable" -> {
                if (!enabled) {
                    Notifier.error(player, "{} is already disabled!", configurationName);
                    return;
                }

                globalConfig.setEnabled(configuration, false);
                Notifier.success(player, "Disabled {}!", configurationName);
            }

            default -> {
                Notifier.error(player, "Invalid argument! Expected {}, got {}.", getCompleterValues(2), argument);
            }
        }
    }
}
