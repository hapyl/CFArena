package me.hapyl.fight.game.heroes;

import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

public class HeroProfile {

    private final Hero hero;
    private final List<Archetype> archetypes;

    private Gender gender;
    private Affiliation affiliation;
    private Race race;

    HeroProfile(Hero hero) {
        this.hero = hero;
        this.archetypes = Lists.newArrayList(Archetype.NOT_SET);
        this.gender = Gender.UNKNOWN;
        this.affiliation = Affiliation.NOT_SET;
        this.race = Race.HUMAN;
    }

    @Nonnull
    public Hero hero() {
        return hero;
    }

    @Nonnull
    public Gender getGender() {
        return gender;
    }

    public void setGender(@Nonnull Gender gender) {
        this.gender = gender;
    }

    @Nonnull
    public Affiliation getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(@Nonnull Affiliation affiliation) {
        this.affiliation = affiliation;
    }

    @Nonnull
    public Race getRace() {
        return race;
    }

    public void setRace(@Nonnull Race race) {
        this.race = race;
    }

    @Nonnull
    public List<Archetype> getArchetypes() {
        return Collections.unmodifiableList(archetypes);
    }

    public void setArchetypes(@Nonnull Archetype... archetypes) {
        this.archetypes.clear();
        this.archetypes.addAll(Arrays.asList(archetypes));
    }

    public void iterateArchetypes(BiConsumer<Integer, Archetype> consumer) {
        int index = 0;
        for (Archetype archetype : archetypes) {
            consumer.accept(index++, archetype);
        }
    }

    @Nonnull
    public String getSimpleArchetypesDisplay() {
        final StringBuilder builder = new StringBuilder();

        iterateArchetypes((i, archetype) -> {
            if (i != 0) {
                builder.append(" ");
            }

            builder.append(archetype.getPrefix());
        });

        return builder.toString();
    }
}
