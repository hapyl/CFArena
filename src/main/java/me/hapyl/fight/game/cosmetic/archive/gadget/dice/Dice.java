package me.hapyl.fight.game.cosmetic.archive.gadget.dice;

import me.hapyl.fight.CF;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.database.entry.CurrencyEntry;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.gadget.Gadget;
import me.hapyl.fight.game.reward.Reward;
import me.hapyl.fight.game.reward.RewardDisplay;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.util.Validate;
import me.hapyl.spigotutils.module.util.WeightedCollection;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class Dice extends Gadget {

    private final WeightedCollection<DiceSide> sideWeight;
    private final long rollCost;
    private final Reward rollJackpot;

    public Dice(String name, Rarity rarity, long rollCost, @Nonnull Reward rollJackpot) {
        super(name, rarity, Material.PLAYER_HEAD);

        setCooldownSec(2);

        this.sideWeight = new WeightedCollection<>();
        this.rollCost = rollCost;
        this.rollJackpot = rollJackpot;

        setDescription("""
                Costs &6%,d coins&7 to roll between 1-6.
                                
                If you roll a &5&l6&7, earn:
                """, rollCost);
    }

    @Override
    public void addExtraLore(@Nonnull ItemBuilder builder, @Nonnull Player player) {
        rollJackpot.getDisplay(player).forEach(builder::addLore);

        builder.addLore();
        builder.addLore("But &close&7 this gadget.");
    }

    public void onRoll(Player player, DiceSide side) {
        final int number = side.getSide();

        if (number == 6) {
            final RewardDisplay display = rollJackpot.getDisplay(player);

            getHandle().setUnlocked(player, false);
            rollJackpot.grant(player);

            Chat.sendMessage(player, "&6&lDICE! &eYou rolled a %s&e and earned:", side.toString());
            display.forEach(string -> Chat.sendMessage(player, string));
        }
        else {
            Chat.sendMessage(player, "&6&lDICE! &eYou rolled a %s&e!", side.toString());
        }
    }

    public void setSide(int side, String texture, int weight) {
        Validate.isTrue(side >= 1 && side <= 6, "Side must be between 1 and 6, not %s!".formatted(side));

        final DiceSide diceSide = new DiceSide(side, texture);

        if (side == 6) {
            this.texture = texture;
        }

        sideWeight.add(diceSide, weight);
    }

    @Nonnull
    @Override
    public Response execute(@Nonnull Player player) {
        final PlayerDatabase database = CF.getDatabase(player);
        final CurrencyEntry currency = database.currencyEntry;

        if (!currency.has(Currency.COINS, rollCost)) {
            return Response.error("You don't have enough coins to roll this dice!");
        }

        currency.subtract(Currency.COINS, rollCost);
        new DiceRoll(player, this);

        return Response.OK;
    }

    @Nonnull
    public DiceSide getRandomSide() {
        return sideWeight.get();
    }
}
