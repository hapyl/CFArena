package me.hapyl.fight.game.talents.himari;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.himari.HimariData;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.registry.Key;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class HimariTalent extends Talent {
    public HimariTalent(@Nonnull Key key, @Nonnull String name) {
        super(key, name);
    }

    // This method is execute replacement because execute is used as precondition method
    @Nullable
    public abstract Response executeHimari(@Nonnull GamePlayer player);

    // Allow executing this talent
    public void allowExecution(@Nonnull GamePlayer player) {
        setCooldownSec(0);
        player.sendMessage(getName() + " is now ready to be used!");
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
