package me.hapyl.fight.game.heroes.nyx;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Removable;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.ComparableTo;
import me.hapyl.fight.util.ItemStackRandomizedData;
import me.hapyl.fight.util.Located;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class ChaosDroplet extends TickingGameTask implements Removable, Located, ComparableTo<Item> {

    private final GamePlayer player;
    private final Item item;
    public ChaosDroplet(GamePlayer player, Location location) {
        this.player = player;
        this.item = location.getWorld().dropItem(
                location,
                item(),
                self -> {
                    self.setUnlimitedLifetime(true);
                    self.setCanMobPickup(false);
                    self.setCanPlayerPickup(true);
                }
        );

        this.item.setVelocity(new Vector(0.0d, 0.25d, 0.0d));

        runTaskTimerAsync(0, 2);
    }

    @Nonnull
    public GamePlayer getPlayer() {
        return player;
    }

    @Nonnull
    @Override
    public Location getLocation() {
        return item.getLocation();
    }

    @Override
    public void remove() {
        this.item.remove();
        cancel();
    }

    @Override
    public int compareTo(@Nonnull Item o) {
        return ComparableTo.comparingObjects(item, o);
    }

    @Override
    public void run(int tick) {
        CF.getAlivePlayers().forEach(player -> {
            final Location location = getLocation().add(0.0d, 0.25d, 0.0d);

            // Show different particles to teammates/enemies
            if (this.player.isSelfOrTeammate(player)) {
                player.spawnWorldParticle(location, Particle.WITCH, 5, 0.25f, 0.25f, 0.25, 0.05f);
            }
            else {
                player.spawnWorldParticle(location, Particle.ASH, 5, 0.25f, 0.25f, 0.25f, 0.025f);
            }
        });
    }

    public static ItemStack item() {
        return ItemStackRandomizedData.of("ed5d46bafb21727276d202ccd130f598a6956c79a4cf07a143f74c97b1be918c");
    }
}
