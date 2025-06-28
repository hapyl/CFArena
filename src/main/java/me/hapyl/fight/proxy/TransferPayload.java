package me.hapyl.fight.proxy;

import me.hapyl.fight.CF;
import me.hapyl.fight.util.Combiner;
import me.hapyl.fight.util.Object2Bytes;
import me.hapyl.fight.util.Sha256;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class TransferPayload {
    
    private static final NamespacedKey keyServer = CF.makeKey("payload_server_from");
    private static final NamespacedKey keySecret = CF.makeKey("payload_secret");
    private static final NamespacedKey keyTimestamp = CF.makeKey("payload_timestamp");
    
    private static final Object2Bytes<ServerType> converterServer = Object2Bytes.ofString(ServerType::ofId, ServerType::toString);
    private static final Object2Bytes<Sha256> converterSecret = Object2Bytes.of(bytes -> Sha256.fromBase64(new String(bytes, StandardCharsets.UTF_8)), Sha256::toBase64);
    private static final Object2Bytes<Long> converterTimestamp = Object2Bytes.of(bytes -> ByteBuffer.wrap(bytes).getLong(), timestamp -> ByteBuffer.allocate(Long.BYTES).putLong(timestamp).array());
    
    private final ServerType server;
    private final Sha256 secret;
    private final long timestamp;
    
    TransferPayload(@Nonnull ServerType server, @Nonnull Sha256 secret, long timestamp) {
        this.server = server;
        this.secret = secret;
        this.timestamp = timestamp;
    }
    
    @Nonnull
    public ServerType server() {
        return server;
    }
    
    @Nonnull
    public Sha256 secret() {
        return secret;
    }
    
    public long timestamp() {
        return timestamp;
    }
    
    public void store(@Nonnull Player player) {
        player.storeCookie(keyServer, converterServer.asBytes(server));
        player.storeCookie(keySecret, converterSecret.asBytes(secret));
        player.storeCookie(keyTimestamp, converterTimestamp.asBytes(timestamp));
    }
    
    @Nonnull
    static CompletableFuture<@Nullable TransferPayload> retrieve(@Nonnull Player player) {
        final CompletableFuture<byte @Nullable []> serverFuture = player.retrieveCookie(keyServer);
        final CompletableFuture<byte @Nullable []> secretFuture = player.retrieveCookie(keySecret);
        final CompletableFuture<byte @Nullable []> timestampFuture = player.retrieveCookie(keyTimestamp);
        
        return serverFuture.thenCombine(secretFuture, Combiner::of)
                           .thenCombine(
                                   timestampFuture, (combiner, timestampBytes) -> {
                                       final byte[] serverBytes = combiner.a();
                                       final byte[] secretBytes = combiner.b();
                                       
                                       if (serverBytes == null || serverBytes.length == 0
                                               || secretBytes == null || secretBytes.length == 0
                                               || timestampBytes == null || timestampBytes.length == 0) {
                                           // Either illegal payload or not transferred using /server command
                                           return null;
                                       }
                                       
                                       converterServer.asObject(serverBytes);
                                       
                                       final ServerType serverType = converterServer.asObjectOrNull(serverBytes);
                                       final Sha256 secret = converterSecret.asObjectOrNull(secretBytes);
                                       final Long timestamp = converterTimestamp.asObjectOrNull(timestampBytes);
                                       
                                       if (serverType == null || secret == null || timestamp == null) {
                                           // Ugly magic codes but I fucking hate Components
                                           throw new IllegalArgumentException("Malformed cookie!\n§8server = %s\n§8secret = %s\n§8timestamp = %s".formatted(
                                                   objToStringOrNull(serverType, false),
                                                   objToStringOrNull(secret, true),
                                                   objToStringOrNull(timestamp, false)
                                           ));
                                       }
                                       
                                       return new TransferPayload(serverType, secret, timestamp);
                                   }
                           );
    }
    
    private static <T> String objToStringOrNull(T obj, boolean secret) {
        return obj == null ? "§4null" : "§a" + (secret ? "<secret>" : obj.toString());
    }
    
}
