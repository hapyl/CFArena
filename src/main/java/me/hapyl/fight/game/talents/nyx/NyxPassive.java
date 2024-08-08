package me.hapyl.fight.game.talents.nyx;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.shield.Shield;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.game.ui.display.AscendingDisplay;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class NyxPassive extends PassiveTalent {

    @DisplayField private final double healing = 10;
    @DisplayField private final double effectResIncrease = 25;
    @DisplayField private final double explosionRadius = 3.0;

    @DisplayField(percentage = true) private final double shieldCapacityScaling = 0.10d;
    @DisplayField(percentage = true) private final double damageScaling = 0.065d;

    @DisplayField private final int buffDuration = Tick.fromSecond(6);

    public NyxPassive() {
        super("nyx passive", Material.BEDROCK);

        setDescription("""
                Whenever Nyx or her ally debuffs an enemy, and she has a %s stack, Nyx will launch a followup attack.

                Upon hit, it deals &CAoE damage&7 and grants a &9Void Shield&7 to the teammate who triggered this attack.

                &6Void Shied
                Whenever the shield is broken or refreshed, heals its target and increases %s.
                """.formatted(Named.THE_CHAOS, AttributeType.EFFECT_RESISTANCE));

        setCooldownSec(2.5f);
    }

    @Override
    public boolean isDisplayAttributes() {
        return true;
    }

    public void createShield(@Nonnull GamePlayer nyx, @Nonnull GamePlayer target) {
        final double health = nyx.getHealth();
        final double shieldCapacity = health * shieldCapacityScaling;

        if (shieldCapacity < 0) {
            return;
        }

        final Shield previousShield = target.getShield();

        // Refreshed
        if (previousShield instanceof VoidShield) {
            buff(nyx, target);
        }

        target.setShield(new VoidShield(nyx, target, shieldCapacity));
    }

    public void buff(@Nonnull GamePlayer nyx, @Nonnull GamePlayer player) {
        player.heal(healing, nyx);

        final EntityAttributes attributes = player.getAttributes();
        attributes.increaseTemporary(Temper.VOID_SHIELD, AttributeType.EFFECT_RESISTANCE, effectResIncrease, buffDuration);
    }

    public void execute(@Nonnull GamePlayer nyx, @Nonnull GamePlayer player, @Nonnull LivingGameEntity target) {
        final Location location = target.getLocation();
        final double damage = nyx.scaleHealth(damageScaling);

        Collect.nearbyEntities(location, explosionRadius, entity -> !player.isSelfOrTeammate(entity))
                .forEach(entity -> {
                    // Add assist to nyx
                    entity.getEntityData().addAssistingPlayer(nyx);

                    // Make the player who triggered the followup the damager,
                    // nyx gets the assist if entity dies from this attack
                    entity.setLastDamager(player);
                    entity.damage(damage, EnumDamageCause.CHAOS);
                });

        createShield(nyx, player);

        // Fx -- todo
    }

    private class VoidShield extends Shield {

        private final GamePlayer nyx;

        public VoidShield(@Nonnull GamePlayer nyx, @Nonnull GamePlayer player, double maxCapacity) {
            super(player, maxCapacity);

            this.nyx = nyx;
        }

        @Override
        public void display(double damage, @Nonnull Location location) {
            new AscendingDisplay("%1$s&l⚫ %1$s%2$.0f".formatted(Color.VOID, damage), 20).display(player.getEyeLocation());
        }

        @Override
        public void onBreak() {
            buff(nyx, player);
        }
    }

}
