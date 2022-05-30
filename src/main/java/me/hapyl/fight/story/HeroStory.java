package me.hapyl.fight.story;

import me.hapyl.fight.game.heroes.Heroes;

import java.util.ArrayList;
import java.util.List;

public class HeroStory implements Story {

	private final Heroes heroes;
	private final List<Page<String>> pages = new ArrayList<>();

	public HeroStory(Heroes heroes) {
		this.heroes = heroes;
	}

	public Heroes getHeroes() {
		return heroes;
	}
}
