package me.hapyl.fight.command;

import me.hapyl.eterna.module.util.ArgumentList;
import me.hapyl.fight.CF;
import me.hapyl.fight.Message;
import me.hapyl.fight.config.Environment;
import me.hapyl.fight.config.EnvironmentProperty;
import me.hapyl.fight.database.rank.PlayerRank;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class EnvironmentCommand extends CFCommand {
    public EnvironmentCommand(@Nonnull String name) {
        super(name, PlayerRank.ADMIN);

        setAliases("env");
    }

    @Override
    protected void execute(@Nonnull Player player, @Nonnull ArgumentList args, @Nonnull PlayerRank rank) {
        // environment (environment) [value]
        final Environment environment = CF.environment();

        if (args.length == 0) {
            Message.success(player, "Current environment values:");

            for (String string : environment.names()) {
                final EnvironmentProperty<?> environmentProperty = environment.byName(string);

                if (environmentProperty == null) {
                    throw new NullPointerException("The environment property must not be null!");
                }

                Message.info(
                        player,
                        "&a'%s': &e%s".formatted(environmentProperty.name(), environmentProperty.value())
                );
            }
        }
        else {
            final String name = args.get(0).toString();
            final EnvironmentProperty<?> property = environment.byName(name.toLowerCase());

            if (property == null) {
                Message.error(player, "Invalid property: {%s}!".formatted(name));
                return;
            }

            final String propertyName = property.name();
            final Object propertyValue = property.value();

            if (args.length == 1) {
                Message.success(player, "Environment value of {%s} is {%s}.".formatted(propertyName, propertyValue));
            }
            else {
                final String valueToSet = args.get(1).toString();
                final boolean success = property.value(valueToSet);

                if (success) {
                    Message.broadcastStaff("{%s} set the environment value of {%s} to {%s}!".formatted(
                            player.getName(),
                            propertyName,
                            property.value()
                    ));
                }
                else {
                    Message.error(
                            player,
                            "The environment value of {%s} is already set to {%s}!".formatted(
                                    propertyName,
                                    property.value()
                            )
                    );
                }
            }
        }
    }

    @Nullable
    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return completerSort(CF.environment().names(), args);
        }

        return null;
    }
}
