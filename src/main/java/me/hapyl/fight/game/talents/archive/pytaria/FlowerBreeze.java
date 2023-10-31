package me.hapyl.fight.game.talents.archive.pytaria;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.util.CollectionUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class FlowerBreeze extends Talent {

    @DisplayField private final double healthSacrifice = 15.0d;
    @DisplayField(scaleFactor = 100.0d) public final double attackIncrease = 0.9d;
    @DisplayField(scaleFactor = 100.0d) public final double defenseIncrease = 2.0d;

    public FlowerBreeze() {
        super("Flower Breeze", """
                Feel the breeze of the flowers that damages your but grants &c%s &7and &b%s &7boost for {duration}.
                                
                &8;;This ability cannot kill.
                """.formatted(AttributeType.ATTACK, AttributeType.DEFENSE), Type.COMBAT);

        setDuration(80);
        setItem(Material.RED_DYE);
        setCooldown(getDuration() * 4);
    }

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

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();

        player.playWorldSound(Sound.ENTITY_HORSE_BREATHE, 0.0f);
        player.addPotionEffect(PotionEffectType.SLOW, 10, 2);

        final World world = location.getWorld();

        // can't go lower than 1 heart
        player.setHealth(Math.max(2, player.getHealth() - healthSacrifice));

        if (world != null) {
            for (int i = 0; i < 20; i++) {
                final Item item = world.dropItemNaturally(location, new ItemStack(CollectionUtils.randomElement(flowers, flowers[0])));
                item.setPickupDelay(10000);
                item.setTicksLived(5900);
            }
        }

        Temper.FLOWER_BREEZE.temper(player.getAttributes());
        return Response.OK;
    }
}
