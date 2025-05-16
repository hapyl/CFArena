package me.hapyl.fight.game.heroes;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.KeyFunction;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.eterna.module.util.Compute;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.entry.HeroEntry;
import me.hapyl.fight.game.heroes.alchemist.Alchemist;
import me.hapyl.fight.game.heroes.archer.Archer;
import me.hapyl.fight.game.heroes.archer_tutorial.TutorialArcher;
import me.hapyl.fight.game.heroes.aurora.Aurora;
import me.hapyl.fight.game.heroes.bloodfield.Bloodfiend;
import me.hapyl.fight.game.heroes.bounty_hunter.BountyHunter;
import me.hapyl.fight.game.heroes.dark_mage.DarkMage;
import me.hapyl.fight.game.heroes.dlan.Dylan;
import me.hapyl.fight.game.heroes.doctor.DrEd;
import me.hapyl.fight.game.heroes.echo.Echo;
import me.hapyl.fight.game.heroes.ender.Ender;
import me.hapyl.fight.game.heroes.engineer.Engineer;
import me.hapyl.fight.game.heroes.frostbite.Freazly;
import me.hapyl.fight.game.heroes.geo.Geo;
import me.hapyl.fight.game.heroes.gunner.Gunner;
import me.hapyl.fight.game.heroes.harbinger.Harbinger;
import me.hapyl.fight.game.heroes.healer.Healer;
import me.hapyl.fight.game.heroes.heavy_knight.SwordMaster;
import me.hapyl.fight.game.heroes.hercules.Hercules;
import me.hapyl.fight.game.heroes.himari.Himari;
import me.hapyl.fight.game.heroes.inferno.Inferno;
import me.hapyl.fight.game.heroes.jester.Jester;
import me.hapyl.fight.game.heroes.juju.JuJu;
import me.hapyl.fight.game.heroes.km.KillingMachine;
import me.hapyl.fight.game.heroes.knight.BlastKnight;
import me.hapyl.fight.game.heroes.librarian.Librarian;
import me.hapyl.fight.game.heroes.mage.Mage;
import me.hapyl.fight.game.heroes.miku.MikuHatsune;
import me.hapyl.fight.game.heroes.moonwalker.Moonwalker;
import me.hapyl.fight.game.heroes.nightmare.Nightmare;
import me.hapyl.fight.game.heroes.ninja.Ninja;
import me.hapyl.fight.game.heroes.nyx.Nyx;
import me.hapyl.fight.game.heroes.orc.Orc;
import me.hapyl.fight.game.heroes.pytaria.Pytaria;
import me.hapyl.fight.game.heroes.rogue.Rogue;
import me.hapyl.fight.game.heroes.ronin.Ronin;
import me.hapyl.fight.game.heroes.shadow_assassin.ShadowAssassin;
import me.hapyl.fight.game.heroes.shaman.Shaman;
import me.hapyl.fight.game.heroes.shark.Shark;
import me.hapyl.fight.game.heroes.spark.Spark;
import me.hapyl.fight.game.heroes.swooper.Swooper;
import me.hapyl.fight.game.heroes.taker.Taker;
import me.hapyl.fight.game.heroes.tamer.Tamer;
import me.hapyl.fight.game.heroes.techie.Techie;
import me.hapyl.fight.game.heroes.troll.Troll;
import me.hapyl.fight.game.heroes.vampire.Vampire;
import me.hapyl.fight.game.heroes.vortex.Vortex;
import me.hapyl.fight.game.heroes.warden.Warden;
import me.hapyl.fight.game.heroes.witcher.WitcherClass;
import me.hapyl.fight.game.heroes.zealot.Zealot;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.registry.AbstractStaticRegistry;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * {@inheritDoc}
 */
public final class HeroRegistry extends AbstractStaticRegistry<Hero> {
    
    public static final Archer ARCHER;
    public static final Alchemist ALCHEMIST;
    @MarkedAsIncomplete public static final Moonwalker MOONWALKER;
    @MarkedAsIncomplete public static final Hercules HERCULES;
    public static final Mage MAGE;
    public static final Pytaria PYTARIA;
    public static final Troll TROLL;
    public static final Nightmare NIGHTMARE;
    public static final DrEd DR_ED;
    public static final Ender ENDER;
    public static final Spark SPARK;
    public static final ShadowAssassin SHADOW_ASSASSIN;
    @MarkedAsIncomplete public static final WitcherClass WITCHER;
    public static final Vortex VORTEX;
    public static final Freazly FREAZLY;
    public static final DarkMage DARK_MAGE;
    public static final BlastKnight BLAST_KNIGHT;
    public static final Ninja NINJA;
    public static final Taker TAKER;
    public static final JuJu JUJU;
    public static final Swooper SWOOPER;
    public static final Tamer TAMER;
    public static final Shark SHARK;
    @LetsPretendThisHeroDoesNotExistAndIgnoreThisLittleGoofyAhhGuyOkayQuestionMark public static final Librarian LIBRARIAN;
    public static final Techie TECHIE;
    @MarkedAsIncomplete public static final KillingMachine WAR_MACHINE;
    public static final Harbinger HARBINGER;
    public static final Shaman SHAMAN;
    @MarkedAsIncomplete public static final Healer HEALER;
    public static final Vampire VAMPIRE;
    public static final BountyHunter BOUNTY_HUNTER;
    public static final SwordMaster SWORD_MASTER;
    public static final Engineer ENGINEER;
    public static final Orc ORC;
    public static final Bloodfiend BLOODFIEND;
    public static final Zealot ZEALOT;
    public static final Ronin RONIN;
    @MarkedAsIncomplete public static final Jester JESTER;
    public static final Rogue ROGUE;
    @MarkedAsIncomplete public static final Geo GEO;
    public static final Aurora AURORA;
    public static final Nyx NYX;
    @MarkedAsIncomplete public static final Gunner GUNNER;
    @MarkedAsIncomplete public static final Echo ECHO;
    public static final Himari HIMARI;
    public static final Warden WARDEN;
    public static final Inferno INFERNO;
    public static final MikuHatsune MIKU;
    public static final Dylan DYLAN;
    
    // *=* Tutorial Hero *=* // Please keep last
    public static final TutorialArcher TUTORIAL_ARCHER;
    
    private static final Set<Hero> values;
    private static final List<Hero> playable;
    
    private static final Map<Archetype, List<Hero>> byArchetype;
    
    private static GlobalHeroStats globalStats;
    
    static {
        AbstractStaticRegistry.ensure(HeroRegistry.class, Hero.class);
        
        values = new LinkedHashSet<>();
        playable = new ArrayList<>();
        byArchetype = new HashMap<>();
        
        /*/ ⬇️ Register below ⬇️ /*/
        
        ARCHER = register("archer", Archer::new);
        ALCHEMIST = register("alchemist", Alchemist::new);
        MOONWALKER = register("moonwalker", Moonwalker::new);
        HERCULES = register("hercules", Hercules::new);
        MAGE = register("mage", Mage::new);
        PYTARIA = register("pytaria", Pytaria::new);
        TROLL = register("troll", Troll::new);
        NIGHTMARE = register("nightmare", Nightmare::new);
        DR_ED = register("dr_ed", DrEd::new);
        ENDER = register("ender", Ender::new);
        SPARK = register("spark", Spark::new);
        SHADOW_ASSASSIN = register("shadow_assassin", ShadowAssassin::new);
        WITCHER = register("witcher", WitcherClass::new);
        VORTEX = register("vortex", Vortex::new);
        FREAZLY = register("freazly", Freazly::new);
        DARK_MAGE = register("dark_mage", DarkMage::new);
        BLAST_KNIGHT = register("blast_knight", BlastKnight::new);
        NINJA = register("ninja", Ninja::new);
        TAKER = register("taker", Taker::new);
        JUJU = register("juju", JuJu::new);
        SWOOPER = register("swooper", Swooper::new);
        TAMER = register("tamer", Tamer::new);
        SHARK = register("shark", Shark::new);
        LIBRARIAN = register("librarian", Librarian::new);
        TECHIE = register("techie", Techie::new);
        WAR_MACHINE = register("war_machine", KillingMachine::new);
        HARBINGER = register("harbinger", Harbinger::new);
        SHAMAN = register("shaman", Shaman::new);
        HEALER = register("healer", Healer::new);
        VAMPIRE = register("vampire", Vampire::new);
        BOUNTY_HUNTER = register("bounty_hunter", BountyHunter::new);
        SWORD_MASTER = register("sword_master", SwordMaster::new);
        ENGINEER = register("engineer", Engineer::new);
        ORC = register("orc", Orc::new);
        BLOODFIEND = register("bloodfiend", Bloodfiend::new);
        ZEALOT = register("zealot", Zealot::new);
        RONIN = register("ronin", Ronin::new);
        JESTER = register("jester", Jester::new);
        ROGUE = register("rogue", Rogue::new);
        GEO = register("geo", Geo::new);
        AURORA = register("aurora", Aurora::new);
        NYX = register("nyx", Nyx::new);
        GUNNER = register("gunner", Gunner::new);
        ECHO = register("echo", Echo::new);
        HIMARI = register("himari", Himari::new);
        WARDEN = register("warden", Warden::new);
        INFERNO = register("inferno", Inferno::new);
        MIKU = register("miku_hatsune", MikuHatsune::new);
        DYLAN = register("dylan", Dylan::new);
        
        // Keep last
        TUTORIAL_ARCHER = register("tutorial_archer", TutorialArcher::new);
        
        calculateGlobalStats();
    }
    
    @Nonnull
    public static GlobalHeroStats getGlobalStats() {
        return Objects.requireNonNull(globalStats, "Global stats hasn't yet been instantiated!");
    }
    
    public static void calculateGlobalStats() {
        if (globalStats != null) {
            globalStats.clear();
        }
        
        globalStats = new GlobalHeroStats();
    }
    
    @Nonnull
    public static Hero ofString(@Nonnull String string) {
        return AbstractStaticRegistry.ofString(values, string, defaultHero());
    }
    
    @Nullable
    public static Hero ofStringOrNull(@Nonnull String string) {
        return AbstractStaticRegistry.ofStringOrNull(values, string);
    }
    
    @Nonnull
    public static Hero defaultHero() {
        return ARCHER;
    }
    
    @Nonnull
    public static List<Hero> values() {
        return new ArrayList<>(values);
    }
    
    @Nonnull
    public static List<Hero> playable() {
        return CF.environment().allowDisabledHeroes.isEnabled() ? values() : new ArrayList<>(playable);
    }
    
    @Nonnull
    public static List<Hero> playableRespectFavourites(@Nonnull Player player) {
        final PlayerProfile profile = CF.getProfile(player);
        final List<Hero> playable = playable();
        
        playable.sort((a, b) -> {
            final HeroEntry heroEntry = profile.getDatabase().heroEntry;
            return (heroEntry.isFavourite(b) ? 1 : 0) - (heroEntry.isFavourite(a) ? 1 : 0);
        });
        
        return playable;
    }
    
    @Nonnull
    public static List<Hero> playableRespectLockedFavourites(@Nonnull Player player) {
        final List<Hero> heroes = playableRespectFavourites(player);
        heroes.sort(Comparator.comparingInt(a -> (a.isLocked(player) ? 1 : 0)));
        
        return heroes;
    }
    
    @Nonnull
    public static Hero randomHero(@Nonnull Player player) {
        final List<Hero> playable = playable();
        playable.removeIf(hero -> hero.isLocked(player));
        
        return CollectionUtils.randomElement(playable, defaultHero());
    }
    
    @Nonnull
    public static Hero randomHero() {
        return CollectionUtils.randomElement(playable, defaultHero());
    }
    
    @Nonnull
    public static Set<Hero> byArchetype(@Nonnull Archetype archetype) {
        return setFromByMap(byArchetype, archetype);
    }
    
    @Nonnull
    public static List<String> keys() {
        return AbstractStaticRegistry.keys(values);
    }
    
    private static <K> Set<Hero> setFromByMap(Map<K, List<Hero>> map, K key) {
        final List<Hero> list = map.get(key);
        
        return list != null ? new HashSet<>(list) : new HashSet<>();
    }
    
    private static <T extends Hero> T register(@Nonnull String stringKey, @Nonnull KeyFunction<T> fn) {
        final T hero = fn.apply(Key.ofString(stringKey));
        
        values.add(hero);
        
        if (!hero.isDisabled()) {
            playable.add(hero);
        }
        
        hero.getProfile().getArchetypes().forEach(archetype -> {
            byArchetype.compute(archetype, Compute.listAdd(hero));
        });
        
        return hero;
    }
    
    
}
