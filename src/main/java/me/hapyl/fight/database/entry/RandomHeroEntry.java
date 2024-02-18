package me.hapyl.fight.database.entry;

import com.google.common.collect.Sets;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.spigotutils.module.util.CollectionUtils;
import me.hapyl.spigotutils.module.util.Enums;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class RandomHeroEntry extends PlayerDatabaseEntry {

    /**
     * <blockquote>
     * random_hero_prefs:
     * {
     * enabled: bool,
     * include: []
     * }
     * </blockquote>
     */

    public RandomHeroEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase);

        setPath("random_hero_prefs");
    }

    public boolean isEnabled() {
        return getValueInPath("enabled", false);
    }

    public void setEnabled(boolean enabled) {
        setValueInPath("enabled", enabled);
    }

    @Nonnull
    public Set<Archetype> getInclude() {
        final Set<Archetype> include = Sets.newHashSet();

        for (Archetype archetype : Archetype.values()) {
            if (getValueInPath("include." + archetype.name(), false)) {
                include.add(archetype);
            }
        }

        return include;
    }

    public void setInclude(@Nonnull Set<Archetype> include) {
        for (Archetype archetype : Archetype.values()) {
            final boolean contains = include.contains(archetype);

            setValueInPath("include." + archetype.name(), contains ? true : null);
        }
    }

    @Nullable
    public Heroes getLastSelectedHero() {
        return Enums.byName(Heroes.class, getValueInPath("lastSelectedHero", ""));
    }

    public void setLastSelectedHero(@Nullable Heroes hero) {
        setValueInPath("lastSelectedHero", hero != null ? hero.name() : null);
    }

    @Nonnull
    public Heroes getRandomHero() {
        final Set<Archetype> include = getInclude();
        final Player player = getOnlinePlayer();

        if (player == null) {
            return Heroes.DEFAULT_HERO;
        }

        if (include.isEmpty()) {
            return Heroes.randomHero(player);
        }

        final Set<Heroes> availableHeroes = Sets.newHashSet();

        include.forEach(archetype -> {
            final List<Heroes> heroes = Heroes.byArchetype(archetype);
            heroes.removeIf(hero -> hero.isLocked(player));

            availableHeroes.addAll(heroes);
        });

        return CollectionUtils.randomElement(availableHeroes, Heroes.DEFAULT_HERO);
    }
}
