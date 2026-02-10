package me.hapyl.fight.invite;

import me.hapyl.eterna.module.registry.Key;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import java.util.Objects;

@ApiStatus.NonExtendable
public interface Identifier {
    
    @Nonnull
    Identifier GUESS_WHO = new IdentifierImpl(Key.ofString("guess_who"), "play a game of Guess Who");
    
    @Nonnull
    Key key();
    
    @Nonnull
    String prompt();
    
    class IdentifierImpl implements Identifier {
    
        private final Key key;
        private final String prompt;
        
        IdentifierImpl(Key key, String prompt) {
            this.key = key;
            this.prompt = prompt;
        }
        
        @Nonnull
        @Override
        public Key key() {
            return key;
        }
        
        @Nonnull
        @Override
        public String prompt() {
            return prompt;
        }
        
        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            
            final IdentifierImpl that = (IdentifierImpl) o;
            return Objects.equals(this.key, that.key);
        }
        
        @Override
        public int hashCode() {
            return Objects.hashCode(this.key);
        }
    }
    
}
