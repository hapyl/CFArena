package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.event.custom.PlayerPreconditionEvent;
import me.hapyl.fight.game.effect.Effect;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public class ArcaneMuteEffect extends Effect implements Listener {
    public ArcaneMuteEffect() {
        super("Arcane Mute", EffectType.NEGATIVE);

        setDescription("""
                Prevents players from using their talents and deafens them.
                """);
    }

    @EventHandler()
    public void handlePrecondition(PlayerPreconditionEvent ev) {
        final GamePlayer player = ev.getPlayer();

        if (player.hasEffect(this)) {
            ev.setCancelled(true, "Arcane Mute!");
        }
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity, int amplifier, int duration) {
        entity.asPlayer(player -> {
            Chat.sendTitle(player, "&e&l☠", "&7Shhhhh...", 10, 20, 10);
        });
    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity, int amplifier) {
        entity.sendMessage("&e&l☠ &aArcane Mute is gone.");
    }

}
