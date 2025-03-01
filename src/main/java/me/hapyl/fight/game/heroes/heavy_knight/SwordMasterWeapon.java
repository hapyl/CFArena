package me.hapyl.fight.game.heroes.heavy_knight;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SwordMasterWeapon extends Weapon {
    public SwordMasterWeapon() {
        super(Material.NETHERITE_SWORD, Key.ofString("shaman_weapon"));

        setName("Basta");
        setDescription("""
                A royal claymore.
                
                &7&o;;This thing was too big to be called a sword.
                """);

        setDamage(8.0d);

        setAbility(AbilityType.RIGHT_CLICK, new LeapAbility());
    }

    private class LeapAbility extends Ability {

        @DisplayField private final double leapMagnitudeY = 0.35d;
        @DisplayField private final double leapMagnitude = 1.125d;

        public LeapAbility() {
            super("Leap", """
                    Leap forward.
                    
                    You won't take fall damage for a short duration after leaping.
                    """);

            setCooldownSec(6);
        }

        @Nullable
        @Override
        public Response execute(@Nonnull GamePlayer player, @Nonnull ItemStack item) {
            final Location location = player.getLocation();
            final Vector vector = location.getDirection().normalize().multiply(leapMagnitude).setY(leapMagnitudeY);

            player.setVelocity(vector);
            player.addEffect(EffectType.FALL_DAMAGE_RESISTANCE, 100);

            // Fx
            player.playWorldSound(Sound.ENTITY_CAMEL_DASH, 0.75f);
            player.playWorldSound(Sound.BLOCK_NETHERITE_BLOCK_BREAK, 0.75f);

            return Response.OK;
        }
    }
}
