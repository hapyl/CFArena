package me.hapyl.fight.game.talents.archive.bloodfiend;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public abstract class TauntTalent<T extends Taunt> extends Talent {

    protected final Map<Player, T> playerTaunt;

    public TauntTalent(@Nonnull String name) {
        super(name);

        this.playerTaunt = Maps.newHashMap();
    }

    @Override
    public Response execute(Player player) {
        final T taunt = getTaunt(player);

        if (taunt != null) {
            taunt.remove();
            Chat.sendMessage(player, "&aYour previous %s was removed!", taunt.getName());
        }

        playerTaunt.put(player, createTaunt(player));
        return Response.OK;
    }

    @Nullable
    public T getTaunt(Player player) {
        return playerTaunt.get(player);
    }

    public abstract T createTaunt(Player player);

    @Override
    public void onDeath(Player player) {
        final T taunt = playerTaunt.remove(player);

        if (taunt != null) {
            taunt.remove();
        }
    }

    @Override
    public void onStop() {
        playerTaunt.values().forEach(Taunt::remove);
        playerTaunt.clear();
    }

    public void removeTaunt(Player player) {
        playerTaunt.remove(player);
    }
}
