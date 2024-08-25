package me.hapyl.fight.game.talents.techie;


import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.registry.Key;
import me.hapyl.fight.util.displayfield.DisplayField;

import javax.annotation.Nonnull;

public abstract class TechieTalent extends Talent implements DeviceHack {

    @DisplayField private int castingTime;

    public TechieTalent(@Nonnull Key key, @Nonnull String name) {
        super(key, name);

        setDescription("""
                Equip a &bhacking device&7; after a short &3casting time&7, %s
                """.formatted(getHackDescription()));
    }

    public void setCastingTime(int castingTime) {
        this.castingTime = castingTime;
    }

    public int getCastingTime() {
        return castingTime;
    }

    @Nonnull
    public abstract String getHackDescription();

    @Override
    public final Response execute(@Nonnull GamePlayer player) {
        startDevice(player);
        return Response.OK;
    }
}
