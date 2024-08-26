package me.hapyl.fight.game.cosmetic.archive.gadget.dice;

import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.reward.CurrencyReward;
import me.hapyl.fight.game.reward.Reward;
import me.hapyl.fight.game.reward.RewardDescription;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class HighClassDice extends Dice {
    public HighClassDice() {
        super("High-Class Dice", Rarity.LEGENDARY, 66_666, new HighClassDice.DiceReward());

        setSide(1, "4675996c9164cf409f9fc9024231ca301a4f024e3a306c8f3f4caa062a5576b8", 50);
        setSide(2, "6c8c54dc6c2d40625a72e95d8a80f04a8f9fead318f370a97a82ab8872542477", 50);
        setSide(3, "47ed98ce4e10b59767334789e33b112dafba93691eef64273288c2f1fd324e34", 50);
        setSide(4, "9e6800bc7b0230694b1c0f24bfa9edb22f3478f9e4ee7fc4c4398a8799f3840e", 50);
        setSide(5, "826aa157fe7680b3bd21d53c061e4a61c46a96078d641ca6bbfc604e219de19e", 50);
        setSide(6, "586b745566284a05366baff2807d9d8f8344612aabddeb012c47c7252e34e731", 1);
    }

    private static class DiceReward extends CurrencyReward {

        protected DiceReward() {
            super("High Class Dice");

            withCoins(1_000_000);
            withExp(10_000);
            withRubies(100);
        }

        @Nonnull
        @Override
        public RewardDescription getDescription(@Nonnull Player player) {
            final RewardDescription display = super.getDescription(player);

            display.add(Cosmetics.DICE_STATUS.getCosmetic().getFormatted());
            return display;
        }

        @Override
        public void grant(@Nonnull Player player) {
            super.grant(player);

            Cosmetics.DICE_STATUS.setUnlocked(player, true);
        }
    }

}
