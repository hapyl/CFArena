package me.hapyl.fight.npc;

import me.hapyl.eterna.module.util.DependencyInjector;
import me.hapyl.eterna.module.util.Runnables;
import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.registry.Registries;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public final class PersistentNPCManager extends DependencyInjector<Main> implements Listener {

    public PersistentNPCManager(@Nonnull Main main) {
        super(main);

        CF.registerEvents(this);

        // Reload catcher
        Bukkit.getOnlinePlayers().forEach(PersistentNPCManager::handleOnJoin);
    }

    public static void handleOnJoin(@Nonnull Player player) {
        Runnables.runLater(
                () -> {
                    for (PersistentNPC npc : Registries.npcs().values()) {
                        npc.show(player);
                    }
                }, 10L
        );
    }

    public static void handleOnQuit(@Nonnull Player player) {
        Registries.npcs().values().forEach(npc -> npc.hide(player));
    }
}
