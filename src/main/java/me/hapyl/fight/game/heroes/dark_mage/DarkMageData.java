package me.hapyl.fight.game.heroes.dark_mage;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.heroes.witcher.WitherData;
import org.bukkit.Particle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class DarkMageData extends PlayerData implements Ticking {

    private final DarkMageSpell darkMageSpell;
    private final Set<LivingGameEntity> witheredEntities;

    private WitherData witherData;

    public DarkMageData(GamePlayer player) {
        super(player);

        this.darkMageSpell = new DarkMageSpell(player);
        this.witheredEntities = Sets.newHashSet();
    }

    public void addWithered(@Nonnull LivingGameEntity entity) {
        witheredEntities.add(entity);
    }

    @Nullable
    public WitherData getWitherData() {
        return witherData;
    }

    @Nonnull
    public DarkMageSpell getDarkMageSpell() {
        return darkMageSpell;
    }

    @Override
    public void remove() {
        removeWither();
        darkMageSpell.remove();
    }

    @Override
    public void remove(@Nonnull LivingGameEntity entity) {
        witheredEntities.remove(entity);
    }

    public void newWither(int duration) {
        removeWither();
        witherData = new WitherData(player, duration);
    }

    public void removeWither() {
        if (witherData != null) {
            witherData.remove();
        }

        witherData = null;
    }

    public void cast() {
        darkMageSpell.cast(this);
    }

    public int getWitheredCount() {
        return witheredEntities.size();
    }

    public void resetWitheredCountWithFx() {
        witheredEntities.clear();

        // Fx
    }
    
    @Override
    public void tick() {
        witheredEntities.forEach(entity -> {
            player.spawnParticle(
                    entity.getLocation().add(0, 2.5, 0),
                    Particle.SMOKE,
                    5,
                    0.1,
                    0.1,
                    0.1,
                    0.01f
            );
        });
    }
}
