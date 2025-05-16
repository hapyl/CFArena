package me.hapyl.fight.game.heroes.shaman;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.talents.shaman.ShamanMark;
import me.hapyl.fight.game.talents.shaman.Totem;

import java.util.LinkedList;

public class ShamanData extends PlayerData {

    public final LinkedList<Totem> totems;
    public final double maxOverheal = 100;
    
    public ShamanMark mark;
    private double overheal;

    public ShamanData(GamePlayer player) {
        super(player);
        
        this.totems = Lists.newLinkedList();
    }

    public void increaseOverheal(double amount) {
        overheal = Math.min(overheal + amount, maxOverheal);

        player.spawnBuffDisplay("%s &a+%.0f".formatted(Named.OVERHEAL.getPrefix(), amount), 15);
    }

    public void decreaseOverheal(double amount) {
        overheal = Math.max(overheal - amount, 0);

        player.spawnDebuffDisplay("%s &c-%.0f".formatted(Named.OVERHEAL.getPrefix(), amount), 15);
    }

    public double getOverheal() {
        return overheal;
    }

    @Override
    public void remove() {
        totems.forEach(Totem::cancel);
        totems.clear();
        
        overheal = 0;
        
        if (mark != null) {
            mark.cancel();
        }
    }

    public boolean isOverheadMaxed() {
        return overheal == maxOverheal;
    }
    
}
