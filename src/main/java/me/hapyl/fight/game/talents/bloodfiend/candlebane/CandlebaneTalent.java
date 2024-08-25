package me.hapyl.fight.game.talents.bloodfiend.candlebane;


import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.bloodfiend.taunt.TauntTalent;
import me.hapyl.fight.registry.Key;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import java.util.Collection;

public class CandlebaneTalent extends TauntTalent {

    @DisplayField protected final short pillarHeight = 7;
    @DisplayField protected final short pillarClicks = 10;
    @DisplayField(suffix = "❤") protected final double damagePerInterval = 5.0d;

    public CandlebaneTalent(@Nonnull Key key) {
        super(key, "Candlebane Pillar", 10, 50);

        setItem(Material.RED_CANDLE);
        setDurationSec(60);
        setCooldownSec(20);
    }

    @Nonnull
    @Override
    public String getDescription() {
        return """
                Periodically deals &cdamage&7.
                """;
    }

    @Nonnull
    @Override
    public String getHowToRemove() {
        return """
                The pillar must be compacted by alternating &eleft&7 and &6right&7 clicks.
                """;
    }

    @Nonnull
    @Override
    public Candlebane createTaunt(@Nonnull GamePlayer player, @Nonnull Location location) {
        return new Candlebane(this, player, location);
    }

    @Nonnull
    public Collection<Candlebane> getPillars() {
        return getTaunts(Candlebane.class);
    }

}
