package me.hapyl.fight.game.heroes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.collection.HeroStatsCollection;
import me.hapyl.fight.database.entry.HeroEntry;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.heroes.storage.*;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.util.SmallCaps;
import me.hapyl.spigotutils.module.util.CollectionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Registry for Heroes.
 * <p>
 * Make sure not to change names, as it will break the database.
 */
public enum Heroes {

    // New Hero -> Halloween

    ARCHER(new Archer()),
    ALCHEMIST(new Alchemist()),
    MOONWALKER(new Moonwalker()),
    HERCULES(new Hercules()),
    MAGE(new Mage()),
    PYTARIA(new Pytaria()),
    TROLL(new Troll()),
    NIGHTMARE(new Nightmare()),
    DR_ED(new DrEd()),
    ENDER(new Ender()),
    SPARK(new Spark()),
    SHADOW_ASSASSIN(new ShadowAssassin()),
    WITCHER(new WitcherClass()),
    VORTEX(new Vortex()),
    FREAZLY(new Freazly()),
    DARK_MAGE(new DarkMage()),
    BLAST_KNIGHT(new BlastKnight()),
    NINJA(new Ninja()),
    TAKER(new Taker()),
    JUJU(new JuJu()),
    SWOOPER(new Swooper()),
    TAMER(new Tamer()),
    SHARK(new Shark()),
    LIBRARIAN(new Librarian()),
    TECHIE(new Techie()),
    WAR_MACHINE(new KillingMachine()),

    HARBINGER(new Harbinger()),
    SHAMAN(new Shaman()),

    // 1.5
    HEALER(new Healer()),
    VAMPIRE(new Vampire()),
    BOUNTY_HUNTER(new BountyHunter()),
    SWORD_MASTER(new SwordMaster()),
    ZEALOT(new Zealot()),
    ENGINEER(new Engineer()),

    ;

    private final static List<Heroes> PLAYABLE = Lists.newArrayList();
    private final static Map<Role, List<Heroes>> BY_ROLE = Maps.newHashMap();

    static {
        for (Heroes value : values()) {
            if (!value.isValidHero()) {
                continue;
            }

            // by role
            final Role role = value.getHero().getRole();
            final List<Heroes> byRole = byRole(role);

            byRole.add(value);
            BY_ROLE.put(role, byRole);

            // playable
            PLAYABLE.add(value);
        }
    }

    private final Hero hero;
    private final HeroStatsCollection stats; // can't store in hero object because requires enum

    Heroes(Hero hero) {
        this.hero = hero;
        this.stats = new HeroStatsCollection(this);

        if (hero instanceof Listener listener) {
            Main.getPlugin().addEvent(listener);
        }
    }

    public HeroStatsCollection getStats() {
        return stats;
    }

    public boolean isValidHero() {
        return !(hero instanceof DisabledHero);
    }

    /**
     * Returns a handle of a hero.
     * <p>
     * Note that this method only returns a base handle,
     * for specific hero handles, use {@link #getHero(Class)}.
     *
     * @return handle of a hero.
     */
    public Hero getHero() {
        if (hero == null) {
            throw new NullPointerException("%s doesn't have a handle!".formatted(name()));
        }

        return hero;
    }

    /**
     * Returns a handle of a hero.
     * <p>
     * This method tries to cast the handle to the specified class.
     *
     * @param cast - Cast to.
     * @return handle of a hero.
     * @throws IllegalArgumentException if the cast is invalid.
     */
    @Nonnull
    public <E extends Hero> E getHero(Class<E> cast) throws IllegalArgumentException {
        if (cast.isInstance(hero)) {
            return cast.cast(hero);
        }

        throw new IllegalArgumentException("Invalid cast! Expected %s, got %s.".formatted(
                cast.getSimpleName(),
                hero.getClass().getSimpleName()
        ));
    }

    /**
     * Returns list of players that have this hero selected.
     * Or empty list if no players have this hero selected.
     *
     * @return list of players that have this hero selected.
     */
    public List<GamePlayer> getPlayers() {
        final List<GamePlayer> players = new ArrayList<>();
        final GameInstance gameInstance = Manager.current().getGameInstance();

        if (gameInstance == null) {
            return players;
        }

        gameInstance.getPlayers().forEach((uuid, gp) -> {
            if (isSelected(gp.getPlayer())) {
                players.add(gp);
            }
        });

        return players;
    }

    /**
     * Returns list of alive payers that have this hero selected.
     *
     * @return list of alive players that have this hero selected.
     */
    public List<GamePlayer> getAlivePlayers() {
        final List<GamePlayer> players = getPlayers();
        players.removeIf(filter -> !filter.isAlive());
        return players;
    }

    /**
     * Returns true if player's current hero is this hero.
     *
     * @param player - Player to check.
     * @return true if player's current hero is this hero.
     */
    public boolean isSelected(Player player) {
        return Manager.current().getCurrentEnumHero(player) == this;
    }

    /**
     * Returns true if player has this hero favourite.
     *
     * @param player - Player to check.
     * @return true if player has this hero favourite.
     */
    public boolean isFavourite(Player player) {
        return PlayerProfile.getOrCreateProfile(player).getDatabase().getHeroEntry().isFavourite(this);
    }

    /**
     * Sets if player has this hero favourite.
     *
     * @param player - Player to set.
     * @param flag   - True if favourite, false if not.
     */
    public void setFavourite(Player player, boolean flag) {
        PlayerProfile.getOrCreateProfile(player).getDatabase().getHeroEntry().setFavourite(this, flag);
    }

    /**
     * Returns true if this hero is locked for player.
     *
     * @param player - Player to check.
     * @return true if this hero is locked for player.
     */
    public boolean isLocked(Player player) {
        return false;
    }

    /**
     * Returns actual name of the hero, not enum.
     *
     * @return actual name of the hero, not enum.
     */
    public String getName() {
        return hero.getName();
    }

    /**
     * Returns name of the hero in small caps.
     *
     * @return name of the hero in small caps.
     * @see SmallCaps
     */
    public String getNameSmallCaps() {
        return hero.getNameSmallCaps();
    }

    /**
     * Returns all playable heroes.
     *
     * <p>
     * Note that players can still select non-playable hero using <i>'-IKnowItsDisabledHeroAndWillBreakTheGame'</i> argument.
     * </p>
     *
     * @return all playable heroes.
     */
    public static List<Heroes> playable() {
        return Lists.newArrayList(PLAYABLE);
    }

    // static members

    /**
     * Returns all playable heroes sorted by favourites.
     *
     * @param player - Player to sort by favourites.
     * @return all playable heroes sorted by favourites.
     */
    public static List<Heroes> playableRespectFavourites(Player player) {
        final List<Heroes> playable = playable();
        playable.sort((a, b) -> {
            final HeroEntry heroEntry = PlayerProfile.getOrCreateProfile(player).getDatabase().getHeroEntry();
            return (heroEntry.isFavourite(b) ? 1 : 0) - (heroEntry.isFavourite(a) ? 1 : 0);
        });
        return playable;
    }

    /**
     * Returns playable heroes names by their Enum name.
     *
     * @return playable heroes names by their Enum name.
     */
    public static List<String> playableStings() {
        return playable().stream().map(Enum::name).toList();
    }

    /**
     * Returns list of heroes by role.
     *
     * @param role - Role to get heroes by.
     * @return list of heroes by role.
     */
    public static List<Heroes> byRole(Role role) {
        return Lists.newArrayList(BY_ROLE.computeIfAbsent(role, (s) -> Lists.newArrayList()));
    }

    /**
     * Returns a random playable hero.
     *
     * @return random playable hero.
     */
    public static Heroes randomHero() {
        return CollectionUtils.randomElement(playable());
    }

}
