package me.hapyl.fight.game.talents.storage.tamer;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.heroes.HeroHandle;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Nulls;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import java.util.HashMap;
import java.util.Map;

public class MineOBall extends Talent implements Listener {

    private final Map<Player, TamerPack> tamerPackMap = new HashMap<>();

    public MineOBall() {
        super("Mine 'o Ball", "Summon a pack of beasts that will attack nearby opponents.");

        setCdSec(10);
        setTexture("5fe47640843744cd5796979d1196fb938317ec42b09fccb2c545ee4c925ac2bd");
    }

    // Don't allow to target owner. (Happens on spawn since we're the closest.)
    @EventHandler()
    public void handleEntityTargetLivingEntityEvent(EntityTargetLivingEntityEvent ev) {
        final Entity entity = ev.getEntity();
        final LivingEntity target = ev.getTarget();

        if (entity instanceof LivingEntity living
                && target instanceof Player player
                && HeroHandle.TAMER.validatePlayer(player, Heroes.TAMER)) {

            final TamerPack pack = getPack(player);
            if (pack != null && pack.isInPack(living)) {
                ev.setTarget(null);
                ev.setCancelled(true);
            }
        }
    }

    @Override
    public void onDeath(Player player) {
        Nulls.runIfNotNull(tamerPackMap.get(player), TamerPack::recall);
    }

    @Override
    public void onStop() {
        tamerPackMap.values().forEach(TamerPack::remove);
        tamerPackMap.clear();
    }

    public boolean isPackEntity(Player player, LivingEntity entity) {
        final TamerPack pack = getPack(player);
        return entity != null && pack != null && pack.isInPack(entity);
    }

    public TamerPack getPack(Player player) {
        return tamerPackMap.get(player);
    }

    @Override
    public void onStart() {
        // This controls 'AI' of the packs.
        new GameTask() {
            @Override
            public void run() {
                Manager.current().getCurrentGame().getAlivePlayers().forEach(gp -> {
                    final Player player = gp.getPlayer();
                    final TamerPack pack = getPack(player);

                    if (pack == null) {
                        return;
                    }

                    pack.updateEntitiesNames(player);
                    pack.getEntities().forEach(entity -> {
                        final Location location = entity.getLocation();

                        // Teleport to owner if too far away
                        if (location.distance(player.getLocation()) >= 50.0d) {
                            entity.teleport(player);
                        }

                        if (!(entity instanceof Creature creature)) {
                            return;
                        }

                        final LivingEntity target = creature.getTarget();

                        // if target is null or has died then change it
                        if (target == null || (target instanceof Player playerTarget && !GamePlayer.getPlayer(
                                playerTarget).isAlive())) {

                            final Player nearest = Utils.getNearestPlayer(location, 30.0d, player);
                            if (nearest == null) {
                                return;
                            }

                            creature.setTarget(nearest);
                            Bukkit.getPluginManager()
                                    .callEvent(new EntityTargetLivingEntityEvent(
                                            entity,
                                            nearest,
                                            EntityTargetEvent.TargetReason.CLOSEST_PLAYER
                                    ));

                            // Fx
                            PlayerLib.spawnParticle(location, Particle.LAVA, 5, 0.2d, 0.8d, 0.2d, 0.0f);
                            PlayerLib.playSound(location, Sound.ENTITY_ZOMBIFIED_PIGLIN_ANGRY, 2.0f);
                        }
                    });

                });
            }
        }.runTaskTimer(0, 20);
    }

    @Override
    public Response execute(Player player) {
        final TamerPack oldPack = getPack(player);
        if (oldPack != null) {
            oldPack.recall();
        }

        final TamerPack pack = TamerPacks.newRandom();
        pack.spawn(player);
        tamerPackMap.put(player, pack);

        // Fx
        Chat.sendMessage(player, "&a☀ You just summoned &e%s&a!", pack.getName());

        return Response.OK;
    }
}
