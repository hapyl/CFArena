package me.hapyl.fight.game.talents.archive.bloodfiend;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.TalentReference;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.bloodfield.Bloodfiend;
import me.hapyl.fight.game.heroes.archive.bloodfield.BloodfiendData;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Utils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Set;

public abstract class Taunt extends GameTask {

    protected final Player player;
    protected final Location initialLocation;
    private int tick;

    public Taunt(Player player, Location location) {
        this.player = player;
        this.initialLocation = Utils.anchorLocation(location);
        this.initialLocation.subtract(0.0d, 1.35d, 0.0d);
    }

    public void start(int duration) {
        tick = duration;
        runTaskTimer(0, 1);
    }

    public void remove() {
        cancel();
    }

    public int getTimeLeft() {
        return tick;
    }

    public abstract void run(int tick);

    @Override
    public final void run() {
        run(tick--);

        // Fx
        if (tick % 10 == 0) {
            getSucculencePlayers().forEach(target -> {
                target.playSound(initialLocation, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 0.0f);
            });
        }

        // Warn about taunt!
        if (tick <= 100 && (tick % 2 == 0)) {
            getSucculencePlayers().forEach(target -> {
                target.sendWarning("&c%s is about to explode!".formatted(getName()), 20);
                target.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 2f - (1.5f / 100 * tick));
            });
        }

        if (tick <= 0) {
            explode();
        }
    }

    @Nonnull
    public String getName() {
        if (this instanceof TalentReference<?> talentReference) {
            return talentReference.getTalent().getName();
        }

        return "Unnamed";
    }

    @Nonnull
    public String getCharacter() {
        return "";
    }

    public void explode() {
        final Bloodfiend bloodfiend = getBloodfiend();
        final BloodfiendData data = bloodfiend.getData(player);

        data.getSucculencePlayers().forEach(target -> {
            target.setLastDamager(CF.getOrCreatePlayer(player));
            target.setLastDamageCause(EnumDamageCause.CHALICE);
            target.die(true);

            // Fx
            target.sendMessage("%s &c%s's %s took your life!", getCharacter(), player.getName(), getName());
            target.playSound(Sound.ENTITY_PLAYER_DEATH, 1.0f);
            target.playSound(Sound.ENTITY_HUSK_DEATH, 0.0f);
        });

        remove();
    }

    @Nonnull
    public final World getWorld() {
        final World world = initialLocation.getWorld();

        if (world == null) {
            throw new IllegalArgumentException("unloaded world");
        }

        return world;
    }

    @Nonnull
    public final Bloodfiend getBloodfiend() {
        return Heroes.BLOODFIEND.getHero(Bloodfiend.class);
    }

    @Nonnull
    public final Set<GamePlayer> getSucculencePlayers() {
        return getBloodfiend().getData(player).getSucculencePlayers();
    }
}
