package me.hapyl.fight.game.heroes.himari;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.talents.himari.HimariTalent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HimariData extends PlayerData {

    private final Himari himari;
    private HimariTalent himariTalent;

    public HimariData(GamePlayer player, Himari himari) {
        super(player);

        this.himari = himari;
    }

    @Nonnull
    public Himari getHero() {
        return himari;
    }

    public void setTalent(@Nullable HimariTalent talent) {
        this.himariTalent = talent;

        // Allow execution // idk if used for something if cooldown is used stop cooldown or something idk
        if (talent != null) {
            talent.allowExecution(player);
        }
    }

    @Nullable
    public HimariTalent getTalent() {
        return himariTalent;
    }

    @Override
    public void remove() {

    }
}
