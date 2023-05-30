package me.hapyl.fight.game.talents.storage.nightmare;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

public class Paranoia extends Talent {

    public Paranoia() {
        super(
                "Paranoia",
                "Launch a cloud of darkness in front of you that travels forward, applying &e&lParanoia &7effect to whoever it touches for {duration}&7."
        );

        setDuration(100);
        setItem(Material.CHARCOAL);
        setCooldown(360);
    }

    @Override
    public Response execute(Player player) {
        final Location location = player.getLocation();
        final ArmorStand stand = Entities.ARMOR_STAND.spawn(location.clone().add(0.0d, 1.0d, 0.0d), me -> {
            me.setInvulnerable(true);
            me.setVisible(false);
            me.setSmall(true);
            me.setMarker(true);
            me.getLocation().setYaw(location.getYaw());
            me.getLocation().setPitch(location.getPitch());
        });

        PlayerLib.playSound(location, Sound.AMBIENT_CAVE, 1.0f);

        new GameTask() {
            private double currentIteration = 2.5d;

            @Override
            public void run() {
                if ((currentIteration -= 0.1d) <= 0) {
                    stand.remove();
                    this.cancel();
                    return;
                }

                // Teleport forward
                final Location standLocation = stand.getLocation();
                stand.teleport(standLocation.add(standLocation.getDirection()).multiply(1));

                // Fx
                PlayerLib.spawnParticle(standLocation, Particle.SQUID_INK, 2, 0.1d, 0.1d, 0.1d, 0.1f);
                PlayerLib.playSound(standLocation, Sound.BLOCK_ANVIL_STEP, 1.5f);

                // Apply blindness
                Utils.getPlayersInRange(standLocation, 2.0d).forEach(target -> {
                    if (player == target || !Manager.current().isPlayerInGame(target)) {
                        return;
                    }
                    GamePlayer.getPlayer(target).addEffect(GameEffectType.PARANOIA, getDuration(), true);
                });

            }
        }.runTaskTimer(0, 1);

        return Response.OK;
    }
}
