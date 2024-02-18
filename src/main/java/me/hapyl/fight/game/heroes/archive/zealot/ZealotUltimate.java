package me.hapyl.fight.game.heroes.archive.zealot;

import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class ZealotUltimate extends UltimateTalent {

    @DisplayField public final double baseDamage = 4.0d;
    @DisplayField public final double landingOffset = 10.0d;
    @DisplayField public final double distance = 5.0d;
    @DisplayField public final int impactTime = 15;
    @DisplayField public final double directionOffset = 2.5d;
    @DisplayField public final double landingSpeed = Math.PI / 14;

    public ZealotUltimate(@Nonnull Hero hero) {
        super(hero, "Maintain Order", """
                Command a &egiant sword&7 to &afall down&7 from the &bsky&7.

                Upon landing, &4explodes&7 violently, inflicting %s on nearby &cenemies&7 based on your %s stacks.
                """.formatted(AttributeType.FEROCITY, Named.FEROCIOUS_STRIKE), 60);

        setType(Type.DAMAGE);
        setItem(Material.GOLDEN_SWORD);
        setDurationSec(12);

        setSound(Sound.ENTITY_WITHER_HURT, 0.0f);
    }
}
