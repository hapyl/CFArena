package me.hapyl.fight.game.cosmetic.archive;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.ShutdownAction;
import me.hapyl.fight.util.Nulls;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.function.Consumer;

public class ElectrocuteCosmetic extends Cosmetic {

    public ElectrocuteCosmetic() {
        super("Electrocute", "Bzz~t.", Type.DEATH, Rarity.EPIC, Material.LIGHT_BLUE_STAINED_GLASS);
    }

    @Override
    public void onDisplay(Display display) {
        final Player player = display.getPlayer();

        if (player == null) {
            return;
        }

        final Set<ArmorStand> chamber = Sets.newHashSet();
        final Location location = player.getLocation();
        final HumanNPC npc = new HumanNPC(location, "", player.getName());

        location.subtract(0.0d, 1.3d, 0.0d);
        location.setYaw(0);
        location.setPitch(0);

        npc.setCollision(false);
        npc.showAll();

        // Create chamber
        final double shift = 0.275d;
        final double shiftY = 0.62d;

        for (double i = 0; i < 3; i++) {
            chamber.add(createStand(location.clone().add(shift, i * shiftY, shift)));
            chamber.add(createStand(location.clone().add(-shift, i * shiftY, shift)));
            chamber.add(createStand(location.clone().add(shift, i * shiftY, -shift)));
            chamber.add(createStand(location.clone().add(-shift, i * shiftY, -shift)));
        }

        // Create bottom
        //chamber.add(createBottom(location.clone().add(shift, -shiftY, shift)));
        //chamber.add(createBottom(location.clone().add(-shift, -shiftY, shift)));
        //chamber.add(createBottom(location.clone().add(shift, -shiftY, -shift)));
        //chamber.add(createBottom(location.clone().add(-shift, -shiftY, -shift)));

        //chamber.add(createStand(location.clone().add(0.0d, -shiftY, 0.0d)));

        final BlockData lightBlueStainedGlassBlockData = Material.LIGHT_BLUE_STAINED_GLASS.createBlockData();
        final BlockData lightningRodBlockData = Material.LIGHTNING_ROD.createBlockData();

        final Location strikeLocation = location.add(0.0d, 3.0d, 0.0d);

        GameTask.runTaskTimerTimes((task, tick) -> {
            if (tick == 0) {
                npc.remove();
                chamber.forEach(ArmorStand::remove);
                chamber.clear();

                PlayerLib.spawnParticle(location, Particle.POOF, 10, 0.0d, 0.4d, 0.0d, 0.1f);
                return;
            }

            display.getWorld().strikeLightningEffect(strikeLocation);
        }, 10, 3, 10).setShutdownAction(ShutdownAction.IGNORE);

    }

    private ArmorStand createStand(Location location) {
        return createStand(location, null);
    }

    private ArmorStand createBottom(Location location) {
        return createStand(location, self -> Nulls.runIfNotNull(self.getEquipment(), equipment -> {
            equipment.setHelmet(ItemBuilder.of(Material.WAXED_OXIDIZED_CUT_COPPER).asIcon());
            //final ItemStack itemStack = new ItemStack(BOTTOM_MATERIAL);
            //final ItemMeta meta = itemStack.getItemMeta();
            //
            //if (meta instanceof BlockDataMeta blockMeta) {
            //    final BlockData blockData = blockMeta.getBlockData(BOTTOM_MATERIAL);
            //
            //    if (blockData instanceof Stairs stairs) {
            //        stairs.setShape(Stairs.Shape.OUTER_RIGHT);
            //        stairs.setHalf(Bisected.Half.TOP);
            //        stairs.setFacing(face);
            //
            //        blockMeta.setBlockData(stairs);
            //        itemStack.setItemMeta(blockMeta);
            //
            //        location.getBlock().setType(BOTTOM_MATERIAL);
            //        location.getBlock().setBlockData(stairs, false);
            //    }
            //
            //}
            //
            //equipment.setHelmet(itemStack);
        }));
    }

    private ArmorStand createStand(Location location, Consumer<ArmorStand> consumer) {
        return Entities.ARMOR_STAND_MARKER.spawn(location, self -> {
            self.setInvisible(true);
            Nulls.runIfNotNull(self.getEquipment(), equipment -> {
                equipment.setHelmet(new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS));
            });

            if (consumer != null) {
                consumer.accept(self);
            }
        });
    }
}
