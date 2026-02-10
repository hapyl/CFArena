package me.hapyl.fight.game.maps.winery;

import me.hapyl.eterna.module.util.Direction;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.maps.features.Geyser;

import javax.annotation.Nonnull;

public class Steam extends Geyser {
    public Steam(int x, int y, int z, Direction direction) {
        super(x, y, z, direction);

        setTexture("b56792965b606ba85f87a510cebe110c0599fb69815105e606e106c5919d14c2");
    }

    @Override
    public void affectEntityTick(@Nonnull LivingGameEntity entity, int tick) {
        if (tick % 10 != 0) {
            return;
        }

        entity.damage(1, DamageCause.STEAM);
    }

}
