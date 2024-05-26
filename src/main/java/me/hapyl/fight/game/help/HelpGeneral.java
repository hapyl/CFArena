package me.hapyl.fight.game.help;

import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.gui.styled.ReturnData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HelpGeneral extends HelpGUI {

    public HelpGeneral(Player player) {
        super(player, "General");
    }

    @Nullable
    @Override
    public ReturnData getReturnData() {
        return null;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        final PlayerProfile profile = PlayerProfile.getProfileOrThrow(player);
        final ItemStack heroItem = profile.getSelectedHero().getHero().getItem(player);

        setItem(
                20,
                new ItemBuilder(Material.IRON_SWORD)
                        .setName("About Combat")
                        .addSmartLore("The game's combat mechanics are quite different from vanilla Minecraft.")
                        .addLore()
                        .addLore(Color.BUTTON + "Click to learn about combat.")
                        .asIcon(), HelpCombat::new
        );

        setItem(
                22,
                new ItemBuilder(heroItem)
                        .setName("About Heroes")
                        .addSmartLore("There are many unique Heroes!")
                        .addLore()
                        .addLore(Color.BUTTON + "Click to learn about heroes.")
                        .asIcon(), HelpHeroes::new
        );

        setItem(
                24,
                new ItemBuilder(Material.BOOK)
                        .addSmartLore("Meet the developers.")
                        .addLore()
                        .addLore(Color.BUTTON + "Click to meet them.")
                        .asIcon(), HelpDevelopers::new
        );
    }

    @Nonnull
    @Override
    public Material getBorder() {
        return Material.LIGHT_GRAY_STAINED_GLASS_PANE;
    }

}
