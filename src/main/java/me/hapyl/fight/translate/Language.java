package me.hapyl.fight.translate;

import me.hapyl.fight.database.PlayerDatabase;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum Language {

    ENGLISH("4cac9774da1217248532ce147f7831f67a12fdcca1cf0cb4b3848de6bc94b4", "en"),
    RUSSIAN("16eafef980d6117dabe8982ac4b4509887e2c4621f6a8fe5c9b735a83d775ad", "ru");

    private final String headTexture;
    private final String fileName;

    Language(String headTexture, String fileName) {
        this.headTexture = headTexture;
        this.fileName = fileName + ".yml";
    }

    @Nonnull
    public String getHeadTexture() {
        return headTexture;
    }

    @Nonnull
    public String getFileName() {
        return fileName;
    }

    @Nonnull
    public String getTranslated(@Nonnull String key) {
        return Translate.getTranslated(this, key);
    }

    @Nonnull
    public String getTranslated(@Nonnull TranslateKey key) {
        return Translate.getTranslated(this, key);
    }

    @Nonnull
    public String getFormatted(@Nonnull String message, @Nullable Object... format) {
        return Translate.getFormatted(this, message, format);
    }

    @Nonnull
    public String getFormatted(@Nonnull TranslateKey key) {
        return Translate.getFormatted(this, getTranslated(key));
    }

    @Nonnull
    public static Language getPlayerLanguage(@Nonnull Player player) {
        return PlayerDatabase.getDatabase(player).getLanguage();
    }
}
