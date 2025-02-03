package me.hapyl.fight.game.talents.vampire;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.*;
import org.bukkit.entity.Bat;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class BatTransferTalent extends Talent {

    private final PlayerMap<BatTransfer> playerBats = PlayerMap.newMap();

    @DisplayField private final double damage = 2.0d;
    @DisplayField private final double batSpeed = 0.9;
    @DisplayField private final double hitBoxSize = 1.5d;
    @DisplayField private final double healthDecrease = 10;

    @DisplayField private final int impairDuration = Tick.fromSecond(8);

    private final TemperInstance temperInstance = Temper.SWARM.newInstance()
                                                              .decrease(AttributeType.MAX_HEALTH, healthDecrease)
                                                              .message("&4\uD83C\uDF36 Ouch! &8(&c-%s ❤&8)".formatted(healthDecrease));

    public BatTransferTalent(@Nonnull Key key) {
        super(key, "Nightfang");

        setDescription("""
                Transform into a bat and dash forward, &bbiting&7 and dealing &cdamage&7 to &cenemies&7 along the way.
                
                Transform back after {duration} or hitting an obstacle.
                &8&o;;Press jump or sneak button to manually transform.
                """);

        setType(TalentType.MOVEMENT);
        setItem(Material.BLACK_DYE);

        setCooldownSec(8.0f);
        setDurationSec(2.5f);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final BatTransfer previousTransfer = playerBats.put(player, new BatTransfer(player));

        if (previousTransfer != null) {
            previousTransfer.cancel();
        }

        return Response.OK;
    }

    @Override
    public void onStop(@Nonnull GameInstance instance) {
        playerBats.forEachAndClear(BatTransfer::cancel0);
    }

    private class BatTransfer extends TickingGameTask {
        private final GamePlayer player;
        private final Vector direction;
        private final Bat bat;

        private BatTransfer(GamePlayer player) {
            this.player = player;

            final Location location = player.getEyeLocation();
            this.direction = player.getDirection().multiply(batSpeed);

            this.bat = Entities.BAT.spawn(
                    location, self -> {
                        self.setAI(false);
                        self.setAwake(true);
                        self.setInvulnerable(true);
                        self.setSilent(true);
                    }
            );

            runTaskTimer(0, 1);
        }

        @Override
        public void onTaskStop() {
            this.bat.remove();

            this.player.setGameMode(GameMode.ADVENTURE);
            this.player.addEffect(Effects.SLOW_FALLING, 10, true);
            this.player.addEffect(Effects.FALL_DAMAGE_RESISTANCE, 100, true);

            // Fx
            this.player.playWorldSound(Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1.25f);
            this.player.playWorldSound(Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 0.75f);

            this.player.spawnWorldParticle(Particle.SMOKE, 20, 0.3, 0.3, 0.3, 0.05f);
            this.player.spawnWorldParticle(Particle.LARGE_SMOKE, 10, 0.3, 0.3, 0.3, 0.025f);
        }

        @Override
        public void run(int tick) {
            if (tick >= BatTransferTalent.this.getDuration()) {
                cancel();
                return;
            }

            // Push forward
            final Location location = bat.getLocation();
            location.add(direction);

            // Block collision detection
            if (!location.getBlock().isEmpty()) {
                cancel();
                return;
            }

            // Entity collection detection
            Collect.nearbyEntities(location, hitBoxSize, player::isNotSelfOrTeammate)
                   .forEach(entity -> {
                       if (temperInstance.isTempered(entity)) {
                           return;
                       }

                       entity.damage(damage, player, EnumDamageCause.BAT_BITE);
                       temperInstance.temper(entity, impairDuration, player);

                       // Fx
                       player.playWorldSound(entity.getLocation(), Sound.ENTITY_FOX_BITE, 0.0f);
                   });

            // Add the wobble
            LocationHelper.offset(
                    location, 0, Math.sin(tick * 20) * 0.2, 0, () -> bat.teleport(location)
            );

            // Sync player
            player.spectate(bat);

            // Get out early
            final Input input = player.input();

            if (input.isJump() || input.isSneak()) {
                cancel();
            }

            // Fx
            if (tick % 20 == 0 || Math.random() < 0.05) {
                player.playWorldSound(location, Sound.ENTITY_BAT_AMBIENT, 0.5f);
            }
        }
    }
}
