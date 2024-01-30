package me.hapyl.fight.game.heroes.archive.moonwalker;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.game.weapons.ability.held.HeldAbility;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MoonwalkerWeapon extends Weapon {

    public final ChargeAbility ability;
    private final PlayerMap<RayOfDeath> rayOfDeathMap;

    @DisplayField
    private final int unitsNeeded = 20;

    public MoonwalkerWeapon() {
        super(Material.IRON_HOE);

        setName("Stinger");
        setDamage(2.0d); // Melee damage
        setId("MOON_WEAPON");

        this.rayOfDeathMap = PlayerMap.newMap();

        setAbility(AbilityType.HOLD_RIGHT_CLICK, ability = new ChargeAbility());
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        rayOfDeathMap.removeAnd(player, RayOfDeath::cancel);
    }

    @Override
    public void onStop() {
        rayOfDeathMap.forEachAndClear(RayOfDeath::cancel);
    }

    public class ChargeAbility extends HeldAbility {

        public final double maxRange = 15.0d;
        public final double step = 0.5d;
        public final double damage = 4.0d;
        public final int damagePeriod = 9;

        public ChargeAbility() {
            super("Laser Focus", """
                    Start charging a power laser.
                                        
                    Once charged, unleash a &6Ray of Death&7.
                                        
                    &6Ray of Death
                    Continuously deals damage in a straight line, piercing though blocks and enemies.
                                        
                    &8;;The cooldown of this ability starts after the Ray of Death finishes.
                    """, 750);

            setCooldownSec(6);
            setDurationSec(3);
        }

        @Nonnull
        @Override
        public Weapon getWeapon() {
            return MoonwalkerWeapon.this;
        }

        @Override
        public boolean onUnitGain(@Nonnull GamePlayer player, int totalUnits) {
            if (player.hasCooldown(getMaterial())) {
                return false;
            }

            if (totalUnits < unitsNeeded) {

                // Fx
                player.playWorldSound(Sound.ENCHANT_THORNS_HIT, 0.5f + (1.25f / unitsNeeded * totalUnits));

                return true;
            }

            final RayOfDeath oldRay = rayOfDeathMap.put(player, new RayOfDeath(player, this));
            player.setCooldownIgnoreModifier(getMaterial(), 10000);

            if (oldRay != null) {
                oldRay.cancel();
            }

            return true;
        }
    }

    public class QuickShot extends Ability {

        public QuickShot() {
            super("Quick Shot", """
                    Shoot a quick arrow that deals &b50%&7 of the normal damage.
                    """);

            setCooldown(20);
        }

        @Nullable
        @Override
        public Response execute(@Nonnull GamePlayer player, @Nonnull ItemStack item) {
            player.launchProjectile(Arrow.class, self -> {
                self.setDamage(getDamage() / 2);
                self.setCritical(false);
            });

            player.playWorldSound(Sound.ENTITY_ARROW_SHOOT, 1.25f);
            return Response.OK;
        }
    }

}
