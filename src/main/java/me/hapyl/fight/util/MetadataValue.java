package me.hapyl.fight.util;

import me.hapyl.fight.Main;
import org.bukkit.metadata.FixedMetadataValue;

public class MetadataValue extends FixedMetadataValue {
    public MetadataValue(Object value) {
        super(Main.getPlugin(), value);
    }
}
