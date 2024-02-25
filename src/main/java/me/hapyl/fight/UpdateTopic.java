package me.hapyl.fight;

import me.hapyl.spigotutils.module.chat.Gradient;
import me.hapyl.spigotutils.module.chat.gradient.Interpolators;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import java.awt.*;

public class UpdateTopic {

    private final String topic;
    private final Color colorFrom;
    private final Color colorTo;

    public UpdateTopic(@Nonnull String topic) {
        this.topic = topic;
        this.colorFrom = null;
        this.colorTo = null;
    }

    public UpdateTopic(@Nonnull String topic, int r, int g, int b, int r2, int g2, int b2) {
        this.topic = topic;
        this.colorFrom = new Color(r, g, b);
        this.colorTo = new Color(r2, g2, b2);
    }

    @Nonnull
    public String getTopic() {
        if (this.colorFrom == null && this.colorTo == null) {
            return ChatColor.GOLD + ChatColor.BOLD.toString() + topic;
        }

        return new Gradient(topic).makeBold().rgb(colorFrom, colorTo, Interpolators.QUADRATIC_FAST_TO_SLOW);
    }
}
