package me.hapyl.fight.translate;

import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import java.util.Map;

public class TranslateMap {

    private final Language language;
    private final Map<TranslateKey, String> translateMap;

    public TranslateMap(Language language) {
        this.language = language;
        this.translateMap = Maps.newHashMap();
    }

    @Nonnull
    public Language getLanguage() {
        return language;
    }

    @Nonnull
    public String getString(@Nonnull TranslateKey key) {
        return translateMap.getOrDefault(key, key.toString());
    }

    protected void put(@Nonnull String key, @Nonnull String stringValue) {
        translateMap.put(new TranslateKey(key), stringValue);
    }
}
