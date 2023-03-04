package me.hapyl.fight.game.profile;

import me.hapyl.fight.database.entry.CosmeticEntry;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.cosmetic.PrefixCosmetic;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.setting.Setting;
import me.hapyl.fight.game.ui.UIFormat;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ProfileDisplay {

    private final PlayerProfile profile;
    private final CosmeticEntry cosmetics;
    private String customName;

    public ProfileDisplay(PlayerProfile profile) {
        this.profile = profile;
        this.customName = profile.getPlayer().getName();
        this.cosmetics = profile.getDatabase().getCosmetics();
    }

    public String getDisplayName() {
        final StringBuilder builder = new StringBuilder();
        final Player player = profile.getPlayer();

        // nullable player if game does not exist
        final GamePlayer gamePlayer = profile.getGamePlayer();
        if (gamePlayer != null) {
            if (gamePlayer.isDead()) {
                builder.append("&4☠☠☠ ");
            }
            if (gamePlayer.isSpectator()) {
                builder.append("&7&lSpectator ");
            }
        }

        builder.append(ChatColor.GOLD).append(profile.getSelectedHero().getHero().getName()).append(" ");

        final String prefix = getPrefix();
        if (!prefix.isEmpty()) {
            builder.append(prefix).append(" ");
        }

        builder.append(player.isOp() ? ChatColor.RED : ChatColor.YELLOW);
        builder.append(customName).append("&f: ");

        return builder.toString();
    }

    public String getPrefix() {
        final Cosmetics cosmetic = cosmetics.getSelected(Type.PREFIX);
        return cosmetic == null ? "" : ((PrefixCosmetic) cosmetic.getCosmetic()).getPrefix();
    }

    public String getPrefixPreview(PrefixCosmetic prefix) {
        return "&6&l" + profile.getSelectedHero().getHero().getName() + " " + prefix.getPrefix() + " &e" + profile.getPlayer().getName();
    }

    public String getDisplayNameTab() {
        final Player player = profile.getPlayer();
        final StringBuilder builder = new StringBuilder();
        final Heroes hero = Manager.current().getSelectedHero(player);
        final boolean isSpectator = Setting.SPECTATE.isEnabled(player);

        builder.append(isSpectator ? "&7&o" : "&6&l");
        builder.append(hero.getHero().getName()).append(" ");
        final String prefix = getPrefix();
        if (!prefix.isEmpty()) {
            builder.append(prefix).append(" ");
        }
        builder.append(player.isOp() ? (isSpectator ? "&7🛡 " : "&c🛡 ") : isSpectator ? "" : "&e");
        builder.append(player.getName());

        // append players ping colored depending on their ping
        builder.append(" ").append(formatPing());

        final GamePlayer gamePlayer = profile.getGamePlayer();
        if (gamePlayer != null && !gamePlayer.isAlive()) {
            builder.append(UIFormat.DIV);

            if (gamePlayer.isSpectator()) {
                builder.append("&7&lSpectator");
            }
            else if (gamePlayer.isDead()) {
                builder.append("&4☠☠☠");
            }
        }

        return Chat.format(builder.toString());
    }

    private String formatPing() {
        final int ping = profile.getPlayer().getPing();

        if (ping < 100) {
            return "&a" + ping + "ms";
        }
        else if (ping < 150) {
            return "&e" + ping + "ms";
        }
        else if (ping < 200) {
            return "&c" + ping + "ms";
        }
        else {
            return "&4" + ping + "ms";
        }
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

}
