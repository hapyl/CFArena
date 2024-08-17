package me.hapyl.fight.game.ui;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.player.tablist.EntryList;
import me.hapyl.eterna.module.player.tablist.EntryTexture;
import me.hapyl.eterna.module.player.tablist.PingBars;
import me.hapyl.eterna.module.player.tablist.Tablist;
import me.hapyl.eterna.module.util.SmallCaps;
import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.CollectibleEntry;
import me.hapyl.fight.database.entry.DailyRewardEntry;
import me.hapyl.fight.database.entry.StatisticEntry;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.IGameInstance;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.challenge.Challenge;
import me.hapyl.fight.game.challenge.PlayerChallenge;
import me.hapyl.fight.game.challenge.PlayerChallengeList;
import me.hapyl.fight.game.collectible.relic.RelicHunt;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.experience.Experience;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.maps.EnumLevel;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.stats.StatType;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.util.TimeFormat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PlayerTablist extends Tablist {

    private final static Map<StatType, String> statTypeNameMapped = Map.of(
            StatType.KILLS, SmallCaps.format("kills"),
            StatType.ASSISTS, SmallCaps.format("assists"),
            StatType.DEATHS, SmallCaps.format("deaths"),
            StatType.DAMAGE_DEALT, SmallCaps.format("damage dealt"),
            StatType.DAMAGE_TAKEN, SmallCaps.format("damage taken"),
            StatType.ULTIMATE_USED, SmallCaps.format("ultimate used"),
            StatType.WINS, SmallCaps.format("wins"),
            StatType.PLAYED, SmallCaps.format("times played")
    );

    private final PlayerUI ui;
    private final PlayerProfile profile;
    private final Player player;
    private final Manager manager;

    public PlayerTablist(PlayerUI ui) {
        this.ui = ui;
        this.profile = ui.profile;
        this.player = ui.getPlayer();
        this.manager = Manager.current();
    }

    public void update() {
        updatePlayers();
        updateSystem();
        updateTheEye();
        updateStatistics();
    }

    private void updatePlayers() {
        final EntryList entryList = new EntryList();
        final List<PlayerProfile> profiles = Lists.newArrayList();

        manager.forEachProfile(profiles::add);

        entryList.append("    &a&lᴘʟᴀʏᴇʀs &7(%s)".formatted(CF.getOnlinePlayerCount()), EntryTexture.GREEN);
        entryList.append();

        profiles.removeIf(PlayerProfile::isHidden);

        final Experience experience = Main.getPlugin().getExperience();

        profiles.sort((p1, p2) -> {
            final long p1Level = experience.getLevel(p1.getPlayer());
            final long p2Level = experience.getLevel(p2.getPlayer());

            return Long.compare(p2Level, p1Level);
        });

        profiles.sort((p1, p2) -> {
            final PlayerRank rank1 = p1.getRank();
            final PlayerRank rank2 = p2.getRank();

            return Integer.compare(rank2.ordinal(), rank1.ordinal());
        });

        for (PlayerProfile profile : profiles) {
            final String displayNameTab = profile.getDisplay().toStringTab();

            entryList.append(displayNameTab, EntryTexture.of(profile.getPlayer()), PingBars.byValue(profile.getPlayer().getPing()));
        }

        setColumn(0, entryList);
    }

    private void updateSystem() {
        final EntryList entryList = new EntryList();

        entryList.append("   &e&lsʏsᴛᴇᴍ &7(%s&7 tps)".formatted(CF.getTpsFormatted()), EntryTexture.YELLOW);
        entryList.append();

        final String mapName = manager.getCurrentMap().getName();
        final IGameInstance game = manager.getCurrentGame();

        if (!manager.isGameInProgress()) {
            entryList.append("&b&lLobby:", EntryTexture.AQUA);
            entryList.append(" &7ᴍᴀᴘ: &f" + mapName);
            entryList.append(" &7ᴍᴏᴅᴇ: &f" + manager.getCurrentMode().getName());
            entryList.append(" &7ꜰᴀɪʀ ᴍᴏᴅᴇ: &f" + ChatColor.stripColor(manager.getFairMode().getMastery()));
        }
        else {
            final GamePlayer gamePlayer = CF.getPlayer(player);

            if (gamePlayer == null) {
                entryList.append("&4Error! Report this!");
            }
            else {
                entryList.append("&6&lGame: &8" + game.hexCode(), EntryTexture.GOLD);
                entryList.append(" &7ᴍᴀᴘ: &f" + mapName);
                entryList.append(" &7ᴛɪᴍᴇ ʟᴇғᴛ: &f" + ui.getTimeLeftString(game));
                entryList.append(" &7sᴛᴀᴛᴜs: &f" + gamePlayer.getStatusString());
            }
        }

        final GameTeam playerTeam = GameTeam.getEntryTeam(profile.getEntry());

        entryList.append();

        // Teammates
        if (playerTeam == null) {
            entryList.append("&4Error loading team.");
        }
        else {
            final ChatColor color = playerTeam.getColor();
            final List<GamePlayer> gamePlayers = playerTeam.getPlayers();

            entryList.append(playerTeam.getColor() + "&lTeam: &7(%s)".formatted(playerTeam.getName()), EntryTexture.of(color));

            int toFill = 4;

            // If gamePlayers are empty means not in a game
            if (!gamePlayers.isEmpty()) {
                for (GamePlayer teammate : gamePlayers) {
                    entryList.append("&8- &a%s &7⁑ &c&l%s  &b%s".formatted(
                            teammate.getName(),
                            teammate.getHealthFormatted(player),
                            teammate.getUltimateString(GamePlayer.UltimateColor.PRIMARY)
                    ));

                    toFill--;
                }
            }
            else {
                final List<Player> bukkitPlayers = playerTeam.getBukkitPlayers();

                for (Player bukkitPlayer : bukkitPlayers) {
                    final PlayerProfile profile = PlayerProfile.getProfile(bukkitPlayer);
                    toFill--;

                    if (profile == null) {
                        entryList.append("&4Error loading profile for " + bukkitPlayer.getName());
                        continue;
                    }

                    entryList.append("&8- %s &8(%s&8)".formatted(profile.getDisplay().getNamePrefixed(), profile.getSelectedHeroString()));
                }
            }

            for (int i = 0; i < toFill; i++) {
                entryList.append("&8- Empty!");
            }
        }

        // Crates
        final PlayerDatabase database = profile.getDatabase();
        final long totalCrates = database.crateEntry.getTotalCratesCount();

        entryList.append();
        entryList.append("&6&lCrates:", EntryTexture.GOLD);

        if (totalCrates == 0) {
            entryList.append(" &8No crates!");
        }
        else {
            entryList.append(" &a%,1d unopened crates!".formatted(totalCrates));
        }

        setColumn(1, entryList);
    }

    private void updateTheEye() {
        final EntryList entryList = new EntryList();
        final PlayerDatabase database = profile.getDatabase();
        final DailyRewardEntry dailyRewardEntry = database.dailyRewardEntry;
        final CollectibleEntry collectibleEntry = database.collectibleEntry;

        entryList.append("   &2&lᴛʜᴇ ᴇʏᴇ", EntryTexture.DARK_GREEN);
        entryList.append();

        // Daily Reward
        entryList.append("&e&lDaily Reward:", EntryTexture.YELLOW);

        final PlayerRank playerRank = database.getRank();
        for (DailyRewardEntry.Type rewardType : DailyRewardEntry.Type.values()) {
            final PlayerRank rewardRank = rewardType.rank;
            final String rewardPrefix = rewardRank.getPrefixWithFallback();

            if (!playerRank.isOrHigher(rewardRank)) {
                entryList.append(" %s &cIneligible".formatted(rewardPrefix));
                continue;
            }

            final boolean canClaim = dailyRewardEntry.canClaim(rewardType);

            entryList.append(" %s %s".formatted(
                    rewardPrefix,
                    canClaim ? "&a&lAVAILABLE!" : "&c" + rewardType.reward.format(player)
            ));
        }

        // Relic Hunt
        entryList.append();
        entryList.append("&b&lRelic Hunt:", EntryTexture.AQUA);

        final RelicHunt relicHunt = Main.getPlugin().getRelicHunt();
        final EnumLevel currentMap = manager.isGameInProgress() ? manager.getCurrentMap() : EnumLevel.SPAWN;

        final int totalRelics = relicHunt.getTotalRelics();
        final int totalRelicsFound = collectibleEntry.getFoundList().size();

        final int relicInZone = relicHunt.byZone(currentMap).size();

        entryList.append(" &7ᴛᴏᴛᴀʟ ғᴏᴜɴᴅ: %s".formatted(Chat.makeStringFractional(totalRelicsFound, totalRelics)));

        if (relicInZone == 0) {
            entryList.append(" &7ɪɴ ᴄᴜʀʀᴇɴᴛ ᴀʀᴇᴀ: &cNone!");
        }
        else {
            final int foundInZone = relicHunt.getFoundListIn(player, currentMap).size();
            entryList.append(" &7ɪɴ ᴄᴜʀʀᴇɴᴛ ᴀʀᴇᴀ: %s".formatted(Chat.makeStringFractional(foundInZone, relicInZone)));
        }

        // Daily challenges
        entryList.append();
        entryList.append(
                "&a&lDaily Bonds: &8(%s)".formatted(TimeFormat.format(Challenge.getTimeUntilReset(), TimeFormat.HOURS)),
                EntryTexture.GREEN
        );

        final PlayerChallengeList challengeList = profile.getChallengeList();

        int index = 0;
        for (PlayerChallenge challenge : challengeList.getChallenges()) {
            final boolean hasClaimedRewards = challenge.hasClaimedRewards();

            if (index++ != 0) {
                entryList.append();
            }

            entryList.append(" %s%s %s".formatted(
                    hasClaimedRewards ? Color.SUCCESS : Color.WHITE,
                    challenge.getName(),
                    challenge.getProgressString()
            ));
            entryList.append("  &7&o%s".formatted(challenge.getDescription()));
        }

        setColumn(2, entryList);
    }

    private void updateStatistics() {
        final EntryList entryList = new EntryList();
        final Hero hero = profile.getHero();
        final PlayerDatabase database = profile.getDatabase();
        final StatisticEntry statisticEntry = database.statisticEntry;

        entryList.append("    &3&lsᴛᴀᴛɪsᴛɪᴄs", EntryTexture.DARK_AQUA);
        entryList.append();

        // Global stats
        forEachStats(statisticEntry::getStat, entryList);

        entryList.append();
        entryList.append("&b&l" + hero.getName(), EntryTexture.AQUA);

        forEachStats(stat -> statisticEntry.getHeroStat(hero, stat), entryList);

        setColumn(3, entryList);
    }

    private void forEachStats(Function<StatType, Double> fn, EntryList list) {

        int index = 0;
        for (StatType statType : StatType.values()) {
            final String name = statTypeNameMapped.get(statType);

            if (name == null) {
                continue;
            }

            boolean mod = index++ % 2 == 0;

            final Double value = fn.apply(statType);
            list.append(" %s: &b%,.0f".formatted((mod ? Color.GRAY : Color.GRAYER) + name, value));
        }
    }

}
