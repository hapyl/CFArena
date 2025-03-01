package me.hapyl.fight.game.cosmetic.gadget.dice;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.util.ThreadRandom;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class DiceRoll extends TickingGameTask {

    private static final double maxTick = 20.0d;

    private final Player player;
    private final Dice dice;
    private final Location location;
    private final Vector vector;

    private final ArmorStand stand;
    private final DiceSide rolled;

    public DiceRoll(Player player, Dice dice) {
        this.player = player;
        this.dice = dice;
        this.location = player.getLocation().subtract(0.0d, 0.5d, 0.0d);
        this.vector = location.getDirection().normalize().setY(0.0d).multiply(0.175d);

        this.stand = Entities.ARMOR_STAND.spawn(
                location, self -> {
                    self.setSilent(true);
                    self.setGravity(false);
                    self.setInvulnerable(true);
                    self.setInvisible(true);
                    self.setHelmet(dice.getRandomSide().getItem());

                    CFUtils.lockArmorStand(self);
                }
        );

        this.rolled = dice.getRandomSide();
        runTaskTimer(0, 1);
    }

    @Override
    public void run(int tick) {
        if (tick >= maxTick) {
            reveal();
            cancel();
            return;
        }

        location.add(vector);

        final double y = Math.sin((Math.PI * 1.3d) * (tick / maxTick)) * 1.3d;
        LocationHelper.offset(location, 0, y, 0, () -> stand.teleport(location));

        // Fx
        if (modulo(2)) {
            stand.setHeadPose(new EulerAngle(
                    ThreadRandom.nextDouble(50),
                    ThreadRandom.nextDouble(50),
                    ThreadRandom.nextDouble(50)
            ));

            PlayerLib.playSound(location, Sound.BLOCK_WOOD_STEP, 0.0f);
        }
    }

    private void reveal() {
        final int number = rolled.getSide();
        final Location location = stand.getLocation();

        stand.setHelmet(rolled.getItem());
        stand.setHeadPose(EulerAngle.ZERO);
        stand.setCustomName(rolled.toString());
        stand.setCustomNameVisible(true);

        dice.onRoll(player, rolled);

        if (number == 6) {
            PlayerLib.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.75f);
            PlayerLib.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0f);
            PlayerLib.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.25f);
        }
        else {
            PlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 0.4f * number);
        }

        GameTask.runLater(
                () -> {
                    stand.remove();
                    PlayerLib.spawnParticle(location.add(0.0d, 1.75d, 0.0d), Particle.LARGE_SMOKE, 10, 0.1d, 0.1d, 0.1d, 0.05f);
                }, 20
        );
    }
}
