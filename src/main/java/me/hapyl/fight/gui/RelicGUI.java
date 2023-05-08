package me.hapyl.fight.gui;

import me.hapyl.fight.Main;
import me.hapyl.fight.game.collectible.relic.Relic;
import me.hapyl.fight.game.collectible.relic.RelicHunt;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.util.ProgressBarBuilder;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.Arguments;
import me.hapyl.spigotutils.module.inventory.gui.PlayerDynamicGUI;
import me.hapyl.spigotutils.module.inventory.gui.SlotPattern;
import me.hapyl.spigotutils.module.inventory.gui.SmartComponent;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public class RelicGUI extends PlayerDynamicGUI {
    private final RelicHunt relicHunt;

    public RelicGUI(Player player) {
        super(player, "Relic Hunt", 5);

        relicHunt = Main.getPlugin().getCollectibles().getRelicHunt();
        openInventory();
    }

    @Override
    public void setupInventory(@Nonnull Arguments arguments) {
        final SmartComponent component = newSmartComponent();

        for (GameMaps zone : GameMaps.values()) {
            if (!relicHunt.anyIn(zone)) {
                continue;
            }

            final List<Relic> relics = relicHunt.getIn(zone);
            final List<Relic> found = relicHunt.getFoundListIn(getPlayer(), zone);
            final int foundSize = found.size();

            final ItemBuilder builder = ItemBuilder.of(zone.getMap().getMaterial(), zone.getName())
                    .addLore()
                    .addLore("There are %s &drelics&7 in &a%s&7.", relics.size(), zone.getName())
                    .addLore();

            if (foundSize == 0) {
                builder.addLore("&cYou haven't found any relics!");
            }
            else if (foundSize == relics.size()) {
                builder.addLore("&aYou have found all the relics!");
            }
            else {
                builder.addLore("&aYou found &b%s&7 relics.", foundSize);
            }

            builder.addLore(ProgressBarBuilder.of("-", foundSize, relics.size()));
            component.add(builder.asIcon());
        }

        component.apply(this, SlotPattern.DEFAULT, 1);
    }
}
