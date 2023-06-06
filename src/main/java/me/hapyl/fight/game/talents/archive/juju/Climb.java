package me.hapyl.fight.game.talents.archive.juju;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Supplier;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class Climb extends Talent {

    @DisplayField(suffix = "Maximum Bounce Time") private final int cdDeadLine = 80;
    @DisplayField private final int cooldown = 160;
    @DisplayField private final double magnitude = 0.6d;

    private final Map<Player, GameTask> tasks = new HashMap<>();

    public Climb() {
        super("Climb");
        setItem(Material.LEATHER_BOOTS);

        addDescription(
                "Use the wall you're hugging to climb it and perform back-flip, gaining speed boost. Cooldown of this ability stars upon landing or after &b{cdDeadline}&7."
        );
    }

    @Override
    public void onStop() {
        tasks.clear();
    }

    public void cancelTask(Player player) {
        final GameTask task = tasks.get(player);

        if (task != null) {
            task.cancel();
            tasks.remove(player);
        }
    }

    @Override
    public Response execute(Player player) {
        final Location playerLocation = player.getLocation();
        final Location location = playerLocation.add(playerLocation.getDirection().multiply(1).setY(0.0d));

        if (location.getBlock().getType().isAir()) {
            return Response.error("Not hugging wall.");
        }

        // Flip
        player.teleport(new Supplier<>(player.getLocation()).supply(loc -> loc.setYaw(loc.getYaw() + 180)));

        GameTask.runLater(() -> { // had to introduce delay because it broke for no reason
            player.setVelocity(player.getLocation().getDirection().normalize().multiply(magnitude).setY(magnitude));
        }, 1);

        PlayerLib.addEffect(player, PotionEffectType.SPEED, 60, 1);
        PlayerLib.playSound(playerLocation, Sound.BLOCK_SLIME_BLOCK_BREAK, 0.75f);

        if (!Heroes.JUJU.getHero().isUsingUltimate(player)) {
            taskController(player);
        }

        return Response.OK;
    }

    private void taskController(Player player) {
        final GameTask oldTask = tasks.get(player);
        if (oldTask != null) {
            return;
        }

        tasks.put(player, GameTask.runTaskTimerTimes((self, tick) -> {
            if (tick == 0 || player.isOnGround()) {
                startCd(player, cooldown);
                self.cancel();
                tasks.remove(player);
            }
        }, 1, cdDeadLine));
    }

}
