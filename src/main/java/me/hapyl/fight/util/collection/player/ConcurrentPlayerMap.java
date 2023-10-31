package me.hapyl.fight.util.collection.player;

import me.hapyl.fight.game.entity.GamePlayer;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentPlayerMap<V> extends ConcurrentHashMap<GamePlayer, V> implements PlayerMap<V> {
}
