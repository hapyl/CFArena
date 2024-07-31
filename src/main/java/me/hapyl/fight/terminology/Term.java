package me.hapyl.fight.terminology;

import me.hapyl.fight.util.ContextQuery;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;

public class Term implements ContextQuery {

    public static final ChatColor DEFAULT_SUFFIX_COLOR = ChatColor.GRAY;

    private final String[] strings;

    /**
     * {@link Term#builder()} or extend.
     */
    protected Term() {
        this.strings = new String[3];
    }

    protected void setName(@Nonnull String name) {
        this.strings[0] = name;
    }

    protected void setShortDescription(@Nonnull String shortDescription) {
        this.strings[1] = shortDescription;
    }

    protected void setDescription(@Nonnull String description) {
        this.strings[2] = description;
    }

    @Nonnull
    public String getName() {
        return strings[0];
    }

    @Nonnull
    public String getShortDescription() {
        return strings[1];
    }

    @Nonnull
    public String getDescription() {
        return strings[2];
    }

    @Override
    public boolean isMatching(@Nonnull String query) {
        if (query.length() < 3) {
            return false;
        }

        query = query.toLowerCase();

        for (String string : strings) {
            if (string.toLowerCase().contains(query)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public final String toString() {
        return (ChatColor.WHITE + ChatColor.UNDERLINE.toString()) + getName() + DEFAULT_SUFFIX_COLOR;
    }

    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements me.hapyl.fight.util.Builder<Term> {

        private final Term term;

        Builder() {
            this.term = new Term();
        }

        public Builder setName(String name) {
            term.setName(name);
            return this;
        }

        public Builder setShortDescription(String shortDescription) {
            term.setShortDescription(shortDescription);
            return this;
        }

        public Builder setDescription(String description) {
            term.setDescription(description);
            return this;
        }

        @Nonnull
        @Override
        public Term build() {
            return term;
        }
    }
}
