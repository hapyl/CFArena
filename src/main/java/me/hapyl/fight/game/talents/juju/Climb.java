package me.hapyl.fight.game.talents.juju;

import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Supplier;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class Climb extends Talent {

    @DisplayField(suffix = "Maximum Bounce Time") private final int cdDeadline = 80;
    @DisplayField private final int cooldown = 160;
    @DisplayField private final double magnitude = 0.6d;

    private final PlayerMap<GameTask> tasks = PlayerMap.newMap();

    public Climb() {
        super("Climb");
        setItem(Material.LEATHER_BOOTS);

        addDescription(
                "Use the wall you're hugging to climb it and perform back-flip, gaining speed boost. Cooldown of this ability stars upon landing or after &b{cdDeadline}&7."
        );
    }

    @Override
    public void onStop(@Nonnull GameInstance instance) {
        tasks.clear();
    }

    public void cancelTask(GamePlayer player) {
        final GameTask task = tasks.get(player);

        if (task != null) {
            task.cancel();
            tasks.remove(player);
        }
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
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

        player.addEffect(Effects.SPEED, 1, 60);
        player.playSound(Sound.BLOCK_SLIME_BLOCK_BREAK, 0.75f);

        if (!player.isUsingUltimate()) {
            taskController(player);
        }

        return Response.OK;
    }

    private void taskController(GamePlayer player) {
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
        }, 1, cdDeadline));
    }

}
