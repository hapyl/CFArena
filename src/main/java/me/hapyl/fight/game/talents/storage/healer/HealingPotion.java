package me.hapyl.fight.game.talents.storage.healer;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import org.bukkit.entity.Player;

public class HealingPotion extends Talent {
    public HealingPotion() {
        super("Healing Potion");
    }

    @Override
    public Response execute(Player player) {
        return null;
    }
}
