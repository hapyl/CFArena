package me.hapyl.fight.packet.entity;

import me.hapyl.spigotutils.module.reflect.DataWatcherType;
import net.minecraft.world.entity.monster.EntityGuardian;
import org.bukkit.Location;

public class PacketGuardian extends PacketEntity<EntityGuardian> {

    public PacketGuardian(Location location) {
        super(new EntityGuardian(NMSEntityType.GUARDIAN, getWorld(location)), location);
    }

    public void setBeamTarget(PacketEntity<?> entity) {
        setDataWatcherValue(DataWatcherType.INT, 17, entity.getId());
    }
}
