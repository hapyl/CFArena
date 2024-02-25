package me.hapyl.fight.util;

import com.google.common.collect.Lists;
import me.hapyl.spigotutils.module.chat.messagebuilder.MessageBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

import javax.annotation.Nonnull;
import java.util.List;

// Jesus fuck spigot HoverEvent sucks balls
public class HoverEventBuilder implements Builder<HoverEvent> {

    private final List<String> strings;

    public HoverEventBuilder() {
        this.strings = Lists.newArrayList();
    }

    public HoverEventBuilder append(@Nonnull String string) {
        this.strings.add(string);
        return this;
    }

    public HoverEventBuilder append() {
        return append("");
    }

    @Nonnull
    @Override
    public HoverEvent build() {
        final Text[] text = new Text[strings.size()];

        for (int i = 0; i < strings.size(); i++) {
            final boolean isLast = i == strings.size() - 1;
            final String string = strings.get(i);

            text[i] = MessageBuilder.text(string + (!isLast ? "\n" : ""));
        }

        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, text);
    }

}
