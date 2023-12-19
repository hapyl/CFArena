package me.hapyl.fight.game.heroes.archive.ninja;

import me.hapyl.fight.CF;
import me.hapyl.fight.event.io.DamageInput;
import me.hapyl.fight.event.io.DamageOutput;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.UltimateCallback;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.ninja.NinjaSmoke;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.MaterialCooldown;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.math.Tick;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class Ninja extends Hero implements Listener, UIComponent, MaterialCooldown {

    private final double ultimateDamage = 20.0d;

    private final ItemStack throwingStar = new ItemBuilder(Material.NETHER_STAR, "THROWING_STAR").setName("Throwing Star")
            .setAmount(5)
            .addClickEvent(player -> {
                final GamePlayer gamePlayer = CF.getPlayer(player);

                if (gamePlayer == null) {
                    return;
                }

                shootStar(gamePlayer);
            })
            .withCooldown(10)
            .build();
    private final int doubleJumpCooldown = Tick.fromSecond(5);

    public Ninja() {
        super(
                "Ninja",
                "An extremely well-trained fighter with a gift from the wind."
        );

        setArchetype(Archetype.MOBILITY);
        setItem("1413159cfab50aba283e68c1659d74412392fbcb1f7d663d1bd2a2a6430c2743");

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(Color.WHITE);
        equipment.setLeggings(Material.CHAINMAIL_LEGGINGS);
        equipment.setBoots(Material.CHAINMAIL_BOOTS);

        setWeapon(new NinjaWeapon());

        setUltimate(new UltimateTalent(
                "Throwing Stars",
                "Equip &b5&7 dead-accurate &6throwing stars&7 that deal &c%.0f&7 damage upon hitting an enemy.".formatted(ultimateDamage),
                70
        ).setItem(Material.NETHER_STAR).setSound(Sound.ITEM_TRIDENT_RIPTIDE_1, 0.75f));
    }

    @Override
    public boolean processInvisibilityDamage(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity, double damage) {
        executeShadowStrike(player, entity);
        return false;
    }

    public void executeShadowStrike(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity) {
        final NinjaSmoke ninjaSmoke = getSecondTalent();

        Temper.SHADOWSTRIKE.temper(player, AttributeType.DODGE, ninjaSmoke.dodgeIncrease, ninjaSmoke.buffDuration);
        player.removeEffect(GameEffectType.INVISIBILITY);

        entity.addPotionEffect(PotionEffectType.SLOW, 20, 5);

        // Fx
        player.playWorldSound(Sound.BLOCK_ANVIL_LAND, 1.25f);
        player.playWorldSound(Sound.ENTITY_SQUID_HURT, 0.75f);

        entity.spawnWorldParticle(Particle.SWEEP_ATTACK, 1);
    }

    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        setUsingUltimate(player, true);

        player.setItemAndSnap(HotbarSlots.HERO_ITEM, throwingStar);
        player.setCooldown(throwingStar.getType(), 20);

        return UltimateCallback.OK;
    }

    @Override
    public void onPlayersReveal(@Nonnull GamePlayer player) {
        player.setAllowFlight(true);
    }

    @Override
    public void onRespawn(@Nonnull GamePlayer player) {
        onPlayersReveal(player);
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        player.addPotionEffect(PotionEffectType.SPEED, 999999, 0);
    }

    @EventHandler()
    public void handleDoubleJump(PlayerToggleFlightEvent ev) {
        final GamePlayer player = CF.getPlayer(ev.getPlayer());

        if (player == null || !validatePlayer(player) || hasCooldown(player)) {
            return;
        }

        ev.setCancelled(true);

        player.setVelocity(new Vector(0.0d, 1.0d, 0.0d));
        player.setFlying(false);
        player.setAllowFlight(false);

        startCooldown(player);
        player.schedule(() -> {
            player.setAllowFlight(true);
            player.playSound(Sound.ENTITY_PHANTOM_FLAP, 1.0f);
        }, getCooldown());

        // Fx
        player.playWorldSound(Sound.ENTITY_BAT_TAKEOFF, 1.2f);
        player.spawnWorldParticle(Particle.EXPLOSION_NORMAL, 5, 0.2d, 0.0d, 0.2d, 0.03f);
    }

    @Override
    @Nonnull
    public String getString(@Nonnull GamePlayer player) {
        return hasCooldown(player) ? "&f🌊 &l" + getCooldownFormatted(player) : "";
    }

    @Override
    public NinjaWeapon getWeapon() {
        return (NinjaWeapon) super.getWeapon();
    }

    @Override
    public DamageOutput processDamageAsDamager(DamageInput input) {
        final GamePlayer player = input.getDamagerAsPlayer();
        final LivingGameEntity entity = input.getEntity();
        final NinjaWeapon weapon = getWeapon();

        if (entity == player || player == null || !input.isEntityAttack() || player.hasCooldown(weapon.getMaterial())) {
            return DamageOutput.OK;
        }

        if (!player.isHeldSlot(HotbarSlots.WEAPON)) {
            return DamageOutput.OK;
        }

        weapon.noAbilityWeapon.give(player);
        player.setCooldown(weapon.getMaterial(), weapon.stunCd);

        // Fx
        player.playWorldSound(Sound.ITEM_SHIELD_BREAK, 0.75f);
        player.spawnWorldParticle(entity.getEyeLocation(), Particle.VILLAGER_ANGRY, 5, 0.2d, 0.2d, 0.2d, 0.0f);

        // Return task
        player.schedule(weapon::give, weapon.stunCd);

        return DamageOutput.OK;
    }

    @Override
    public DamageOutput processDamageAsVictim(DamageInput input) {
        if (input.getDamageCause() == EnumDamageCause.FALL) {
            return DamageOutput.CANCEL;
        }

        return null;
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.NINJA_DASH.getTalent();
    }

    @Override
    public NinjaSmoke getSecondTalent() {
        return (NinjaSmoke) Talents.NINJA_SMOKE.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.FLEET_FOOT.getTalent();
    }

    @Nonnull
    @Override
    public Material getCooldownMaterial() {
        return Material.FIREWORK_ROCKET;
    }

    @Override
    public int getCooldown() {
        return doubleJumpCooldown;
    }

    private void shootStar(GamePlayer player) {
        final ItemStack item = player.getHeldItem();
        item.setAmount(item.getAmount() - 1);

        if (item.getAmount() <= 0) {
            setUsingUltimate(player, false);
            player.snapToWeapon();
        }

        CFUtils.rayTraceLine(
                player,
                40,
                0.5d,
                ultimateDamage,
                EnumDamageCause.THROWING_STARS,
                location -> player.spawnWorldParticle(location, Particle.FIREWORKS_SPARK, 1, 0.0d, 0.0d, 0.0d, 0.015f),
                entity -> player.playWorldSound(entity.getLocation(), Sound.ITEM_TRIDENT_HIT, 2.0f)
        );

        player.playWorldSound(Sound.ITEM_TRIDENT_THROW, 1.5f);
    }
}
