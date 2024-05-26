package me.hapyl.fight.game.heroes.frostbite;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.Removable;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Contact;
import me.hapyl.spigotutils.module.entity.Entities;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class FrostbiteBullet implements Removable, Contact<ArmorStand, LivingGameEntity> {

    private static final double SHIFT = 0.2d;
    private static final double Y_OFFSET = 1.0d;

    private static final ItemStack[] ITEMS = {
            new ItemStack(Material.ICE),
            new ItemStack(Material.PACKED_ICE),
            new ItemStack(Material.BLUE_ICE)
    };

    private final GamePlayer player;
    private final Location location;
    private final Vector vector;
    private final ArmorStand[] armorStands;

    public FrostbiteBullet(GamePlayer player) {
        this.player = player;
        this.location = player.getLocation().subtract(0.0d, Y_OFFSET, 0.0d);
        this.vector = location.getDirection().normalize();
        this.armorStands = new ArmorStand[3];

        for (int i = 0; i < this.armorStands.length; i++) {
            this.armorStands[i] = createStand(i);
        }

        // Fx
        player.playWorldSound(Sound.ENTITY_BAT_HURT, 0.75f);
    }

    public void teleport(Location location) {
        for (int i = 0; i < armorStands.length; i++) {
            final ArmorStand stand = armorStands[i];

            if (offsetLocation(location, SHIFT * i, loc -> {
                stand.teleport(loc);
                return false;
            })) {
                break;
            }
        }
    }

    @Override
    public void remove() {
        CFUtils.clearArray(armorStands);
    }

    @Override
    public void onContact(@Nonnull ArmorStand armorStand, @Nonnull LivingGameEntity entity, @Nonnull Location location) {
    }

    private ArmorStand createStand(int index) {
        location.add(vector).multiply(SHIFT);

        return Entities.ARMOR_STAND_MARKER.spawn(location, self -> {
            self.setInvisible(true);
            self.setGravity(true);
            self.setSilent(true);
            self.setSmall(true);
            self.setRightArmPose(new EulerAngle(Math.toRadians(-105.0d), 0.0d, Math.toRadians(45.0d)));

            final EntityEquipment equipment = self.getEquipment();
            if (equipment != null) {
                equipment.setItemInMainHand(ITEMS[index % armorStands.length]);
            }
        });
    }

    private boolean offsetLocation(Location location, double offset, Function<Location, Boolean> consumer) {
        final double x = offset * vector.getX();
        final double y = offset * vector.getY() - Y_OFFSET;
        final double z = offset * vector.getZ();

        location.add(x, y, z);
        final boolean returnValue = consumer.apply(location);
        location.subtract(x, y, z);

        return returnValue;
    }
}
