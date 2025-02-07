package me.hapyl.fight.game.experience;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.Main;
import me.hapyl.fight.game.reward.Reward;
import me.hapyl.fight.gui.styled.ReturnData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.gui.styled.StyledItem;
import me.hapyl.fight.gui.styled.profile.PlayerProfileGUI;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

// Not using PageGUI because it's a named pattern
public class ExperienceGUI extends StyledGUI {

    private final int[][] slots;
    private final Experience experience;

    public ExperienceGUI(Player player) {
        super(player, "Experience", Size.FOUR);

        slots = new int[][] {
                { 11, 19, 29, 15, 25, 33 },
                { 10, 18, 28, 16, 26, 34 }
        };

        experience = Main.getPlugin().getExperience();
        PlayerLib.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.75f);

        openInventory();
    }

    @Nullable
    @Override
    public ReturnData getReturnData() {
        return ReturnData.of("Profile", PlayerProfileGUI::new);
    }

    @Override
    public void onUpdate() {
        final long playerLevel = experience.getLevel(player);
        final ExperienceLevel[] feed = experience.getLevelFeed(playerLevel);

        setHeader(StyledItem.ICON_LEVELLING.asIcon());

        Material material = Material.MAGENTA_STAINED_GLASS_PANE;

        for (int[] type : slots) {
            for (int slot : type) {
                setItem(slot, new ItemBuilder(material).setName("").asIcon());
            }

            material = Material.PURPLE_STAINED_GLASS_PANE;
        }

        int slot = 20;
        for (ExperienceLevel level : feed) {
            final ItemStack item = createItem(level);

            setItem(slot++, item);
        }
    }

    @Nonnull
    public ItemStack createItem(@Nonnull ExperienceLevel experienceLevel) {
        final ItemBuilder builder = new ItemBuilder(Material.PAPER);
        final long level = experienceLevel.getLevel();
        final boolean levelReached = level <= experience.getLevel(player);
        final boolean isPrestige = experienceLevel.isPrestige();

        // Next level
        if (level == experience.getLevel(player) + 1) {
            final long expScaled = experience.getExpScaled(player);
            final long expScaledNext = experience.getExpScaledNext(player);

            builder.setType(isPrestige ? Material.GOLD_BLOCK : Material.YELLOW_STAINED_GLASS_PANE);

            builder.setName((isPrestige ? "&e&l" : "&e") + "Level " + level);
            builder.addLore("&8Next Level");
            builder.addLore();
            builder.addLore("&7Progress: &e%s&7/&a%s".formatted(expScaled, expScaledNext));
            builder.addLore(experience.getProgressBar(player) + " &8%.2f%%".formatted((float) expScaled / (float) expScaledNext * 100.0f));
        }
        else {
            // Reached level
            if (levelReached) {
                builder.setType(isPrestige ? Material.EMERALD_BLOCK : Material.LIME_STAINED_GLASS_PANE);

                builder.setName((isPrestige ? "&a&l" : "&a") + "Level " + level);
                builder.addLore("&8Reached Level");
            }
            // Future level
            else {
                builder.setType(isPrestige ? Material.REDSTONE_BLOCK : Material.RED_STAINED_GLASS_PANE);

                builder.setName((isPrestige ? "&c&l" : "&c") + "Level " + level);
                builder.addLore("&8Future Level");
            }
        }

        builder.setAmount((int) level);
        builder.addLore();

        // Display rewards
        if (experienceLevel.hasRewards()) {
            builder.addLore("&7Rewards: " + BukkitUtils.checkmark(levelReached));

            final List<Reward> rewards = experienceLevel.getRewards();

            for (Reward reward : rewards) {
                reward.getDescription(player).forEach(builder::addLore);
            }

        }
        else {
            builder.addLore("&cNo rewards :(");
        }

        return builder.build();
    }

}
