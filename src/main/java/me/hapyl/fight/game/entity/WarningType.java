package me.hapyl.fight.game.entity;

public enum WarningType {
    /**
     * The player should look out.
     */
    WARNING {
        @Override
        public String toString() {
            return "&6/&l❗&6\\";
        }
    },

    /**
     * The player is in danger.
     */
    DANGER {
        @Override
        public String toString() {
            return "&4/&l❗&4\\";
        }
    }
}
