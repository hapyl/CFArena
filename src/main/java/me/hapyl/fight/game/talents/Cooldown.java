package me.hapyl.fight.game.talents;

public interface Cooldown {

    int getCooldown();

    Cooldown setCooldown(int cooldown);

    default Cooldown setCooldownSec(float cooldownSec) {
        return setCooldown((int) (cooldownSec * 20));
    }

}
