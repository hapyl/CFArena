package me.hapyl.fight.proxy;

import com.google.common.collect.Lists;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.util.ThreadOps;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public enum ServerType {
    
    BUILD("build", 30000),
    DEVELOPMENT("development", 30001);
    
    private final String id;
    private final InetSocketAddress address;
    
    ServerType(@Nonnull String id, int port) {
        this.id = id;
        this.address = new InetSocketAddress(InetAddress.getLoopbackAddress(), port);
    }
    
    @Nonnull
    public String id() {
        return id;
    }
    
    @Nonnull
    public InetSocketAddress address() {
        return address;
    }
    
    public int port() {
        return address.getPort();
    }
    
    public boolean isThisServer() {
        return this == CF.getPlugin().thisServer();
    }
    
    public boolean isBound() {
        try (Socket socket = new Socket()) {
            socket.connect(address, 1_000);
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return name().toLowerCase();
    }
    
    @Nonnull
    public static ServerType currentType() {
        return Arrays.stream(values())
                     .filter(type -> type.port() == Bukkit.getPort())
                     .findFirst()
                     .orElseThrow(() -> new IllegalStateException("Illegal port!"));
    }
    
    @Nullable
    public static ServerType ofId(@Nonnull String id) {
        return Arrays.stream(values())
                     .filter(type -> type.id.equals(id))
                     .findFirst()
                     .orElse(null);
    }
    
    @Nonnull
    public static CompletableFuture<List<ServerTypeDescription>> listServers() {
        final CompletableFuture<List<ServerTypeDescription>> future = new CompletableFuture<>();
        
        ThreadOps.async(() -> {
            final List<ServerTypeDescription> descriptions = Lists.newArrayList();
            
            for (ServerType server : values()) {
                if (server.isThisServer()) {
                    descriptions.add(new ServerTypeDescription(server, Status.THIS));
                }
                else {
                    descriptions.add(new ServerTypeDescription(server, server.isBound() ? Status.ONLINE : Status.OFFLINE));
                }
            }
            
            future.complete(descriptions);
        });
        
        return future;
    }
    
    public record ServerTypeDescription(@Nonnull ServerType type, @Nonnull Status status) {
        
        @Nonnull
        @Override
        public String toString() {
            final String ch = status == Status.THIS ? "⭐" : "⏺";
            
            return "%s%s &6%s".formatted(status.color.bold(), ch, type);
        }
    }
    
    public enum Status {
        THIS(Color.GREEN),
        ONLINE(Color.GREEN),
        OFFLINE(Color.DARK_GRAY);
        
        private final Color color;
        
        Status(Color color) {
            this.color = color;
        }
    }
}
