package me.hapyl.fight.game.heroes.archive.orc;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.IGamePlayer;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.attribute.PlayerAttributes;
import me.hapyl.fight.game.attribute.Temper;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.orc.OrcAxe;
import me.hapyl.fight.game.talents.archive.orc.OrcGrowl;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Orc extends Hero {

    /**
     * WEAPON:
     * - Axe
     * - RIGHT CLICK:
     * -- Throw: (10s max)
     * --- When enemy hit:
     * ---- Freeze & Slow
     * --- When hit block:
     * ----- Stuck in block, flies back longer.
     * --- Flies back parabola
     * <p>
     * ABILITY 1:
     * - Grown
     * -- 8 blocks radius zone:
     * --- Slowness, Weakness (6s)
     * <p>
     * ABILITY 2: INPUT
     * - LEFT CLICK: Eula
     * - RIGHT CLICK: Dash damage
     * <p>
     * PASSIVE: (3s, cd 30s)
     * If keep taking damage by player mini ultimate.
     * Negative effects deal less damage
     * <p>
     * ULTIMATE: (20s)
     * - Berserk Mode
     * -- -70 defense
     * -- Crit chance increase
     * -- Speed
     * -- Attack
     */

    public Orc() {
        super("Pakarat Rakab");

        final HeroAttributes attributes = getAttributes();
        attributes.setValue(AttributeType.HEALTH, 150);
        attributes.setValue(AttributeType.DEFENSE, 0.75d);
        attributes.setValue(AttributeType.SPEED, 0.22d);
        attributes.setValue(AttributeType.CRIT_CHANCE, 0.15d);

        setWeapon(new OrcWeapon());

        setUltimate(
                new UltimateTalent("Berserk", 70)
                        .setDurationSec(20)
                        .setCooldown(30)
                        .appendDescription("""
                                Enter berserk mode for {duration}.
                                                                
                                While active, your %s, %s and %s is increased, but you lose 70 %s.
                                """, AttributeType.ATTACK, AttributeType.SPEED, AttributeType.CRIT_CHANCE, AttributeType.DEFENSE)
        );
    }

    @Override
    public void useUltimate(Player player) {
        enterBerserk(player, getUltimateDuration());
    }

    @Override
    public void onDeath(Player player) {
        if (!(getWeapon() instanceof OrcWeapon orcWeapon)) {
            return;
        }

        orcWeapon.remove(player);
    }

    public void enterBerserk(Player player, int duration) {
        final IGamePlayer gamePlayer = GamePlayer.getPlayer(player);
        final PlayerAttributes attributes = gamePlayer.getAttributes();

        attributes.increaseTemporary(Temper.BERSERK_MODE, AttributeType.ATTACK, 0.5d, duration);
        attributes.increaseTemporary(Temper.BERSERK_MODE, AttributeType.SPEED, 0.05d, duration);
        attributes.increaseTemporary(Temper.BERSERK_MODE, AttributeType.CRIT_CHANCE, 0.4d, duration);
        attributes.decreaseTemporary(Temper.BERSERK_MODE, AttributeType.DEFENSE, 0.7d, duration);

        // Fx
        new GameTask() {
            private int tick = 0;

            @Override
            public void run() {
                if (tick++ >= duration) {
                    cancel();
                    return;
                }

                final Location location = player.getLocation();

                // Sound FX
                if (tick % 20 == 0) {
                    PlayerLib.playSound(location, Sound.ENTITY_PIGLIN_AMBIENT, 0.75f);
                    PlayerLib.playSound(location, Sound.ENTITY_PIGLIN_ANGRY, 1.25f);
                }

                // Particle FX
                if (tick % 10 != 0) {
                    PlayerLib.spawnParticle(location, Particle.LAVA, 1, 0, 0, 0, 0.1f);
                }
            }
        };
    }

    @Override
    public OrcGrowl getFirstTalent() {
        return (OrcGrowl) Talents.ORC_GROWN.getTalent();
    }

    @Override
    public OrcAxe getSecondTalent() {
        return (OrcAxe) Talents.ORC_AXE.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return null;
    }
}
