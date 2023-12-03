package me.hapyl.fight.game.talents.archive.bloodfiend.candlebane;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.archive.bloodfiend.taunt.TauntTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Collection;

public class CandlebaneTalent extends TauntTalent<Candlebane> {

    @DisplayField protected final short pillarHeight = 7;
    @DisplayField protected final short pillarClicks = 10;
    @DisplayField protected final double damage = 50.0d;
    @DisplayField(suffix = "&c❤") protected final double damagePerInterval = 2.0d;
    @DisplayField protected final int interval = 50;

    //@DisplayField protected final int spawnInterval = Tick.fromSecond(10);

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
                &bTaunted&7 player will suffer &c{damagePerInterval} damage every &b{interval}&7.
                """;
    }

    @Nonnull
    @Override
    public String getHowToRemove() {
        return """
                The pillar must be compacted by clicking on it with a &acorrect&7 click.
                &8&o;;The correct click is displayed above the pillar.
                """;
    }

    @Override
    public double getExplosionDamage() {
        return damage;
    }

    @Override
    @Nonnull
    public EnumDamageCause getDamageCause() {
        return EnumDamageCause.CANDLEBANE;
    }

    @Override
    public Candlebane createTaunt(Player player, GamePlayer target) {
        return new Candlebane(this, player, target);
    }

    @Nonnull
    public Collection<Candlebane> getPillars() {
        return playerTaunt.values();
    }

}
