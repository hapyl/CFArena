package me.hapyl.fight.game.maps.supply;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.particle.ParticleBuilder;
import me.hapyl.eterna.module.util.Described;
import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public abstract class Supply implements Described {
    
    protected final int respawnTime;
    protected final ItemStack itemStack;
    
    private final String name;
    private String description;
    
    protected Supply(int respawnTime, @Nonnull String name, @Nonnull String texture) {
        this.name = name + " Supply";
        this.description = "";
        this.respawnTime = respawnTime;
        this.itemStack = ItemBuilder.playerHeadUrl(texture).toItemStack();
    }
    
    @Nonnull
    @Override
    public String getName() {
        return name;
    }
    
    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }
    
    @Override
    public void setDescription(@Nonnull String description) {
        this.description = description;
    }
    
    public int respawnTime() {
        return respawnTime;
    }
    
    @Nonnull
    public <T extends SupplyInstance> T newInstance(@Nonnull Location location, @Nonnull BiFunction<Supply, Location, T> fn) {
        return fn.apply(this, location);
    }
    
    public abstract void pickup(@Nonnull GamePlayer player);
    
    public abstract void tick(@Nonnull SupplyInstance instance);
    
    protected void particle(SupplyInstance instance, ParticleBuilder builder) {
        final int tick = instance.getTick();
        final Location location = instance.location();
        
        final double rad = Math.toRadians(tick * 5);
        final double x = Math.cos(rad) * 0.6;
        final double y = Math.sin(rad * 4) * 0.05 + 1;
        final double z = Math.sin(rad) * 0.6;
        
        LocationHelper.offset(location, x, y, z, (Consumer<Location>) builder::display);
        LocationHelper.offset(location, -x, y, -z, (Consumer<Location>) builder::display);
    }
}
