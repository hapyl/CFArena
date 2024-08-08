package me.hapyl.fight.game.ui.splash;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.eterna.module.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

// This only works if player has enabled the resource pack!
public class SplashText {

    private final Player player;
    private final List<BossBar> bossBars;

    public SplashText(@Nonnull Player player) {
        this.player = player;
        this.bossBars = Lists.newArrayList();
    }

    public void setVisible(boolean value) {
        bossBars.forEach(bossBar -> bossBar.setVisible(value));
    }

    public void remove() {
        bossBars.forEach(BossBar::removeAll);
        bossBars.clear();
    }

    public SplashText create(int startLine, @Nonnull String... text) {
        remove();

        for (int i = 0; i < startLine; i++) {
            createBar("");
        }

        for (String s : text) {
            createBar(Chat.format(s));
        }

        bossBars.forEach(bossBar -> bossBar.addPlayer(player));
        return this;
    }

    private void createBar(String text) {
        bossBars.add(Bukkit.createBossBar(text, BarColor.RED, BarStyle.SOLID));
    }

    @Nullable
    public static SplashText create(@Nonnull Player player, int startLine, @Nonnull String... text) {
        final PlayerProfile profile = PlayerProfile.getProfile(player);

        if (profile == null || !profile.isResourcePack()) {
            return null;
        }

        return new SplashText(player).create(startLine, text);
    }

}
