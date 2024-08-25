package me.hapyl.fight.game.achievement;

import me.hapyl.fight.event.custom.AttributeChangeEvent;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.registry.Key;
import me.hapyl.fight.registry.Registries;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public class DefenselessAchievement extends Achievement implements Listener {

    DefenselessAchievement(@Nonnull Key key) {
        super(key, "Defenseless", "Get zero and less defense.");
    }

    @EventHandler
    public void handleAttributeChangeEvent(AttributeChangeEvent ev) {
        final LivingGameEntity entity = ev.getEntity();
        final AttributeType type = ev.getType();

        if (!(entity instanceof GamePlayer player)) {
            return;
        }

        if (type != AttributeType.DEFENSE) {
            return;
        }

        if (ev.getNewValue() > 0.0d) {
            return;
        }

        Registries.getAchievements().DEFENSELESS.complete(player.getPlayer());
    }
}
