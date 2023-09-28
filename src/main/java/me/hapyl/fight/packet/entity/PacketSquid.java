package me.hapyl.fight.packet.entity;

import net.minecraft.world.entity.animal.EntitySquid;
import org.bukkit.Location;

public class PacketSquid extends PacketEntity<EntitySquid> {
    public PacketSquid(Location location) {
        super(new EntitySquid(NMSEntityType.SQUID, getWorld(location)), location);
    }
}
