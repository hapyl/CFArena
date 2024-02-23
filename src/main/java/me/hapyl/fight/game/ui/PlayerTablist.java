package me.hapyl.fight.game.ui;

import com.google.common.collect.Lists;
import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.CollectibleEntry;
import me.hapyl.fight.database.entry.DailyRewardEntry;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.IGameInstance;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.collectible.relic.RelicHunt;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.experience.Experience;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.spigotutils.module.player.tablist.EntryList;
import me.hapyl.spigotutils.module.player.tablist.EntryTexture;
import me.hapyl.spigotutils.module.player.tablist.PingBars;
import me.hapyl.spigotutils.module.player.tablist.Tablist;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerTablist extends Tablist {

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

    private void updateStatistics() {
        final EntryList entryList = new EntryList();

        entryList.append("&3&lsᴛᴀᴛɪsᴛɪᴄs", EntryTexture.DARK_AQUA);
        entryList.append();
        entryList.append("&8Nothing here yet!");

        setColumn(3, entryList);
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
            final String displayNameTab = profile.getDisplay().getDisplayNameTab();

            entryList.append(displayNameTab, EntryTexture.of(profile.getPlayer()), PingBars.byValue(player.getPing()));
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
            entryList.append(); // Empty line to keep teammates at the same line ¯\_(ツ)_/¯
        }
        else {
            final GamePlayer gamePlayer = CF.getPlayer(player);

            if (gamePlayer == null) {
                entryList.append("&4Error! Report this!");
            }
            else {
                entryList.append("Game: &8" + game.hexCode(), EntryTexture.GOLD);
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
                            teammate.getHealthFormatted(),
                            teammate.getUltimateString(ChatColor.AQUA)
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

                    entryList.append("&8- %s &7(%s)".formatted(profile.getDisplay().getNamePrefixed(), profile.getHero().getName()));
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
                entryList.append(" %s &cCannot claim!".formatted(rewardPrefix));
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

        final RelicHunt relicHunt = Main.getPlugin().getCollectibles().getRelicHunt();
        final GameMaps currentMap = manager.isGameInProgress() ? manager.getCurrentMap() : GameMaps.SPAWN;

        final int totalRelics = relicHunt.getTotalRelics();
        final int totalRelicsFound = collectibleEntry.getFoundList().size();

        final int relicInZone = relicHunt.byZone(currentMap).size();
        final int foundInZone = relicHunt.getFoundListIn(player, currentMap).size();

        entryList.append(" &7ᴛᴏᴛᴀʟ ғᴏᴜɴᴅ: %s".formatted(CFUtils.makeStringFractional(totalRelicsFound, totalRelics)));
        entryList.append(" &7ɪɴ ᴄᴜʀʀᴇɴᴛ ᴀʀᴇᴀ: %s".formatted(CFUtils.makeStringFractional(foundInZone, relicInZone)));

        setColumn(2, entryList);
    }

}
