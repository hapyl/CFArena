package me.hapyl.fight.game.heroes.alchemist;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

class AlchemistWeapon extends Weapon {
    protected AlchemistWeapon() {
        super(Material.STICK, Key.ofString("alchemist_stick"));

        setName("Stick");
        setDescription("""
                A stick used for potion brewing can apparently also be used for combat.
                """);

        addEnchant(Enchantment.KNOCKBACK, 1);
        setDamage(8.0d);

        setAbility(AbilityType.RIGHT_CLICK, new AlchemistWeaponAbility());
    }

    private static class AlchemistWeaponAbility extends Ability {

        @DisplayField
        private final double radius = 1.5d;

        public AlchemistWeaponAbility() {
            super(
                    "Twack", """
                            Fling your stick in front of you, dealing &chigh&7 knockback to enemies.
                            """
            );

            setCooldownSec(10);
        }

        @Nullable
        @Override
        public Response execute(@Nonnull GamePlayer player, @Nonnull ItemStack item) {
            final Location location = player.getLocationInFrontFromEyes(1);

            Collect.nearbyEntities(location, radius, player::isNotSelfOrTeammateOrHasEffectResistance)
                   .forEach(entity -> {
                       entity.setVelocity(player.getDirection().multiply(0.8d).setY(0.6d));
                   });

            // Fx
            player.playWorldSound(location, Sound.ENTITY_BREEZE_WIND_BURST, 1.25f);
            player.spawnWorldParticle(location, Particle.SWEEP_ATTACK, 1);
            return Response.OK;
        }
    }
}
