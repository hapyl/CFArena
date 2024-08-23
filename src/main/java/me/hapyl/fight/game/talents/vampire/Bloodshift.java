package me.hapyl.fight.game.talents.vampire;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.Outline;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.vampire.VampireData;
import me.hapyl.fight.game.heroes.vampire.VampireState;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class Bloodshift extends Talent {

    @DisplayField public final double healthDrainPerOneDamage = 1.0d;
    @DisplayField public final double damageBoostPercent = 0.5d;

    @DisplayField public final double healthRegenPerOneDamage = 0.75d;

    public Bloodshift(@Nonnull DatabaseKey key) {
        super(key, "Bloodshift");

        setDescription("""
                Enter &c%s&7 state for {duration}.
                
                While in this state, instead of dealing damage, your attacks will heal yourself based on the damage.
                """.formatted(
                Chat.capitalize(VampireState.SUSTAIN)
        ));

        setType(TalentType.ENHANCE);
        setItem(Material.BEETROOT);

        setDurationSec(3.5f);
        setCooldownSec(10.0f);
    }

    public double calculateDamage(@Nonnull GamePlayer player) {
        final double health = player.getHealth();
        final double maxHealth = player.getMaxHealth();
        final double healthPercent = health / maxHealth;

        return 1 + healthPercent * damageBoostPercent;
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final VampireData data = HeroRegistry.VAMPIRE.getPlayerData(player);

        data.setState(VampireState.SUSTAIN);
        player.setOutline(Outline.RED);

        player.schedule(() -> {
            data.setState(VampireState.DAMAGE);
            player.setOutline(Outline.CLEAR);
        }, getDuration());

        // Fx

        return Response.OK;
    }
}
