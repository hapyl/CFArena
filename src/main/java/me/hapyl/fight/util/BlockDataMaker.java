package me.hapyl.fight.util;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public final class BlockDataMaker {

    private BlockDataMaker() {
    }

    @Nonnull
    public static <T extends BlockData> BlockData of(@Nonnull Material material, @Nonnull Class<T> clazz, @Nonnull Consumer<T> consumer) {
        return material.createBlockData(data -> {
            if (!clazz.isInstance(data)) {
                throw new ClassCastException("%s needs %s block data, not %s!".formatted(
                        material.name(),
                        material.getData().getSimpleName(),
                        clazz.getSimpleName()
                ));
            }

            consumer.accept(clazz.cast(data));
        });
    }

}
