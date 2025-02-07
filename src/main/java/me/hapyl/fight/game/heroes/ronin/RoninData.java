package me.hapyl.fight.game.heroes.ronin;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;

public class RoninData extends PlayerData {

    public ChargeAttackData chargeAttack;

    public RoninData(GamePlayer player) {
        super(player);
    }

    @Override
    public void remove() {
        cancelChargeAttack();
    }

    public void cancelChargeAttack() {
        if (chargeAttack == null) {
            return;
        }

        chargeAttack.cancel();
        chargeAttack = null;
    }
}
