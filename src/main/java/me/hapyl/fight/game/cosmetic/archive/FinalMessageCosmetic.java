package me.hapyl.fight.game.cosmetic.archive;

import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.gamemode.CFGameMode;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.util.CollectionUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class FinalMessageCosmetic extends Cosmetic {

    private final String[] AVAILABLE_MESSAGES = new String[] {
            "Shit.",
            "I'm dead :(",
            "I was lagging!",
            "I'm not even mad.",
            "Hacker.",
            "You just got lucky.",
            "Well, I tried..."
    };

    public FinalMessageCosmetic() {
        super("Final Message", "Let them know your final words.", Type.DEATH, Rarity.EPIC, Material.PAPER);

        setIcon(Material.PAPER);
    }

    @Override
    public void addExtraLore(@Nonnull ItemBuilder builder, @Nonnull Player player) {
        builder.addLore("&7Available Messages:");

        for (String message : AVAILABLE_MESSAGES) {
            builder.addLore("- &b" + message);
        }
    }

    @Override
    public void onDisplay(Display display) {
        createArmorStand(display.getLocation().add(0.0d, 0.25d, 0.0d), "&e%s's final words:".formatted(display.getName()));
        createArmorStand(display.getLocation(), "&b&l" + CollectionUtils.randomElement(AVAILABLE_MESSAGES, AVAILABLE_MESSAGES[0]));
    }

    private void createArmorStand(Location location, String message) {
        final ArmorStand stand = Entities.ARMOR_STAND_MARKER.spawn(location, armorStand -> {
            armorStand.setVisible(false);
            armorStand.setSmall(true);
            armorStand.setInvulnerable(true);
            armorStand.setCustomName(Chat.format(message));
            armorStand.setCustomNameVisible(true);
        });

        // Remove upon respawn in respawn allowed modes.
        final CFGameMode currentMode = Manager.current().getCurrentGame().getMode();
        if (currentMode.isAllowRespawn()) {
            GameTask.runLater(stand::remove, currentMode.getRespawnTime());
        }
    }
}
