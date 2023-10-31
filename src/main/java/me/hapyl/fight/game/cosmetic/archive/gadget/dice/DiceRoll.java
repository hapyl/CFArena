package me.hapyl.fight.game.cosmetic.archive.gadget.dice;

import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.ThreadRandom;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class DiceRoll extends TickingGameTask {

    private final int maxTick = 20;

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

        this.stand = Entities.ARMOR_STAND.spawn(location, self -> {
            self.setSilent(true);
            self.setGravity(false);
            self.setInvulnerable(true);
            self.setInvisible(true);
            self.setHelmet(dice.getRandomSide().getItem());

            CFUtils.lockArmorStand(self);
        });

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

        // Math is too hard, using what works
        if (tick <= (maxTick / 2)) {
            location.add(0.0d, 0.065d, 0.0d);
        }
        else {
            location.subtract(0.0d, 0.175d, 0.0d);
        }

        stand.teleport(location);

        // Fx
        if (tick % 3 == 0) {
            stand.setHeadPose(new EulerAngle(
                    ThreadRandom.nextDouble(90),
                    ThreadRandom.nextDouble(90),
                    ThreadRandom.nextDouble(90)
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

        if (number < 6) {
            PlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 0.4f * number);
        }
        else {
            PlayerLib.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.75f);
            PlayerLib.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0f);
            PlayerLib.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.25f);
        }

        GameTask.runLater(() -> {
            stand.remove();
            PlayerLib.spawnParticle(location.add(0.0d, 1.0d, 0.0d), Particle.EXPLOSION_NORMAL, 5, 0.1d, 0.1d, 0.1d, 0.05f);
        }, 20);
    }
}
