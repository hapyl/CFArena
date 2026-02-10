package me.hapyl.fight.glowingblocks;

import me.hapyl.eterna.module.entity.packet.NMSEntityType;
import me.hapyl.eterna.module.entity.packet.PacketEntity;
import me.hapyl.eterna.module.reflect.EntityDataType;
import net.minecraft.world.entity.monster.Shulker;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public class PacketShulker extends PacketEntity<Shulker> {
    public PacketShulker(@Nonnull Location location) {
        super(new Shulker(NMSEntityType.SHULKER, getWorld(location)), location);
        
        setDataWatcherValue(EntityDataType.BYTE, 0, (byte) 0x40);
    }
}
