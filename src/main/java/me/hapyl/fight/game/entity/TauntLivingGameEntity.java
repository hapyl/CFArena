package me.hapyl.fight.game.entity;

import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.module.entity.Entities;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Taunt entities, well, taunt enemies.
 */
public class TauntLivingGameEntity extends LivingGameEntity {

    protected final GameTeam tauntTeam;

    protected double tauntRadius;

    public TauntLivingGameEntity(@Nonnull Location location, @Nullable GameTeam tauntTeam) {
        super(makeTauntEntity(location, tauntTeam));

        this.tauntTeam = tauntTeam;
        this.tauntRadius = 5.0d;
        this.setValidState(false);
    }

    @Override
    @SuppressWarnings("all")
    public void tick() {
        // *=* Don't call super! *=* //

        // Taunt entities
    }

    private static LivingEntity makeTauntEntity(Location location, GameTeam team) {
        final Villager entity = Entities.VILLAGER.spawn(location, self -> {
            self.setBaby();
            self.setInvulnerable(true);
            self.setInvisible(true);
            self.setSilent(true);
        });

        return entity;
    }
}
