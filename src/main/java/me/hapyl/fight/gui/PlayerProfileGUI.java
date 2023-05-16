package me.hapyl.fight.gui;

import me.hapyl.fight.game.cosmetic.gui.CollectionGUI;
import me.hapyl.fight.game.experience.ExperienceGUI;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PlayerProfileGUI extends PlayerGUI {
    public PlayerProfileGUI(Player player) {
        super(player, "You Profile", 4);
        updateInventory();
    }

    private void updateInventory() {
        setItem(
                4,
                new ItemBuilder(Material.PLAYER_HEAD)
                        .setSkullOwner(getPlayer().getName())
                        .setName("&aProfile")
                        .setSmartLore("&8Oh hey, it's you!")
                        .asIcon()
        );

        setItem(19, ItemBuilder.of(Material.CHEST, "Cosmetics", "Browse your cosmetics!").asIcon(), CollectionGUI::new);
        setItem(21, ItemBuilder.of(Material.EMERALD, "Experience", "Browse your experience progress!").asIcon(), ExperienceGUI::new);
        setItem(23, ItemBuilder.of(Material.DIAMOND, "Achievements", "Browse your achievements!").asIcon(), AchievementGUI::new);

        setItem(25, ItemBuilder.of(Material.CREEPER_BANNER_PATTERN, "Chat Settings", "Change you chat settings").asIcon(), this::comingSoon);

        //setItem(31, ItemBuilder.of(Material.APPLE, "Friends", "Browse your friends!").asIcon(), player -> {
        //    player.sendMessage("coming soon");
        //});

        openInventory();
    }

    private void comingSoon(Player player) {
        Chat.sendMessage(player, "&cThis feature is coming soon™!");
    }
}
