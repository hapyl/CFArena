package me.hapyl.fight.terminology;

import me.hapyl.fight.util.ContextQuery;
import me.hapyl.fight.util.Styler;

import javax.annotation.Nonnull;

public class Term implements ContextQuery {

    public static final Styler TERM_STYLER = new Styler()
            .setPrefix("&8&l⌈&f&n")
            .setSuffix("&8&l⌋&7");

    private final String[] strings;

    /**
     * {@link Term#builder()} or extend.
     */
    protected Term() {
        this.strings = new String[3];
    }

    @Nonnull
    public String getName() {
        return strings[0];
    }

    @Override
    public void setName(@Nonnull String name) {
        this.strings[0] = name;
    }

    @Nonnull
    public String getShortDescription() {
        return strings[1];
    }

    protected void setShortDescription(@Nonnull String shortDescription) {
        this.strings[1] = shortDescription;
    }

    @Nonnull
    public String getDescription() {
        return strings[2];
    }

    @Override
    public void setDescription(@Nonnull String description) {
        this.strings[2] = description;
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
        return TERM_STYLER.style(getName());
    }

    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements me.hapyl.eterna.module.util.Builder<Term> {

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
