package me.hapyl.fight.game.talents.archive.bloodfiend.chalice;

import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.archive.bloodfiend.taunt.TauntTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.annotation.Nonnull;

public class BloodChaliceTalent extends TauntTalent<BloodChalice> implements Listener {

    @DisplayField(percentage = true) public final double healingPercent = 0.8d;
    @DisplayField protected final short chaliceHealth = 2;

    public BloodChaliceTalent() {
        super("Blood Chalice");

        setType(Type.SUPPORT);
        setItem(Material.SKELETON_SKULL);
        setDurationSec(30);
        setCooldownSec(15);
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
                &8&o;;Doing so {chaliceHealth} times will cause chalice to break.
                """;
    }

    @Override
    @Nonnull
    public EnumDamageCause getDamageCause() {
        return EnumDamageCause.CHALICE;
    }

    @Nonnull
    @Override
    public BloodChalice createTaunt(@Nonnull GamePlayer player, @Nonnull GamePlayer target, @Nonnull Location location) {
        return new BloodChalice(this, player, target, location);
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
