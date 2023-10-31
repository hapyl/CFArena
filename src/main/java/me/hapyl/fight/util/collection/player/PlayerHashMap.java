package me.hapyl.fight.util.collection.player;

import me.hapyl.fight.game.entity.GamePlayer;

import java.util.HashMap;

public class PlayerHashMap<V> extends HashMap<GamePlayer, V> implements PlayerMap<V> {
}
