package me.hapyl.fight.game.talents.himari;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Constants;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.himari.HimariData;
import me.hapyl.fight.game.talents.Talent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public abstract class HimariTalent extends Talent {

    protected static final String howToGetString = "&8&o;;This talent must be rolled through Lucky Day!";

    public HimariTalent(@Nonnull Key key, @Nonnull String name) {
        super(key, name);

        setCooldown(Constants.INDEFINITE_COOLDOWN);
    }

    @Nonnull
    @Override
    public String getTalentClassType() {
        return "Lucky " + super.getTalentClassType();
    }

    // This method is execute replacement because execute is used as precondition method
    @Nonnull
    public abstract Response executeHimari(@Nonnull GamePlayer player);

    // Allow executing this talent
    public void allowExecution(@Nonnull GamePlayer player) {
        stopCd(player);
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        startCd(player, Constants.INDEFINITE_COOLDOWN);
    }

    @Override
    public final Response execute(@NotNull GamePlayer player) {
        HimariData data = player.getPlayerData(HeroRegistry.HIMARI);
        HimariTalent talent = data.getTalent();

        if (talent == null || !talent.equals(this)) {
            return Response.error("This talent is not unlocked yet..");
        }
        Response response = executeHimari(player);

        if (!response.isOk()) {
            return response;
        }

        data.setTalent(null);
        return response;
    }

}
