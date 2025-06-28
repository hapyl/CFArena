package me.hapyl.fight.game.cosmetic.gadget.guesswho;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.cosmetic.gadget.Gadget;
import me.hapyl.fight.gui.styled.StyledTexture;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class GuessWhoGadgetCosmetic extends Gadget {
    public GuessWhoGadgetCosmetic(@Nonnull Key key) {
        super(key, "Guess Who");
        
        setDescription("""
                       Allows to play Guess Who!
                       """);
        
        setTexture(StyledTexture.ICON_GUESS_WHO.texture());
        
        setCooldownSec(10);
        setExclusive(true);
    }
    
    @Nonnull
    @Override
    public Response execute(@Nonnull Player player) {
        new GuessWhoLobbyGUI(player);
        return Response.ok();
    }
}
