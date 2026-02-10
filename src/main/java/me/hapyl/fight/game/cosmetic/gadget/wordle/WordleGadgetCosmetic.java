package me.hapyl.fight.game.cosmetic.gadget.wordle;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.cosmetic.gadget.Gadget;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class WordleGadgetCosmetic extends Gadget {
    public WordleGadgetCosmetic(@Nonnull Key key) {
        super(key, "Wordle");
        
        setDescription("""
                       Allows you to play a Wordle!
                       """);
        
        setTexture("43609f1faf81ed49c5894ac14c94ba52989fda4e1d2a52fd945a55ed719ed4");
        
        setCooldownSec(2);
        setExclusive(true); // Level reward
    }
    
    @Nonnull
    @Override
    public Response execute(@Nonnull Player player) {
        new WordleTypeGUI(player);
        return Response.ok();
    }
}
