package me.hapyl.fight.game.heroes.frostbite;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.UltimateResponse;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class FrostbiteUltimate extends UltimateTalent {

    @DisplayField public final int blockCount = 24;
    @DisplayField public final double distance = 10.0d;

    @DisplayField public final double critChanceReduction = 1;
    @DisplayField public final double critDamageReduction = 1;
    @DisplayField public final double cooldownIncrease = 0.5d;

    @DisplayField public final int debuffDuration = 20;

    public FrostbiteUltimate(int pointCost) {
        super("Eternal Freeze", pointCost);

        setDescription("""
                Unleash the {name} upon your enemies, creating a massive &f&lsnow field&7 that &4debuffs&7 enemies.
                                
                The field orbits around for {duration}:
                └ &bDecreasing&7 enemies %s and %s.
                └ &bSlowing&7 and &bimpairing&7 vision.
                └ Increasing cooldowns.
                """, AttributeType.CRIT_CHANCE, AttributeType.CRIT_DAMAGE);

        setType(TalentType.IMPAIR);
        setItem(Material.HEART_OF_THE_SEA);
        setDurationSec(10);
    }

    @Nonnull
    @Override
    public UltimateResponse useUltimate(@Nonnull GamePlayer player) {
        new EternalFreeze(player, this);

        return UltimateResponse.OK;
    }
}
