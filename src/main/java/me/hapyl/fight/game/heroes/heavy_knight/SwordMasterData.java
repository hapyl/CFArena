package me.hapyl.fight.game.heroes.heavy_knight;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.talents.Talent;

import javax.annotation.Nonnull;

public class SwordMasterData extends PlayerData {

    public final BufferedOrder<Talent> buffer;

    public SwordMasterData(SwordMaster hero, GamePlayer player) {
        super(player);

        final Talent passive = hero.getPassiveTalent();

        this.buffer = new BufferedOrder<>(2500, hero.getFirstTalent(), hero.getSecondTalent(), hero.getThirdTalent()) {
            @Override
            public void onCorrectOrder() {
                clear();

                passive.startCd(player, passive.getCooldown());
            }

            @Override
            public boolean offer(@Nonnull Talent talent) {
                if (passive.hasCd(player)) {
                    return false;
                }

                return super.offer(talent);
            }
        };
    }

    @Override
    public void remove() {

    }
}
