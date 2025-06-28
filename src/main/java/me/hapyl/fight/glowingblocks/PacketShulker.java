package me.hapyl.fight.glowingblocks;

import me.hapyl.eterna.module.entity.packet.NMSEntityType;
import me.hapyl.eterna.module.entity.packet.PacketEntity;
import me.hapyl.eterna.module.reflect.DataWatcherType;
import net.minecraft.world.entity.monster.EntityShulker;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public class PacketShulker extends PacketEntity<EntityShulker> {
    public PacketShulker(@Nonnull Location location) {
        super(new EntityShulker(NMSEntityType.SHULKER, getWorld(location)), location);
        
        setDataWatcherValue(DataWatcherType.BYTE, 0, (byte) 0x40);
    }
}
