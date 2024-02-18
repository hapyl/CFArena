package me.hapyl.fight.gui.styled.eye;

import me.hapyl.fight.Main;
import me.hapyl.fight.game.collectible.relic.Relic;
import me.hapyl.fight.game.collectible.relic.RelicHunt;
import me.hapyl.fight.game.collectible.relic.Type;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.maps.GameMap;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.gui.RelicRewardGUI;
import me.hapyl.fight.gui.styled.*;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public class RelicHuntGUI extends StyledGUI {

    private final RelicHunt relicHunt;

    private int mapIndex = 0;

    public RelicHuntGUI(Player player) {
        super(player, "Relic Hunt", Size.FIVE);

        relicHunt = Main.getPlugin().getCollectibles().getRelicHunt();

        openInventory();
    }

    @Nullable
    @Override
    public ReturnData getReturnData() {
        return ReturnData.of("The Eye", EyeGUI::new);
    }

    @Override
    public void onUpdate() {
        final List<GameMaps> mapsWithRelics = relicHunt.getMapsWithRelics();

        setHeader(StyledTexture.RELIC_HUNT.asIcon());

        // Fill with black panes
        fillItem(20, 24, ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE, "").asIcon());

        int index = 0;
        for (int i = mapIndex; i < mapsWithRelics.size(); i++) {
            if (index >= 5) {
                break;
            }

            final GameMaps enumMap = mapsWithRelics.get(i);
            final GameMap map = enumMap.getMap();

            final List<Relic> relicsInMap = relicHunt.byZone(enumMap);
            final List<Relic> relicsFoundInMap = relicHunt.getFoundListIn(player, enumMap);

            final int relicsInMapSize = relicsInMap.size();
            final int relicsFoundInMapSize = relicsFoundInMap.size();

            final double foundPercentage = (double) relicsFoundInMapSize / relicsInMapSize;

            final ItemBuilder builder = new ItemBuilder(map.getMaterial())
                    .setName(map.getName())
                    .addLore("&8Relics in " + map.getName())
                    .addLore()
                    .addLore("There are &b%s &drelics&7 in this area.", relicsInMap.size())
                    .addLore();

            if (foundPercentage == 0) {
                builder.addLore(Color.ERROR + "You haven't found any relics in this area!");
                builder.addLore("&8Go find some relics!");
            }
            else if (foundPercentage == 1) {
                builder.addLore(Color.SUCCESS + "You have found all the relics in this area!");
                builder.addLore("&8Nice!");
            }
            // Display progress if found any but not all
            else {
                builder.addLore("&bᴘʀᴏɢʀᴇss %.0f%%", foundPercentage * 100);

                final int foundBars = (int) (foundPercentage * 20);
                builder.addLore(
                        "&a|".repeat(foundBars) + "&c|".repeat(20 - foundBars) + " &7%s/%s",
                        relicsFoundInMapSize, relicsInMapSize
                );
            }

            setItem(20 + index, builder.asIcon());
            index++;
        }

        // Arrows
        if (mapIndex > 0) {
            setItem(19, StyledTexture.ARROW_LEFT.asButton("Cycle Left", "cycle left"), click -> {
                mapIndex--;
                update();
            });
        }

        if (mapIndex < Math.ceil(mapsWithRelics.size() / 5.0d)) {
            setItem(25, StyledTexture.ARROW_RIGHT.asButton("Cycle Right", "cycle right"), click -> {
                mapIndex++;
                update();
            });
        }

        setItem(30, createTotalRelicsItem());
        setItem(32, StyledItem.ICON_RELIC_REWARDS.asButton("browse"), RelicRewardGUI::new);
    }

    private ItemStack createTotalRelicsItem() {
        final ItemBuilder builder = ItemBuilder.of(Material.CREEPER_BANNER_PATTERN, "Total Relics", "&8By Type", "");

        for (Type type : Type.values()) {
            final List<Relic> relics = relicHunt.byType(type);

            if (relics.isEmpty()) {
                continue;
            }

            final List<Relic> foundByType = relicHunt.getFoundListByType(getPlayer(), type);
            final int foundSize = foundByType.size();
            final double percentDone = (double) foundSize / relics.size();
            final Color color
                    = percentDone >= 1 ? Color.SUCCESS
                    : percentDone >= 0.75 ? Color.GOLD
                    : percentDone >= 0.5 ? Color.YELLOW
                    : Color.RED;

            builder.addLore(
                    "%s&7/%s %s %s",
                    color.color(foundSize),
                    relics.size(),
                    type.getName(),
                    CFUtils.checkmark(percentDone >= 1 ? true : null)
            );
        }

        return builder.asIcon();
    }

}
