package me.hapyl.fight.game.cosmetic.gadget.dice;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.reward.RepeatableReward;
import me.hapyl.fight.game.reward.RewardDescription;
import me.hapyl.fight.game.reward.RewardResource;
import me.hapyl.fight.registry.Registries;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class HighClassDiceCosmetic extends Dice {
    public HighClassDiceCosmetic(@Nonnull Key key) {
        super(key, "High-Class Dice", Rarity.LEGENDARY, 66_666, new HighClassDiceCosmetic.DiceReward());

        setSide(1, "4675996c9164cf409f9fc9024231ca301a4f024e3a306c8f3f4caa062a5576b8", 50);
        setSide(2, "6c8c54dc6c2d40625a72e95d8a80f04a8f9fead318f370a97a82ab8872542477", 50);
        setSide(3, "47ed98ce4e10b59767334789e33b112dafba93691eef64273288c2f1fd324e34", 50);
        setSide(4, "9e6800bc7b0230694b1c0f24bfa9edb22f3478f9e4ee7fc4c4398a8799f3840e", 50);
        setSide(5, "826aa157fe7680b3bd21d53c061e4a61c46a96078d641ca6bbfc604e219de19e", 50);
        setSide(6, "586b745566284a05366baff2807d9d8f8344612aabddeb012c47c7252e34e731", 1);
    }

    private static class DiceReward extends RepeatableReward {

        protected DiceReward() {
            super("High Class Dice");

            withResource(RewardResource.COINS, 1_000_000);
            withResource(RewardResource.EXPERIENCE, 10_000);
            withResource(RewardResource.RUBY, 100);
        }

        @Override
        public void appendDescription(@Nonnull Player player, @Nonnull RewardDescription description) {
            description.append(Registries.getCosmetics().DICE_STATUS.getFormatted());
        }

        @Override
        public void doGrant(@Nonnull Player player) {
            super.doGrant(player);

            Registries.getCosmetics().DICE_STATUS.setUnlocked(player, true);
        }
    }

}
