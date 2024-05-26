package me.hapyl.fight.game.talents.ender;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class TeleportPearl extends Talent implements Listener {

    private final Set<EnderPearl> enderPearls = new HashSet<>();

    public TeleportPearl() {
        super(
                "Rideable Pearl",
                """
                        Throw an ender pearl and mount to ride it all the way!
                        &6&lSNEAK &7to throw normally.
                        """
        );

        setType(TalentType.MOVEMENT);
        setItem(Material.ENDER_PEARL);
        setCooldown(160);
    }

    @Override
    public void onStop() {
        enderPearls.clear();
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final EnderPearl pearl = player.launchProjectile(EnderPearl.class);

        enderPearls.add(pearl);
        pearl.setShooter(player.getPlayer());

        if (!player.isSneaking()) {
            player.playSound(Sound.ENTITY_HORSE_SADDLE, 1.5f);
            pearl.addPassenger(player.getPlayer());
        }

        return Response.OK;
    }

    @EventHandler()
    public void handleProjectileHitEvent(ProjectileHitEvent ev) {
        final Projectile projectile = ev.getEntity();
        if (!(projectile instanceof EnderPearl pearl) || !(projectile.getShooter() instanceof Player player)) {
            return;
        }

        enderPearls.remove(pearl);
    }

    @EventHandler()
    public void handleDismountEvent(EntityDismountEvent ev) {
        final Entity entity = ev.getEntity();
        final Entity dismounted = ev.getDismounted();

        if (!(entity instanceof Player player) || !(dismounted instanceof EnderPearl enderPearl)) {
            return;
        }

        if (!enderPearls.contains(enderPearl) || enderPearl.getShooter() != player) {
            return;
        }

        enderPearls.remove(enderPearl);
        enderPearl.remove();

        Chat.sendMessage(player, "&aYour ender pearl has disappeared.");
    }

}
