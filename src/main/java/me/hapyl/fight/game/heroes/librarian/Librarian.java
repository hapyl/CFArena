package me.hapyl.fight.game.heroes.librarian;

import me.hapyl.fight.CF;
import me.hapyl.fight.annotate.KeepNull;
import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.heroes.UltimateResponse;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.librarian.EntityDarkness;
import me.hapyl.fight.game.talents.librarian.LibrarianTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.Nulls;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.chat.Gradient;
import me.hapyl.spigotutils.module.chat.gradient.Interpolators;
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
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * This hero is pain in the ass...
 * Slots are hardcoded, make sure not to change them.
 */
public class Librarian extends Hero implements ComplexHero, Listener, Disabled {

    private final Map<Integer, LibrarianTalent> talentMap = new HashMap<>();
    private final PlayerMap<Grimoire> grimoireMap = PlayerMap.newMap();
    private final String grimoireLevelUpGradient = new Gradient("Grimoire Level Up!")
            .makeBold()
            .rgb(ChatColor.RED.getColor(), ChatColor.BLUE.getColor(), Interpolators.LINEAR);

    private final String grimoireIsMaxGradient = new Gradient("Grimoire Maxed!")
            .rgb(ChatColor.RED.getColor(), ChatColor.BLUE.getColor(), Interpolators.LINEAR);

    public Librarian(@Nonnull Heroes handle) {
        super(handle, "Librarian of Void");

        setDescription("""
                Mislead by the &0void&7, sacrifices were made.
                """);

        setItem(Material.BOOK);
        setGender(Gender.MALE);

        talentMap.put(1, (LibrarianTalent) Talents.BLACK_HOLE.getTalent());
        talentMap.put(2, (LibrarianTalent) Talents.ENTITY_DARKNESS.getTalent());
        talentMap.put(3, (LibrarianTalent) Talents.LIBRARIAN_SHIELD.getTalent());
        talentMap.put(4, (LibrarianTalent) Talents.WEAPON_DARKNESS.getTalent());

        setItem("a88b1cd9574672e8e3262f210c0dddbc082ea7569e8e70f0c07b4bee75e32f62");

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(47, 32, 40, TrimPattern.WARD, TrimMaterial.GOLD);
        equipment.setLeggings(Material.NETHERITE_LEGGINGS);
        equipment.setBoots(84, 37, 62);

        setWeapon(new Weapon(Material.NETHERITE_SHOVEL).setName("Staff").setDamage(7.5d));

    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        grimoireMap.put(player, new Grimoire(player));
        giveGrimoireBook(player);
    }

    @Override
    public void onStart() {
        final int grimoireLevelUpDelay = 900; // 40

        new GameTask() {
            @Override
            public void run() {
                CF.getAlivePlayers(Heroes.LIBRARIAN).forEach(player -> {
                    final Player playerPlayer = player.getPlayer();
                    final Grimoire grimoire = grimoireMap.get(player);

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

    public int getGrimoireLevel(GamePlayer player) {
        return grimoireMap.getOrDefault(player, new Grimoire(player)).getUsedAtLevel();
    }

    public UltimateResponse useUltimate(@Nonnull GamePlayer player) {
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

                Collect.nearbyEntities(castLocation, 20).forEach(entity -> {
                    if (entity.equals(player)) {
                        player.addEffect(Effects.SPEED, 1, 20);
                        // fixme -> Strength was here replace with attack in 2034 when you finally decide to fix this hero
                    }
                    else {
                        entity.addEffect(Effects.GLOWING, 1, 30);
                    }
                    entity.addEffect(Effects.BLINDNESS, 1, 30);
                });

            }
        }.runTaskTimer(0, 10);

        return UltimateResponse.OK;
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
        return null;
    }

    @Override
    public Talent getPassiveTalent() {
        return null;
    }

    public void grantSpellItems(GamePlayer player) {
        final PlayerInventory inventory = player.getInventory();
        applyICD(player);

        talentMap.forEach((slot, talent) -> {
            inventory.setItem(slot, talent.getItem());
            if (slot == 2) {
                Nulls.runIfNotNull(inventory.getItem(2), item -> item.setAmount(3));
            }
        });

        // Fx
        player.playSound(Sound.ENTITY_PLAYER_LEVELUP, 0.75f);
        player.playSound(Sound.ENTITY_PLAYER_LEVELUP, 1.25f);
    }

    public boolean hasICD(GamePlayer player) {
        return player.hasCooldown(getItem().getType());
    }

    public void applyICD(GamePlayer player) {
        player.setCooldown(getItem().getType(), 10);
    }

    public void removeSpellItems(GamePlayer player, LibrarianTalent talent) {
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

    public void giveGrimoireBook(GamePlayer player) {
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
        final GamePlayer player = CF.getPlayer(ev.getPlayer());

        if (player == null || !validatePlayer(player) || ev.getNewSlot() != 1) {
            return;
        }

        final ItemStack item = player.getInventory().getItem(ev.getNewSlot());

        if (!GrimoireBook.isGrimmoreItem(item)) {
            return;
        }

        final Grimoire grimoire = grimoireMap.get(player);
        if (GrimoireBook.hasCooldown(player) || grimoire == null) {
            player.sendMessage("&cCannot use Grimoire now! On cooldown for %ss.".formatted(GrimoireBook.getCooldownString(player)));
            return;
        }

        grimoire.markUsedNow();
        grantSpellItems(player);
        player.snapToWeapon();
    }
}
