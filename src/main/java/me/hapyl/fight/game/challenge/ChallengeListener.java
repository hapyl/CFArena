package me.hapyl.fight.game.challenge;

import me.hapyl.fight.CF;
import org.bukkit.event.Listener;

public class ChallengeListener implements Listener {

    public ChallengeListener() {
        CF.registerEvents(this);
    }


}
