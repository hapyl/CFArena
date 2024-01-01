package me.hapyl.fight.game.talents.archive.engineer;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.util.Collect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class EngineerSentry extends EngineerTalent {

    private final double radius = 35;

    public EngineerSentry() {
        super("Spotter", 4);

        setDescription("""
                Create a &bSpotter&7 that will &emark&7 all nearby &cenemies&7.
                """);

        setItem(Material.ARROW);
        setDurationSec(10);
        setCooldownSec(15);

        setDisplayData(
                0,
                "/summon block_display ~-0.5 ~-0.5 ~-0.5 {Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[2.0000f,0.0000f,0.0000f,-1.0000f,0.0000f,0.5000f,0.0000f,0.0000f,0.0000f,0.0000f,2.0000f,-1.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.5000f,0.0000f,0.0000f,-0.7500f,0.0000f,0.5000f,0.0000f,0.5000f,0.0000f,0.0000f,0.5000f,-0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.5000f,0.0000f,0.0000f,-0.2500f,0.0000f,0.5000f,0.0000f,0.5000f,0.0000f,0.0000f,0.5000f,-0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.5000f,0.0000f,0.0000f,0.2500f,0.0000f,0.5000f,0.0000f,0.5000f,0.0000f,0.0000f,0.5000f,-0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.5000f,0.0000f,0.0000f,-0.2500f,0.0000f,0.5000f,0.0000f,0.5000f,0.0000f,0.0000f,0.5000f,-0.7500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.5000f,0.0000f,0.0000f,-0.2500f,0.0000f,0.5000f,0.0000f,0.5000f,0.0000f,0.0000f,0.5000f,0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.5000f,0.0000f,0.0000f,-0.5000f,0.0000f,0.5000f,0.0000f,0.7500f,0.0000f,0.0000f,0.5000f,-0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.5000f,0.0000f,0.0000f,-0.5000f,0.0000f,0.5000f,0.0000f,0.7500f,0.0000f,0.0000f,0.5000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.5000f,0.0000f,0.0000f,0.0000f,0.0000f,0.5000f,0.0000f,0.7500f,0.0000f,0.0000f,0.5000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:exposed_copper\",Properties:{}},transformation:[0.5000f,0.0000f,0.0000f,0.0000f,0.0000f,0.5000f,0.0000f,0.7500f,0.0000f,0.0000f,0.5000f,-0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:redstone_block\",Properties:{}},transformation:[0.7500f,0.0000f,0.0000f,-0.3750f,0.0000f,0.7500f,0.0000f,1.2500f,0.0000f,0.0000f,0.7500f,-0.3750f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:daylight_detector\",Properties:{inverted:\"false\"}},transformation:[0.7500f,0.0000f,0.0000f,-0.3750f,0.0000f,0.7500f,0.0000f,2.0000f,0.0000f,0.0000f,0.7500f,-0.3750f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:lightning_rod\",Properties:{facing:\"up\",powered:\"false\"}},transformation:[1.5000f,0.0000f,0.0000f,-0.7500f,0.0000f,1.5000f,0.0000f,2.2500f,0.0000f,0.0000f,1.5000f,-0.7500f,0.0000f,0.0000f,0.0000f,1.0000f]}]}"
        );
    }

    @Override
    @Nonnull
    public Construct create(@Nonnull GamePlayer player, @Nonnull Location location) {

        return new Construct(player, location, this) {
            @Override
            public void onCreate() {
            }

            @Override
            public void onDestroy() {

            }

            @Nonnull
            @Override
            public ImmutableArray<Double> healthScaled() {
                return ImmutableArray.of(20d, 40d, 60d, 80d);
            }

            @Nonnull
            @Override
            public ImmutableArray<Integer> durationScaled() {
                return ImmutableArray.of(10, 20, 30, 40);
            }

            @Override
            public void onTick() {
                if (modulo(40)) {
                    Collect.nearbyEntities(location, radius).forEach(entity -> {
                        if (player.isSelfOrTeammate(entity)) {
                            return;
                        }

                        entity.addPotionEffect(PotionEffectType.GLOWING, getDuration(), 1);
                    });
                }
            }
        };
    }

}
