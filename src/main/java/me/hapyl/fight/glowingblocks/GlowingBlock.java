package me.hapyl.fight.glowingblocks;

import me.hapyl.eterna.module.reflect.glowing.GlowingColor;
import me.hapyl.eterna.module.reflect.team.PacketTeam;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.UUID;

// TODO @May 25, 2025 (xanyjl) ->

public class GlowingBlock {
    
    private final Location location;
    private final GlowingColor color;
    private final PacketShulker shulker;
    private final PacketTeam team;
    
    public GlowingBlock(@Nonnull Location location, @Nonnull GlowingColor color) {
        this.location = location;
        this.color = color;
        this.shulker = new PacketShulker(location);
        this.team = new PacketTeam(UUID.randomUUID().toString());
    }
 
    public void show(@Nonnull Player player) {
        team.create(player);
        team.color(player, color.bukkit);
        team.entry(player, shulker.bukkit().getUniqueId().toString());
        
        shulker.show(player);
    }
    
    public void hide(@Nonnull Player player) {
        team.destroy(player);
        shulker.hide(player);
    }
    
}
