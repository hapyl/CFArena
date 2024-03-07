package me.hapyl.fight.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.reflect.Reflect;
import me.hapyl.spigotutils.module.reflect.protocol.ProtocolListener;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class PayloadProtocol extends ProtocolListener {

    private final String brandName = "§b§lEterna§r";
    private final String meta = "_customBrand";

    public PayloadProtocol() {
        super(PacketType.Play.Server.CUSTOM_PAYLOAD);
    }

    @Override
    public void onPacketReceiving(@Nonnull PacketEvent event) {
    }

    @Override
    public void onPacketSending(@Nonnull PacketEvent event) {
        final Player player = event.getPlayer();
        final PacketContainer packet = event.getPacket();

        if (packet.getMeta(meta).isPresent()) {
            return;
        }

        final PacketContainer packetContainer = new PacketContainer(
                PacketType.Play.Server.CUSTOM_PAYLOAD,
                new ClientboundCustomPayloadPacket(new BrandPayload(brandName))
        );

        packetContainer.setMeta(meta, true);
        Reflect.sendPacket(player, packetContainer);
    }
}
