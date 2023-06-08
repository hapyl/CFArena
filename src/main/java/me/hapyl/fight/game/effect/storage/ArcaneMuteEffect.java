package me.hapyl.fight.game.effect.storage;

import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class ArcaneMuteEffect extends GameEffect {
    public ArcaneMuteEffect() {
        super("Arcane Mute");
        setDescription(Talents.ARCANE_MUTE.getTalent().getDescription());
        setPositive(false);
        setTalentBlocking(true);
    }

    @Override
    public void onStart(LivingEntity entity) {
        if (!(entity instanceof Player player)) {
            return;
        }

        Chat.sendTitle(player, "&e&l☠", "&7Shhhhh...", 10, 20, 10);
    }

    @Override
    public void onStop(LivingEntity entity) {
        Chat.sendMessage(entity, "&e&l☠ &aArcane Mute is gone.");
    }

    @Override
    public void onTick(LivingEntity entity, int tick) {
    }
}
