package me.hapyl.fight.game.talents.archive.engineer;

import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class EngineerSentry extends EngineerTalent {

    public EngineerSentry() {
        super("Spotter", 4);

        setDescription("""
                Create a Spotter.
                He will mark any player nearby.
                """);

        setItem(Material.ARROW);
        setCooldownSec(15);

    }

    @Override
    @Nonnull
    public Construct create(@Nonnull GamePlayer player, @Nonnull Location location) {
        return new Construct(player, location, this) {
            @Override
            public void onCreate() {

            }

            @Override
            public ImmutableArray<Double> healthScaled() {
                return ImmutableArray.of(20d, 40d, 60d, 80d);
            }

            @Override
            public ImmutableArray<Integer> durationScaled() {
                return ImmutableArray.of(10, 20, 30, 40);
            }

            @Override
            public void onDestroy() {
                player.sendMessage("&cYour previous &lSpotter &cwas destroyed!");
            }

            @Override
            public void onTick() {
                if(modulo(40)){
                    Collect.nearbyEntities(location,35).forEach(entity->{
                        if(entity.equals(player)){
                            return;
                        }
                        entity.addPotionEffect(PotionEffectType.GLOWING,200,1);
                    });


                }
            }
        };
    }

    @Nonnull
    @Override
    public Response predicate(@Nonnull GamePlayer player, @Nonnull Location location) {
        return Response.OK;
    }
}
