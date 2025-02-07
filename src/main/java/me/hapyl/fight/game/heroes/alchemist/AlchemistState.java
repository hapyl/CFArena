package me.hapyl.fight.game.heroes.alchemist;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.loadout.HotBarLoadout;
import me.hapyl.fight.game.talents.TalentRegistry;

import javax.annotation.Nonnull;

public enum AlchemistState {

    NORMAL {
        @Override
        public void apply(@Nonnull GamePlayer player) {
            // Do remove the items in case we're cancelling
            super.apply(player);

            player.giveTalentItems();
        }
    },

    CHOOSING_POTION,
    USING_POTION;

    public void apply(@Nonnull GamePlayer player) {
        TalentRegistry.POTION.potionMap.keySet().forEach(slot -> player.setItem(slot, null));
    }
}
