package me.hapyl.fight.game.entity.named.boss;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.entity.EntityType;
import me.hapyl.fight.game.entity.named.NamedEntityType;
import me.hapyl.fight.game.entity.named.NamedGameEntity;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public class LeechBoss extends NamedEntityType {

    public LeechBoss(@Nonnull Key key) {
        super(key, "Leech");

        setType(EntityType.BOSS);
    }

    @Nonnull
    @Override
    public NamedGameEntity<?> create(@Nonnull Location location) {
        throw new NotImplementedException();
    }
}
