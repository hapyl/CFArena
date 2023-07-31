package me.hapyl.fight.game.talents.archive.pytaria;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.Temper;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.CollectionUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class FlowerBreeze extends Talent {

    @DisplayField private final double healthSacrifice = 15.0d;
    @DisplayField(scaleFactor = 100.0d) private final double attackIncrease = 0.9d;
    @DisplayField(scaleFactor = 100.0d) private final double defenseIncrease = 2.0d;

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
    public Response execute(Player player) {
        final Location location = player.getLocation();
        PlayerLib.playSound(location, Sound.ENTITY_HORSE_BREATHE, 0.0f);
        PlayerLib.addEffect(player, PotionEffectType.SLOW, 10, 2);

        final World world = location.getWorld();
        final GamePlayer gamePlayer = CF.getOrCreatePlayer(player);

        // can't go lower than 1 heart
        gamePlayer.setHealth(Math.max(2, gamePlayer.getHealth() - healthSacrifice));

        if (world != null) {
            for (int i = 0; i < 20; i++) {
                final Item item = world.dropItemNaturally(location, new ItemStack(CollectionUtils.randomElement(flowers, flowers[0])));
                item.setPickupDelay(10000);
                item.setTicksLived(5900);
            }
        }

        //PlayerLib.addEffect(player, PotionEffectType.DAMAGE_RESISTANCE, getDuration(), 1);
        //PlayerLib.addEffect(player, PotionEffectType.INCREASE_DAMAGE, getDuration(), 1);

        final EntityAttributes attributes = GamePlayer.getPlayer(player).getAttributes();
        attributes.increaseTemporary(Temper.FLOWER_BREEZE, AttributeType.ATTACK, attackIncrease, getDuration());
        attributes.increaseTemporary(Temper.FLOWER_BREEZE, AttributeType.DEFENSE, defenseIncrease, getDuration());

        return Response.OK;
    }
}
