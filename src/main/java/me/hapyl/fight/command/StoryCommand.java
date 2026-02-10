package me.hapyl.fight.command;

import me.hapyl.eterna.module.util.ArgumentList;
import me.hapyl.fight.Message;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.story.Chapter;
import me.hapyl.fight.story.HeroStory;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class StoryCommand extends CFCommand {
    public StoryCommand(@Nonnull String name) {
        super(name, PlayerRank.ADMIN);
    }

    @Override
    protected void execute(@Nonnull Player player, @Nonnull ArgumentList args, @Nonnull PlayerRank rank) {
        // story (hero) (chapter)
        final Hero hero = HeroRegistry.ofStringOrNull(args.getString(0));
        final int index = args.getInt(1);

        if (hero == null) {
            Message.error(player, "Invalid hero {%s}!".formatted(args.getString(0)));
            return;
        }

        final HeroStory story = hero.getStory();

        if (story == null) {
            Message.error(player, "{%s} doesn't have any story at the moment!".formatted(hero.getName()));
            return;
        }

        final Chapter chapter = story.getChapter(index);

        if (chapter == null) {
            Message.error(player, "There is not chapter {%s}!".formatted(index));
            return;
        }

        chapter.openBook(player);
    }
}
