package me.hapyl.fight.game.talents.dylan;

import me.hapyl.eterna.module.math.Geometry;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.dylan.Dylan;
import me.hapyl.fight.game.heroes.dylan.Rebuke;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.terminology.EnumTerm;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class DylanPassive extends PassiveTalent {
    
    @DisplayField public final int threshold = 15;
    @DisplayField(percentage = true) public final double rebukeDamage = 6.66;
    @DisplayField public final int fireDuration = Tick.fromSeconds(3);
    
    @DisplayField public final short maxScorchStacks = 3;
    @DisplayField(percentage = true) public final double attackIncreasePerStack = 0.1;
    @DisplayField(percentage = true) public final double maxHealthIncreasePerStack = 0.2;
    
    public DylanPassive(@Nonnull Key key) {
        super(key, "Hellish Rebuke/Infernal Whelp");
        
        setDescription("""
                       &6Hellish Rebuke
                       When taking a &fdirect&7 damage, you can use your inhuman &d⭐ Reaction&7 to rebuke the attacker, dealing &cdamage&7 equal to &4%1$.0f%%&7 of the damage taken as %2$s and setting them on &efire&7.
                       
                       &6Infernal Whelp
                       When &3%3$s&7 acts, it gains one stack of %4$s, up to &e%5$s&7 stacks.
                       
                       Each stack increases &3%3$s&7's %6$s and %7$s.
                       """.formatted(
                rebukeDamage * 100, EnumTerm.TRUE_DAMAGE,
                Dylan.familiarName, Named.SCORCH, maxScorchStacks,
                AttributeType.ATTACK, AttributeType.MAX_HEALTH
        ));
        
        setType(TalentType.ENHANCE);
        setMaterial(Material.BLAZE_POWDER);
        
        setCooldownSec(9);
    }
    
    @Override
    public boolean isDisplayAttributes() {
        return true;
    }
    
    public void rebuke(@Nonnull Rebuke rebuke) {
        final GamePlayer player = rebuke.player();
        final LivingGameEntity entity = rebuke.entity();
        final double damage = rebuke.damage() * rebukeDamage;
        
        entity.damageNoKnockback(damage, player, DamageCause.HELLISH_REBUKE);
        entity.setFireTicks(fireDuration);
        
        // Callback
        rebuke.cancel(Rebuke.Type.REBUKED);
        
        final Location from = player.getMidpointLocation();
        final Location to = entity.getMidpointLocation();
        
        final Vector vector = to.toVector().subtract(from.toVector()).normalize().multiply(0.5);
        
        // Fx
        Geometry.drawLine(
                from, to, 0.2, location -> {
                    player.spawnWorldParticle(
                            location, Particle.FLAME, 0,
                            vector.getX() * player.random.nextDouble() * 0.666,
                            vector.getY(),
                            vector.getZ() * player.random.nextDouble() * 0.666,
                            1f
                    );
                    
                    player.spawnWorldParticle(location, Particle.SMOKE, 0, vector.getX(), vector.getY(), vector.getZ(), 0.05f);
                }
        );
    }
    
}

