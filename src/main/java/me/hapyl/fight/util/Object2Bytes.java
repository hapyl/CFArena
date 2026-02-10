package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public interface Object2Bytes<T> {
    
    @Nonnull
    T asObject(@Nonnull byte[] bytes);
    
    @Nullable
    default T asObjectOrNull(@Nullable byte[] bytes) {
        try {
            return bytes != null ? asObject(bytes) : null;
        }
        catch (Exception e) {
            return null;
        }
    }
    
    byte[] asBytes(@Nonnull T t);
    
    @Nonnull
    static <T> Object2Bytes<T> of(@Nonnull Function<byte[], T> asObject, @Nonnull Function<T, byte[]> asBytes) {
        return new Object2Bytes<>() {
            @Nonnull
            @Override
            public T asObject(@Nonnull byte[] bytes) {
                return asObject.apply(bytes);
            }
            
            @Override
            public byte[] asBytes(@Nonnull T t) {
                return asBytes.apply(t);
            }
        };
    }
    
    @Nonnull
    static <T> Object2Bytes<T> ofString(@Nonnull Function<String, T> asObject, @Nonnull Function<T, String> asBytes) {
        return new Object2Bytes<>() {
            private static final Charset charset = StandardCharsets.UTF_8;
            
            @Nonnull
            @Override
            public T asObject(@Nonnull byte[] bytes) {
                return asObject.apply(new String(bytes, charset));
            }
            
            @Override
            public byte[] asBytes(@Nonnull T t) {
                return asBytes.apply(t).getBytes(charset);
            }
        };
    }
    
    
}
