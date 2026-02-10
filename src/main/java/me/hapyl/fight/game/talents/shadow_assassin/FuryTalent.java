package me.hapyl.fight.game.talents.shadow_assassin;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.shadow_assassin.ShadowAssassin;
import me.hapyl.fight.game.heroes.shadow_assassin.ShadowAssassinData;
import me.hapyl.fight.util.displayfield.DisplayField;

public abstract class FuryTalent extends ShadowAssassinModeSpecificTalent {

    @DisplayField public final int furyCost;

    public FuryTalent(ShadowAssassinTalent parent, int furyCost) {
        super(parent);

        this.furyCost = furyCost;
    }

    @Override
    public Response execute1(GamePlayer player, ShadowAssassin hero) {
        final ShadowAssassinData data = hero.getData(player);

        if (data.getEnergy() < furyCost) {
            return Response.error("Not enough energy!");
        }

        final Response execute = super.execute1(player, hero);

        if (execute != null && !execute.isError()) {
            data.subtractEnergy(furyCost);
        }

        return execute;
    }
}
