package me.hapyl.fight.proxy;

import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public enum ServerType {

    BUILD(25565),
    DEVELOPMENT(33333);

    public final int port;

    ServerType(int port) {
        this.port = port;
    }

    public boolean isEnabled() {
        try (Socket ignored = new Socket(InetAddress.getLocalHost(), port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Nonnull
    public static ServerType currentType() {
        final int port = Bukkit.getServer().getPort();

        for (ServerType type : values()) {
            if (type.port == port) {
                return type;
            }
        }

        throw new IllegalStateException("Illegal port.");
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
