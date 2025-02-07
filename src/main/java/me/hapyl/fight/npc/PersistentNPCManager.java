package me.hapyl.fight.npc;

import me.hapyl.eterna.module.util.DependencyInjector;
import me.hapyl.eterna.module.util.Runnables;
import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.annotation.Nonnull;

public final class PersistentNPCManager extends DependencyInjector<Main> implements Listener {

    public PersistentNPCManager(@Nonnull Main main) {
        super(main);

        CF.registerEvents(this);

        // Reload catcher
        Bukkit.getOnlinePlayers().forEach(this::update);
    }

    @EventHandler()
    public void handlePlayerJoinEvent(PlayerJoinEvent ev) {
        update(ev.getPlayer());
    }

    @EventHandler
    public void handlePlayerQuitEvent(PlayerQuitEvent ev) {
        getPlugin().getRegistries().npcRegistry.values().forEach(npc -> {
            npc.hide(ev.getPlayer());
        });
    }

    public void update(@Nonnull Player player) {
        Runnables.runLater(() -> {
            for (PersistentNPC npc : getPlugin().getRegistries().npcRegistry.values()) {
                npc.show(player);
            }
        }, 10L);
    }


}
