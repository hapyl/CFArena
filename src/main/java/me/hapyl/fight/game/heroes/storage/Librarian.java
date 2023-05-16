package me.hapyl.fight.game.heroes.storage;

import me.hapyl.fight.annotate.KeepNull;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.storage.extra.Grimoire;
import me.hapyl.fight.game.heroes.storage.extra.GrimoireBook;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.storage.extra.LibrarianTalent;
import me.hapyl.fight.game.talents.storage.librarian.EntityDarkness;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Nulls;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.chat.Gradient;
import me.hapyl.spigotutils.module.chat.gradient.Interpolators;
import me.hapyl.spigotutils.module.player.EffectType;
import me.hapyl.spigotutils.module.player.PlayerLib;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

/**
 * This hero is pain in the ass...
 * Slots are hardcoded, make sure not to change them.
 */
public class Librarian extends Hero implements ComplexHero, Listener, DisabledHero {

    private final Map<Integer, LibrarianTalent> talentMap = new HashMap<>();
    private final Map<Player, Grimoire> grimoireMap = new HashMap<>();
    private final String grimoireLevelUpGradient = new Gradient("Grimoire Level Up!")
            .makeBold()
            .rgb(ChatColor.RED.getColor(), ChatColor.BLUE.getColor(), Interpolators.LINEAR);

    private final String grimoireIsMaxGradient = new Gradient("Grimoire Maxed!")
            .rgb(ChatColor.RED.getColor(), ChatColor.BLUE.getColor(), Interpolators.LINEAR);

    public Librarian() {
        super("Librarian of Void", "Mislead by the &0void&7, sacrifices were made.", Material.BOOK);

        talentMap.put(1, (LibrarianTalent) Talents.BLACK_HOLE.getTalent());
        talentMap.put(2, (LibrarianTalent) Talents.ENTITY_DARKNESS.getTalent());
        talentMap.put(3, (LibrarianTalent) Talents.LIBRARIAN_SHIELD.getTalent());
        talentMap.put(4, (LibrarianTalent) Talents.WEAPON_DARKNESS.getTalent());

        setRole(Role.STRATEGIST);
        setItem("a88b1cd9574672e8e3262f210c0dddbc082ea7569e8e70f0c07b4bee75e32f62");

        final HeroEquipment equipment = getEquipment();
        equipment.setChestplate(47, 32, 40);
        equipment.setLeggings(Material.NETHERITE_LEGGINGS);
        equipment.setBoots(84, 37, 62);

        setWeapon(new Weapon(Material.NETHERITE_SHOVEL).setName("Staff").setDamage(7.5d));

        setUltimate(new UltimateTalent(
                "Void of Blindness",
                "Create massive void of blindness field for <duration>. Everyone who dares steps inside, will be affected by paranoia and glow. Librarian also gets a &c&ldamage &7and &b&lspeed &7boost.",
                70
        ).setItem(Material.SQUID_SPAWN_EGG).setDuration(240));
    }

    @Override
    public void onStart(Player player) {
        grimoireMap.put(player, new Grimoire(player));
        giveGrimoireBook(player);
    }

    @Override
    public void onDeath(Player player) {
    }

    @Override
    public void onStart() {
        final int grimoireLevelUpDelay = 900; // 40

        new GameTask() {
            @Override
            public void run() {
                Manager.current().getCurrentGame().getAlivePlayers(Heroes.LIBRARIAN).forEach(player -> {
                    final Player playerPlayer = player.getPlayer();
                    final Grimoire grimoire = grimoireMap.get(playerPlayer);

                    if (grimoire.isMaxed()) {
                        return;
                    }

                    grimoire.nextBook();

                    // Update book if not using spells
                    final PlayerInventory inventory = playerPlayer.getInventory();
                    final ItemStack item = inventory.getItem(1);
                    if (GrimoireBook.isGrimmoreItem(item)) {
                        inventory.setItem(1, grimoire.getCurrentBook().getItem());
                    }

                    // Fx
                    Chat.sendTitle(
                            playerPlayer,
                            "",
                            grimoire.isMaxed() ? grimoireIsMaxGradient : grimoireLevelUpGradient,
                            5,
                            15,
                            5
                    );
                    PlayerLib.playSound(playerPlayer, Sound.ENTITY_PLAYER_LEVELUP, 1.75f);
                });
            }
        }.runTaskTimer(grimoireLevelUpDelay, grimoireLevelUpDelay);
    }

    public int getGrimoireLevel(Player player) {
        return grimoireMap.getOrDefault(player, new Grimoire(player)).getUsedAtLevel();
    }

    @Override
    public void useUltimate(Player player) {
        final Location castLocation = player.getLocation().add(0.0d, 0.5d, 0.0d);
        PlayerLib.playSound(castLocation, Sound.ENTITY_SQUID_SQUIRT, 0.0f);

        new GameTask() {
            private int tick = getUltimateDuration();

            @Override
            public void run() {
                if ((tick -= 10) <= 0) {
                    this.cancel();
                    return;
                }

                PlayerLib.spawnParticle(castLocation, Particle.SQUID_INK, 50, 5, 1.5, 5, 0.05f);
                PlayerLib.spawnParticle(castLocation, Particle.SQUID_INK, 10, 5, 1.5, 5, 2.0f);

                Utils.getEntitiesInRange(castLocation, 20).forEach(entity -> {
                    if (entity == player) {
                        PlayerLib.addEffect(player, EffectType.SPEED, 20, 1);
                        PlayerLib.addEffect(player, EffectType.STRENGTH, 20, 0);
                    }
                    else {
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 30, 1));
                    }
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 1));
                });

            }
        }.runTaskTimer(0, 10);
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.BLACK_HOLE.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.ENTITY_DARKNESS.getTalent();
    }

    @Override
    public Talent getThirdTalent() {
        return Talents.LIBRARIAN_SHIELD.getTalent();
    }

    @Override
    public Talent getFourthTalent() {
        return Talents.WEAPON_DARKNESS.getTalent();
    }

    @Override
    @KeepNull
    public Talent getFifthTalent() {
        return Talent.NULL;
    }

    @Override
    public Talent getPassiveTalent() {
        return null;
    }

    public void grantSpellItems(Player player) {
        final PlayerInventory inventory = player.getInventory();
        applyICD(player);

        talentMap.forEach((slot, talent) -> {
            inventory.setItem(slot, talent.getItem());
            if (slot == 2) {
                Nulls.runIfNotNull(inventory.getItem(2), item -> item.setAmount(3));
            }
        });

        // Fx
        PlayerLib.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.75f);
        PlayerLib.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.25f);
    }

    public boolean hasICD(Player player) {
        return player.hasCooldown(getItem().getType());
    }

    public void applyICD(Player player) {
        player.setCooldown(getItem().getType(), 10);
    }

    public void removeSpellItems(Player player, LibrarianTalent talent) {
        final PlayerInventory inventory = player.getInventory();

        for (int i = 1; i <= 4; i++) {
            final ItemStack item = inventory.getItem(i);

            if (item != null) {
                item.setAmount(item.getAmount() - (talent instanceof EntityDarkness ? 1 : 3));
            }
        }

        if (inventory.contains(talent.getMaterial())) {
            return;
        }

        GrimoireBook.applyCooldown(player, (talent.getGrimoireCd() * 20));
        giveGrimoireBook(player);
    }

    public void giveGrimoireBook(Player player) {
        final PlayerInventory inventory = player.getInventory();
        final Grimoire grimoire = grimoireMap.get(player);

        if (grimoire != null) {
            inventory.setItem(1, grimoire.getCurrentBook().getItem());
        }
    }

    private int indexOfTalent(Talents enumTalent) {
        return switch (enumTalent) {
            default -> 1;
            case ENTITY_DARKNESS -> 2;
            case LIBRARIAN_SHIELD -> 3;
            case WEAPON_DARKNESS -> 4;
        };
    }

    @EventHandler()
    public void handlePlayerInteract(PlayerItemHeldEvent ev) {
        final Player player = ev.getPlayer();
        if (!validatePlayer(player) || ev.getNewSlot() != 1) {
            return;
        }

        final ItemStack item = player.getInventory().getItem(ev.getNewSlot());
        if (!GrimoireBook.isGrimmoreItem(item)) {
            return;
        }

        final Grimoire grimoire = grimoireMap.get(player);
        if (GrimoireBook.hasCooldown(player) || grimoire == null) {
            Chat.sendMessage(
                    player,
                    "&cCannot use Grimoire now! On cooldown for %ss.".formatted(GrimoireBook.getCooldownString(player))
            );
            return;
        }

        grimoire.markUsedNow();
        grantSpellItems(player);
        player.getInventory().setHeldItemSlot(0);
    }
}
