package me.hapyl.fight.game.talents.pytaria;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.techie.Talent;
import me.hapyl.fight.game.task.TimedGameTask;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.util.CollectionUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class FlowerBreeze extends Talent {

    @DisplayField(scaleFactor = 100.0d) public final double attackIncrease = 0.3d;
    @DisplayField(scaleFactor = 100.0d) public final double defenseIncrease = 1.5d;
    @DisplayField private final double healthSacrifice = 15.0d;

    private final Material[] flowers = {
            Material.POPPY,
            Material.DANDELION,
            Material.ALLIUM,
            Material.RED_TULIP,
            Material.ORANGE_TULIP,
            Material.PINK_TULIP,
            Material.WHITE_TULIP,
            Material.OXEYE_DAISY,
            Material.CORNFLOWER,
            Material.AZURE_BLUET
    };

    private final TemperInstance temper =
            Temper.FLOWER_BREEZE.newInstance("Flower Breeze")
                    .increase(AttributeType.ATTACK, attackIncrease)
                    .increase(AttributeType.DEFENSE, defenseIncrease);

    public FlowerBreeze() {
        super("Flower Breeze", """
                Feel the breeze of the flowers that damages your but grants &c%s &7and &b%s &7boost for {duration}.
                                
                &8;;This ability cannot kill.
                """.formatted(AttributeType.ATTACK, AttributeType.DEFENSE));

        setType(TalentType.ENHANCE);
        setItem(Material.RED_DYE);
        setDurationSec(3);
        setCooldownSec(16);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();
        final World world = player.getWorld();

        player.playWorldSound(Sound.ENTITY_HORSE_BREATHE, 0.0f);
        player.addEffect(Effects.SLOW, 2, 10);

        // can't go lower than 1 heart
        player.setHealth(Math.max(2, player.getHealth() - healthSacrifice));
        temper.temper(player, getDuration());

        // Fx
        new TimedGameTask(20) {
            private final double distance = 1.25d;

            @Override
            public void run(int tick) {
                final double x = Math.sin(tick) * distance;
                final double y = 2.0d / maxTick * tick;
                final double z = Math.cos(tick) * distance;

                dropItem(location -> location.add(x, y, z), location -> location.subtract(x, y, z));
                dropItem(location -> location.subtract(x, y, z), location -> location.add(x, y, z));

                final float pitch = 0.5f + (1.5f / maxTick * tick);
                player.playWorldSound(location, Sound.BLOCK_LAVA_POP, pitch);
                player.playWorldSound(location, Sound.BLOCK_AZALEA_PLACE, pitch);
            }

            private void dropItem(Consumer<Location> preConsumer, Consumer<Location> postConsumer) {
                preConsumer.accept(location);
                world.dropItem(location, new ItemStack(CollectionUtils.randomElement(flowers, flowers[0])), self -> {
                    self.setPickupDelay(10000);
                    self.setTicksLived(5990);
                });
                postConsumer.accept(location);
            }
        }.runTaskTimer(0, 1);

        return Response.OK;
    }
}
