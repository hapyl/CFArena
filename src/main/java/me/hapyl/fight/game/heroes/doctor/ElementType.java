package me.hapyl.fight.game.heroes.doctor;

import me.hapyl.fight.game.talents.InsteadOfNull;
import me.hapyl.eterna.module.chat.Chat;
import org.bukkit.Material;

import javax.annotation.Nonnull;

/**
 * A decision was made to hard code every block value.
 */
public enum ElementType {

    VERY_HEAVY(new VeryHeavyElement()),
    HEAVY(new HeavyElement()),
    STURDY(new SturdyElement()),
    SHARP(new SharpElement()),
    SOFT(new SoftElement()),
    NULL(new Element()),
    ;

    private final Element element;

    ElementType(Element element) {
        this.element = element;
    }

    public Element getElement() {
        return element;
    }

    @Nonnull
    @InsteadOfNull("NULL")
    public static ElementType getElement(Material material) {
        if (!material.isBlock()) {
            return ElementType.NULL;
        }

        // Player heads are card-coded for named heads
        if (material == Material.PLAYER_HEAD) {
            return STURDY;
        }

        // Check if a type has a value
        for (ElementType type : values()) {
            if (type.element.isApplicable(material)) {
                return type;
            }
        }

        return NULL;
    }

    @Override
    public String toString() {
        return Chat.capitalize(this);
    }
}