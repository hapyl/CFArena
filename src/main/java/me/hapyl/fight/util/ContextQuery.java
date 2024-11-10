package me.hapyl.fight.util;

import me.hapyl.eterna.module.util.Described;

import javax.annotation.Nonnull;

public interface ContextQuery extends Described {

    default boolean isMatching(@Nonnull String query) {
        // disallow words lower than 3 chars
        // to not match 'an', 'the', etc
        if (query.length() < 3) {
            return false;
        }

        query = query.toLowerCase();

        return getNameLowerCase().contains(query) || getDescriptionLowerCase().contains(query);
    }

}
