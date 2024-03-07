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
import java.awt.image.BufferedImage;
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
        final PacketContainer packet = event.getPacket();
        final StructureModifier<WrappedServerPing> serverPings = packet.getServerPings();
        final WrappedServerPing ping = new WrappedServerPing();

        ping.setMotD(motD[0] + "\n" + motD[1]);
        ping.setFavicon(favicon);
        ping.setPlayers(hoverData);

        ping.setVersionProtocol(serverPings.read(0).getVersionProtocol());
        ping.setEnforceSecureChat(false);
        ping.setPlayersVisible(true);

        ping.setVersionName("§6[§cCF is on §4%s§c!§6]".formatted(Main.requireMinecraftVersion));

        final int playerCount = CF.getOnlinePlayerCount();

        ping.setPlayersOnline(playerCount);
        ping.setPlayersMaximum(playerCount + 1);

        serverPings.write(0, ping);
    }

    private String[] createMotD() {
        final String[] motD = new String[2];
        motD[0] = makeMotD("&6&lᴄғ ᴀʀᴇɴᴀ &7◆ &8v%s".formatted(CF.getVersionNoSnapshot()), CenterChat.CENTER_PX_MOTD);

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
                &e&l&k|| &6Website: &b&kno website
                &e&l&k|| &6Game Version: &b%s
                """.formatted(CF.getVersionNoSnapshot()));
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

    private WrappedServerPing.CompressedImage loadFavicon() {
        try {
            final Main plugin = Main.getPlugin();
            final InputStream resource = plugin.getResource("favicon.png");

            if (resource == null) {
                return null;
            }

            final WrappedServerPing.CompressedImage compressedImage = WrappedServerPing.CompressedImage.fromPng(resource);
            final BufferedImage bufferedImage = compressedImage.getImage();

            final int width = bufferedImage.getWidth();
            final int height = bufferedImage.getHeight();

            if (width != 64 || height != 64) {
                throw new IllegalArgumentException("Favicon must be 64x64, not %sx%s!".formatted(width, height));
            }

            return compressedImage;
        } catch (Exception e) {
            throw new IllegalArgumentException("Error loading favicon! " + e.getMessage());
        }
    }

}
