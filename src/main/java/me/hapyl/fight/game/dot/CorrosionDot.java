package me.hapyl.fight.game.dot;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.color.Color;

import javax.annotation.Nonnull;

public class CorrosionDot extends Dot {
    CorrosionDot(@Nonnull Key key) {
        super(key, "", "", Color.DEFAULT, 20, 20);
    }
    
    @Override
    public void affect(@Nonnull DotInstance instance) {
    
    }
    
    @Override
    public void exhaust(@Nonnull DotInstance instance) {
    
    }
}
