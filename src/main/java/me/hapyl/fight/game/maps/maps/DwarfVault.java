package me.hapyl.fight.game.maps.maps;

import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.cooldown.Cooldown;
import me.hapyl.fight.game.maps.EnumLevel;
import me.hapyl.fight.game.maps.Level;
import me.hapyl.fight.game.maps.LevelFeature;
import me.hapyl.fight.game.maps.Size;
import me.hapyl.fight.util.BoundingBoxCollector;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class DwarfVault extends Level implements Listener {

    public final BoundingBoxCollector lavaBoundingBox = new BoundingBoxCollector(5973, 24, -36, 6029, 33, 36);
    public final Vector lavaPushVector = new Vector(0.0d, 3.1d, 0.0d);
    public final double lavaDamage = 20.0d;

    public DwarfVault(@Nonnull EnumLevel handle) {
        super(handle, "Dwarfs' Vault");

        setDescription("");
        setMaterial(Material.RAW_GOLD);
        setSize(Size.MEDIUM);
        setTicksBeforeReveal(100);

        addLocation(6000, 64, 0, 180f, 0f);
        addLocation(6000, 64, -40);
        addLocation(6000, 62.25, -70);
        addLocation(6020, 75, -70, 90f, 0f);
        addLocation(5980, 75, -70, -90f, 0f);
        addLocation(6000, 75, -49, -180f, 0f);
        addLocation(6011.0, 64, -82.0, 90f, 0f);

        addFeature(new LevelFeature("Bouncy Lava", """
                Hot but bouncy lava that allows you to get back to the platform.
                """) {
            @Override
            public void tick(int tick) {
                if (tick % 20 == 0) {
                    lavaBoundingBox.collect(getWorld(), null).forEach(DwarfVault.this::launchUp);
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

        if (lavaBoundingBox.isWithin(entity)) {
            launchUp(entity);
            ev.setCancelled(true);
        }
    }

    public void launchUp(@Nonnull LivingGameEntity entity) {
        if (entity.hasCooldown(Cooldown.DWARF_LAVA)) {
            return;
        }

        entity.setVelocity(lavaPushVector);
        entity.damage(lavaDamage, EnumDamageCause.DWARF_LAVA);
        entity.startCooldown(Cooldown.DWARF_LAVA);

        // Fx
        entity.playSound(Sound.BLOCK_LAVA_POP, 0.0f);
        entity.playSound(Sound.BLOCK_REDSTONE_TORCH_BURNOUT, 0.0f);
        entity.playSound(Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 0.75f);
    }
}
