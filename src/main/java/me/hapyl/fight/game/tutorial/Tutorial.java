package me.hapyl.fight.game.tutorial;

import me.hapyl.fight.Main;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

// This tutorial is displayed when a player joins the server for the a time, or can be accessed via /tutorial
public class Tutorial extends TutorialGUI {

    public Tutorial(Player player) {
        super(player, "General");
    }

    @Override
    public void onButtonClick(Player player) {
        player.closeInventory();

        Chat.broadcast("&a%s has finished reading the tutorial!", player.getName());
    }

    @Nonnull
    @Override
    public ItemStack getButtonIcon() {
        return ItemBuilder.of(Material.EMERALD_BLOCK, "&a&lGot It!")
                .addLore("")
                .addSmartLore("&c&lKeep in Mind!")
                .addSmartLore(
                        "&6The game is under heavy development, and the programmer is a little dumb, so expect bugs and glitches.",
                        "&6"
                )
                .addLore("")
                .addLore("&eClick to complete tutorial")
                .asIcon();
    }

    @Override
    public void updateInventory() {
        setItem(4, ItemBuilder.of(Material.GOLDEN_AXE, "&aWelcome!", "&7To " + Main.GAME_NAME)
                .addLore("")
                .addSmartLore(
                        "%s &7is a PvP mini-game where you can choose from a variety of heroes to fight against other players, or team up and defend against the Horde!".formatted(
                                Main.GAME_NAME)
                )
                .addLore()
                .addSmartLore("Each hero has unique abilities and roles that you can use to your advantage.")
                .asIcon());

        setItem(20, ItemBuilder.of(Material.IRON_SWORD, "Combat")
                .addLore()
                .addSmartLore("%s&7's combats mechanics are quite different from vanilla Minecraft.".formatted(Main.GAME_NAME))
                .addLore()
                .addLore("&eClick here to learn more about Combat")
                .asIcon(), TutorialCombat::new);

        setItem(22, ItemBuilder.of(Material.PLAYER_HEAD, "Heroes")
                .addLore()
                .addSmartLore("%s&7 has a variety of heroes that you can choose from.".formatted(Main.GAME_NAME))
                .addLore()
                .addLore("&eClick here to learn more about Heroes")
                .asIcon(), TutorialHeroes::new);

        setItem(24, ItemBuilder.of(Material.BOOK, "Developers")
                .addLore()
                .addSmartLore("Meet the developers.")
                .addLore()
                .addLore("&eClick here to meet them (real)")
                .asIcon(), TutorialDevelopers::new);

    }

}
