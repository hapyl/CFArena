package me.hapyl.fight.game.heroes.moonwalker;

import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.moonwalker.MoonPassive;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MoonwalkerWeapon extends Weapon {

    protected final PlayerMap<RayOfDeath> rayOfDeathMap;
    private final Moonwalker hero;

    public MoonwalkerWeapon(Moonwalker hero) {
        super(Material.IRON_HOE);

        this.hero = hero;

        setName("Stinger");
        setDamage(2.0d); // Melee damage
        setId("MOON_WEAPON");

        this.rayOfDeathMap = PlayerMap.newMap();

        setAbility(AbilityType.HOLD_RIGHT_CLICK, new ChargeAbility());
        setAbility(AbilityType.LEFT_CLICK, new RayOfDeathAbility());
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        rayOfDeathMap.removeAnd(player, RayOfDeath::cancel);
    }

    @Override
    public void onStop() {
        rayOfDeathMap.forEachAndClear(RayOfDeath::cancel);
    }

    private MoonwalkerData getData(GamePlayer player) {
        return hero.getPlayerData(player);
    }

    public class ChargeAbility extends Ability {

        @DisplayField private final double energyTransfer = 10;

        public ChargeAbility() {

            super("Laser Focus", """
                    Gather &eMoonlit Energy&7 from a nearby &eMoonlit Zone&7.
                    """, 750);

            setCooldownSec(6);
        }

        @Nullable
        @Override
        public Response execute(@Nonnull GamePlayer player, @Nonnull ItemStack item) {
            if (rayOfDeathMap.containsKey(player)) {
                return Response.error("Cannot gather energy while Ray of Death is active!");
            }

            final MoonwalkerData data = getData(player);
            final MoonZone zone = data.getZone(player.getLocation());

            if (zone == null) {
                player.sendMessage("&cNowhere to gather energy from!");
                startCooldown(player, 10);

                return Response.AWAIT;
            }

            final MoonPassive passive = hero.getPassiveTalent();

            data.weaponEnergy += zone.decreaseEnergy(energyTransfer, passive.weaponEnergyConversion);

            // Fx
            if (zone.energy <= 0) {
                return Response.OK;
            }

            return Response.AWAIT;
        }
    }

    public class RayOfDeathAbility extends Ability {

        public final MoonwalkerWeapon weapon = MoonwalkerWeapon.this;

        @DisplayField protected final double maxDistance = 20;
        @DisplayField protected final double damage = 3;
        @DisplayField protected final double minEnergyToActivate = 10;
        @DisplayField protected final double energyDrainPerTick = 2;
        @DisplayField protected final int cdPerSecondActive = 3;
        @DisplayField protected final int firstShotDelay = 4;
        @DisplayField protected final int damagePeriod = 4;

        public RayOfDeathAbility() {
            super("Ray of Death", """
                    Spend %1$s to channel a &6Ray of Death&7.
                                        
                    &6Ray of Death
                    A deadly ray that passes through blocks and entities, dealing rapid damage.
                                        
                    Continuously spends %1$s to sustain the ray.
                    The cooldown of this ability is based on the duration of the ray.
                                        
                    If there is not enough %1$s, unleash a short range attack instead.
                    """.formatted(Named.MOONLIT_ENERGY));

            setCooldownSec(2); // This is the no energy cooldown
        }

        @Nullable
        @Override
        public Response execute(@Nonnull GamePlayer player, @Nonnull ItemStack item) {
            if (rayOfDeathMap.containsKey(player)) {
                return Response.AWAIT;
            }

            final MoonwalkerData data = getData(player);

            if (data.weaponEnergy < minEnergyToActivate) {
                Debug.info("small hit");
                return Response.OK;
            }

            final RayOfDeath ray = new RayOfDeath(player, data, this);
            rayOfDeathMap.put(player, ray);

            return Response.AWAIT;
        }

    }

}
