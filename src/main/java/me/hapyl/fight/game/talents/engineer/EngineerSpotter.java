package me.hapyl.fight.game.talents.engineer;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.Location;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class EngineerSpotter extends EngineerConstructTalent {
    
    private final double radius = 35;
    
    public EngineerSpotter(@Nonnull Key key) {
        super(
                key,
                "Spotter",
                99999,
                99999,
                "/summon block_display ~-0.5 ~-0.5 ~-0.5 {Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[2.0000f,0.0000f,0.0000f,-1.0000f,0.0000f,0.5000f,0.0000f,0.0000f,0.0000f,0.0000f,2.0000f,-1.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.5000f,0.0000f,0.0000f,-0.7500f,0.0000f,0.5000f,0.0000f,0.5000f,0.0000f,0.0000f,0.5000f,-0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.5000f,0.0000f,0.0000f,-0.2500f,0.0000f,0.5000f,0.0000f,0.5000f,0.0000f,0.0000f,0.5000f,-0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.5000f,0.0000f,0.0000f,0.2500f,0.0000f,0.5000f,0.0000f,0.5000f,0.0000f,0.0000f,0.5000f,-0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.5000f,0.0000f,0.0000f,-0.2500f,0.0000f,0.5000f,0.0000f,0.5000f,0.0000f,0.0000f,0.5000f,-0.7500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.5000f,0.0000f,0.0000f,-0.2500f,0.0000f,0.5000f,0.0000f,0.5000f,0.0000f,0.0000f,0.5000f,0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.5000f,0.0000f,0.0000f,-0.5000f,0.0000f,0.5000f,0.0000f,0.7500f,0.0000f,0.0000f,0.5000f,-0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.5000f,0.0000f,0.0000f,-0.5000f,0.0000f,0.5000f,0.0000f,0.7500f,0.0000f,0.0000f,0.5000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.5000f,0.0000f,0.0000f,0.0000f,0.0000f,0.5000f,0.0000f,0.7500f,0.0000f,0.0000f,0.5000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.5000f,0.0000f,0.0000f,0.0000f,0.0000f,0.5000f,0.0000f,0.7500f,0.0000f,0.0000f,0.5000f,-0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:redstone_block\",Properties:{}},transformation:[0.7500f,0.0000f,0.0000f,-0.3750f,0.0000f,0.7500f,0.0000f,1.2500f,0.0000f,0.0000f,0.7500f,-0.3750f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:daylight_detector\",Properties:{inverted:\"false\"}},transformation:[0.7500f,0.0000f,0.0000f,-0.3750f,0.0000f,0.7500f,0.0000f,2.0000f,0.0000f,0.0000f,0.7500f,-0.3750f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:lightning_rod\",Properties:{facing:\"up\",powered:\"false\"}},transformation:[1.5000f,0.0000f,0.0000f,-0.7500f,0.0000f,1.5000f,0.0000f,2.2500f,0.0000f,0.0000f,1.5000f,-0.7500f,0.0000f,0.0000f,0.0000f,1.0000f]}]}"
        );
        
        setDescription("""
                       Create a &bSpotter&7 that will &emark&7 all nearby &cenemies&7.
                       """);
        
        setMaterial(Material.ARROW);
        setDurationSec(10);
        setCooldownSec(15);
    }
    
    @Override
    @Nonnull
    public Construct create(@Nonnull GamePlayer player, @Nonnull Location location) {
        throw new IllegalStateException();
    }
    
}
