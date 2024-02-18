package me.hapyl.fight.game;

public enum GameResultType {
    DRAW("&b&lᴅʀᴀᴡ"),
    SINGLE_WINNER("&a&lᴡɪɴɴᴇʀ"),
    MULTIPLE_WINNERS("&a&lᴡɪɴɴᴇʀs"),
    NO_WINNERS("&8ɴᴏ ᴡɪɴɴᴇʀs :(");

    private final String string;

    GameResultType(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }
}
