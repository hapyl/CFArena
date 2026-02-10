package me.hapyl.fight.game.maps;

import me.hapyl.eterna.module.chat.Chat;

public enum Size {

	SMALL,
	MEDIUM,
	LARGE,
	MASSIVE;

	@Override
	public String toString() {
		return "&8%s Map".formatted(Chat.capitalize(name()));
	}
}
