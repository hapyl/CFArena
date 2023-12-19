package me.hapyl.fight.game.heroes.archive.ronin;

import com.google.common.collect.Maps;
import me.hapyl.fight.event.io.DamageInput;
import me.hapyl.fight.event.io.DamageOutput;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.DisabledHero;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.UltimateCallback;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.spigotutils.module.math.Tick;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class Ronin extends Hero implements Listener, DisabledHero {

    private final int chargeAttackCooldown = Tick.fromSecond(5);
    private final Map<Player, ChargeAttack> chargeAttackMap;

    /**
     * WEAPON
     * 3 perfect hits = 4th hit (any) more dmg 100% bleed for 10s.
     * <p>
     * ABILITY 1
     * Move charge attack to ability.
     * ABILITY 2
     * Small dash, damage and slowness.
     * <p>
     * ABILITY 3
     * AoE in front cone x3 BIG DAMAJE.
     * <p>
     * Right click block/reflect. ANVIL_LAND SOUND
     * <p>
     * ULTIMATE
     * - Lose 50% of current health.
     * - Increase speed and attack.
     * - All hits apply bleed.
     */
    public Ronin() {
        super("Ronin");

        setArchetype(Archetype.DAMAGE);
        setItem("267bf069fefb40be22724b02e6c4fbe2133ef5e112bc551a4f0042ea99dcf6a2");

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(5, 2, 41, TrimPattern.SNOUT, TrimMaterial.GOLD);
        equipment.setLeggings(Material.NETHERITE_LEGGINGS, TrimPattern.SHAPER, TrimMaterial.NETHERITE);
        equipment.setBoots(5, 2, 41, TrimPattern.TIDE, TrimMaterial.GOLD);

        setWeapon(new RoninWeapon());

        setUltimate(new UltimateTalent("Harakiri", """
                                
                """, 30));

        chargeAttackMap = Maps.newConcurrentMap();
    }

    @Override
    public RoninWeapon getWeapon() {
        return (RoninWeapon) super.getWeapon();
    }

    @Nullable
    @Override
    public DamageOutput processDamageAsDamager(DamageInput input) {
        final GamePlayer player = input.getDamagerAsPlayer();
        final ChargeAttack chargeAttack = chargeAttackMap.remove(player);

        if (chargeAttack == null) {
            return DamageOutput.OK;
        }

        final Strength strength = chargeAttack.getStrength();

        return new DamageOutput(input.getDamage() * strength.multiplier);
    }

    @EventHandler()
    public void handleClick(PlayerInteractEvent ev) {
        final EquipmentSlot hand = ev.getHand();
        final Action action = ev.getAction();
        final Player player = ev.getPlayer();

        if (!validatePlayer(player)
                || (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)
                || hand == EquipmentSlot.OFF_HAND
                || player.hasCooldown(getWeapon().getType())) {
            return;
        }

        final ChargeAttack chargeAttack = getChargeAttack(player);
        chargeAttack.increment();
    }

    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        return UltimateCallback.OK;
    }

    @Override
    public Talent getFirstTalent() {
        return null;
    }

    @Override
    public Talent getSecondTalent() {
        return null;
    }

    @Override
    public Talent getPassiveTalent() {
        return null;
    }

    public void failChargeAttack(Player player) {
        chargeAttackMap.remove(player);

        player.setCooldown(getWeapon().getType(), chargeAttackCooldown);
    }

    @Nonnull
    private ChargeAttack getChargeAttack(Player player) {
        return chargeAttackMap.computeIfAbsent(player, fn -> new ChargeAttack(this, player));
    }
}
