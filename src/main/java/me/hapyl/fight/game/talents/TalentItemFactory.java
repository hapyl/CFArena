package me.hapyl.fight.game.talents;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.util.SmallCaps;
import me.hapyl.fight.ItemCreator;
import me.hapyl.fight.game.Constants;
import me.hapyl.fight.game.ItemFactory;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.displayfield.DisplayFieldSerializer;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.function.Consumer;

public class TalentItemFactory extends ItemFactory<Talent> {
    
    public final ItemCreator description;
    public final ItemCreator details;
    
    public TalentItemFactory(@Nonnull Talent talent) {
        super(talent);
        
        this.description = new ItemCreatorDescription();
        this.details = new ItemCreatorDetails();
    }
    
    private class ItemCreatorDescription extends TalentItemCreator {
        @Nonnull
        @Override
        public ItemBuilder createBuilder() {
            final ItemBuilder builder = super.createBuilder();
            
            // Append description
            builder.addTextBlockLore(StaticTalentFormat.format(product.description, product));
            
            // Append alt usage
            if (!product.autoAdd) {
                builder.addLore("");
                builder.addSmartLore(product.altUsage, "&8&o");
            }
            
            product.juiceDescription(builder);
            
            return builder;
        }
    }
    
    private class ItemCreatorDetails extends TalentItemCreator {
        @Nonnull
        @Override
        public ItemBuilder createBuilder() {
            final ItemBuilder builder = super.createBuilder();
            
            builder.removeLore();
            builder.addLore("&8Details");
            
            // Type description
            builder.addLore();
            builder.addLore("&f&l" + SmallCaps.format(product.type.getNameLowerCase()));
            builder.addSmartLore(product.type.getDescription());
            
            // Attributes
            builder.addLore();
            builder.addLore("&f&lᴀᴛᴛʀɪʙᴜᴛᴇꜱ");
            
            if (product.cd > 0) {
                builder.addLore("%s: &f&l%s".formatted(product.cooldownString(), product.cd >= Constants.INDEFINITE_COOLDOWN ? CFUtils.INF_CHAR : CFUtils.formatTick(product.cd)));
            }
            else if (product.cd == Constants.INFINITE_DURATION) {
                builder.addLore("%s: &f&lDynamic".formatted(product.cooldownString()));
            }
            
            if (product.duration > 0) {
                builder.addLore("Duration: &f&l%s".formatted(CFUtils.formatTick(product.duration)));
            }
            
            if (product.point > 0) {
                builder.addLore("Energy Generation: &f&l%s".formatted(product.point));
            }
            
            product.juiceDetails(builder);
            
            // Display fields
            DisplayFieldSerializer.serialize(builder, product);
            
            return builder;
        }
    }
    
    private class TalentItemCreator implements ItemCreator {
        private ItemStack cachedItem;
        
        @Nonnull
        @Override
        public ItemStack createItem() {
            if (cachedItem == null) {
                cachedItem = createBuilder().build();
            }
            
            return cachedItem;
        }
        
        @Nonnull
        @Override
        @OverridingMethodsMustInvokeSuper
        public ItemBuilder createBuilder() {
            final ItemBuilder builder = new ItemBuilder(product.material.material())
                    .setName(Color.SUCCESS + product.name)
                    .addLore("&8" + product.getTypeFormattedWithClassType())
                    .addLore();
            
            // Execute function if present
            // - The function now handles both extra data AND texture
            final Consumer<ItemBuilder> function = product.material.function();
            
            if (function != null) {
                function.accept(builder);
            }
            
            // Juice the builder
            product.juice(builder);
            
            // Set cooldown key
            builder.setCooldown(cd -> cd.setCooldownGroup(product.cooldownKey().asNamespacedKey()));
            builder.hideFlags();
            
            return builder;
        }
    }
    
}
