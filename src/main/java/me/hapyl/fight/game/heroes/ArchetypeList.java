package me.hapyl.fight.game.heroes;

import com.google.common.collect.Lists;
import me.hapyl.fight.util.SequencedCollectionWrapper;

import javax.annotation.Nonnull;
import java.util.*;

public class ArchetypeList implements Iterable<Archetype>, SequencedCollectionWrapper<Archetype> {

    private final Hero hero;
    private final List<Archetype> archetypes; // keep list for order

    ArchetypeList(Hero hero) {
        this.hero = hero;
        this.archetypes = Lists.newArrayList();
    }

    public ArchetypeList add(@Nonnull Archetype archetype) {
        this.archetypes.add(archetype);
        return this;
    }

    public ArchetypeList addAll(@Nonnull Archetype... archetypes) {
        this.archetypes.addAll(Arrays.asList(archetypes));
        return this;
    }

    @Nonnull
    public Hero getHero() {
        return hero;
    }

    /**
     * Gets a sorted view of this hero archetypes.
     *
     * @return a sorted view of this hero archetypes.
     */
    @Nonnull
    public List<Archetype> getArchetypes() {
        final List<Archetype> archetypes = Lists.newArrayList();

        for (Archetype archetype : Archetype.values()) {
            if (this.archetypes.contains(archetype)) {
                archetypes.add(archetype);
            }
        }

        return archetypes;
    }

    @Nonnull
    @Override
    public Iterator<Archetype> iterator() {
        return archetypes.iterator();
    }

    @Nonnull
    @Override
    public SequencedCollection<Archetype> getCollection() {
        return archetypes;
    }

    @Override
    public String toString() {
        return getSimpleDisplay();
    }

    @Nonnull
    public String getSimpleDisplay() {
        final List<Archetype> archetypes = getArchetypes();
        final StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < archetypes.size(); i++) {
            if (i != 0) {
                stringBuilder.append(" ");
            }

            stringBuilder.append(archetypes.get(i).getPrefix());
        }

        return stringBuilder.toString();
    }

}
