package me.hapyl.fight.guesswho.gui;

import com.google.common.collect.Sets;
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
import java.util.Set;

public class GuessWhoRuleOutGUI extends GuessWhoGUI {

    private final Set<Heroes> ruledOut;
    private final int boardCount;

    public GuessWhoRuleOutGUI(GuessWhoPlayer data) {
        super(data, (data.isMyTurn() ? "Rule Out" : "Preview"));

        this.ruledOut = Sets.newHashSet();
        this.boardCount = data.getBoardSize();

        openInventory();
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
                    .addLore("&8&m                          &8&m")
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

        final boolean hasRuledOutAny = hasRuledOutAny();

        final ItemBuilder builder = new ItemBuilder(hasRuledOutAny ? Material.RED_DYE : Material.GRAY_DYE)
                .setName("Switch to Guessing Mode")
                .addLore();

        if (hasRuledOutAny) {
            builder.addLore(Color.ERROR + "Cannot switch!");
            setPanelItem(6, builder.asIcon());
        }
        else {
            builder.addSmartLore("Ready to guess, sure you know the answer?");
            builder.addLore();
            builder.addLore(Color.BUTTON + "Click to switch!");
            builder.addSmartLore("You can only guess once!", "&c&o");
            builder.addSmartLore("Guessing incorrectly will result in a loss!", "&c&o");
            setPanelItem(6, builder.asIcon(), player -> new GuessWhoGuessGUI(data));
        }

        // Next turn
        final ItemBuilder endTurnBuilder = new ItemBuilder(Material.GOLD_BLOCK)
                .setName("End Turn")
                .addLore()
                .addSmartLore("End your turn to rule those heroes out:")
                .addLore();

        if (hasRuledOutAny) {
            ruledOut.forEach(hero -> endTurnBuilder.addLore(" &c- &f" + hero.getNameSmallCaps()));

            endTurnBuilder.addLore();
            endTurnBuilder.addLore(Color.BUTTON + "Click to end turn!");

            setPanelItem(4, endTurnBuilder.asIcon(), player -> {
                data.ruleOut(ruledOut);
                data.getGame().nextTurn();

                ruledOut.clear();
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

        builder.addLore();

        if (ruledOut.contains(enumHero)) {
            builder.setType(Material.YELLOW_DYE);

            builder.addLore(Color.YELLOW + "Marked for ruling out!");
            builder.addLore(Color.BUTTON + "Click to remove!");
        }
        else {
            if (isLastHero()) {
                builder.addLore(Color.ERROR + "Cannot rule out!");
                builder.addSmartLore("It's the last hero, either end turn or guess!", "&a&o");
            }
            else {
                builder.addLore(Color.BUTTON + "Click to mark for ruling out!");
            }
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

        if (ruledOut.contains(hero)) {
            ruledOut.remove(hero);
            data.playSound(Sound.BLOCK_END_PORTAL_FRAME_FILL, 0.0f);
        }
        else {
            if (isLastHero()) {
                data.sendMessage("Cannot rule out because it's the last hero, either end turn or guess!");
                data.playSound(Sound.ENTITY_VILLAGER_NO, 1.0f);
                return;
            }

            if (data.isRuledOut(hero)) {
                data.sendMessage("This hero is ruled out!");
                return;
            }

            ruledOut.add(hero);
            data.playSound(Sound.BLOCK_END_PORTAL_FRAME_FILL, 1.0f);
        }

        update();
    }

    private boolean isLastHero() {
        return boardCount - ruledOut.size() <= 1;
    }

    private boolean hasRuledOutAny() {
        return !ruledOut.isEmpty();
    }
}
