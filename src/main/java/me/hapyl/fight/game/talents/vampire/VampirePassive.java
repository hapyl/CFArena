package me.hapyl.fight.game.talents.vampire;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.heroes.Affiliation;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.util.MapMaker;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import java.util.Map;

public class VampirePassive extends PassiveTalent {

    private final Map<AttributeType, Double> legionAttributes = MapMaker.<AttributeType, Double>ofLinkedHashMap()
            .put(AttributeType.MAX_HEALTH, 5.0d)
            .put(AttributeType.ATTACK, 10.0d)
            .put(AttributeType.CRIT_DAMAGE, 7.5d)
            .put(AttributeType.SPEED, 3.0d)
            .put(AttributeType.ATTACK_SPEED, 4.0d)
            .makeMap();

    public VampirePassive(@Nonnull DatabaseKey key) {
        super(key, "Blood Thirst/Legion");

        setDescription("""
                &b&l&nBlood Thirst
                Dealing &cdamage&7 &4&ndrains&7 your &chealth&7, increasing the &cdamage&7 based on your &a&ncurrent&7 health.
                
                &b&l&nLegion
                The more heroes from the %1$s&7 are in the &a&nteam&7, the stronger your &fspirit&7 is.
                
                Each hero increases the following stats:
                %2$s
                """.formatted(Affiliation.CHATEAU, formatAttributeIncrease()));

        legionAttributes.forEach(((attribute, value) -> {
            addAttributeDescription(attribute.getName() + " Increase", value);
        }));

        setItem(Material.OMINOUS_BOTTLE);
    }

    public TemperInstance getLegionIncrease(int heroCount) {
        final TemperInstance temperInstance = Temper.LEGION.newInstance();

        legionAttributes.forEach((attribute, value) -> {
            temperInstance.increaseScaled(attribute, value * heroCount);
        });

        return temperInstance;
    }

    private String formatAttributeIncrease() {
        final StringBuilder builder = new StringBuilder();

        legionAttributes.forEach((attribute, value) -> {
            builder.append("&7› ").append(attribute.toString()).append("\n");
        });

        return builder.toString();
    }

}
