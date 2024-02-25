package me.hapyl.fight.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import com.google.common.collect.Lists;
import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.UpdateTopic;
import me.hapyl.fight.VersionInfo;
import me.hapyl.spigotutils.module.chat.CenterChat;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.reflect.protocol.ProtocolListener;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public class MotDProtocol extends ProtocolListener {

    private final WrappedServerPing.CompressedImage favicon;
    private final List<WrappedGameProfile> hoverData;
    private final String[] motD;

    public MotDProtocol() {
        super(PacketType.Status.Server.SERVER_INFO);

        this.favicon = loadFavicon();
        this.hoverData = createHoverData();
        this.motD = createMotD();
    }

    @Override
    public void onPacketReceiving(@Nonnull PacketEvent event) {
    }

    @Override
    public void onPacketSending(@Nonnull PacketEvent event) {
        if (motD[0] == null) {
            createMotD();
        }

        if (hoverData.isEmpty()) {
            createHoverData();
        }

        final PacketContainer packet = event.getPacket();
        final StructureModifier<WrappedServerPing> serverPings = packet.getServerPings();
        final WrappedServerPing ping = new WrappedServerPing();

        ping.setMotD(motD[0] + "\n" + motD[1]);
        ping.setFavicon(favicon); // FIXME (hapyl): 024, Feb 24: This doesn't work for some reason
        ping.setPlayers(hoverData);

        ping.setEnforceSecureChat(false);
        ping.setPlayersVisible(true);

        ping.setVersionName("§cCF is on §4%s§c!".formatted(Main.requireMinecraftVersion));
        ping.setPlayersOnline(0);
        ping.setPlayersMaximum(-1);

        serverPings.write(0, ping);
    }

    private String[] createMotD() {
        final String[] motD = new String[2];
        motD[0] = makeMotD("&6&lᴄʟᴀssᴇs ғɪɢʜᴛ &7◆ &8v%s".formatted(CF.getVersionNoSnapshot()), CenterChat.CENTER_PX_MOTD);

        final VersionInfo versionInfo = Main.versionInfo;
        final StringBuilder builder = new StringBuilder();

        int index = 0;
        for (UpdateTopic topic : versionInfo.getUpdateTopic()) {
            if (index++ != 0) {
                builder.append(" &7◆ ");
            }

            builder.append(topic.getTopic());
        }

        motD[1] = makeMotD(builder.toString(), CenterChat.CENTER_PX_MOTD);
        return motD;
    }

    private List<WrappedGameProfile> createHoverData() {
        return makeHoverData("""
                &6ᴄʟᴀssᴇs ғɪɢʜᴛ
                """);
    }

    private String makeMotD(String string, int length) {
        return Chat.color(CenterChat.makeString(string, length));
    }

    private List<WrappedGameProfile> makeHoverData(String i) {
        final List<WrappedGameProfile> list = Lists.newArrayList();
        final String[] splits = i.split("\n");

        for (String split : splits) {
            list.add(new WrappedGameProfile(UUID.randomUUID(), Chat.color(split)));
        }

        return list;
    }

    // FIXME (hapyl): 024, Feb 24: Doesn't work for some reason
    private WrappedServerPing.CompressedImage loadFavicon() {
        try {
            final Main plugin = Main.getPlugin();
            final InputStream resource = plugin.getResource("favicon.png");

            if (resource == null) {
                return null;
            }

            return WrappedServerPing.CompressedImage.fromPng(resource);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
