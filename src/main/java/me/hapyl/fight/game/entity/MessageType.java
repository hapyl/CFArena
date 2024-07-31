package me.hapyl.fight.game.entity;

import me.hapyl.fight.ux.Notifier;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;

public enum MessageType {

    INFO(Notifier::info),
    SUCCESS(Notifier::success),
    WARNING(Notifier::warning),
    ERROR(Notifier::error);

    private final BiConsumer<CommandSender, String> consumer;

    MessageType(BiConsumer<CommandSender, String> consumer) {
        this.consumer = consumer;
    }

    public void send(@Nonnull Player player, @Nonnull String message) {
        consumer.accept(player, message);
    }

}
