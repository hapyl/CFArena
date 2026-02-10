package me.hapyl.fight.game.talents.echo;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.talents.Talent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class EchoTalent extends Talent {
    protected EchoTalent(@Nonnull Key key, @Nonnull String name) {
        super(key, name);
    }

    @Override
    public void setDescription(@Nonnull String description) {
        super.setDescription("""
                While in the %s, %s
                """.formatted(Named.ECHO_WORLD, description));
    }

    @Nonnull
    public abstract Response executeEcho(@Nonnull GamePlayer player);

    @Override
    public final @Nullable Response execute(@Nonnull GamePlayer player) {
        if (!HeroRegistry.ECHO.getPlayerData(player).isInEchoWorld()) {
            return Response.error("Cannot use this in the \"real\" world!");
        }

        return executeEcho(player);
    }
}
