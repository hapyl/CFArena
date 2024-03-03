package me.hapyl.fight.globalconfig;

import me.hapyl.fight.Main;
import me.hapyl.fight.ux.Notifier;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;

public interface Configurable {

    @Nonnull
    Configuration getConfiguration();

    default boolean checkDisabledAndSendError(@Nonnull CommandSender sender) {
        final boolean disabled = isDisabled();

        if (disabled) {
            Notifier.error(sender, "This feature is currently disabled!");
        }

        return disabled;
    }

    default boolean isEnabled() {
        return Main.getPlugin().getDatabase().getGlobalConfig().isEnabled(getConfiguration());
    }

    default boolean isDisabled() {
        return !isEnabled();
    }

}
