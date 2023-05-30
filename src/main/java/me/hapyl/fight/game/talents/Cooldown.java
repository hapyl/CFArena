package me.hapyl.fight.game.talents;

public interface Cooldown {

    int getCooldown();

    Cooldown setCooldown(int cooldown);

    default Cooldown setCooldownSec(int cooldownSec) {
        return setCooldown(cooldownSec * 20);
    }

}
