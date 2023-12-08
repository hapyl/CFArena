package me.hapyl.fight.game.entity.ping;

import me.hapyl.spigotutils.module.player.synthesizer.Synthesizer;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;

public enum PingType {

    NORMAL(
            Synthesizer.singleTrack("ba-bc")
                    .basedrumWhere('b', 1.0f)
                    .plingWhere('a', 1.25f)
                    .plingWhere('c', 0.75f)
                    .toSynthesizer(),
            ChatColor.YELLOW,
            30
    ),

    WARNING(
            new Synthesizer()
                    .addTrack("a-a-ab")
                    .plingWhere('a', 0.6f)
                    .bassWhere('b', 0.5f)
                    .toSynthesizer()
                    .addTrack("c-c-cb")
                    .snareWhere('c', 0.75f)
                    .basedrumWhere('b', 0.0f)
                    .toSynthesizer(),
            ChatColor.RED,
            100
    );

    private final Synthesizer queue;
    private final ChatColor color;
    private final int duration;

    PingType(Synthesizer queue, ChatColor color, int duration) {
        this.queue = queue;
        this.color = color;
        this.duration = duration;
    }

    @Nonnull
    public Synthesizer getSound() {
        return queue;
    }

    @Nonnull
    public ChatColor getColor() {
        return color;
    }

    public int getDuration() {
        return duration;
    }
}
