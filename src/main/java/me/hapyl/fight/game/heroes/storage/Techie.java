package me.hapyl.fight.game.heroes.storage;

import me.hapyl.fight.game.AbstractGameInstance;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.heroes.ClassEquipment;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.Role;
import me.hapyl.fight.game.heroes.storage.extra.Lockdown;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentHandle;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComplexComponent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.hologram.Hologram;
import me.hapyl.spigotutils.module.reflect.glow.Glowing;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashSet;
import java.util.Set;

public class Techie extends Hero implements UIComplexComponent, Listener {

    private final Set<Lockdown> lockdownSet = new HashSet<>();

    public final int lockdownWindupTime = 200;
    public final int lockdownRadius = 20;
    public final int lockdownAffectTime = 170;

    private final int neuralTheftPeriod = 200;

    public Techie() {
        super("Techie");

        setRole(Role.STRATEGIST);

        this.setInfo(
                "Anonymous hacker, who hacked his way to the fight. Weak by himself, but specifies on traps that makes him stronger.");
        this.setItem(Material.IRON_TRAPDOOR);

        final ClassEquipment equipment = this.getEquipment();
        equipment.setHelmet(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWU1Yjc4OTg3YzcwZDczZjJhZDkzYTQ1NGY4NWRjYWI0NzZjNWI1Njc5ZjUwZWFhZjU1M2QyNDA0ZWRjOWMifX19"
        );
        equipment.setChestplate(205, 205, 205);
        equipment.setLeggings(217, 217, 217);
        equipment.setBoots(255, 230, 204);

        this.setWeapon(new Weapon(Material.IRON_SWORD).setName("Nano Sword")
                                                      .setDamage(7.0d)
                                                      .addEnchant(Enchantment.KNOCKBACK, 1));

        this.setUltimate(new UltimateTalent("Lockdown",
                                            String.format(
                                                    "Place a device that charges over &b%ss&7. When charged, explodes and affects all opponents in &b%s &7blocks radius by &6&lLockdown &7for &b%ss&7.__&c&lThe device can be broken.",
                                                    BukkitUtils.roundTick(lockdownWindupTime),
                                                    lockdownRadius,
                                                    BukkitUtils.roundTick(lockdownAffectTime)
                                            ), 60
        ).setItem(Material.DAYLIGHT_DETECTOR).setSound(Sound.BLOCK_BELL_USE, 0.0f));

    }

    @Override
    public void onStop() {
        lockdownSet.forEach(Lockdown::remove);
        lockdownSet.clear();
    }

    @EventHandler()
    public void handleMovement(PlayerMoveEvent ev) {
        final Player player = ev.getPlayer();
        if (Manager.current().isGameInProgress() && GamePlayer.getPlayer(player).hasEffect(GameEffectType.LOCK_DOWN)) {
            ev.setCancelled(true);
            Chat.sendTitle(player, "&c&lLOCKDOWN", "&cUnable to move!", 0, 20, 0);
        }
    }

    @Override
    public void onStart() {
        new GameTask() {
            @Override
            public void run() {
                Heroes.TECHIE.getAlivePlayers().forEach(player -> {
                    int amountRevealed = 0;
                    final AbstractGameInstance game = Manager.current().getCurrentGame();

                    for (final GamePlayer alive : game.getAlivePlayers()) {
                        if (alive.compare(player)) {
                            continue;
                        }

                        ++amountRevealed;
                        revealPlayer(player.getPlayer(), alive.getPlayer());
                    }

                    if (amountRevealed > 0) {
                        player.sendTitle("", "&l%s &fPlayers Revealed".formatted(amountRevealed), 5, 15, 5);
                        player.playSound(Sound.ENCHANT_THORNS_HIT, 2.0f);
                    }

                });
            }
        }.runTaskTimer(neuralTheftPeriod, neuralTheftPeriod);
    }

    @EventHandler()
    public void handleEntityDamage(EntityDamageEvent ev) {
        final Entity entity = ev.getEntity();
        if (!(entity instanceof LivingEntity living) || !Lockdown.isLockdownEntity(living)) {
            return;
        }

        ev.setCancelled(true);
        living.setHealth(living.getHealth() - ev.getDamage());

        if (living.getHealth() <= 0.0d) {
            living.remove();
        }

    }

    private void revealPlayer(Player player, LivingEntity revealed) {
        // Glowing
        final Glowing glowing = new Glowing(revealed, ChatColor.AQUA, 20);
        glowing.addPlayer(player);
        glowing.glow();

        // If revealed not player don't show health.
        if (!(revealed instanceof Player revealedPlayer)) {
            return;
        }

        // Don't show health if further than 10 blocks, will not be visible
        if (revealed.getLocation().distance(player.getLocation()) > 10.0d) {
            return;
        }

        // Health
        final Hologram hologram = new Hologram();
        hologram.addLine("&a&l" + revealed.getName())
                .addLine("&c&l%s &c❤".formatted(GamePlayer.getPlayer(revealedPlayer).getHealthFormatted()))
                .create(revealed.getEyeLocation())
                .show(player);

        new GameTask() {
            private int tick = 30;

            @Override
            public void run() {
                if (tick < 0) {
                    hologram.destroy();
                    this.cancel();
                    return;
                }

                hologram.teleport(revealed.getEyeLocation().add(0.0d, 0.25d, 0.0d));
                --tick;
            }
        }.addCancelEvent(hologram::destroy).runTaskTimer(0, 1);
    }

    @Override
    public void useUltimate(Player player) {
        final Location location = player.getLocation();

        if (!location.getBlock().getType().isAir()) {
            return;
        }

        lockdownSet.add(new Lockdown(player));
        Chat.sendMessage(player, "&aCountdown initiated!");

    }

    @Override
    public String predicateMessage() {
        return "Location is obstructed.";
    }

    @Override
    public boolean predicateUltimate(Player player) {
        return player.getLocation().getBlock().getType().isAir();
    }


    @Override
    public Talent getFirstTalent() {
        return Talents.TRAP_CAGE.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.TRAP_WIRE.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.NEURAL_THEFT.getTalent();
    }

    @Override
    public String[] getStrings(Player player) {
        return new String[] { "&f⁂ &l" + TalentHandle.TRAP_CAGE.getCages(player).size(),
                              "&f⁑ &l" + TalentHandle.TRAP_WIRE.getTraps(player).size() };
    }

}
