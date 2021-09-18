package kz.hapyl.fight.game.maps;

public enum Maps {

	ARENA(new Map());

	private final Map map;

	Maps(Map map) {
		this.map = map;
	}

	public Map getMap() {
		return map;
	}
}
