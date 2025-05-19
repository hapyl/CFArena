package me.hapyl.fight.game.maps.supply;

import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayData;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.garbage.SynchronizedGarbageEntityCollector;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public class SupplyPlatform implements Ticking, Removable {
    
    public static final int displayThreshold;
    private static final DisplayData platform;
    
    static {
        platform = BDEngine.parse(
                "{Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:smooth_stone_slab\",Properties:{type:\"bottom\"}},transformation:[1f,0f,0f,-0.5f,0f,1f,0f,-0.375f,0f,0f,1f,-0.5f,0f,0f,0f,1f]}]}"
        );
        
        displayThreshold = Tick.fromSeconds(30);
    }
    
    private final Supply supply;
    private final Location location;
    private final DisplayEntity model;
    private final TextDisplay text;
    
    protected SupplyInstance instance;
    protected int tick;
    
    public SupplyPlatform(@Nonnull Supply supply, @Nonnull Location location) {
        this.supply = supply;
        this.location = location;
        this.model = platform.spawn(location, SynchronizedGarbageEntityCollector::add);
        this.text = Entities.TEXT_DISPLAY.spawn(
                LocationHelper.addAsNew(location, 0, 0.5, 0), self -> {
                    self.setBillboard(Display.Billboard.CENTER);
                    
                    SynchronizedGarbageEntityCollector.add(self);
                }
        );
    }
    
    public boolean exists() {
        return instance != null;
    }
    
    @Nonnull
    public Supply supply() {
        return supply;
    }
    
    @Nullable
    public SupplyInstance instance() {
        return instance;
    }
    
    public boolean hackSupply(@Nonnull GamePlayer hacker) {
        if (instance == null || instance instanceof HackedSupplyInstance) {
            return false;
        }
        
        respawn0(() -> supply.newInstance(location, (supply, loc) -> new HackedSupplyInstance(hacker, this, supply, location)));
        return true;
    }
    
    public void tick(int tick) {
        this.tick = tick;
    }
    
    public void respawn() {
        respawn0(() -> supply.newInstance(location, (supply, loc) -> new PlatformSupplyInstance(this, supply, location)));
    }
    
    @Override
    public void tick() {
        // Don't tick the platform is instance exists
        if (instance != null) {
            return;
        }
        
        final int ticksBeforeRespawn = supply.respawnTime - tick++;
        
        // Display the respawn time if < 30s
        if (ticksBeforeRespawn <= displayThreshold) {
            text.text(Component.text("%.1fs".formatted(ticksBeforeRespawn / 20d), NamedTextColor.AQUA));
        }
        else {
            text.text(Component.empty());
        }
        
        // Respawn
        if (ticksBeforeRespawn == 0) {
            respawn();
        }
    }
    
    @Override
    public void remove() {
        model.remove();
        text.remove();
        
        if (instance != null) {
            instance.remove();
        }
    }
    
    private <T extends PlatformSupplyInstance> void respawn0(Supplier<T> supplier) {
        if (instance != null) {
            instance.remove();
        }
        
        instance = supplier.get();
        tick = 0;
    }
}
