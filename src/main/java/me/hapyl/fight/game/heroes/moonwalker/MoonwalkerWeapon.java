package me.hapyl.fight.game.heroes.moonwalker;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MoonwalkerWeapon extends Weapon {

    private final Moonwalker hero;
    private final RayOfDeathAbility ability;

    @DisplayField(scaleFactor = 4) public final int chargingTime = 10;
    @DisplayField public final int maxInactiveTime = 8;

    @DisplayField public final double maxDistance = 25;
    @DisplayField public final double hitBoxSize = 1.5d;
    @DisplayField public final double damage = 3;
    @DisplayField public final double energyToActivate = 10;
    @DisplayField public final double energyDrainPerTick = 2;

    public MoonwalkerWeapon(Moonwalker hero) {
        super(Material.IRON_HOE, Key.ofString("moon_weapon"));

        this.hero = hero;

        setName("Lunar Reaver");
        setDescription("""
                A unique weapon made of unknown materials.
                """);

        setDamage(2.0d); // Melee damage

        setAbility(AbilityType.HOLD_RIGHT_CLICK, this.ability = new RayOfDeathAbility());
    }

    @Nonnull
    public RayOfDeathAbility ability() {
        return ability;
    }

    public class RayOfDeathAbility extends Ability {

        public final MoonwalkerWeapon weapon = MoonwalkerWeapon.this;

        public RayOfDeathAbility() {
            super(
                    "Ray of Death", """
                            Spend &6%1$.0f&7 %2$s to start channeling the &4Ray of Death&7.
                            
                            &6Ray of Death
                            A deadly ray of condensed %2$s that rapidly deals &cdamage&7 to enemies in its path.
                            
                            Continuously spends %2$s to sustain the ray.
                            
                            &8&o;;If stopped channeling, unleash a short range attack.
                            """.formatted(energyToActivate, Named.MOONLIT_ENERGY)
            );

            setCooldownSec(2); // This is the no energy cooldown
        }

        @Nullable
        @Override
        public Response execute(@Nonnull GamePlayer player, @Nonnull ItemStack item) {
            final MoonwalkerData data = hero.getPlayerData(player);

            // Check if there are enough energy to run
            if (data.energy < energyToActivate) {
                return Response.error("Not enough energy!");
            }

            data.incrementWeaponCharge();
            return Response.AWAIT;
        }

    }

}
