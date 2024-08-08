package me.hapyl.fight.perplayer;

import com.google.common.collect.Sets;
import me.hapyl.fight.Main;
import me.hapyl.eterna.module.entity.Entities;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class PlayerEntity<T extends Entity> {

    private final Entities<T> type;
    private final Consumer<T> consumer;
    private final Set<Player> players;

    private T entity;

    public PlayerEntity(Entities<T> type, Consumer<T> consumer) {
        this.type = type;
        this.consumer = consumer;
        this.players = Sets.newHashSet();
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void addPlayers(Player... players) {
        this.players.addAll(List.of(players));
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public void show() {
        if (entity == null) {
            return;
        }

        players.forEach(player -> {
            player.showEntity(getPlugin(), entity);
        });
    }

    public void hide() {
        if (entity == null) {
            return;
        }

        players.forEach(player -> {
            player.hideEntity(getPlugin(), entity);
        });
    }

    public void remove() {
        if (entity == null) {
            return;
        }

        entity.remove();
        players.clear();
    }

    @Nonnull
    public T spawn(@Nonnull Location location) {
        if (entity == null) {
            entity = type.spawn(location, self -> {
                self.setVisibleByDefault(false);
                if (consumer != null) {
                    consumer.accept(self);
                }
            });

            // show entity
            players.forEach(player -> {
                player.showEntity(getPlugin(), entity);
            });
        }

        return entity;
    }

    @Nullable
    public T getEntity() {
        return entity;
    }

    @Nonnull
    private Main getPlugin() {
        return Main.getPlugin();
    }
}
