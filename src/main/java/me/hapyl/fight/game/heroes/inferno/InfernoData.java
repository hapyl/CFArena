package me.hapyl.fight.game.heroes.inferno;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.PlayerData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class InfernoData extends PlayerData {

    @Nullable public InfernoDemon currentDemon;

    public final Set<DamageData> typhoeusDamage;
    public final Set<LivingGameEntity> quaziiBeamHits;

    public InfernoData(@Nonnull GamePlayer player) {
        super(player);

        this.quaziiBeamHits = Sets.newHashSet();
        this.typhoeusDamage = Sets.newHashSet();
    }

    @Override
    public void remove() {
        if (currentDemon == null) {
            return;
        }

        currentDemon.entity().remove();
        currentDemon = null;
    }

    public record DamageData(double damage, long dealtAt) {
    }
}
