package me.hapyl.fight.fx;

import me.hapyl.eterna.module.npc.Npc;
import me.hapyl.eterna.module.npc.NpcProperties;
import me.hapyl.eterna.module.npc.appearance.Appearance;
import me.hapyl.eterna.module.npc.appearance.AppearanceBuilder;
import me.hapyl.eterna.module.reflect.EntityDataType;
import me.hapyl.eterna.module.reflect.Skin;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.fight.annotate.ClonedBeforeMutation;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public class RiptideFx implements Removable {
    
    private static final double Y_OFFSET = 2.0d;
    
    private final Npc npc;
    
    public RiptideFx(@Nonnull @ClonedBeforeMutation Location riptideLocation) {
        final Location location = BukkitUtils.newLocation(riptideLocation);
        location.add(0.0d, Y_OFFSET, 0.0d);
        location.setPitch(90f);
        
        npc = new Npc(location, Component.empty(), AppearanceBuilder.ofMannequin(Skin.of("", "")));
        
        final Appearance appearance = npc.getAppearance();
        appearance.getHandle().getBukkitEntity().setInvisible(true);
        
        final NpcProperties properties = npc.getProperties();
        properties.setCollidable(false);
        
        npc.showAll();
        
        appearance.setEntityDataValue(EntityDataType.BYTE, 8, (byte) 0x04);
        appearance.updateEntityData();
    }
    
    public void teleport(@Nonnull Location location) {
        location.add(0.0d, Y_OFFSET, 0.0d);
        location.setPitch(90f);
        
        npc.setLocation(location);
    }
    
    @Override
    public void remove() {
        npc.destroy();
    }
}
