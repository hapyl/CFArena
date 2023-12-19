package me.hapyl.fight.gui;

import me.hapyl.fight.game.maps.GameMap;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.game.maps.HiddenMapFeature;
import me.hapyl.fight.game.maps.MapFeature;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledItem;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public class MapSelectGUI extends GameManagementSubGUI<GameMaps> {

    public MapSelectGUI(Player player) {
        super(player, "Map Selection", Size.FOUR, GameMaps.getPlayableMaps());
    }

    @Nonnull
    @Override
    public ItemStack getHeaderItem() {
        return StyledItem.ICON_MAP_SELECT.asIcon();
    }

    @Nonnull
    @Override
    public ItemBuilder createItem(@Nonnull GameMaps enumMap, boolean isSelected) {
        final GameMap map = enumMap.getMap();

        final ItemBuilder builder = new ItemBuilder(map.getMaterial())
                .setName(map.getName())
                .addLore("&8/map " + enumMap.name().toLowerCase())
                .addLore()
                .addTextBlockLore(map.getDescription());

        final List<MapFeature> mapFeatures = map.getNonHiddenFeatures();

        if (!mapFeatures.isEmpty()) {
            builder.addLore();
            builder.addLore(mapFeatures.size() == 1 ? "&6&lMap Feature:" : "&6&lMap Features:");

            for (MapFeature feature : mapFeatures) {
                if (feature instanceof HiddenMapFeature) {
                    continue;
                }

                builder.addLore(" &a" + feature.getName());
                builder.addTextBlockLore(
                        feature.getDescription(),
                        "&7&o  ",
                        ItemBuilder.DEFAULT_SMART_SPLIT_CHAR_LIMIT,
                        CFUtils.DISAMBIGUATE
                );
            }
        }

        return builder;
    }

}
