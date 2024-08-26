package me.hapyl.fight.game.experience;

import me.hapyl.fight.Main;
import me.hapyl.fight.game.reward.Reward;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.inventory.gui.PlayerGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class ExperienceDebugGUI extends PlayerGUI {
    public ExperienceDebugGUI(Player player) {
        super(player, "Experience Debug GUI &4&lDEBUG", 6);

        final Experience experience = Main.getPlugin().getExperience();
        experience.getExperienceLevelMap().forEach((lvl, expLevel) -> {
            final ItemBuilder builder = new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE)
                    .setName("Level " + lvl)
                    .setAmount(lvl.intValue());

            final List<Reward> rewards = expLevel.getRewards();

            if (rewards.isEmpty()) {
                builder.addLore("&cNo rewards!");
            }
            else {
                builder.addLore("&aReward: ");
                rewards.forEach(reward -> {
                    reward.getDescription(player).forEach(builder::addLore);
                });
            }

            builder.addLore();
            builder.addLore("&aTotal Exp Needed:");
            builder.addLore("%,d".formatted(expLevel.getExpRequired()));

            setItem(lvl.intValue() - 1, builder.asIcon());
        });

        openInventory();
    }


}
