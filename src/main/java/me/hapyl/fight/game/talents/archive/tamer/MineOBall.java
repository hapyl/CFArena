package me.hapyl.fight.game.talents.archive.tamer;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.Response;
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
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import javax.annotation.Nullable;
import java.util.Map;

public class MineOBall extends Talent implements Listener {

    private final Map<Player, TamerPack> tamerPackMap = Maps.newConcurrentMap();

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

            final TamerPack pack = getPack(player);
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
    public void onDeath(Player player) {
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

    public boolean isPackEntity(Player player, LivingEntity entity) {
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

                        // Teleport to the owner if too far away
                        if (location.distance(player.getLocation()) >= 50.0d) {
                            entity.teleport(player);
                        }

                        if (!(entity instanceof Creature creature) || !entity.hasAI()) {
                            return;
                        }

                        final LivingEntity target = creature.getTarget();

                        // if the target is null or invalid, then change it
                        if (target == null || (target instanceof Player playerTarget && !Utils.isEntityValid(playerTarget))) {
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
    public Response execute(Player player) {
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
        Chat.sendMessage(player, "&a☀ You just summoned &e%s&a!", pack.getName());

        return Response.OK;
    }

    public boolean isInSamePackOrOwner(Entity entity, Entity other) {
        if (!(entity instanceof LivingEntity livingEntity) || !(other instanceof LivingEntity livingEntityOther)) {
            return false;
        }

        if (livingEntity instanceof Player player) {
            final TamerPack pack = getPack(player);
            if (pack == null) {
                return false;
            }

            return pack.isInPack(livingEntityOther);
        }

        return isInSamePack(livingEntity, livingEntityOther);
    }

    @Nullable
    public Player getOwner(LivingEntity entity) {
        for (Map.Entry<Player, TamerPack> entry : tamerPackMap.entrySet()) {
            if (entry.getValue().isInPack(entity)) {
                return entry.getKey();
            }
        }

        return null;
    }
}
