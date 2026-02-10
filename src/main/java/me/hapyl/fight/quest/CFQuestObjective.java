package me.hapyl.fight.quest;

import com.google.common.collect.Sets;
import me.hapyl.eterna.Eterna;
import me.hapyl.eterna.builtin.manager.QuestManager;
import me.hapyl.eterna.module.player.quest.QuestObjective;
import me.hapyl.fight.CF;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public abstract class CFQuestObjective extends QuestObjective {

    private final static Set<Class<? extends IHandler<?>>> HANDLERS;

    static {
        HANDLERS = Sets.newHashSet();
    }

    public CFQuestObjective(@Nonnull String description, double goal) {
        super(description, goal);

        registerHandler(handler());
    }

    @Nonnull
    protected abstract Class<? extends IHandler<?>> handler();

    protected static void registerHandler(@Nonnull Class<? extends IHandler<?>> handler) {
        if (HANDLERS.contains(handler)) {
            return;
        }

        try {
            final Constructor<? extends IHandler<?>> constructor = handler.getDeclaredConstructor();
            constructor.setAccessible(true);

            final IHandler<?> instance = constructor.newInstance();

            CF.registerEvents(instance);
            HANDLERS.add(handler);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {
            throw new IllegalArgumentException("Cannot register handler for %s: %s".formatted(
                    handler.getSimpleName(),
                    ex.getClass().getSimpleName() + "~" + ex.getMessage()
            ));
        }
    }

    protected interface IHandler<T extends Event> extends Listener {

        @EventHandler
        void handle(@Nonnull T ev);

        @Nonnull
        default QuestManager getQuestManager() {
            return Eterna.getManagers().quest;
        }

    }

}
