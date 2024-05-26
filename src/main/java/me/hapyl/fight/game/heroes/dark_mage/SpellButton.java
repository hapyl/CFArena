package me.hapyl.fight.game.heroes.dark_mage;

public enum SpellButton {
    LEFT {
        @Override
        public String toString() {
            return "&a&lᛚ";
        }
    },
    RIGHT {
        @Override
        public String toString() {
            return "&c&lᚱ";
        }
    }
}