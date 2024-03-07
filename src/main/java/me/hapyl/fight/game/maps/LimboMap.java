package me.hapyl.fight.game.maps;

import me.hapyl.fight.game.effect.Effect;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.maps.features.LimboFeature;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class LimboMap extends GameMap {

    protected LimboMap() {
        super("Limbo");

        setDescription("""
                A lighthouse.
                """);

        setMaterial(Material.SCULK_VEIN);
        setSize(Size.LARGE);
        setTicksBeforeReveal(100);
        setTime(18000);
        addFeature(new LimboFeature());

        addLocation(6500.0, 58.5, -17);
        addLocation(6500.0, 58.5, -17);
        addLocation(6500, 124, 37, -180f, 0f);
        addLocation(6500, 124, 37, -180f, 0f);
        addLocation(6500, 69, 53, -180f, 0f);
        addLocation(6500, 69, 53, -180f, 0f);
        addLocation(6485.0, 58, 27.0, 90f, 0f);
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        player.addEffect(Effects.NIGHT_VISION, 1, Effect.INFINITE_DURATION);
    }

}
