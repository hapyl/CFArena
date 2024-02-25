package me.hapyl.fight.command;

import com.google.common.collect.Lists;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import javax.annotation.Nonnull;
import java.util.List;

public class PluginsCommandOverride extends CFCommand {

    private final List<Plugin> publicPlugins;
    private final List<Plugin> customPlugins;

    public PluginsCommandOverride() {
        super("plugins", PlayerRank.DEFAULT);

        setAliases("pl");

        publicPlugins = Lists.newArrayList();
        customPlugins = Lists.newArrayList();

        final PluginManager pluginManager = Bukkit.getPluginManager();
        for (Plugin plugin : pluginManager.getPlugins()) {
            if (plugin.getDescription().getAuthors().contains("hapyl")) {
                customPlugins.add(plugin);
            }
            else {
                publicPlugins.add(plugin);
            }
        }

    }

    @Override
    protected void execute(@Nonnull Player player, @Nonnull String[] args, @Nonnull PlayerRank rank) {
        Chat.sendMessage(player, "&aWe use a combination of public and custom plugins:");

        Chat.sendMessage(player, "&bCustom plugins:");
        displayPlugins(player, customPlugins);

        Chat.sendMessage(player, "");
        Chat.sendMessage(player, "&bPublic plugins:");
        displayPlugins(player, publicPlugins);
    }

    private void displayPlugins(Player player, List<Plugin> plugins) {
        for (Plugin plugin : plugins) {
            Chat.sendMessage(player, "&e- &6%s &8(%s)", plugin.getName(), plugin.getDescription().getVersion());
        }
    }

}
