package me.hapyl.fight.game.experience;

import me.hapyl.fight.Main;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.reward.Reward;
import me.hapyl.fight.gui.styled.profile.PlayerProfileGUI;
import me.hapyl.fight.gui.styled.*;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

// Not using PageGUI because it's a custom pattern
public class ExperienceGUI extends StyledGUI {

    private final int[] slots;
    private final Experience experience;
    private int index;

    public ExperienceGUI(Player player) {
        super(player, "Experience", Size.FIVE);

        experience = Main.getPlugin().getExperience();
        slots = new int[] {
                //9, 10, 19, 28, 37, 46, 47, 48, 39, 30, 21, 12, 13, 14, 23, 32, 41, 50, 51, 52, 43, 34, 25, 16, 17
                0, 1, 10, 19, 28, 37, 38, 39, 30, 21, 12, 3, 4, 5, 14, 23, 32, 41, 42, 43, 34, 25, 16, 7, 8
        };

        index = 1;
        openInventory();
    }

    @Nullable
    @Override
    public ReturnData getReturnData() {
        return ReturnData.of("Profile", PlayerProfileGUI::new);
    }

    @Override
    public void onUpdate() {
        //setHeader(StyledItem.ICON_LEVELLING.asIcon());
        //setReturnItem(1);

        // Add button for the previous page if index is > 0
        if (index > 1) {
            setItem(47, StyledTexture.ARROW_LEFT.asIcon("Previous Page (1-25)", Color.BUTTON + "Click to browse levels from 1 to 25."),
                    e -> {
                        index -= slots.length;
                        update();
                    }
            );
        }

        // Add button for the next page if index < max experience level
        if (index + slots.length < experience.MAX_LEVEL) {
            setItem(51, StyledTexture.ARROW_RIGHT.asIcon("Next Page (26-50)", Color.BUTTON + "Click to browse levels from 26 to 50."),
                    e -> {
                        index += slots.length;
                        update();
                    }
            );
        }

        for (int i = index; i < index + slots.length; i++) {
            final int slot = slots[i - index];
            final ExperienceLevel level = experience.getLevel(i);

            if (level == null) {
                break;
            }

            setItem(slot, createItem(level));
        }
    }

    @Nonnull
    public ItemStack createItem(@Nonnull ExperienceLevel level) {
        final Player player = getPlayer();
        final ItemBuilder builder = new ItemBuilder(Material.PAPER);
        final boolean levelReached = level.getLevel() <= experience.getLevel(player);

        if (level.getLevel() == experience.getLevel(player) + 1) {
            builder.setType(Material.YELLOW_STAINED_GLASS_PANE);
        }
        else {
            if (levelReached) {
                builder.setType(Material.LIME_STAINED_GLASS_PANE);
            }
            else {
                builder.setType(Material.RED_STAINED_GLASS_PANE);
            }
        }

        if (level.getLevel() == experience.getLevel(player) + 1) {
            builder.addLore();
            builder.addLore("&7Progress: &e" + experience.getProgressBar(player) + " &e" + experience.getExpScaled(player) + "&7/&a" +
                    experience.getExpScaledNext(player));
        }

        builder.setAmount((int) level.getLevel());
        builder.setName("Level " + level.getLevel());
        builder.addLore();

        if (level.hasRewards()) {
            builder.addLore("&7Rewards: " + (levelReached ? "&a✔" : ""));

            final List<Reward> rewards = level.getRewards();

            for (Reward reward : rewards) {
                reward.display(player, builder);
            }

        }
        else {
            builder.addLore("&cNo rewards!");
        }

        return builder.build();
    }

    protected void setReturnItem(int slot) {
        StaticStyledGUI.setReturn(this, slot);
    }


}
