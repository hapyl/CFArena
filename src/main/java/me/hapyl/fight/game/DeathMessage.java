package me.hapyl.fight.game;

public record DeathMessage(String message, String damagerSuffix) {

    // Include this in either message or damagerSuffix, and it will be replaced with the damager name
    private static final String DAMAGER_PLACEHOLDER = "{damager}";

    public DeathMessage(String message, String damagerSuffix) {
        this.message = message;

        // If a message has placeholder, then damagerSuffix is not needed
        if (message.contains(DAMAGER_PLACEHOLDER)) {
            this.damagerSuffix = "";
        }
        else {
            // If the suffix has placeholder, then don't append it
            if (damagerSuffix.contains(DAMAGER_PLACEHOLDER)) {
                this.damagerSuffix = damagerSuffix;
            }
            else {
                this.damagerSuffix = damagerSuffix + " " + DAMAGER_PLACEHOLDER;
            }
        }
    }

    public String formatMessage(String damager) {
        return message.replace(DAMAGER_PLACEHOLDER, damager);
    }

    public String formatSuffix(String damager) {
        if (damagerSuffix.isBlank()) {
            return "";
        }

        return damagerSuffix.replace(DAMAGER_PLACEHOLDER, damager);
    }

    public static DeathMessage of(String message, String suffix) {
        return new DeathMessage(message, suffix);
    }

}