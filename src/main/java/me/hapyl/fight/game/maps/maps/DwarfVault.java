package me.hapyl.fight.game.maps.maps;

import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.cooldown.Cooldown;
import me.hapyl.fight.game.maps.GameMap;
import me.hapyl.fight.game.maps.MapFeature;
import me.hapyl.fight.game.maps.Size;
import me.hapyl.fight.util.BoundingBoxCollector;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class DwarfVault extends GameMap implements Listener {

    public final BoundingBoxCollector LAVA_BOUNDING_BOX = new BoundingBoxCollector(1464, 44, 1539, 1531, 60, 1614);
    public final double DAMAGE = 20.0d;
    public final Vector PUSH_VELOCITY = new Vector(0.0d, 3.1d, 0.0d);

    public DwarfVault() {
        super("Dwarfs' Vault");

        setDescription("");
        setMaterial(Material.RAW_GOLD);
        setSize(Size.MEDIUM);
        setTicksBeforeReveal(100);

        addLocation(1500, 91, 1570);
        addLocation(1500, 91, 1532);
        addLocation(1500, 92, 1515);
        addLocation(1500, 90, 1500);
        addLocation(1500, 91, 1478);
        addLocation(1512, 91, 1486);
        addLocation(1519, 102, 1499);
        addLocation(1499, 102, 1522);
        addLocation(1479, 102, 1501);

        addFeature(new MapFeature("Bouncy Lava", """
                Hot but bouncy lava that allows you to get back to the platform.
                """) {
            @Override
            public void tick(int tick) {
                if (tick % 20 == 0) {
                    LAVA_BOUNDING_BOX.collect(getWorld(), null).forEach(DwarfVault.this::launchUp);
                }
            }
        });
    }

    @EventHandler
    public void handleDamage(GameDamageEvent ev) {
        final LivingGameEntity entity = ev.getEntity();
        final EnumDamageCause cause = ev.getCause();

        if (cause == EnumDamageCause.DWARF_LAVA) {
            return;
        }

        if (LAVA_BOUNDING_BOX.isWithin(entity)) {
            launchUp(entity);
            ev.setCancelled(true);
        }
    }

    public void launchUp(@Nonnull LivingGameEntity entity) {
        if (entity.hasCooldown(Cooldown.DWARF_LAVA)) {
            return;
        }

        entity.setVelocity(PUSH_VELOCITY);
        entity.damage(DAMAGE, EnumDamageCause.DWARF_LAVA);
        entity.startCooldown(Cooldown.DWARF_LAVA);

        // Fx
        entity.playSound(Sound.BLOCK_LAVA_POP, 0.0f);
        entity.playSound(Sound.BLOCK_REDSTONE_TORCH_BURNOUT, 0.0f);
        entity.playSound(Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 0.75f);
    }
}
