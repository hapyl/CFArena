package me.hapyl.fight.command;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.command.SimplePlayerAdminCommand;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.Main;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.maps.EnumLevel;
import me.hapyl.fight.game.maps.Level;
import me.hapyl.fight.game.maps.PredicateLocation;
import me.hapyl.fight.game.maps.gamepack.GamePack;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Set;
import java.util.function.Consumer;

public class HighlightLevel extends SimplePlayerAdminCommand {

    private final Set<Entity> markers;
    private BukkitTask task;

    public HighlightLevel(String name) {
        super(name);

        markers = Sets.newHashSet();
    }

    private void removeSpawnedMarker() {
        markers.forEach(Entity::remove);
        markers.clear();

        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    @Override
    protected void execute(Player player, String[] args) {
        final EnumLevel enumLevel = getArgument(args, 0).toEnum(EnumLevel.class, Manager.current().getCurrentMap());

        if (enumLevel == null) {
            player.sendMessage(NamedTextColor.DARK_RED + "Invalid level!");
            return;
        }

        removeSpawnedMarker();

        final Level level = enumLevel.getLevel();

        // Highlight spawn points
        int locationCount = 0;
        for (PredicateLocation location : level.getLocations()) {
            spawn(location.getLocation(), item -> item.setItemStack(new ItemStack(Material.ARMOR_STAND)), """
                    &aLocation &b%s
                    """.formatted(++locationCount)
            );
        }

        for (GamePack gamePack : level.getGamePacks()) {
            int count = 0;
            for (Location location : gamePack.getLocations()) {
                spawn(location, item -> item.setItemStack(gamePack.getTexture()), """
                        &a%s &b%s
                        """.formatted(gamePack.getClass().getSimpleName(), ++count));
            }
        }

        Debug.info("Highlighted %s (%s).".formatted(level.getName(), markers.size()));

        task = new BukkitRunnable() {
            @Override
            public void run() {
                removeSpawnedMarker();

                Debug.info("Removed highlighted.");
            }
        }.runTaskLater(Main.getPlugin(), Tick.fromSecond(30));
    }

    private void spawn(Location location, Consumer<ItemDisplay> consumer, String text) {
        final Location theLocation = BukkitUtils.newLocation(location).add(0, 1, 0);

        final ItemDisplay itemEntity = Entities.ITEM_DISPLAY.spawn(theLocation, self -> {
            self.setGlowing(true);
            self.setGlowColorOverride(Color.GREEN);

            consumer.accept(self);
        });

        final TextDisplay textEntity = Entities.TEXT_DISPLAY.spawn(theLocation.add(0, 1, 0), self -> {
            final StringBuilder builder = new StringBuilder();
            final String[] lines = text.split("\n");

            for (int i = 0; i < lines.length; i++) {
                if (i != 0) {
                    builder.append("\n");
                }

                builder.append(Chat.format(lines[i]));
            }

            self.setText(builder.toString());
            self.setBillboard(Display.Billboard.CENTER);

            self.setGlowing(true);
            self.setGlowColorOverride(Color.GREEN);
        });

        markers.add(itemEntity);
        markers.add(textEntity);
    }
}
