package me.hapyl.fight.gui;

import me.hapyl.fight.Main;
import me.hapyl.fight.game.collectible.relic.Relic;
import me.hapyl.fight.game.collectible.relic.RelicHunt;
import me.hapyl.fight.game.collectible.relic.Type;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.util.ProgressBarBuilder;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.Arguments;
import me.hapyl.spigotutils.module.inventory.gui.PlayerDynamicGUI;
import me.hapyl.spigotutils.module.inventory.gui.SlotPattern;
import me.hapyl.spigotutils.module.inventory.gui.SmartComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public class RelicHuntGUI extends PlayerDynamicGUI {
    private final RelicHunt relicHunt;

    public RelicHuntGUI(Player player) {
        super(player, "Relic Hunt", 5);

        relicHunt = Main.getPlugin().getCollectibles().getRelicHunt();
        openInventory();
    }

    @Override
    public void setupInventory(@Nonnull Arguments arguments) {
        final SmartComponent component = newSmartComponent();
        setArrowBack(40, "The Eye", ref -> new EyeGUI(getPlayer()));

        for (GameMaps zone : GameMaps.values()) {
            if (!relicHunt.anyIn(zone)) {
                continue;
            }

            final List<Relic> relics = relicHunt.byZone(zone);
            final List<Relic> found = relicHunt.getFoundListIn(getPlayer(), zone);
            final int foundSize = found.size();
            final int percent = foundSize * 100 / relics.size();

            final ItemBuilder builder = ItemBuilder.of(zone.getMap().getMaterial(), zone.getName())
                    .addLore()
                    .addLore("There are %s &drelics&7 in &a%s&7.", relics.size(), zone.getName())
                    .addLore();

            if (foundSize == 0) {
                builder.addSmartLore("You haven't found any relics in this area!", "&c");
            }
            else if (foundSize == relics.size()) {
                builder.addSmartLore("You have found all the relics in this are!", "&a");
            }
            else {
                builder.addSmartLore("You found &b%s&a relics in this area.".formatted(foundSize), "&a");
            }

            builder.addLore();
            builder.addLore("&7Progress%s&a%s%%".formatted(
                    percent >= 100 ? " ".repeat(12) : percent >= 10 ? " ".repeat(13) : " ".repeat(15),
                    percent
            ));
            builder.addLore(ProgressBarBuilder.of("■", foundSize, relics.size()));
            component.add(builder.asIcon());
        }

        // Rewards
        setItem(30, createTotalRelicsItem());
        setItem(32, createClaimRewardItem(), RelicRewardGUI::new);

        component.apply(this, SlotPattern.DEFAULT, 1);
    }

    private ItemStack createTotalRelicsItem() {
        final ItemBuilder builder = ItemBuilder.of(Material.CREEPER_BANNER_PATTERN, "Total Relics", "&8By Type", "");

        for (Type value : Type.values()) {
            final List<Relic> relics = relicHunt.byType(value);

            if (relics.isEmpty()) {
                continue;
            }

            final List<Relic> found = relicHunt.getFoundListByType(getPlayer(), value);
            final int size = found.size();
            final float percentDone = (float) size / relics.size();
            final String color = percentDone <= 0.25 ? "&c" : percentDone <= 0.5 ? "&e" : percentDone <= 0.75 ? "&a" : "&a&l";

            builder.addLore("%s%s %s/%s", color, Chat.capitalize(value), size, relics.size());
        }

        return builder.asIcon();
    }

    private ItemStack createClaimRewardItem() {
        final ItemBuilder builder = ItemBuilder.of(Material.CHEST_MINECART, "Rewards");

        builder.addSmartLore("Trade your relics for unique cosmetic rewards and perks!");
        builder.addLore();
        builder.addLore("&eClick to browse");

        return builder.asIcon();
    }
}
