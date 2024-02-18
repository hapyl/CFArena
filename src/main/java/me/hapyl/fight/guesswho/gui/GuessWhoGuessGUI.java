package me.hapyl.fight.guesswho.gui;

import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.guesswho.GuessWhoPlayer;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class GuessWhoGuessGUI extends GuessWhoGUI {

    public GuessWhoGuessGUI(GuessWhoPlayer data) {
        super(data, "Guess");

        openInventory();
    }

    @Nonnull
    @Override
    public ItemBuilder createItem(@Nonnull Heroes enumHero) {
        final ItemBuilder builder = super.createItem(enumHero);

        if (data.isRuledOut(enumHero)) {
            return builder;
        }

        return builder.addLore().addLore(Color.BUTTON + "Click to guess!");
    }

    @Override
    public void onClick(@Nonnull Heroes hero) {
        if (data.isRuledOut(hero)) {
            data.sendMessage("You have ruled out this hero!");
            data.playSound(Sound.ENTITY_VILLAGER_NO, 1.0f);
            return;
        }

        data.guess(hero);
    }

}
