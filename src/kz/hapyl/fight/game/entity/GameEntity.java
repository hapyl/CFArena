package kz.hapyl.fight.game.entity;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// spawn in game entities using this class
public class GameEntity {

	protected static final Map<Entity, GameEntity> storage = new ConcurrentHashMap<>();

	private Player owner;
	private int id;

	public GameEntity() {

	}

	public int getId() {
		return id;
	}

	public static boolean isGameEntity(Entity entity) {
		return storage.containsKey(entity) && !entity.isDead();
	}


}
