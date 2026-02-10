package me.hapyl.fight.game.cosmetic.gadget.guesswho;

import javax.annotation.Nonnull;

public enum GameResult {
    
    GUESSED_CORRECTLY {
        @Nonnull
        @Override
        public String toString(@Nonnull GuessWhoActivity game) {
            final GuessWhoPlayer winner = game.getWinner();
            
            return winner.getName() + " guessed correctly!";
        }
    },
    
    GUESSED_INCORRECTLY {
        @Nonnull
        @Override
        public String toString(@Nonnull GuessWhoActivity game) {
            final GuessWhoPlayer loser = game.getLoser();
            
            return loser.getName() + " guessed incorrectly!";
        }
    },
    
    FORFEIT {
        @Nonnull
        @Override
        public String toString(@Nonnull GuessWhoActivity game) {
            final GuessWhoPlayer loser = game.getLoser();
            
            return loser.getName() + " forfeited.";
        }
    },
    
    LEFT {
        @Nonnull
        @Override
        public String toString(@Nonnull GuessWhoActivity game) {
            final GuessWhoPlayer loser = game.getLoser();
            
            return loser.getName() + " left.";
        }
    };
    
    @Nonnull
    public String toString(@Nonnull GuessWhoActivity game) {
        return "";
    }
    
}
