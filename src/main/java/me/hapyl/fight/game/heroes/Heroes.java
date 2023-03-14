package me.hapyl.fight.game.heroes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.fight.Main;
import me.hapyl.fight.Shortcuts;
import me.hapyl.fight.database.entry.HeroEntry;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.heroes.storage.*;
import me.hapyl.spigotutils.module.util.CollectionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    TAKER(null),
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
    //HEALER(new Healer()),
    VAMPIRE(new Vampire()),
    SWORD_MASTER(new SwordMaster()),

    ;

    public static class Handle {
        public static final Vampire VAMPIRE = (Vampire) Heroes.VAMPIRE.getHero();
    }

    private final static InvalidHero INVALID_HERO = new InvalidHero();
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

    Heroes(Hero hero) {
        this.hero = hero;
        if (hero instanceof Listener listener) {
            Main.getPlugin().addEvent(listener);
        }
    }

    public boolean isValidHero() {
        return !this.getHero().equals(INVALID_HERO);
    }

    public Hero getHero() {
        return hero == null ? INVALID_HERO : hero;
    }

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

    public List<GamePlayer> getAlivePlayers() {
        final List<GamePlayer> players = getPlayers();
        players.removeIf(filter -> !filter.isAlive());
        return players;
    }

    public boolean isSelected(Player player) {
        return Manager.current().getSelectedHero(player) == this;
    }

    public boolean isFavourite(Player player) {
        return Shortcuts.getDatabase(player).getHeroEntry().isFavourite(this);
    }

    public void setFavourite(Player player, boolean flag) {
        Shortcuts.getDatabase(player).getHeroEntry().setFavourite(this, flag);
    }

    public String getName() {
        return hero.getName();
    }

    public String getNameSmallCaps() {
        return hero.getNameSmallCaps();
    }

    // static members
    public static List<Heroes> playable() {
        return Lists.newArrayList(PLAYABLE);
    }

    public static List<Heroes> playableRespectFavourites(Player player) {
        final List<Heroes> playable = playable();
        playable.sort((a, b) -> {
            final HeroEntry heroEntry = Shortcuts.getDatabase(player).getHeroEntry();
            return (heroEntry.isFavourite(b) ? 1 : 0) - (heroEntry.isFavourite(a) ? 1 : 0);
        });
        return playable;
    }

    public static List<String> playableStings() {
        return playable().stream().map(Enum::name).toList();
    }

    public static List<Heroes> byRole(Role role) {
        return Lists.newArrayList(BY_ROLE.computeIfAbsent(role, (s) -> Lists.newArrayList()));
    }

    public static Heroes randomHero() {
        return CollectionUtils.randomElement(playable());
    }
}
