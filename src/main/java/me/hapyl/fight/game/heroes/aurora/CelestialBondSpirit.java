package me.hapyl.fight.game.heroes.aurora;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.aurora.EtherealArrow;
import org.bukkit.Location;

public class CelestialBondSpirit extends EtherealSpirit {
    CelestialBondSpirit(GamePlayer aurora, LivingGameEntity entity) {
        super(aurora, entity);

        final EtherealArrow talent = TalentRegistry.ETHEREAL_ARROW;
        final Location location = entity.getLocation();

        duration = talent.buffDuration;

        for (int i = 0; i < talent.maxStacks; i++) {
            orbiting.add(spawnArrow(location));
        }
    }

    @Override
    protected void tickDown() {
        // Don't tick down
    }
}
