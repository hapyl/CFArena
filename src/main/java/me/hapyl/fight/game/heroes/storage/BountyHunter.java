package me.hapyl.fight.game.heroes.storage;

import me.hapyl.fight.event.DamageInput;
import me.hapyl.fight.event.DamageOutput;
import me.hapyl.fight.game.AbstractGamePlayer;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.heroes.ClassEquipment;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Role;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.storage.bountyhunter.GrappleHookTalent;
import me.hapyl.fight.game.talents.storage.bountyhunter.ShortyShotgun;
import me.hapyl.fight.game.talents.storage.nightmare.ShadowShift;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.ItemStacks;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.math.Tick;
import me.hapyl.spigotutils.module.player.EffectType;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;

public class BountyHunter extends Hero {

    private final ItemStack SMOKE_BOMB =
            new ItemBuilder(Material.ENDERMAN_SPAWN_EGG, "harbinger_smoke_bomb")
                    .setName("Smoke Bomb &7(Right Click)")
                    .addClickEvent(player -> useSmokeBomb(player, player.getLocation()))
                    .build();

    public BountyHunter() {
        super("Bounty Hunter", "She is a skilled bounty hunter.____&o\"Jackpot! Everyone here's got a bounty on their head.\"");

        setRole(Role.MELEE);

        setItemTexture(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2Y0Zjg2NmYxNDMyZjMyNGUzMWIwYTUwMmU2ZTllYmNjZDdhNjZmNDc0ZjFjYTljYjBjZmFiODc5ZWEyMmNlMCJ9fX0="
        );

        setWeapon(new Weapon(Material.IRON_SWORD).setName("Iron Sword").setDamage(6.0d));

        final ClassEquipment equipment = getEquipment();

        equipment.setChestplate(50, 54, 57);
        equipment.setLeggings(80, 97, 68);
        equipment.setBoots(Material.LEATHER_BOOTS);

        setUltimate(new UltimateTalent(
                "Backstab",
                "&cHARDCODED FOR TESTING; &7Instantly teleport behind target entity and backstab them, dealing 30 damage!",
                70
        )
                .setItem(Material.SHEARS)
                .setDurationSec(1)
                .defaultCdFromCost());
    }

    @Nullable
    @Override
    public DamageOutput processDamageAsVictim(DamageInput input) {
        final Player player = input.getPlayer();
        final double damage = input.getDamage();

        final AbstractGamePlayer gamePlayer = GamePlayer.getPlayer(player);
        final double health = gamePlayer.getHealth();

        if (health > 50 && (health - damage <= (gamePlayer.getMaxHealth() / 2.0d))) {
            final PlayerInventory inventory = player.getInventory();

            inventory.setItem(4, SMOKE_BOMB);
            Chat.sendTitle(player, "", "&aSmoke Bomb triggered!", 5, 20, 5);
        }

        return null;
    }

    @Override
    public void useUltimate(Player player) {
        final ShadowShift.TargetLocation targetOutput = getBackstabLocation(player);

        if (targetOutput.getError() != ShadowShift.ErrorCode.OK) {
            return; // should never happen
        }

        final Location playerLocation = player.getLocation();
        final Location location = targetOutput.getLocation();
        final LivingEntity target = targetOutput.getEntity();

        player.teleport(location);
        GamePlayer.damageEntity(target, 30.0d, player, EnumDamageCause.BACKSTAB);

        // Fx
        Chat.sendMessage(player, "&aBackstabbed &7%s&a!", target.getName());
        Chat.sendMessage(target, "&cYou were backstabbed by &7%s&c!", player.getName());

        PlayerLib.playSound(location, Sound.ENTITY_ENDER_DRAGON_FLAP, 0.0f);
        PlayerLib.playSound(location, Sound.ENTITY_IRON_GOLEM_REPAIR, 1.25f);

        player.swingMainHand();

        useSmokeBomb(player, playerLocation);
    }

    @Override
    public boolean predicateUltimate(Player player) {
        final ShadowShift.TargetLocation location = getBackstabLocation(player);

        return location.getError() == ShadowShift.ErrorCode.OK;
    }

    @Override
    public String predicateMessage(Player player) {
        final ShadowShift.TargetLocation location = getBackstabLocation(player);
        return location.getError().getErrorMessage();
    }

    private ShadowShift.TargetLocation getBackstabLocation(Player player) {
        return ((ShadowShift) Talents.SHADOW_SHIFT.getTalent()).getLocationAndCheck0(player, 15.0d, 0.9d);
    }

    private void useSmokeBomb(Player player, Location location) {
        player.getInventory().setItem(4, ItemStacks.AIR);

        final double smokeRadius = 3.0d;
        final double smokeRadiusScaled = (smokeRadius * smokeRadius) / 8.0d;
        final int smokeDuration = Tick.fromSecond(5);

        PlayerLib.addEffect(player, PotionEffectType.SPEED, smokeDuration, 2);

        // Fx and blindness
        GameTask.runTaskTimerTimes(task -> {
            Utils.getPlayersInRange(location, 3.0d).forEach(victim -> {
                PlayerLib.addEffect(victim, EffectType.BLINDNESS, 25, 1);
            });

            PlayerLib.spawnParticle(location, Particle.SQUID_INK, 20, smokeRadiusScaled, smokeRadiusScaled, smokeRadiusScaled, 0.01f);
        }, 0, 1, smokeDuration);
    }

    @Override
    public ShortyShotgun getFirstTalent() {
        return (ShortyShotgun) Talents.SHORTY.getTalent();
    }

    @Override
    public GrappleHookTalent getSecondTalent() {
        return (GrappleHookTalent) Talents.GRAPPLE.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.SMOKE_BOMB.getTalent();
    }
}
