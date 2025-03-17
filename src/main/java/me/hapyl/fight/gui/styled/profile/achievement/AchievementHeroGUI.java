package me.hapyl.fight.gui.styled.profile.achievement;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.registry.Registries;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class AchievementHeroGUI extends AchievementAbstractGUI {

    private final ItemStack headerItem;

    public AchievementHeroGUI(Player player, Hero hero) {
        super(player, hero.getName(), Registries.achievements().heroSpecific(hero));

        this.headerItem = AchievementGUI.createHeroSpecificTexture(hero).asIcon();

        setEmptyContentsItem(new ItemBuilder(Material.CHARCOAL)
                .setName(Color.ERROR + "No achievements!")
                .addLore()
                .addSmartLore("Either %s doesn't have any achievements or they're hiding from you!".formatted(hero.getName()))
                .addLore("&8&o¯\\_(ツ)_/¯")
                .asIcon()
        );

        openInventory(1);
    }

    @Nonnull
    @Override
    public ItemStack headerItem() {
        return this.headerItem;
    }
}
