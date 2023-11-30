package me.hapyl.fight.game.talents.archive.frostbite;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import javax.annotation.Nonnull;

public class IceCageTalent extends Talent implements Listener {

    private final PlayerMap<Snowball> snowballMap = PlayerMap.newMap();
    private final PlayerMap<IceCage> iceCageMap = PlayerMap.newMap();

    public IceCageTalent() {
        super("Ice Cage", """
                Launch a &bsnowball&7 in front of you.
                                     
                Upon hitting a player, immobilize and cage them in ice.
                &8;;The ice is only visible to the hit player.
                """);

        setType(Type.IMPAIR);
        setItem(Material.SNOWBALL);
        setCooldownSec(20);
    }

    @EventHandler()
    public void handleSnowballHit(ProjectileHitEvent ev) {
        if (!(ev.getEntity() instanceof Snowball snowball)) {
            return;
        }

        if (!(snowball.getShooter() instanceof Player player)) {
            return;
        }

        final GamePlayer gamePlayer = CF.getPlayer(player);
        if (gamePlayer == null || snowballMap.get(gamePlayer) != snowball) {
            return;
        }

        final Entity hitEntity = ev.getHitEntity();
        if (!(hitEntity instanceof Player hitPlayer)) {
            return;
        }

        final GamePlayer hitGamePlayer = CF.getPlayer(hitPlayer);
        if (hitGamePlayer == null) {
            return;
        }

        final IceCage oldCage = iceCageMap.remove(gamePlayer);

        if (oldCage != null) {
            oldCage.remove();
        }

        iceCageMap.put(gamePlayer, new IceCage(gamePlayer, hitGamePlayer));
    }

    @Override
    public void onStop() {
        iceCageMap.values().forEach(IceCage::remove);
        iceCageMap.clear();
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Snowball snowball = player.launchProjectile(Snowball.class);

        snowball.setShooter(player.getPlayer());
        snowballMap.put(player, snowball);

        player.playWorldSound(Sound.ENTITY_SNOWBALL_THROW, 1.0f);

        return Response.OK;
    }
}
