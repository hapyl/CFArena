package me.hapyl.fight.story;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.util.Named;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.util.ChatPaginator;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class Chapter implements Named {

    private final String name;
    private final List<String> pages;

    public Chapter(@Nonnull String name) {
        this.name = name;
        this.pages = Lists.newArrayList();
    }

    public Chapter addPage(@Nonnull String page) {
        // TODO (Tue, Aug 27 2024 @xanyjl): Validate max page size because we're using books
        this.pages.add(page);
        return this;
    }

    @Nonnull
    public List<String> getPages() {
        return Collections.unmodifiableList(pages);
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    public void openBook(@Nonnull Player player) {
        final Book.Builder builder = Book.builder();

        builder.author(Component.text(""));
        builder.title(Component.text(""));

        for (String page : this.pages) {
            builder.addPage(Component.text(Chat.color(page)));
        }

        player.openBook(builder);
    }

    private String wrapWord(String page) {
        final StringBuilder builder = new StringBuilder();
        final String[] strings = ChatPaginator.wordWrap(page, 20);

        for (int i = 0; i < strings.length; i++) {
            if (i != 0) {
                builder.append("\n");
            }

            builder.append(strings[i]);
        }

        return Chat.color(builder.toString());
    }
}
