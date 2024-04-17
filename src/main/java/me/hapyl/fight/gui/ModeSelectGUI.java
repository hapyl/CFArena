package me.hapyl.fight.gui;

import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.gamemode.CFGameMode;
import me.hapyl.fight.game.gamemode.Modes;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledItem;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class ModeSelectGUI extends GameManagementSubGUI<Modes> {
    public ModeSelectGUI(Player player) {
        super(player, "Mode Selection", Size.FOUR, Modes.values());
    }

    @Nonnull
    @Override
    public ItemStack getHeaderItem() {
        return StyledItem.ICON_MODE_SELECT.asIcon();
    }

    @Override
    public int getStartIndex() {
        return 2;
    }

    @Nonnull
    @Override
    public ItemBuilder createItem(@Nonnull Modes enumMode, boolean isSelected) {
        final CFGameMode mode = enumMode.getMode();

        final ItemBuilder builder = new ItemBuilder(mode.getMaterial())
                .setName(mode.getName())
                .addLore()
                .addTextBlockLore(mode.getDescription())
                .addLore();

        final int onlinePlayers = Bukkit.getOnlinePlayers().size();
        final int playerRequirements = mode.getPlayerRequirements();
        final boolean requirementsMet = mode.isPlayerRequirementsMet();
        final Color color = (requirementsMet ? Color.GREEN : Color.RED);
        final String minimumPlayersSuffix = color +
                "%s&7/%s %s".formatted(
                        color.color(onlinePlayers),
                        color.color(playerRequirements),
                        color.color(requirementsMet ? "✔" : "❌")
                );

        builder.addLore("&f&lMode Info:");
        builder.addLore(" Minimum Players: %s".formatted(minimumPlayersSuffix));
        builder.addLore(" Time Limit: &a%s".formatted(mode.getTimeLimitFormatted()));

        return builder;
    }

}
