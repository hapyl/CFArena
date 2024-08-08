package me.hapyl.fight.guesswho.gui;

import com.google.common.collect.Sets;
import me.hapyl.fight.database.entry.GuessWhoEntry;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.guesswho.GameResult;
import me.hapyl.fight.guesswho.GuessWhoPlayer;
import me.hapyl.eterna.module.inventory.ItemBuilder;
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
                data.getEntry().incrementStat(GuessWhoEntry.StatType.FORFEITS);

                data.triggerLose();

                // Achievement
                Achievements.FORFEIT_GUESS_WHO.complete(data.getPlayer());
            }, ClickType.SHIFT_RIGHT);
        }

        if (!data.isMyTurn()) {
            return;
        }

        final boolean hasRuledOutAny = hasRuledOutAny();

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
            // If haven't rules out and the last hero on board, guess
            if (boardCount == 1) {
                if (ruledOut.isEmpty()) {
                    builder.addLore(Color.BUTTON + "Click to guess!");
                    builder.addSmartLore("It's the last hero, have to guess!", "&a&o");
                }
                else {
                    builder.addLore(Color.ERROR + "Cannot guess this round!");
                }
            }
            else {
                // If it's the last hero, don't allow ruling out
                if (isLastHeroIncludingRuledOut()) {
                    builder.addLore(Color.ERROR + "Cannot rule out the last hero!");
                    return builder;
                }

                builder.addLore(Color.BUTTON + "Click to rule out!");
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

        if (boardCount == 1) {
            data.guess(hero);
            return;
        }

        if (ruledOut.contains(hero)) {
            ruledOut.remove(hero);
            data.playSound(Sound.BLOCK_END_PORTAL_FRAME_FILL, 0.0f);
        }
        else {
            if (data.isRuledOut(hero)) {
                data.sendMessage("This hero is ruled out!");
                return;
            }

            if (isLastHeroIncludingRuledOut()) {
                data.sendMessage("&cCannot rule out the last hero!");
                data.playSound(Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f);
                return;
            }

            ruledOut.add(hero);
            data.playSound(Sound.BLOCK_END_PORTAL_FRAME_FILL, 1.0f);
        }

        update();
    }

    private boolean isLastHeroIncludingRuledOut() {
        return boardCount - ruledOut.size() <= 1;
    }

    private boolean hasRuledOutAny() {
        return !ruledOut.isEmpty();
    }

}
