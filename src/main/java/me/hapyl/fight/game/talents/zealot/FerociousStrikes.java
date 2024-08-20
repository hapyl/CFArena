package me.hapyl.fight.game.talents.zealot;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.talents.PassiveTalent;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class FerociousStrikes extends PassiveTalent {

    public static final int maxStrikes = 10;

    public FerociousStrikes(@Nonnull DatabaseKey key) {
        super(key, "Ferocious Strikes");

        setDescription("""
                Each time you &nscore&7 a %1$s attack, gain a %2$s stack.
                
                Each %2$s stack will &nimprove&7 your &bultimate&7 &cdamage&7.
                &8;;You cannot gain stacks while the ultimate is active.
                """.formatted(AttributeType.FEROCITY, Named.FEROCIOUS_STRIKE)
        );

        setItem(Material.RED_DYE);
    }
}
