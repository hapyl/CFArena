package kz.hapyl.fight.translate;

public enum Language {

    ENGLISH("english"),
    RUSSIAN("russian");

    private final String fileName;

    Language(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
