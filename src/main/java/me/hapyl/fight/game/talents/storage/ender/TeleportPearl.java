package me.hapyl.fight.game.talents.storage.ender;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.HashSet;
import java.util.Set;

public class TeleportPearl extends Talent implements Listener {

    private final Set<EnderPearl> enderPearls = new HashSet<>();
    @DisplayField(suffix = "&f❤") private final double teleportationHealing = 3.0d;

    public TeleportPearl() {
        super("Rideable Pearl");

        setDescription(
                "Throw an ender pearl and mount to ride it all the way! &e&lSNEAK &7to throw normally.____&7Heals &c%s ❤ &7upon teleport.",
                teleportationHealing
        );

        setCd(160);
        setItem(Material.ENDER_PEARL);
    }

    @Override
    public void onStop() {
        enderPearls.clear();
    }

    @Override
    public Response execute(Player player) {
        final EnderPearl pearl = player.launchProjectile(EnderPearl.class);

        enderPearls.add(pearl);
        pearl.setShooter(player);

        if (!player.isSneaking()) {
            PlayerLib.playSound(player, Sound.ENTITY_HORSE_SADDLE, 1.5f);
            pearl.addPassenger(player);
        }

        return Response.OK;
    }

    @EventHandler()
    public void handleProjectileHitEvent(ProjectileHitEvent ev) {
        if (ev.getEntity() instanceof EnderPearl pearl && enderPearls.contains(pearl)) {
            if (pearl.getShooter() instanceof Player player) {
                GamePlayer.getPlayer(player).heal(teleportationHealing);
                PlayerLib.spawnParticle(player.getEyeLocation().add(0.0d, 0.5d, 0.0d), Particle.HEART, 1, 0, 0, 0, 0);
            }
            enderPearls.remove(pearl);
        }
    }
}
