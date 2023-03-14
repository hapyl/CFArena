package me.hapyl.fight.game.heroes.dvar;

import me.hapyl.fight.Main;
import me.hapyl.fight.database.DatabaseMongo;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class DVarContainer {

    private final DVarApplicable applicable;
    private final DatabaseMongo database;

    public DVarContainer(DVarApplicable applicable) {
        if (applicable == DVarApplicable.ROOT) {
            throw new IllegalArgumentException("Cannot create a DVarContainer with DVarApplicable.ROOT!");
        }

        this.applicable = applicable;
        this.database = Main.getPlugin().getDatabase();
    }

    public void save() {
        if (Main.getPlugin().isDatabaseLegacy()) { // don't care about legacy
            return;
        }

        for (Field field : this.getClass().getDeclaredFields()) {
            final DVar annotation = field.getAnnotation(DVar.class);

            if (annotation == null) {
                continue;
            }

            // Make sure field is public and non-final
            if (!field.canAccess(this)) {
                throw new IllegalArgumentException("Field %s must be accessible!".formatted(field.getName()));
            }

            final int modifiers = field.getModifiers();
            if (!Modifier.isPublic(modifiers)) {
                throw new IllegalArgumentException("Field %s must be public!".formatted(field.getName()));
            }

            final String path = annotation.value();
            // TODO: 006, Mar 6, 2023 -> 

        }
    }

    public void load() {
        if (Main.getPlugin().isDatabaseLegacy()) { // don't care about legacy
            return;
        }

    }

}
