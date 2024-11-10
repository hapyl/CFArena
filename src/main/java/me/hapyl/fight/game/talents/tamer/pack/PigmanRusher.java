package me.hapyl.fight.game.talents.tamer.pack;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.PigZombie;

import javax.annotation.Nonnull;

public class PigmanRusher extends TamerPack {

    @DisplayField private final double pigmanBaseDamage = 10;
    @DisplayField(percentage = true) private final double pigmanInitialSpeed = 1.5;
    @DisplayField(percentage = true) private final double pigmanMaxSpeed = 2.5;

    private final Weapon pigmanWeapon = Weapon.builder(Material.GOLDEN_SWORD, Key.ofString("pigman_sword"))
            .damage(pigmanBaseDamage)
            .build();

    private final Weapon pigmanUltimateWeapon = Weapon.builder(Material.GOLDEN_SWORD, Key.ofString("pigman_ultimate_sword"))
            .damage(pigmanBaseDamage * 2)
            .build();

    public PigmanRusher() {
        super("Pigman Rusher", """
                &bRushes&7 and &cattacks&7 all nearby enemies.
                """, TalentType.DAMAGE);

        attributes.setMaxHealth(50);

        setDurationSec(60);
    }

    @Nonnull
    @Override
    public String toString(ActiveTamerPack pack) {
        final PigmanRurhesEntity entity = pack.getFirstEntityOfType(PigmanRurhesEntity.class);

        if (entity == null) {
            return "";
        }

        return entity.getHealthFormatted() + " " + AttributeType.SPEED.getFormatted(entity.getAttributes()) + "%";
    }

    @Override
    public void onSpawn(@Nonnull ActiveTamerPack pack, @Nonnull Location location) {
        pack.createEntity(location, Entities.ZOMBIFIED_PIGLIN, entity -> {
            return new PigmanRurhesEntity(pack, entity);
        });
    }

    private class PigmanRurhesEntity extends TamerEntity<PigZombie> {

        private final int duration;
        private double currentSpeed;

        public PigmanRurhesEntity(@Nonnull ActiveTamerPack pack, @Nonnull PigZombie entity) {
            super(pack, entity);

            entity.setAdult();

            currentSpeed = attributes.get(AttributeType.SPEED);
            duration = getDuration(pack.player);

            if (isUsingUltimate(pack.player)) {
                pigmanUltimateWeapon.give(this);
            }
            else {
                pigmanWeapon.give(this);
            }
        }

        @Override
        public void tick(int index) {
            super.tick(index);

            // Update speed
            currentSpeed = (pigmanInitialSpeed + ((pigmanMaxSpeed - pigmanInitialSpeed) / duration * tick) - 1) * 0.2; // speed scale  * 100
            attributes.set(AttributeType.SPEED, currentSpeed);

            entity.setAngry(true);
            entity.setAnger(1000);
        }
    }
}
