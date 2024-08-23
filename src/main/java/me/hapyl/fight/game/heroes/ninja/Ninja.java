package me.hapyl.fight.game.heroes.ninja;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Gender;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.UltimateResponse;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.ninja.NinjaSmoke;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.MaterialCooldown;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class Ninja extends Hero implements Listener, UIComponent, MaterialCooldown {

    private final double ultimateDamage = 20.0d;

    private final ItemStack throwingStar = new ItemBuilder(Material.NETHER_STAR, "THROWING_STAR")
            .setName("Throwing Star &6(Right Click)")
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

    public Ninja(@Nonnull DatabaseKey key) {
        super(key, "Ninja");

        setDescription("""
                An extremely well-trained fighter with a gift from the wind.
                """);

        setArchetypes(Archetype.MOBILITY, Archetype.DAMAGE, Archetype.MELEE);
        setGender(Gender.MALE); // maybe female because it's literally jett
        setItem("1413159cfab50aba283e68c1659d74412392fbcb1f7d663d1bd2a2a6430c2743");

        final HeroAttributes attributes = getAttributes();
        attributes.setSpeed(115);

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(Color.WHITE);
        equipment.setLeggings(Material.CHAINMAIL_LEGGINGS);
        equipment.setBoots(Material.CHAINMAIL_BOOTS);

        setWeapon(new NinjaWeapon());
        setUltimate(new NinjaUltimate());
    }

    @Override
    public boolean processInvisibilityDamage(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity, double damage) {
        executeShadowStrike(player, entity);
        return false;
    }

    public void executeShadowStrike(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity) {
        final NinjaSmoke ninjaSmoke = getSecondTalent();

        Temper.SHADOWSTRIKE.temper(player, AttributeType.DODGE, ninjaSmoke.dodgeIncrease, ninjaSmoke.buffDuration);
        player.removeEffect(Effects.INVISIBILITY);

        entity.addEffect(Effects.SLOW, 5, 20);

        // Fx
        player.playWorldSound(Sound.BLOCK_ANVIL_LAND, 1.25f);
        player.playWorldSound(Sound.ENTITY_SQUID_HURT, 0.75f);

        entity.spawnWorldParticle(Particle.SWEEP_ATTACK, 1);
    }

    @Override
    public void onPlayersRevealed(@Nonnull GamePlayer player) {
        player.setAllowFlight(true);
    }

    @Override
    public void onRespawn(@Nonnull GamePlayer player) {
        onPlayersRevealed(player);
    }

    @EventHandler()
    public void handleDoubleJump(PlayerToggleFlightEvent ev) {
        final GamePlayer player = CF.getPlayer(ev.getPlayer());

        if (!validatePlayer(player) || hasCooldown(player)) {
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
        player.spawnWorldParticle(Particle.POOF, 5, 0.2d, 0.0d, 0.2d, 0.03f);
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
    public void processDamageAsDamager(@Nonnull DamageInstance instance) {
        final GamePlayer player = instance.getDamagerAsPlayer();
        final LivingGameEntity entity = instance.getEntity();
        final NinjaWeapon weapon = getWeapon();

        if (entity == player || player == null || !instance.isEntityAttack() || player.hasCooldown(weapon.getMaterial())) {
            return;
        }

        if (!player.isHeldSlot(HotbarSlots.WEAPON)) {
            return;
        }

        weapon.noAbilityWeapon.give(player);
        player.setCooldown(weapon.getMaterial(), weapon.stunCd);

        // Fx
        player.playWorldSound(Sound.ITEM_SHIELD_BREAK, 0.75f);
        player.spawnWorldParticle(entity.getEyeLocation(), Particle.ANGRY_VILLAGER, 5, 0.2d, 0.2d, 0.2d, 0.0f);

        // Return task
        player.schedule(weapon::give, weapon.stunCd);
    }

    @Override
    public void processDamageAsVictim(@Nonnull DamageInstance instance) {
        if (instance.getCause() == EnumDamageCause.FALL) {
            instance.setCancelled(true);
        }
    }

    @Override
    public Talent getFirstTalent() {
        return TalentRegistry.NINJA_DASH;
    }

    @Override
    public NinjaSmoke getSecondTalent() {
        return TalentRegistry.NINJA_SMOKE;
    }

    @Override
    public Talent getPassiveTalent() {
        return TalentRegistry.FLEET_FOOT;
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
            player.setUsingUltimate(false);
            player.snapToWeapon();
        }

        CFUtils.rayTraceLine(
                player,
                40,
                0.5d,
                ultimateDamage,
                EnumDamageCause.THROWING_STARS,
                location -> player.spawnWorldParticle(location, Particle.FIREWORK, 1, 0.0d, 0.0d, 0.0d, 0.015f),
                entity -> player.playWorldSound(entity.getLocation(), Sound.ITEM_TRIDENT_HIT, 2.0f)
        );

        player.playWorldSound(Sound.ITEM_TRIDENT_THROW, 1.5f);
    }

    private class NinjaUltimate extends UltimateTalent {
        public NinjaUltimate() {
            super(Ninja.this, "Throwing Stars", 70);

            setDescription("""
                    Equip &b5&7 dead-accurate &6throwing stars&7 that deal &c%.0f&7 damage upon hitting an enemy.
                    """.formatted(ultimateDamage)
            );

            setItem(Material.NETHER_STAR);
            setSound(Sound.ITEM_TRIDENT_RIPTIDE_1, 0.75f);
        }

        @Nonnull
        @Override
        public UltimateResponse useUltimate(@Nonnull GamePlayer player) {
            player.setUsingUltimate(true);

            player.setItemAndSnap(HotbarSlots.HERO_ITEM, throwingStar);
            player.setCooldown(throwingStar.getType(), 20);

            return UltimateResponse.OK;
        }
    }
}
