package me.hapyl.fight.game.attribute.temper;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.attribute.AttributeType;

import java.util.Map;

public class TemperData {

    public final Temper temper;
    public final Map<AttributeType, AttributeTemper> values;

    public TemperData(Temper temper) {
        this.temper = temper;
        this.values = Maps.newHashMap();
    }

}
