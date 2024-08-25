package me.hapyl.fight.game.heroes.bounty_hunter;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.fight.CF;
import me.hapyl.fight.annotate.StrictTalentPlacement;

import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.bounty_hunter.GrappleHookTalent;
import me.hapyl.fight.game.talents.bounty_hunter.ShortyShotgun;
import me.hapyl.fight.game.talents.nightmare.ShadowShift;
import me.hapyl.fight.game.task.TimedGameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.registry.Key;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.fight.util.displayfield.DisplayFieldProvider;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import javax.annotation.Nonnull;

public class BountyHunter extends Hero implements DisplayFieldProvider {

    private final double smokeRadius = 3.0d;
    private final double smokeRadiusScaled = (smokeRadius * smokeRadius) / 8.0d;
    private final int smokeDuration = Tick.fromSecond(5);

    public final ItemStack smokeBomb = new ItemBuilder(Material.ENDERMAN_SPAWN_EGG, "bounty_hunter_smoke_bomb")
            .setName("💣💣💣 " + Color.BUTTON + " (Right Click)")
            .addClickEvent(player -> {
                final GamePlayer gamePlayer = CF.getPlayer(player);

                if (gamePlayer == null) {
                    return;
                }

                useSmokeBomb(gamePlayer, gamePlayer.getLocation());
            }).build();

    @DisplayField private final double backstabMaxDistance = 15;
    @DisplayField private final double backstabDamage = 30;

    public BountyHunter(@Nonnull Key key) {
        super(key, "Bounty Hunter");

        setAffiliation(Affiliation.MERCENARY);
        setArchetypes(Archetype.MOBILITY);
        setGender(Gender.FEMALE);

        setDescription("""
                She is a skilled bounty hunter.
                
                &8&o;;`Jackpot! Everyone here's got a bounty on their head.`
                """);
        setItem("cf4f866f1432f324e31b0a502e6e9ebccd7a66f474f1ca9cb0cfab879ea22ce0");

        setWeapon(
                new Weapon(Material.IRON_SWORD)
                        .setName("Bloodweep")
                        .setDescription("A handy sword that appeared in her dream.")
                        .setDamage(6.0d)
        );

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(50, 54, 57, TrimPattern.SILENCE, TrimMaterial.NETHERITE);
        equipment.setLeggings(80, 97, 68);
        equipment.setBoots(160, 101, 64, TrimPattern.SILENCE, TrimMaterial.IRON);

        setUltimate(new BountyHunterUltimate());
        copyDisplayFieldsTo(getUltimate());
    }

    @Override
    public void processDamageAsVictim(@Nonnull DamageInstance instance) {
        final GamePlayer player = instance.getEntityAsPlayer();
        final double damage = instance.getDamage();
        final double health = player.getHealth();

        // FIXME (hapyl): 008, Mar 8: what the fuck
        if (health > 50 && (health - damage <= (player.getMaxHealth() / 2.0d))) {
            player.setItem(HotbarSlots.HERO_ITEM, smokeBomb);
            player.sendTitle("&7💣", "&e&lSMOKE BOMB TRIGGERED", 5, 20, 5);
        }
    }

    @Override
    @StrictTalentPlacement
    public ShortyShotgun getFirstTalent() {
        return TalentRegistry.SHORTY;
    }

    @Override
    public GrappleHookTalent getSecondTalent() {
        return TalentRegistry.GRAPPLE;
    }

    @Override
    public Talent getPassiveTalent() {
        return TalentRegistry.SMOKE_BOMB;
    }

    private void spawnPoofParticle(Location location) {
        PlayerLib.spawnParticle(location, Particle.LARGE_SMOKE, 20, 0.0d, 0.5d, 0.0d, 0.25f);
    }

    private ShadowShift.TargetLocation getBackstabLocation(GamePlayer player) {
        return ((ShadowShift) TalentRegistry.SHADOW_SHIFT).getLocationAndCheck0(player, backstabMaxDistance, 0.9d);
    }

    private void useSmokeBomb(GamePlayer player, Location location) {
        player.setItem(HotbarSlots.HERO_ITEM, null);
        player.addEffect(Effects.SPEED, 2, smokeDuration);

        player.snapToWeapon();

        new TimedGameTask(smokeDuration) {
            @Override
            public void run(int tick) {
                Collect.nearbyPlayers(location, smokeRadius).forEach(inRange -> {
                    inRange.addEffect(Effects.BLINDNESS, 1, 25);
                    inRange.addEffect(Effects.INVISIBILITY, 25, true);
                });

                // Fx
                player.spawnWorldParticle(
                        location,
                        Particle.LARGE_SMOKE,
                        20,
                        smokeRadiusScaled,
                        smokeRadiusScaled,
                        smokeRadiusScaled,
                        0.01f
                );
                player.spawnWorldParticle(
                        location,
                        Particle.SMOKE,
                        20,
                        smokeRadiusScaled,
                        smokeRadiusScaled,
                        smokeRadiusScaled,
                        0.01f
                );
            }
        }.runTaskTimer(0, 1);

        // Sfx
        player.playWorldSound(Sound.BLOCK_FIRE_EXTINGUISH, 0.75f);
    }

    private class BountyHunterUltimate extends UltimateTalent {
        public BountyHunterUltimate() {
            super(BountyHunter.this, "Backstab", 70);

            setDescription("""
                    Instantly &bteleport&7 behind the &etarget&7 player, &cstabbing&7 them from behind.
                    """);

            setItem(Material.SHEARS);
            setDurationSec(1);
            defaultCdFromCost();
        }

        @Nonnull
        @Override
        public UltimateResponse useUltimate(@Nonnull GamePlayer player) {
            final ShadowShift.TargetLocation targetOutput = getBackstabLocation(player);

            if (targetOutput.getError() != ShadowShift.ErrorCode.OK) {
                return UltimateResponse.error(targetOutput.getError().getErrorMessage());
            }

            final Location playerLocation = player.getLocation();
            final Location location = targetOutput.getLocation();
            final LivingGameEntity target = targetOutput.getEntity();

            player.teleport(location);
            target.damage(backstabDamage, player, EnumDamageCause.BACKSTAB);

            // Fx
            player.sendMessage("&aBackstabbed &7%s&a!".formatted(target.getName()));
            target.sendMessage("&cYou were backstabbed by &7%s&c!".formatted(player.getName()));

            player.playWorldSound(location, Sound.ENTITY_ENDER_DRAGON_FLAP, 0.0f);
            player.playWorldSound(location, Sound.ENTITY_IRON_GOLEM_REPAIR, 1.25f);

            player.swingMainHand();

            spawnPoofParticle(playerLocation);
            spawnPoofParticle(location);

            return UltimateResponse.OK;
        }
    }
}
