package me.hapyl.fight.game.cosmetic.gui;

public enum PurchaseResult {

    OK("&aThank you for your purchase!"),
    CANCELLED("&cPurchase Cancelled!"),
    NOT_ENOUGH_COINS("&cYou don't have enough coins!"),
    ERROR("&4&lAn error occurred!");

    private final String message;

    PurchaseResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
