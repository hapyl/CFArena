package me.hapyl.fight.game.heroes.librarian;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.chat.Gradient;
import me.hapyl.eterna.module.chat.gradient.Interpolators;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.annotate.KeepNull;
import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Gender;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroProfile;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.librarian.EntityDarkness;
import me.hapyl.fight.game.talents.librarian.LibrarianTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Nulls;
import me.hapyl.fight.util.collection.player.PlayerMap;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
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
public class Librarian extends Hero implements Listener, Disabled {

    private final Map<Integer, LibrarianTalent> talentMap = new HashMap<>();
    private final PlayerMap<Grimoire> grimoireMap = PlayerMap.newMap();
    private final String grimoireLevelUpGradient = new Gradient("Grimoire Level Up!")
            .makeBold()
            .rgb(ChatColor.RED.getColor(), ChatColor.BLUE.getColor(), Interpolators.LINEAR);

    private final String grimoireIsMaxGradient = new Gradient("Grimoire Maxed!")
            .rgb(ChatColor.RED.getColor(), ChatColor.BLUE.getColor(), Interpolators.LINEAR);

    public Librarian(@Nonnull Key key) {
        super(key, "Librarian of Void");

        setDescription("""
                Mislead by the void, sacrifices were made.
                """);

        final HeroProfile profile = getProfile();
        profile.setGender(Gender.MALE);

        talentMap.put(1, TalentRegistry.BLACK_HOLE);
        talentMap.put(2, TalentRegistry.ENTITY_DARKNESS);
        talentMap.put(3, TalentRegistry.LIBRARIAN_SHIELD);
        talentMap.put(4, TalentRegistry.WEAPON_DARKNESS);

        setItem("a88b1cd9574672e8e3262f210c0dddbc082ea7569e8e70f0c07b4bee75e32f62");

        final HeroEquipment equipment = getEquipment();
        equipment.setChestPlate(47, 32, 40, TrimPattern.WARD, TrimMaterial.GOLD);
        equipment.setLeggings(Material.NETHERITE_LEGGINGS);
        equipment.setBoots(84, 37, 62);

        //setWeapon(new Weapon(Material.NETHERITE_SHOVEL).setName("Staff").setDamage(7.5d));
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        grimoireMap.put(player, new Grimoire(player));
        giveGrimoireBook(player);
    }

    @Override
    public void onStart(@Nonnull GameInstance instance) {
        final int grimoireLevelUpDelay = 900; // 40

        new GameTask() {
            @Override
            public void run() {
                getAlivePlayers().forEach(player -> {
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

    @Override
    public Talent getFirstTalent() {
        return TalentRegistry.BLACK_HOLE;
    }

    @Override
    public Talent getSecondTalent() {
        return TalentRegistry.ENTITY_DARKNESS;
    }

    @Override
    public Talent getThirdTalent() {
        return TalentRegistry.LIBRARIAN_SHIELD;
    }

    @Override
    public Talent getFourthTalent() {
        return TalentRegistry.WEAPON_DARKNESS;
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
        return true;
    }

    public void applyICD(GamePlayer player) {
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

    private int indexOfTalent(Talent enumTalent) {
        if (TalentRegistry.ENTITY_DARKNESS == enumTalent) {
            return 2;
        }
        else if (TalentRegistry.LIBRARIAN_SHIELD == enumTalent) {
            return 3;
        }
        else if (TalentRegistry.WEAPON_DARKNESS == enumTalent) {
            return 4;
        }

        return 1;
    }
}
