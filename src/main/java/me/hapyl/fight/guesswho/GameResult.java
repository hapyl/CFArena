package me.hapyl.fight.guesswho;

import javax.annotation.Nonnull;

/**
 * Results:
 * - Winner guessed correctly.
 * - Loser guessed incorrectly.
 * - Loser forfeited.
 * - Lower left.
 */
public enum GameResult {

    GUESSED_CORRECTLY {
        @Nonnull
        @Override
        public String getString(@Nonnull GuessWho game) {
            final GuessWhoPlayer winner = game.getWinner();

            return winner.getName() + " guessed correctly!";
        }
    },

    GUESSED_INCORRECTLY {
        @Nonnull
        @Override
        public String getString(@Nonnull GuessWho game) {
            final GuessWhoPlayer loser = game.getLoser();

            return loser.getName() + " guessed incorrectly!";
        }
    },

    FORFEIT {
        @Nonnull
        @Override
        public String getString(@Nonnull GuessWho game) {
            final GuessWhoPlayer loser = game.getLoser();

            return loser.getName() + " forfeited.";
        }
    },

    LEFT {
        @Nonnull
        @Override
        public String getString(@Nonnull GuessWho game) {
            final GuessWhoPlayer loser = game.getLoser();

            return loser.getName() + " left.";
        }
    },

    ;

    @Nonnull
    public String getString(@Nonnull GuessWho game) {
        return "";
    }

}
