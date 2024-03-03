package me.hapyl.fight.game.cosmetic.skin;

import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public interface SkinEffectHandler {

    void onTick(@Nonnull GamePlayer player, int tick);

    void onKill(@Nonnull GamePlayer player, @Nonnull GameEntity victim);

    void onDeath(@Nonnull GamePlayer player, @Nonnull GameEntity killer);

    void onMove(@Nonnull GamePlayer player, @Nonnull Location to);

    void onStandingStill(@Nonnull GamePlayer player);

}
