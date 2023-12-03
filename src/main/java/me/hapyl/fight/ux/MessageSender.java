package me.hapyl.fight.ux;

import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface MessageSender {

    void send(@Nonnull CommandSender sender, @Nullable Object... format);

}