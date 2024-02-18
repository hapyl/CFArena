package me.hapyl.fight.translate;

import java.util.Objects;

public class TranslateKey {

    private final String key;

    public TranslateKey(String key) {
        this.key = key.toLowerCase();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final TranslateKey that = (TranslateKey) object;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return key;
    }
}
