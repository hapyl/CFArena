package me.hapyl.fight.packet.entity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Collection;

public interface PacketEntityBase {

    void spawn(Player player);

    default void spawn(Player... players) {
        for (Player player : players) {
            spawn(player);
        }
    }

    default void spawnGlobally() {
        Bukkit.getOnlinePlayers().forEach(this::spawn);
    }

    default void spawn(Collection<Player> players) {
        for (Player player : players) {
            spawn(player);
        }
    }

    void hide(Player player);

    default void hide(Player... players) {
        for (Player player : players) {
            hide(player);
        }
    }

    default void hide(Collection<Player> players) {
        for (Player player : players) {
            hide(player);
        }
    }

    default void hideGlobally() {
        Bukkit.getOnlinePlayers().forEach(this::hide);
    }

    void destroy();

    void setVisible(boolean visibility);

    void setCollision(boolean collision);

    void setSilent(boolean silent);

    void setGravity(boolean gravity);

    void setMarker();

    void teleport(@Nonnull Location location);

    int getId();
}
