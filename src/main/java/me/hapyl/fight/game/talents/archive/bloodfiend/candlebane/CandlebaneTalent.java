package me.hapyl.fight.game.talents.archive.bloodfiend.candlebane;

import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.archive.bloodfiend.taunt.TauntTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import java.util.Collection;

public class CandlebaneTalent extends TauntTalent<Candlebane> {

    @DisplayField protected final short pillarHeight = 7;
    @DisplayField protected final short pillarClicks = 10;
    @DisplayField(suffix = "&c❤") protected final double damagePerInterval = 5.0d;
    @DisplayField protected final int interval = 50;

    public CandlebaneTalent() {
        super("Candlebane Pillar");

        setItem(Material.RED_CANDLE);
        setDurationSec(35);
        setCooldownSec(45);
    }

    @Nonnull
    @Override
    public String getDescription() {
        return """
                &bTaunted&7 player will suffer &c{damagePerInterval}&7 damage every &b{interval}&7.
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
    public Candlebane createTaunt(@Nonnull GamePlayer player, @Nonnull GamePlayer target, @Nonnull Location location) {
        return new Candlebane(this, player, target, location);
    }

    @Override
    @Nonnull
    public EnumDamageCause getDamageCause() {
        return EnumDamageCause.CANDLEBANE;
    }

    @Nonnull
    public Collection<Candlebane> getPillars() {
        return playerTaunt.values();
    }

}
