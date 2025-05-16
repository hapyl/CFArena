package me.hapyl.fight.game;

import javax.annotation.Nonnull;

public interface ItemFactoryProvider {
    
    /**
     * Gets the {@link ItemFactory} for this object.
     *
     * @return the item factory for this object.
     * @implNote Note that impl must cast the generic factory to the concrete factory.
     * <pre>{@code
     * class MyProvider implements ItemFactoryProvider {
     *     @Override
     *     public MyFactory itemFactory() {
     *         return myItemFactory;
     *     }
     * }}</pre>
     */
    @Nonnull
    ItemFactory<?> itemFactory();
    
}
