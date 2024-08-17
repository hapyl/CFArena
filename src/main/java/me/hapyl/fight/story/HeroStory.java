package me.hapyl.fight.story;

import me.hapyl.fight.game.heroes.Hero;

import java.util.ArrayList;
import java.util.List;

public class HeroStory implements Story {

	private final Hero heroes;
	private final List<Page<String>> pages = new ArrayList<>();

	public HeroStory(Hero heroes) {
		this.heroes = heroes;
	}

	public Hero getHeroes() {
		return heroes;
	}
}
