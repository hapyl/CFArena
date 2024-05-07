package me.hapyl.fight.game.talents.shadow_assassin;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.shadow_assassin.ShadowAssassin;
import me.hapyl.fight.game.talents.Talent;

public abstract class ShadowAssassinModeSpecificTalent extends Talent {

    public final ShadowAssassinTalent parent;

    public ShadowAssassinModeSpecificTalent(ShadowAssassinTalent parent) {
        super(parent.getName());

        this.parent = parent;
    }

    public Response execute1(GamePlayer player, ShadowAssassin hero) {
        final Response execute = execute(player);

        if (execute != null && !execute.isError()) {
            player.setCooldown(parent.getMaterial(), getCooldown());
        }

        return execute;
    }

}
