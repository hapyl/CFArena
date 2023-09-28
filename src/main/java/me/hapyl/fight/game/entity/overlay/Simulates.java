package me.hapyl.fight.game.entity.overlay;

import org.bukkit.event.entity.EntityDamageByEntityEvent;

public interface Simulates {

    void simulateAttack(EntityDamageByEntityEvent ev);

    void simulateTakeDamage();

}
