package me.hapyl.fight.game.talents.engineer;

import me.hapyl.eterna.module.entity.EntityUtils;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.maps.supply.Supplies;
import me.hapyl.fight.game.maps.supply.Supply;
import me.hapyl.fight.game.maps.supply.SupplyInstance;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.stream.Collectors;

public class EngineerDispenser extends EngineerConstructTalent {
    
    private final Supply[] availableSupplies = {
            Supplies.HEALTH,
            Supplies.ENERGY,
            Supplies.CRIT
    };
    
    @DisplayField private final int spawnPeriod = Tick.fromSeconds(8);
    
    public EngineerDispenser(@Nonnull Key key) {
        super(
                key,
                "Dispenser",
                5,
                5,
                "{Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:vault\",Properties:{facing:\"east\",ominous:\"false\",vault_state:\"active\"}},transformation:[1f,0f,0f,-0.5f,0f,0.1875f,0f,0.15625f,0f,0f,1f,-0.418125f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1531952815,1186492689,-979257649,-2094604857],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDk3NDNjZmE0ZGYxYWIzMWQ0ZTZhMTc4MjRmNmYyNjVkMDNlYTY2NjBjMTc3Y2EwY2IwNDQ2NmFhZGFmMjIzYyJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,0f,2.25f,-0.00125f,-0.6875f,0f,0f,0.148125f,0f,-2.1875f,0f,-0.453125f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1403304694,-1205474309,1775870109,805055381],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjU0OGJhYjNjNDhkYzg1ZWQzMzI4YWQ4YWM4NzE5ZDBjMTM0ODM5NzFkODE4ZWU2ZDIwOTc0ZDkxYTZlYjVlZiJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.6875f,0f,0f,0.31875f,0f,0.6875f,0f,0.86375f,0f,0f,0.625f,0.506875f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1981413025,-310411324,-930483111,-170936354],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjU0OGJhYjNjNDhkYzg1ZWQzMzI4YWQ4YWM4NzE5ZDBjMTM0ODM5NzFkODE4ZWU2ZDIwOTc0ZDkxYTZlYjVlZiJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.6875f,0f,0f,-0.333125f,0f,0.6875f,0f,0.929375f,0f,0f,0.625f,0.48375f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;840813459,-293803305,-182262692,-239195549],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjkwNWRjODI0YWRhY2NlNTY1N2JjOThlNGFkNDFiNTA0NTQ0MDA2Mzc2Y2I4ZGJjZmY5ODE2MzQxNGY4MGQ2ZCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.5625f,0f,0f,0f,0f,0.5625f,0f,0.951875f,0f,0f,0.5625f,0.5f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1320430388,1472893442,806792976,1490013635],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjliZTg5NGFkMTBmZTQ4NGEyZDRmMjg0ZDZhOTBkZDBjYmJjNDI4YjNlMzAxYWY5MGU4YzBjNjBjMDY2YTg0YyJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.6875f,0f,0f,-0.343125f,0f,0f,0.625f,0.75625f,0f,-0.6875f,0f,0.43125f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-2077492531,-674406285,997748093,439406713],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzdlZDVlZjUzMzAwNzczNWVlNDQwZTg0ZjU1M2ViZDY4ODYxZTAyZmM1MmE1ZmY5M2MyN2I1ODQyOTdiMzUwYSJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.6875f,0f,0f,-0.34f,0f,0f,-1.108f,0.31375f,0f,0.625f,0f,0.765f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-764046856,-2030833702,1190623420,-508264710],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzdlZDVlZjUzMzAwNzczNWVlNDQwZTg0ZjU1M2ViZDY4ODYxZTAyZmM1MmE1ZmY5M2MyN2I1ODQyOTdiMzUwYSJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.625f,0f,0f,0f,0f,0f,-1.9205f,0.47625f,0f,0.625f,0f,0.84375f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1750506470,1378281486,116592834,455225318],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjYwNGFkY2JkN2Y3NmE4Yjg0Y2IzMDY2ZWRmMTUxNTQ4ZWEzMmJlMWRjNzA3ODFiZTU2M2Q0M2NiMThhMzk5YiJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.6255f,0f,0f,0f,0f,0.5625f,0f,1.2f,0f,0f,0.5885f,0.69f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1525049326,801219009,1570636771,-894904171],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjU2NzkyOTY1YjYwNmJhODVmODdhNTEwY2ViZTExMGMwNTk5ZmI2OTgxNTEwNWU2MDZlMTA2YzU5MTlkMTRjMiJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.6875f,0f,0f,0f,0f,0f,-0.6875f,0.5f,0f,0.375f,0f,0.845625f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:lightning_rod\",Properties:{facing:\"down\",powered:\"false\"}},transformation:[1f,0f,0f,-0.5f,0f,0.8125f,0f,0.10375f,0f,0f,1f,-0.038125f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-599216358,903105318,1773772970,662047553],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODRmYzU2ZDRiOGI3ZGNkZTlhNzQ4NzQ3MzQ1NDhiZDYyNzY4ZTFmY2E0ZTYzYjRlM2E1YmJjYTViYjMwYWE3MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,0f,1f,-0.375f,0f,0.6875f,0f,0.290625f,-0.8125f,0f,0f,0.125f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1197774410,-2035670974,1679589813,2101748907],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODRmYzU2ZDRiOGI3ZGNkZTlhNzQ4NzQ3MzQ1NDhiZDYyNzY4ZTFmY2E0ZTYzYjRlM2E1YmJjYTViYjMwYWE3MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.8125f,0f,0f,0f,0f,0.6875f,0f,0.290625f,0f,0f,1f,-0.25f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;125779439,-707403223,1175602045,-386560245],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODRmYzU2ZDRiOGI3ZGNkZTlhNzQ4NzQ3MzQ1NDhiZDYyNzY4ZTFmY2E0ZTYzYjRlM2E1YmJjYTViYjMwYWE3MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,0f,-1f,0.375f,0f,0.6875f,0f,0.290625f,0.8125f,0f,0f,0.125f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;2067183530,-918158451,-775852026,1310763548],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjliZTg5NGFkMTBmZTQ4NGEyZDRmMjg0ZDZhOTBkZDBjYmJjNDI4YjNlMzAxYWY5MGU4YzBjNjBjMDY2YTg0YyJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.6875f,0f,0f,-0.33125f,0f,0.6875f,0f,0.24375f,0f,0f,0.625f,0.6f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;313044462,-1882800088,622748315,883652995],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjliZTg5NGFkMTBmZTQ4NGEyZDRmMjg0ZDZhOTBkZDBjYmJjNDI4YjNlMzAxYWY5MGU4YzBjNjBjMDY2YTg0YyJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.6875f,0f,0f,0.325f,0f,0f,0.625f,0.7f,0f,-0.6875f,0f,0.4375f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;684391707,-202223890,1421197039,2063539507],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzdlZDVlZjUzMzAwNzczNWVlNDQwZTg0ZjU1M2ViZDY4ODYxZTAyZmM1MmE1ZmY5M2MyN2I1ODQyOTdiMzUwYSJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.6875f,0f,0f,0.32375f,0f,0f,-1.108f,0.26f,0f,0.625f,0f,0.765f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1685371312,674262023,2069991293,-693624014],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjliZTg5NGFkMTBmZTQ4NGEyZDRmMjg0ZDZhOTBkZDBjYmJjNDI4YjNlMzAxYWY5MGU4YzBjNjBjMDY2YTg0YyJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.6875f,0f,0f,0.33125f,0f,0.6875f,0f,0.24375f,0f,0f,0.625f,0.6f,0f,0f,0f,1f]}]}"
        );
        
        setDescription("""
                       Create a &fDispenser&7 that generates a random supply pack, including:
                       
                       %s
                       &8&o;;Supply packs created by Dispenser cannot be hacked.
                       """.formatted(
                Arrays.stream(availableSupplies)
                      .map(supply -> "&8•&a %s\n&7 ;;%s".formatted(supply.getName(), supply.getDescription()))
                      .collect(Collectors.joining("\n"))
        ));
        
        setType(TalentType.SUPPORT);
        setMaterial(Material.DISPENSER);
        
        setCooldownSec(30);
        
        yOffset = 1.0;
    }
    
    @Nonnull
    @Override
    public Construct create(@Nonnull GamePlayer player, @Nonnull Location location) {
        return new DispenserConstruct(player, location);
    }
    
    private class DispenserConstruct extends Construct {
        
        private SupplyInstance instance;
        
        DispenserConstruct(@Nonnull GamePlayer player, @Nonnull Location location) {
            super(player, location, EngineerDispenser.this);
        }
        
        @Nonnull
        @Override
        public ImmutableInt3Array chargesScaled() {
            return new ImmutableInt3Array(5, 8, 12);
        }
        
        @Nonnull
        @Override
        public ImmutableInt3Array healthScaled() {
            return new ImmutableInt3Array(20, 30, 40);
        }
        
        @Override
        public void onCreate() {
            // Remove slime collision
            constructEntity.setCollision(EntityUtils.Collision.DENY);
        }
        
        @Override
        public void onDestroy() {
            if (instance != null) {
                instance.remove();
            }
        }
        
        @Override
        public boolean onTick() {
            if (tick == 0 || tick % spawnPeriod != 0) {
                return false;
            }
            
            if (instance != null) {
                return false;
            }
            
            instance = CollectionUtils.randomElementOrFirst(availableSupplies).newInstance(
                    location, (supply, loc) -> new SupplyInstance(supply, loc) {
                        @Override
                        public void onPickup(@Nonnull GamePlayer player) {
                            super.onPickup(player);
                            
                            instance = null;
                            charges--;
                        }
                    }
            );
            
            // Fx
            constructEntity.playWorldSound(Sound.BLOCK_DISPENSER_DISPENSE, 0.75f);
            constructEntity.playWorldSound(Sound.ENTITY_EGG_THROW, 0.75f);
            
            return false;
        }
    }
}
