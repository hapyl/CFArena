package me.hapyl.fight.game.talents.ninja;

import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.TimedGameTask;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Tick;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class NinjaSmoke extends Talent {

    @DisplayField(percentage = true) public final double dodgeIncrease = 0.5d;
    @DisplayField public final int buffDuration = Tick.fromSecond(6);
    @DisplayField(suffix = "blocks") private final double smokeDistance = 4.0d;

    public NinjaSmoke() {
        super("Smoke Bomb");

        setDescription("""
                Throw a smoke bomb at your current location and enter %s state.
                                
                While in this state, become &binvisible&7.
                   
                Inflicting &cdamage&7 will &nclear&7 this state and trigger the following effects:
                └ &8Slows&7 the attacked enemy.
                └ Grants you &e{dodgeIncrease} %s for &b{buffDuration}&7.
                """, Named.SHADOWSTRIKE, AttributeType.DODGE);

        setType(TalentType.ENHANCE);
        setItem(Material.INK_SAC);
        setDurationSec(6);
        setCooldownSec(20);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getMidpointLocation();
        final int duration = getDuration();

        player.addEffect(Effects.INVISIBILITY, duration);

        // Fx
        new TimedGameTask(duration) {
            private double distance = 1.0d;

            @Override
            public void run(int tick) {
                if (distance < smokeDistance) {
                    distance = Math.min(distance + 0.5d, smokeDistance);
                }

                player.spawnWorldParticle(location, Particle.CAMPFIRE_COSY_SMOKE, 10, distance / 32, 0.25d, distance / 32, 0.025f);
            }
        }.runTaskTimer(0, 1);

        player.playWorldSound(Sound.ENTITY_PANDA_BITE, 0.0f);

        return Response.OK;
    }
}
