package me.hapyl.fight.game.feedback;

public class LogEntry {

	private final Type type;
	private final String string;

	public LogEntry(Type type, String str, Object... objects) {
		this.type = type;
		this.string = str.formatted(objects);
	}

	public String getString() {
		return string;
	}

	public Type getType() {
		return type;
	}

	public enum Type {
		BUG_FIX,
		UPDATE,
		NERF,
		BUFF,
		OTHER
	}
}
