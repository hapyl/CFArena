package me.hapyl.fight.game.heroes.archive.techie;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.IGameInstance;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroEquipment;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.Role;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.techie.TrapCage;
import me.hapyl.fight.game.talents.archive.techie.TrapWire;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Techie extends Hero implements UIComplexComponent, Listener {

    private final Set<Lockdown> lockdownSet = new HashSet<>();

    public final int LOCKDOWN_WINDUP_TIME = 200;
    public final int LOCKDOWN_RADIUS = 20;
    public final int LOCKDOWN_AFFECT_TIME = 170;

    private final int neuralTheftPeriod = 200;

    /**
     * RECORD IDEAS:
     * <ul>
     *     <li>Skill that tags with a BUG.</li>
     *     <li>Move reveal to skill that also damaged and weakens, like a trap.</li>
     * </ul>
     *
     * Ult still IDK
     */

    public Techie() {
        super("Techie");

        setRole(Role.STRATEGIST);

        setInfo(
                "Anonymous hacker, who hacked his way to the fight. Weak by himself, but specifies on traps that make him stronger."
        );

        setItem("1e5b78987c70d73f2ad93a454f85dcab476c5b5679f50eaaf553d2404edc9c");

        final HeroEquipment equipment = getEquipment();
        equipment.setChestplate(205, 205, 205);
        equipment.setLeggings(217, 217, 217);
        equipment.setBoots(255, 230, 204);

        setWeapon(new Weapon(Material.IRON_SWORD).setName("Nano Sword")
                .setDamage(7.0d)
                .addEnchant(Enchantment.KNOCKBACK, 1));

        // FIXME (hapyl): 0016, May 16, 2023: Lockdown sucks balls
        setUltimate(new UltimateTalent("Lockdown",
                String.format(
                        "Place a device that charges over &b%ss&7. When charged, explodes and affects all opponents in &b%s &7blocks radius by &6&lLockdown &7for &b%ss&7.____%s____&cThis ability can be destroyed!",
                        GameEffectType.LOCK_DOWN.getGameEffect().getDescription(),
                        BukkitUtils.roundTick(LOCKDOWN_WINDUP_TIME),
                        LOCKDOWN_RADIUS,
                        BukkitUtils.roundTick(LOCKDOWN_AFFECT_TIME)
                ), 60
        ).setItem(Material.DAYLIGHT_DETECTOR).setSound(Sound.BLOCK_BELL_USE, 0.0f));

        getUltimate().addAttributeDescription("Lockdown Health", Lockdown.HEALTH);
    }

    @Override
    public void onStop() {
        lockdownSet.forEach(Lockdown::remove);
        lockdownSet.clear();
    }

    @Override
    public void onStart() {
        new GameTask() {
            @Override
            public void run() {
                Heroes.TECHIE.getAlivePlayers().forEach(player -> {
                    int amountRevealed = 0;
                    final IGameInstance game = Manager.current().getCurrentGame();

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
        //Glowing.stopGlowing(revealed); fixme -> This is not needed?

        Glowing.glow(revealed, ChatColor.AQUA, 20, player);

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
    public String predicateMessage(Player player) {
        return "Location is obstructed.";
    }

    @Override
    public boolean predicateUltimate(Player player) {
        return player.getLocation().getBlock().getType().isAir();
    }

    @Override
    public TrapCage getFirstTalent() {
        return (TrapCage) Talents.TRAP_CAGE.getTalent();
    }

    @Override
    public TrapWire getSecondTalent() {
        return (TrapWire) Talents.TRAP_WIRE.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.NEURAL_THEFT.getTalent();
    }

    @Override
    public List<String> getStrings(Player player) {
        return List.of("&f⁂ &l" + getFirstTalent().getCages(player).size(), "&f⁑ &l" + getSecondTalent().getTraps(player).size());
    }

}
