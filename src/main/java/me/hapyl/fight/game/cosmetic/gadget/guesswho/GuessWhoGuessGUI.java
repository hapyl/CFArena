package me.hapyl.fight.game.cosmetic.gadget.guesswho;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.util.StringRandom;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class GuessWhoGuessGUI extends GuessWhoGUI {
    
    private static final ItemStack itemNotThisOne = new ItemBuilder(Material.RED_DYE).setName("&cNot this one!").asIcon();
    
    private final GuessWhoSuspenseGuessReveal reveal;
    
    public GuessWhoGuessGUI(GuessWhoPlayer player, @Nonnull GuessWhoSuspenseGuessReveal reveal) {
        super(player, () -> "%s is guessing...".formatted(reveal.guesser().getName()));
        
        this.reveal = reveal;
    }
    
    @Nonnull
    @Override
    public ItemStack getItem(@Nonnull Hero hero) {
        // If reveal is empty, make correct item
        if (reveal.contains(hero)) {
            return reveal.empty() ? makeCorrectItem(hero) : makeTauntItem(hero);
        }
        
        return itemNotThisOne;
    }
    
    @Override
    public void onClick(@Nonnull Hero hero) {
    }
    
    private ItemStack makeCorrectItem(Hero hero) {
        return ItemBuilder.playerHeadUrl(hero.getTextureUrl())
                          .setName("It was %s!".formatted(hero.getName()))
                          .asIcon();
    }
    
    private ItemStack makeTauntItem(Hero hero) {
        return ItemBuilder.playerHeadUrl(hero.getTextureUrl())
                          .setName("&e%s %s?".formatted(
                                  StringRandom.of(
                                          "Is it",
                                          "Could it be",
                                          "Perhaps"
                                  ), hero.getName()
                          ))
                          .asIcon();
    }
    
}
