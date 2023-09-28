package me.hapyl.fight.packet;

import me.hapyl.fight.game.entity.GamePlayer;
import net.minecraft.network.protocol.game.PacketPlayOutGameStateChange;
import org.bukkit.entity.Player;

public interface StaticPacket {

    class Demo {

        public static final StaticPacket SHOW_HOW_TO_MOVE = player -> new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.f, 101);

    }

    void send(Player player);

    default void send(GamePlayer player) {
        send(player.getPlayer());
    }

}
