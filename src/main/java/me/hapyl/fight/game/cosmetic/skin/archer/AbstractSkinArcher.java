package me.hapyl.fight.game.cosmetic.skin.archer;

import me.hapyl.eterna.module.particle.ParticleBuilder;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.skin.Skin;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import org.bukkit.Color;
import org.bukkit.Location;

import javax.annotation.Nullable;

public abstract class AbstractSkinArcher extends Skin {

    public AbstractSkinArcher() {
        super(HeroRegistry.ARCHER);

        setName("Spring Archer");
        setDescription("""
                The most beautiful season wants the most beautiful cloths.
                """);

        setRarity(Rarity.RARE);
        setRubyPrice(10);
    }

    @Nullable
    public abstract Color getHawkeyeArrowColor();

    @Nullable
    public abstract Color getTripleShotArrowColor();

    @Nullable
    public abstract Color getShockDartArrowColor();

    @Nullable
    public abstract ParticleBuilder getShockDartBlueColor();

    @Nullable
    public abstract ParticleBuilder getShockDartRedColor();

    public abstract boolean hawkeyeArrowTick(GamePlayer player, Location location);

    public abstract boolean boomArrowTick(GamePlayer gamePlayer, Location location);
}
