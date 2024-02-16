package me.hapyl.fight.guesswho.gui;

import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.guesswho.GameResult;
import me.hapyl.fight.guesswho.GuessWhoPlayer;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;

import javax.annotation.Nonnull;

public class GuessWhoRuleOutGUI extends GuessWhoGUI {

    private boolean ruledOut;

    public GuessWhoRuleOutGUI(GuessWhoPlayer data) {
        super(data, (data.isMyTurn() ? "Rule Out" : "Preview"));
    }

    @Override
    public void onOpen() {
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        // Selected hero
        final Heroes guessWhoHero = data.getGuessHero();

        if (guessWhoHero != null) {
            setPanelItem(2, super.createItem(guessWhoHero)
                    .addLore()
                    .addLore("&8&m             &8&m")
                    .addLore()
                    .addSmartLore("Your opponent has to guess this hero!")
                    .addLore()
                    .addLore(Color.BUTTON + "Shift Right Click to forfeit!")
                    .addSmartLore("Forfeiting will result in a loss.", "&8&o")
                    .asIcon(), player -> {
                data.getGame().result = GameResult.FORFEIT;
                data.lose();

                // Achievement
                Achievements.FORFEIT_GUESS_WHO.complete(data.getPlayer());
            }, ClickType.SHIFT_RIGHT);
        }

        if (!data.isMyTurn()) {
            return;
        }

        final ItemBuilder builder = new ItemBuilder(ruledOut ? Material.RED_DYE : Material.GRAY_DYE)
                .setName("Switch to Guessing Mode")
                .addLore();

        if (ruledOut) {
            builder.addLore(Color.ERROR + "Cannot switch in this round!");
            setPanelItem(6, builder.asIcon());
        }
        else {
            builder.addSmartLore("Ready to guess, sure you know the answer?");
            builder.addLore();
            builder.addLore(Color.BUTTON + "Click to switch!");
            builder.addSmartLore("You can only guess once!", "&c&o");
            builder.addSmartLore("Guessing incorrectly will result in a loss!", "&c&o");
            setPanelItem(6, builder.asIcon(), player -> {
                new GuessWhoGuessGUI(data);
            });
        }

        // Next turn
        final ItemBuilder endTurnBuilder = new ItemBuilder(Material.GOLD_BLOCK)
                .setName("End Turn")
                .addLore()
                .addSmartLore("Done ruling out heroes? End the turn then!")
                .addLore();

        if (ruledOut) {
            endTurnBuilder.addLore(Color.BUTTON + "Click to end turn!");
            setPanelItem(4, endTurnBuilder.asIcon(), player -> {
                data.getGame().nextTurn();
                player.closeInventory();
            });
        }
        else {
            endTurnBuilder.addLore(Color.ERROR + "Cannot end turn!");
            endTurnBuilder.addSmartLore("Either rule out or guess before ending a turn!", "&8&o");
            setPanelItem(4, endTurnBuilder.asIcon());
        }

    }

    @Nonnull
    @Override
    public ItemBuilder createItem(@Nonnull Heroes enumHero) {
        final ItemBuilder builder = super.createItem(enumHero);

        if (!data.isMyTurn()) {
            return builder;
        }

        if (data.isLastHero()) {
            builder.addLore();
            builder.addLore(Color.ERROR + "Cannot rule out!");
            builder.addSmartLore("It's the last hero, either end turn or guess!", "&a&o");
        }
        else {
            builder.addLore();
            builder.addLore(Color.BUTTON + "Click to rule out!");
        }

        return builder;
    }

    @Override
    public void onClick(@Nonnull Heroes hero) {
        if (!data.isMyTurn()) {
            data.sendMessage("It's not your turn!");
            data.playSound(Sound.ENTITY_VILLAGER_NO, 1.0f);
            return;
        }

        if (data.isLastHero()) {
            data.sendMessage("Cannot rule out nor guess because it's the last hero, skip turn!");
            data.playSound(Sound.ENTITY_VILLAGER_NO, 1.0f);
            return;
        }

        if (data.isRuledOut(hero)) {
            data.sendMessage("This hero is ruled out!");
            return;
        }

        ruledOut = true;
        data.ruleOut(hero);

        update();
    }
}
