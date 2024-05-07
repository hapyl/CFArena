package me.hapyl.fight.game.heroes.ronin;

import com.google.common.collect.Maps;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talent;
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
import java.util.Map;

public class Ronin extends Hero implements Listener, Disabled {

    private final int chargeAttackCooldown = Tick.fromSecond(5);
    private final Map<GamePlayer, ChargeAttack> chargeAttackMap;

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
    public Ronin(@Nonnull Heroes handle) {
        super(handle, "Ronin");

        setArchetype(Archetype.DAMAGE);
        setGender(Gender.MALE);
        setItem("267bf069fefb40be22724b02e6c4fbe2133ef5e112bc551a4f0042ea99dcf6a2");

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(5, 2, 41, TrimPattern.SNOUT, TrimMaterial.GOLD);
        equipment.setLeggings(Material.NETHERITE_LEGGINGS, TrimPattern.SHAPER, TrimMaterial.NETHERITE);
        equipment.setBoots(5, 2, 41, TrimPattern.TIDE, TrimMaterial.GOLD);

        setWeapon(new RoninWeapon());

        chargeAttackMap = Maps.newConcurrentMap();
    }

    @Override
    public RoninWeapon getWeapon() {
        return (RoninWeapon) super.getWeapon();
    }

    @Override
    public void processDamageAsDamager(@Nonnull DamageInstance instance) {
        final GamePlayer player = instance.getDamagerAsPlayer();
        final ChargeAttack chargeAttack = chargeAttackMap.remove(player);

        if (chargeAttack == null) {
            return;
        }

        final Strength strength = chargeAttack.getStrength();

        instance.multiplyDamage(strength.multiplier);
    }

    @EventHandler()
    public void handleClick(PlayerInteractEvent ev) {
        final EquipmentSlot hand = ev.getHand();
        final Action action = ev.getAction();
        final Player player = ev.getPlayer();

        if (!validatePlayer(player)
                || (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)
                || hand == EquipmentSlot.OFF_HAND
                || player.hasCooldown(getWeapon().getMaterial())) {
            return;
        }

        final GamePlayer gamePlayer = CF.getPlayer(player);

        if (gamePlayer == null) {
            return;
        }

        final ChargeAttack chargeAttack = getChargeAttack(gamePlayer);
        chargeAttack.increment();
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

    public void failChargeAttack(GamePlayer player) {
        chargeAttackMap.remove(player);

        player.setCooldown(getWeapon().getMaterial(), chargeAttackCooldown);
    }

    @Nonnull
    private ChargeAttack getChargeAttack(GamePlayer player) {
        return chargeAttackMap.computeIfAbsent(player, fn -> new ChargeAttack(this, player));
    }
}
