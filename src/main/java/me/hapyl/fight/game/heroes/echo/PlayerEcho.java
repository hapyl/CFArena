package me.hapyl.fight.game.heroes.echo;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.eterna.module.reflect.npc.HumanNPC;

public class PlayerEcho {

    private final GamePlayer player;
    private final HumanNPC echo;

    public PlayerEcho(GamePlayer player, HumanNPC echo) {
        this.player = player;
        this.echo = echo;
    }
}
