package me.hapyl.fight.util;

import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ApiStatus.NonExtendable
public interface Combiner {
    
    @Nonnull
    static <A, B> Object2<A, B> of(@Nullable A a, @Nullable B b) {
        return new Object2<>() {
            @Nullable
            @Override
            public A a() {
                return a;
            }
            
            @Nullable
            @Override
            public B b() {
                return b;
            }
        };
    }
    
    @Nonnull
    static <A, B, C> Object3<A, B, C> of(@Nullable A a, @Nullable B b, @Nullable C c) {
        return new Object3<>() {
            @Nullable
            @Override
            public A a() {
                return a;
            }
            
            @Nullable
            @Override
            public B b() {
                return b;
            }
            
            @Nullable
            @Override
            public C c() {
                return c;
            }
        };
    }
    
    interface Object2<A, B> {
        @Nullable
        A a();
        
        @Nullable
        B b();
    }
    
    interface Object3<A, B, C> {
        @Nullable
        A a();
        
        @Nullable
        B b();
        
        @Nullable
        C c();
    }
    
}
