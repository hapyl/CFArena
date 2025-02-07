package me.hapyl.fight.game.talents.knight;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Removable;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.task.TimedGameTask;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import javax.annotation.Nonnull;
import java.util.Set;

public class Castle extends TimedGameTask implements Removable {

    private final StoneCastle talent;
    private final GamePlayer player;
    private final Location location;
    private final Set<ArmorStand> armorStands;

    public Castle(StoneCastle talent, GamePlayer player) {
        super(talent);

        this.talent = talent;
        this.player = player;
        this.location = player.getLocation();
        this.armorStands = Sets.newHashSet();

        createStands();

        runTaskTimer(0, 1);
    }

    public GamePlayer getPlayer() {
        return player;
    }

    @Nonnull
    public Location getLocation() {
        return location;
    }

    @Override
    public void run(int tick) {
    }

    @Override
    public void remove() {
        cancel0();

        player.getAttributes().resetTemper(Temper.STONE_CASTLE);

        armorStands.forEach(ArmorStand::remove);
        armorStands.clear();
    }

    public boolean isEntityWithin(@Nonnull GameEntity player) {
        return location.distance(player.getLocation()) <= talent.distance;
    }

    private void createStands() {
        new TickingGameTask() {
            private double d = 0.0d;

            @Override
            public void run(int tick) {
                for (int i = 5; i > 0; i--) {
                    next();
                }
            }

            private void next() {
                if (d > Math.PI * 2) {
                    cancel();
                    return;
                }

                final double x = Math.sin(d) * talent.distance;
                final double z = Math.cos(d) * talent.distance;

                final Location location = Castle.this.location.clone();

                BukkitUtils.anchorLocation(location.add(x, 0.0d, z));
                location.subtract(0.0d, 1.5d, 0.0d);

                final ArmorStand stand = Entities.ARMOR_STAND_MARKER.spawn(location, self -> {
                    self.setInvisible(true);
                    self.setSilent(true);
                    self.setHelmet(new ItemStack(talent.getMaterial()));
                    self.setHeadPose(new EulerAngle(Math.toRadians(-45), 0, 0));
                });

                CFUtils.lookAt(stand, Castle.this.location);
                armorStands.add(stand);

                // Fx
                player.playWorldSound(location, Sound.BLOCK_STONE_PLACE, (float) (0.5f + (1.5f / (Math.PI * 2) * d)));

                d += Math.PI / 42;
            }
        }.runTaskTimer(0, 1);
    }
}
