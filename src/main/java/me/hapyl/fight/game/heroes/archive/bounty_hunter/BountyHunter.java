package me.hapyl.fight.game.heroes.archive.bounty_hunter;

import me.hapyl.fight.CF;
import me.hapyl.fight.annotate.StrictTalentPlacement;
import me.hapyl.fight.event.io.DamageInput;
import me.hapyl.fight.event.io.DamageOutput;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Affiliation;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.UltimateCallback;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.bounty_hunter.GrappleHookTalent;
import me.hapyl.fight.game.talents.archive.bounty_hunter.ShortyShotgun;
import me.hapyl.fight.game.talents.archive.nightmare.ShadowShift;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.task.TimedGameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.fight.util.displayfield.DisplayFieldProvider;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.math.Tick;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

    public BountyHunter() {
        super("Bounty Hunter", """
                She is a skilled bounty hunter.
                                
                &8&o;;"Jackpot! Everyone here's got a bounty on their head."
                """);

        setAffiliation(Affiliation.MERCENARY);
        setArchetype(Archetype.MOBILITY);

        setItem("cf4f866f1432f324e31b0a502e6e9ebccd7a66f474f1ca9cb0cfab879ea22ce0");

        setWeapon(new Weapon(Material.IRON_SWORD).setName("Iron Sword").setDamage(6.0d));

        final HeroAttributes attributes = getAttributes();
        attributes.setDefense(100);

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(50, 54, 57, TrimPattern.SILENCE, TrimMaterial.NETHERITE);
        equipment.setLeggings(80, 97, 68);
        equipment.setBoots(160, 101, 64, TrimPattern.SILENCE, TrimMaterial.IRON);

        setUltimate(new UltimateTalent(
                "Backstab", """
                Instantly &bteleport&7 behind the &etarget&7 player, &cstabbing&7 them from behind.
                """, 70
        )
                .setItem(Material.SHEARS)
                .setDurationSec(1)
                .defaultCdFromCost());

        copyDisplayFieldsTo(getUltimate());
    }

    @Nullable
    @Override
    public DamageOutput processDamageAsVictim(DamageInput input) {
        final GamePlayer player = input.getEntityAsPlayer();
        final double damage = input.getDamage();
        final double health = player.getHealth();

        if (health > 50 && (health - damage <= (player.getMaxHealth() / 2.0d))) {
            player.setItem(HotbarSlots.HERO_ITEM, smokeBomb);
            player.sendTitle("&7💣", "&e&lSMOKE BOMB TRIGGERED", 5, 20, 5);
        }

        return null;
    }

    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        final ShadowShift.TargetLocation targetOutput = getBackstabLocation(player);

        if (targetOutput.getError() != ShadowShift.ErrorCode.OK) {
            return UltimateCallback.OK; // should never happen
        }

        final Location playerLocation = player.getLocation();
        final Location location = targetOutput.getLocation();
        final LivingGameEntity target = targetOutput.getEntity();

        player.teleport(location);
        target.damage(backstabDamage, player, EnumDamageCause.BACKSTAB);

        // Fx
        player.sendMessage("&aBackstabbed &7%s&a!", target.getName());
        target.sendMessage("&cYou were backstabbed by &7%s&c!", player.getName());

        player.playWorldSound(location, Sound.ENTITY_ENDER_DRAGON_FLAP, 0.0f);
        player.playWorldSound(location, Sound.ENTITY_IRON_GOLEM_REPAIR, 1.25f);

        player.swingMainHand();

        spawnPoofParticle(playerLocation);
        spawnPoofParticle(location);

        return UltimateCallback.OK;
    }

    @Override
    public boolean predicateUltimate(@Nonnull GamePlayer player) {
        final ShadowShift.TargetLocation location = getBackstabLocation(player);

        return location.getError() == ShadowShift.ErrorCode.OK;
    }

    @Override
    public String predicateMessage(@Nonnull GamePlayer player) {
        final ShadowShift.TargetLocation location = getBackstabLocation(player);
        return location.getError().getErrorMessage();
    }

    @Override
    @StrictTalentPlacement
    public ShortyShotgun getFirstTalent() {
        return (ShortyShotgun) Talents.SHORTY.getTalent().setTalentSlot(HotbarSlots.TALENT_1);
    }

    @Override
    public GrappleHookTalent getSecondTalent() {
        return (GrappleHookTalent) Talents.GRAPPLE.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.SMOKE_BOMB.getTalent();
    }

    private void spawnPoofParticle(Location location) {
        PlayerLib.spawnParticle(location, Particle.SMOKE_LARGE, 20, 0.0d, 0.5d, 0.0d, 0.25f);
    }

    private ShadowShift.TargetLocation getBackstabLocation(GamePlayer player) {
        return ((ShadowShift) Talents.SHADOW_SHIFT.getTalent()).getLocationAndCheck0(player, backstabMaxDistance, 0.9d);
    }

    private void useSmokeBomb(GamePlayer player, Location location) {
        player.setItem(HotbarSlots.HERO_ITEM, null);
        player.addPotionEffect(PotionEffectType.SPEED, smokeDuration, 2);

        new TimedGameTask(smokeDuration) {
            @Override
            public void run(int tick) {
                Collect.nearbyPlayers(location, 3.0d).forEach(inRange -> {
                    inRange.addPotionEffect(PotionEffectType.BLINDNESS, 25, 1);
                    inRange.addEffect(GameEffectType.INVISIBILITY, 25, true);
                });

                // Fx
                player.spawnWorldParticle(
                        location,
                        Particle.SMOKE_LARGE,
                        20,
                        smokeRadiusScaled,
                        smokeRadiusScaled,
                        smokeRadiusScaled,
                        0.01f
                );
                player.spawnWorldParticle(
                        location,
                        Particle.SMOKE_NORMAL,
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
}
