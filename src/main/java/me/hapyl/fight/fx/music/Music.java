package me.hapyl.fight.fx.music;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.task.TickingGameTask;
import org.bukkit.Location;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * A multi instance "music" player.
 */
public class Music {

    private final Map<Integer, List<Note>> notes;

    private int currentNote;
    private int bpt;

    public Music() {
        this.notes = Maps.newHashMap();
        this.bpt = 1;
    }

    public Music setBpt(int bpt) {
        this.bpt = bpt;
        return this;
    }

    public Music addNote(@Nonnull Note note) {
        notes.computeIfAbsent(currentNote, fn -> Lists.newArrayList()).add(note);
        return this;
    }

    public Music addNotes(float pitch, @Nonnull Sound... sounds) {
        for (Sound sound : sounds) {
            addNote(sound, pitch);
        }

        return this;
    }

    public Music addNote(@Nonnull Sound sound, float pitch) {
        return addNote(new Note(sound, pitch));
    }

    public Music nextNote(@Nonnull Note note) {
        currentNote++;
        return addNote(note);
    }

    public Music nextNote(@Nonnull Sound sound, float pitch) {
        return nextNote(new Note(sound, pitch));
    }

    public Music nextNote() {
        currentNote++;
        return this;
    }

    public void play(@Nonnull Location location, @Nonnull final Note note) {
        note.play(location);
    }

    public final void play(@Nonnull Location location) {
        new TickingGameTask() {
            @Override
            public void run(int tick) {
                if (tick >= notes.size()) {
                    cancel();
                    Debug.info("finished playing");
                    return;
                }

                final List<Note> list = notes.get(tick);

                if (!list.isEmpty()) {
                    for (Note note : list) {
                        play(location, note);
                    }
                }

            }
        }.runTaskTimer(0, bpt);
    }
}
