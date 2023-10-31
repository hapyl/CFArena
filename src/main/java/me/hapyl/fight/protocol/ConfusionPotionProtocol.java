package me.hapyl.fight.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.google.common.collect.Maps;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.spigotutils.module.reflect.protocol.ProtocolListener;
import me.hapyl.spigotutils.module.util.Runnables;
import me.hapyl.spigotutils.module.util.ThreadRandom;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class ConfusionPotionProtocol extends ProtocolListener {

    private final long DELAY = 300;
    private final Map<UUID, Long> lastAffected;

    public ConfusionPotionProtocol() {
        super(PacketType.Play.Client.POSITION);
        this.lastAffected = Maps.newHashMap();
    }

    @Override
    public void onPacketReceiving(PacketEvent ev) {
        if (true) {
            return;
        }

        final Player player = ev.getPlayer();
        final PacketContainer packet = ev.getPacket();

        if (packet.getMeta("amnesia").isPresent()) {
            return;
        }

        final GamePlayer gamePlayer = CF.getPlayer(player);

        if (gamePlayer == null || !gamePlayer.hasEffect(GameEffectType.AMNESIA)) {
            return;
        }

        final long millis = System.currentTimeMillis();

        if (lastAffected.containsKey(player.getUniqueId())) {
            if (millis - lastAffected.get(player.getUniqueId()) < DELAY) {
                return;
            }
        }

        final PacketContainer clone = packet.deepClone();
        clone.setMeta("amnesia", true);

        final StructureModifier<Double> doubles = clone.getDoubles();
        doubles.write(0, randomDirection(doubles.read(0)));
        doubles.write(2, randomDirection(doubles.read(2)));

        ProtocolLibrary.getProtocolManager().receiveClientPacket(player, clone);
        // actually sync player position for them
        Runnables.runSync(() -> {
            player.teleport(player.getLocation());
        });
        lastAffected.put(player.getUniqueId(), millis);

        ev.setCancelled(true);
    }

    private double randomDirection(double d) {
        return d + ThreadRandom.nextDouble(-0.5d, 0.5d);
    }

    @Override
    public void onPacketSending(PacketEvent ev) {

    }
}
