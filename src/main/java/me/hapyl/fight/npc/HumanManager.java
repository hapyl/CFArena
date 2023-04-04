package me.hapyl.fight.npc;

import com.google.common.collect.Maps;
import me.hapyl.fight.Main;
import me.hapyl.spigotutils.module.reflect.npc.Human;
import me.hapyl.spigotutils.module.util.Runnables;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;

import static me.hapyl.fight.npc.StaticHuman.getLocation;

public final class HumanManager implements Listener {

    private final Map<Player, Human> playerNpc;

    public HumanManager(Main main) {
        this.playerNpc = Maps.newHashMap();

        main.getServer().getPluginManager().registerEvents(this, main);

        for (StaticHuman value : StaticHuman.values()) {
            value.getHuman().showAll();
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            updateAll(player);
        }
    }

    private void createPlayerNpc(Player player) {
        final Human human = Human.create(getLocation(-14.0, 63, 0.5, -90.0f, 0.0f), "&aYourself?", player.getName());

        human.setLookAtCloseDist(10);
        human.show(player);

        playerNpc.put(player, human);
    }

    private void deletePlayerNpc(Player player) {
        final Human human = playerNpc.get(player);

        if (human != null) {
            human.remove();
        }

        playerNpc.remove(player);
    }

    @EventHandler()
    public void handlePlayerJoinEvent(PlayerJoinEvent ev) {
        updateAll(ev.getPlayer());
    }

    @EventHandler()
    public void handlePlayerQuitEvent(PlayerQuitEvent ev) {
        final Player player = ev.getPlayer();

        deletePlayerNpc(player);
    }

    public void updateAll(Player player) {
        Runnables.runLater(() -> {
            for (StaticHuman value : StaticHuman.values()) {
                value.getHuman().show(player);
            }

            createPlayerNpc(player);
        }, 10L);
    }


}
