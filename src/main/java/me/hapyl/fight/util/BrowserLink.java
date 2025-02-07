package me.hapyl.fight.util;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.chat.LazyEvent;
import me.hapyl.eterna.module.player.PlayerLib;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public record BrowserLink(@Nonnull String name, @Nonnull String link) {

    public void openUrl(@Nonnull Player player) {
        Chat.sendMessage(player, "");
        Chat.sendClickableHoverableMessage(
                player,
                LazyEvent.openUrl(link),
                LazyEvent.showText("&e&nClick to open '%s'!".formatted(name)),
                "&8[&c&l\uD83D\uDCE2&8] &6&l&nCLICK HERE&e to open '%s' in your browser!".formatted(name)
        );
        Chat.sendMessage(player, "");

        PlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f);
        player.closeInventory();
    }

    @Override
    @Nonnull
    public String name() {
        return name;
    }

    @Override
    @Nonnull
    public String link() {
        return link;
    }
}
