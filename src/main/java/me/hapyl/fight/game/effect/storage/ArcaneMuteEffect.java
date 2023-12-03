package me.hapyl.fight.game.effect.storage;

import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.entity.Player;

public class ArcaneMuteEffect extends GameEffect {
    public ArcaneMuteEffect() {
        super("Arcane Mute");
        this.setDescription(Talents.ARCANE_MUTE.getTalent().getDescription());
        this.setPositive(false);
    }

    @Override
    public void onStart(Player player) {
        Chat.sendTitle(player, "&e&l☠", "&7Shhhhh...", 10, 20, 10);
    }

    @Override
    public void onStop(Player player) {
        Chat.sendMessage(player, "&e&l☠ &aArcane Mute is gone.");
    }

    @Override
    public void onTick(Player player, int tick) {
    }
}
