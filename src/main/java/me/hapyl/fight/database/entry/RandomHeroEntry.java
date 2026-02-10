package me.hapyl.fight.database.entry;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroProfile;
import me.hapyl.fight.game.heroes.HeroRegistry;
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

    public RandomHeroEntry(@Nonnull PlayerDatabase playerDatabase) {
        super(playerDatabase, "random_hero_prefs");
    }

    public boolean isEnabled() {
        return getValue("enabled", false);
    }

    public void setEnabled(boolean enabled) {
        setValue("enabled", enabled);
    }

    @Nonnull
    public Set<Archetype> getInclude() {
        final Set<Archetype> include = Sets.newHashSet();

        for (Archetype archetype : Archetype.values()) {
            if (getValue("include." + archetype.name(), false)) {
                include.add(archetype);
            }
        }

        return include;
    }

    public void setInclude(@Nonnull Set<Archetype> include) {
        for (Archetype archetype : Archetype.values()) {
            final boolean contains = include.contains(archetype);

            setValue("include." + archetype.name(), contains ? true : null);
        }
    }

    @Nullable
    public Hero getLastSelectedHero() {
        return HeroRegistry.ofStringOrNull(getValue("lastSelectedHero", ""));
    }

    public void setLastSelectedHero(@Nullable Hero hero) {
        setValue("lastSelectedHero", hero != null ? hero.getKeyAsString() : null);
    }

    @Nonnull
    public Hero getRandomHero() {
        final Set<Archetype> include = getInclude();
        final Player player = player().orElse(null);

        if (player == null) {
            return HeroRegistry.defaultHero();
        }

        // If all archetypes allowed, return randomHero()
        if (include.isEmpty()) {
            return HeroRegistry.randomHero(player);
        }

        // Else filter by archetype
        final List<Hero> availableHeroes = HeroRegistry.playable();
        
        availableHeroes.removeIf(hero -> {
            if (hero.isLocked(player)) {
                return true;
            }
            else {
                final HeroProfile profile = hero.getProfile();
                final List<Archetype> archetypes = profile.getArchetypes();
                
                for (Archetype archetype : include) {
                    if (!archetypes.contains(archetype)) {
                        return true;
                    }
                }
                
                return false;
            }
        });
        
        return CollectionUtils.randomElement(availableHeroes, HeroRegistry.defaultHero());
    }
}
