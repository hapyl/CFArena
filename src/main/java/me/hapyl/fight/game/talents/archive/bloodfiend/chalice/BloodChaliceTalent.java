package me.hapyl.fight.game.talents.archive.bloodfiend.chalice;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.archive.bloodfiend.taunt.TauntTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.annotation.Nonnull;

public class BloodChaliceTalent extends TauntTalent<BloodChalice> implements Listener {

    @DisplayField protected final short chaliceHealth = 3;
    @DisplayField protected final double maxDistance = 30.0d;
    @DisplayField private final double damage = 25.0d;
    @DisplayField(suffix = "%", suffixSpace = false) public final double healingPercent = 50;

    public BloodChaliceTalent() {
        super("Blood Chalice");

        setItem(Material.SKELETON_SKULL);
        setDurationSec(20);
        setCooldownSec(30);
    }

    @Nonnull
    @Override
    public String getDescription() {
        return """
                The chalice will convert &c{healingPercent}&7 of damage dealt into healing.
                &8&o;;Only against taunted player.
                """;
    }

    @Nonnull
    @Override
    public String getHowToRemove() {
        return """
                Hitting the chalice will cause it to change location and &ccrack&7 a little.
                &8&o;;Doing so %s times will cause chalice to break.
                """.formatted(chaliceHealth);
    }

    @Override
    public double getExplosionDamage() {
        return damage;
    }

    @Override
    @Nonnull
    public EnumDamageCause getDamageCause() {
        return EnumDamageCause.CHALICE;
    }

    @Override
    public BloodChalice createTaunt(Player player, GamePlayer target) {
        return new BloodChalice(this, player, target);
    }

    @EventHandler()
    public void handleEntityDamageByEntityEvent(EntityDamageEvent ev) {
        final Entity entity = ev.getEntity();

        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }

        for (BloodChalice value : playerTaunt.values()) {
            if (!value.isPart(livingEntity)) {
                return;
            }

            final int noDamageTicks = livingEntity.getNoDamageTicks();

            ev.setDamage(0.0d);
            ev.setCancelled(true);

            if (noDamageTicks > 0) {
                return;
            }

            value.crack();
        }
    }

}
