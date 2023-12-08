package me.hapyl.fight.game.talents.archive.tamer;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Nulls;
import me.hapyl.fight.util.collection.player.PlayerMap;
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
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class MineOBall extends Talent implements Listener, TamerTalent {

    private final PlayerMap<TamerPack> tamerPackMap = PlayerMap.newConcurrentMap();

    public MineOBall() {
        super("Mine 'o Ball", """
                Summon a pack of beasts that will attack nearby opponents.
                                
                &b&lBeasts:
                """);

        for (TamerPacks value : TamerPacks.values()) {
            addDescription("&7- &f{}\n", value.getPack().getName());
        }

        setCooldownSec(10);
        setTexture("5fe47640843744cd5796979d1196fb938317ec42b09fccb2c545ee4c925ac2bd");
    }

    // Don't allow targeting an owner. (Happens on spawn since we're the closest.)
    @EventHandler()
    public void handleEntityTargetLivingEntityEvent(EntityTargetLivingEntityEvent ev) {
        final Entity entity = ev.getEntity();
        final LivingEntity target = ev.getTarget();

        if (entity instanceof LivingEntity living
                && target instanceof Player player
                && Heroes.TAMER.getHero().validatePlayer(player)) {

            final GamePlayer gamePlayer = CF.getPlayer(player);

            if (gamePlayer == null) {
                return;
            }

            final TamerPack pack = getPack(gamePlayer);
            if (pack != null && pack.isInPack(living)) {
                ev.setTarget(null);
                ev.setCancelled(true);
            }
        }
    }

    @EventHandler()
    public void handlePackEntityDeath(EntityDeathEvent ev) {
        final LivingEntity entity = ev.getEntity();

        for (TamerPack value : tamerPackMap.values()) {
            if (value.isInPack(entity)) {
                value.remove(entity);
                break;
            }
        }
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        Nulls.runIfNotNull(tamerPackMap.get(player), TamerPack::recall);
    }

    @Override
    public void onStop() {
        tamerPackMap.values().forEach(TamerPack::removeAll);
        tamerPackMap.clear();
    }

    public boolean isInSamePack(LivingEntity living, LivingEntity entity) {
        for (TamerPack value : tamerPackMap.values()) {
            if (value.isInPack(living) && value.isInPack(entity)) {
                return true;
            }
        }

        return false;
    }

    public boolean isPackEntity(GamePlayer player, LivingEntity entity) {
        final TamerPack pack = getPack(player);
        return entity != null && pack != null && pack.isInPack(entity);
    }

    public boolean isPackEntity(LivingEntity entity) {
        for (TamerPack pack : tamerPackMap.values()) {
            if (pack.isInPack(entity)) {
                return true;
            }
        }

        return false;
    }

    @Nullable
    public TamerPack getPack(GamePlayer player) {
        return player == null ? null : tamerPackMap.get(player);
    }

    @Override
    public void onStart() {
        // This controls 'AI' of the packs.
        new GameTask() {
            @Override
            public void run() {
                CF.getAlivePlayers().forEach(player -> {
                    final TamerPack pack = getPack(player);

                    if (pack == null) {
                        return;
                    }

                    pack.updateEntitiesNames(player);
                    pack.getEntities().forEach(entity -> {
                        final Location location = entity.getLocation();

                        // Teleport to the owner if too far away
                        if (location.distance(player.getLocation()) >= 50.0d) {
                            entity.teleport(player.getLocation());
                        }

                        if (!(entity instanceof Creature creature) || !entity.hasAI()) {
                            return;
                        }

                        final LivingEntity target = creature.getTarget();

                        // if the target is null or invalid, then change it
                        if (target == null || (target instanceof Player playerTarget && !CFUtils.isEntityValid(playerTarget))) {
                            final LivingEntity newTarget = pack.findNearestTarget();

                            if (newTarget == null) {
                                return; // don't care
                            }

                            creature.setTarget(newTarget);
                            creature.setAware(true);

                            Bukkit.getPluginManager()
                                    .callEvent(new EntityTargetLivingEntityEvent(
                                            entity,
                                            newTarget,
                                            EntityTargetEvent.TargetReason.CUSTOM
                                    ));

                            // Fx
                            PlayerLib.spawnParticle(location, Particle.LAVA, 5, 0.2d, 0.8d, 0.2d, 0.0f);
                            PlayerLib.playSound(location, Sound.ENTITY_ZOMBIFIED_PIGLIN_ANGRY, 2.0f);
                        }
                    });

                });
            }
        }.runTaskTimer(0, 5);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final TamerPack oldPack = getPack(player);
        if (Heroes.TAMER.getHero().isUsingUltimate(player)) {
            return Response.error("Can't summon during Ultimate");
        }

        if (oldPack != null) {
            oldPack.recall();
        }

        final TamerPack pack = TamerPacks.newRandom(player);
        pack.spawn();

        tamerPackMap.put(player, pack);

        // Fx
        player.sendMessage("&a☀ You just summoned &e%s&a!", pack.getName());

        return Response.OK;
    }

    public boolean isInSamePackOrOwner(Entity entity, Entity other) {
        if (!(entity instanceof LivingEntity livingEntity) || !(other instanceof LivingEntity livingEntityOther)) {
            return false;
        }

        if (livingEntity instanceof Player player) {
            final TamerPack pack = getPack(CF.getPlayer(player));
            if (pack == null) {
                return false;
            }

            return pack.isInPack(livingEntityOther);
        }

        return isInSamePack(livingEntity, livingEntityOther);
    }

    @Nullable
    public GamePlayer getOwner(LivingEntity entity) {
        for (Map.Entry<GamePlayer, TamerPack> entry : tamerPackMap.entrySet()) {
            if (entry.getValue().isInPack(entity)) {
                return entry.getKey();
            }
        }

        return null;
    }
}
