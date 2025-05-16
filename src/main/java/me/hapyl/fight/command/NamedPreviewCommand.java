package me.hapyl.fight.command;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.util.ArgumentList;
import me.hapyl.eterna.module.util.Described;
import me.hapyl.eterna.module.util.Named;
import me.hapyl.fight.Message;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.NamedColoredPrefixed;
import me.hapyl.fight.game.color.Color;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

public class NamedPreviewCommand extends CFCommand {
    public NamedPreviewCommand(@Nonnull String name) {
        super(name, PlayerRank.ADMIN);
    }
    
    @Override
    protected void execute(@Nonnull Player player, @Nonnull ArgumentList args, @Nonnull PlayerRank rank) {
        final String targetPackage = "me.hapyl.fight." + args.getString(0);
        final String targetField = args.getString(1);
        
        try {
            final Class<?> clazz = Class.forName(targetPackage);
            final Field field = clazz.getDeclaredField(targetField);
            field.setAccessible(true);
            
            final Object obj = field.get(null); // Must be static
            
            String name = null;
            String description = null;
            
            // Fetch name
            if (obj instanceof NamedColoredPrefixed ncp) {
                name = ncp.toString();
            }
            else if (obj instanceof Named named) {
                name = named.getName();
            }
            
            // Fetch description
            if (obj instanceof Described described) {
                description = described.getDescription();
            }
            
            if (name == null) {
                Message.error(player, "The given field doesn't implement Named!");
                return;
            }
            
            final Inventory inventory = Bukkit.createInventory(null, InventoryType.DISPENSER, Component.text("", Color.DEFAULT));
            
            inventory.setItem(
                    4, new ItemBuilder(Material.PAPER)
                            .setName(name)
                            .addTextBlockLore(description != null ? description : "&8No description.")
                            .asIcon()
            );
            
            player.openInventory(inventory);
        }
        catch (ClassNotFoundException e) {
            Message.error(player, "Could not find class {%s}!".formatted(targetPackage));
        }
        catch (NoSuchFieldException e) {
            Message.error(player, "Could not find field {%s}!".formatted(targetField));
        }
        catch (IllegalAccessException e) {
            Message.error(player, "Could not access field {%s}!".formatted(targetField));
        }
    }
}
