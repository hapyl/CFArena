package me.hapyl.fight.game.entity;


import me.hapyl.fight.game.ui.UIComplexComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UIComponentCache implements UIComplexComponent {

    private static final long CACHE_DURATION = 1_500;

    protected final Map<Class<?>, UIComponent> cache;

    public UIComponentCache() {
        this.cache = new LinkedHashMap<>();
    }

    @Nullable
    @Override
    public List<String> getStrings(@Nonnull GamePlayer player) {
        cache.values().removeIf(component -> System.currentTimeMillis() - component.creationTime >= CACHE_DURATION);

        if (cache.isEmpty()) {
            return null;
        }

        final List<String> strings = new ArrayList<>();

        for (UIComponent component : cache.values()) {
            strings.add(component.string);
        }

        return strings;
    }

    protected static class UIComponent {

        private final String string;
        private final long creationTime;

        protected UIComponent(String string) {
            this.string = string;
            this.creationTime = System.currentTimeMillis();
        }

    }

}
