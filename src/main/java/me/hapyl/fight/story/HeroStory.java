package me.hapyl.fight.story;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.heroes.Hero;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class HeroStory implements Story {

    private final Hero hero;
    private final List<Chapter> chapters;

    public HeroStory(@Nonnull Hero hero) {
        this.hero = hero;
        this.chapters = Lists.newArrayList();
    }

    @Nullable
    public Chapter getChapter(int index) {
        if (index < 0 || index >= chapters.size()) {
            return null;
        }

        return chapters.get(index);
    }

    protected void addChapter(@Nonnull Chapter chapter) {
        this.chapters.add(chapter);
    }

    @Nonnull
    public Hero getHero() {
        return hero;
    }
}
