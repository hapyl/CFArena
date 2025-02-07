package me.hapyl.fight.gui;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.fight.game.maps.EnumLevel;
import me.hapyl.fight.game.maps.HiddenLevelFeature;
import me.hapyl.fight.game.maps.Level;
import me.hapyl.fight.game.maps.LevelFeature;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public class MapSelectGUI extends GameManagementSubGUI<EnumLevel> {

    public MapSelectGUI(Player player) {
        super(player, "Map Selection", Size.FOUR, EnumLevel.getPlayableMaps());
    }

    @Nonnull
    @Override
    public ItemStack getHeaderItem() {
        return StyledItem.ICON_MAP_SELECT.asIcon();
    }

    @Nonnull
    @Override
    public ItemBuilder createItem(@Nonnull EnumLevel enumMap, boolean isSelected) {
        final Level map = enumMap.getLevel();

        final ItemBuilder builder = new ItemBuilder(map.getMaterial())
                .setName(map.getName())
                .addLore("&8/map " + enumMap.name().toLowerCase())
                .addLore()
                .addTextBlockLore(map.getDescription());

        final List<LevelFeature> levelFeatures = map.getNonHiddenFeatures();

        if (!levelFeatures.isEmpty()) {
            builder.addLore();
            builder.addLore(levelFeatures.size() == 1 ? "&6&lMap Feature:" : "&6&lMap Features:");

            for (LevelFeature feature : levelFeatures) {
                if (feature instanceof HiddenLevelFeature) {
                    continue;
                }

                builder.addLore(" &a" + feature.getName());
                builder.addTextBlockLore(
                        feature.getDescription(),
                        "&7&o  ",
                        ItemBuilder.DEFAULT_SMART_SPLIT_CHAR_LIMIT
                );
            }
        }

        return builder;
    }

}
