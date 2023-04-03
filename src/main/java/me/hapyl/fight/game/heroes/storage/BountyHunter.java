package me.hapyl.fight.game.heroes.storage;

import me.hapyl.fight.event.DamageInput;
import me.hapyl.fight.event.DamageOutput;
import me.hapyl.fight.game.AbstractGamePlayer;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.heroes.ClassEquipment;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.storage.bountyhunter.GrappleHookTalent;
import me.hapyl.fight.game.talents.storage.bountyhunter.ShortyShotgun;
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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;

public class BountyHunter extends Hero {

    /**
     * [✔] Weapon:
     * IRON SWORD
     * - If combo x4 same target no miss:
     * -- Bleed applied (5damage over 5s)
     *
     * [✔] Ability 1:
     * - Shorty
     * - Knocks
     * - When VERY CLOSE RANGE DAMAGE (<1block to enemy):
     * -- Reset CD
     * -- Vulnerability for 5s
     * - 2.5s
     *
     * [✔] Ability 2 (Hook):
     * - Lead or fishing rod if possible
     * - 10 HP
     * - Can hook to block to get closer.
     * - Can hook to players to get closer.
     * - If player is hooked they can get out by hitting it.
     *
     * [✔] Passive:
     * - When <50% health gets
     * - Speed buff.
     * - Blindness.
     * - Cd until 50% health
     *
     * ULTIMATE (Backstab):
     * - Dagger
     * - Click at target tp behind deal 30 damage
     */

    private final ItemStack SMOKE_BOMB =
            new ItemBuilder(Material.ENDERMAN_SPAWN_EGG, "harbinger_smoke_bomb")
                    .setName("Smoke Bomb &7(Right Click)")
                    .addClickEvent(this::useSmokeBomb)
                    .build();

    public BountyHunter() {
        super("Bounty Hunter", "She is a skilled bounty hunter.____&o\"Jackpot! Everyone here's got a bounty on their head.\"");

        setItemTexture(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2Y0Zjg2NmYxNDMyZjMyNGUzMWIwYTUwMmU2ZTllYmNjZDdhNjZmNDc0ZjFjYTljYjBjZmFiODc5ZWEyMmNlMCJ9fX0=");

        setWeapon(new Weapon(Material.IRON_SWORD).setName("Iron Sword").setDamage(6.0d));

        final ClassEquipment equipment = getEquipment();

        equipment.setChestplate(50, 54, 57);
        equipment.setLeggings(80, 97, 68);
        equipment.setBoots(Material.LEATHER_BOOTS);
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

    }

    private void useSmokeBomb(Player player) {
        final Location location = player.getLocation();
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
