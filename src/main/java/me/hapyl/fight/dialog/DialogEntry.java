package me.hapyl.fight.dialog;

import me.hapyl.fight.npc.PersistentNPC;

import javax.annotation.Nonnull;

public interface DialogEntry {

    void display(@Nonnull ActiveDialog dialog);

    default int getDelay() {
        return 20;
    }

    @Nonnull
    static DialogString[] string(@Nonnull String... many) {
        final DialogString[] array = new DialogString[many.length];

        for (int i = 0; i < many.length; i++) {
            array[i] = new DialogString(many[i]);
        }

        return array;
    }

    @Nonnull
    static DialogNpcEntry[] npc(@Nonnull PersistentNPC npc, @Nonnull String... many) {
        final DialogNpcEntry[] array = new DialogNpcEntry[many.length];

        for (int i = 0; i < many.length; i++) {
            array[i] = new DialogNpcEntry(npc, many[i]);
        }

        return array;
    }

}
