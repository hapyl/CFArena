package me.hapyl.fight.notifier;

public enum Notification {

    READ_PATCH_NOTES("&aRead the latest patch notes here: &e%s"),

    ;

    private final String string;

    Notification(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }
}
