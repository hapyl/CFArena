package me.hapyl.fight;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public interface MaterialData {
    
    @Nonnull
    Material material();
    
    @Nullable
    default Consumer<ItemBuilder> function() {
        return null;
    }
    
    @Nonnull
    static MaterialData of(@Nonnull Material material, @Nonnull Consumer<ItemBuilder> function) {
        return new MaterialData() {
            @Nonnull
            @Override
            public Material material() {
                return material;
            }
            
            @Nonnull
            @Override
            public Consumer<ItemBuilder> function() {
                return function;
            }
        };
    }
    
}
