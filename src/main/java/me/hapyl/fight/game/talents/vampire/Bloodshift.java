package me.hapyl.fight.game.talents.vampire;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.player.sound.SoundQueue;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.Outline;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class Bloodshift extends Talent {

    @DisplayField public final double healthDrainPerOneDamage = 1.0d;
    @DisplayField public final double damageBoostPercent = 0.75d;

    @DisplayField public final double healingFromDamage = 0.75d;
    @DisplayField public final double damageReduction = 1 - healingFromDamage;

    private final SoundQueue soundFx = new SoundQueue()
            .appendSameSound(
                    Sound.ENTITY_GENERIC_DRINK,
                    2,
                    1.0f, 1.25f, 1.0f, 1.25f, 0.75f
            );

    public Bloodshift(@Nonnull Key key) {
        super(key, "Bloodshift");

        setDescription("""
                Enter &c%s&7 state for {duration}.
                
                While in this state, convert &b%.0f%%&7 of the &cdamage&7 dealt into &ahealing&7.
                """.formatted(
                Chat.capitalize("s"),
                healingFromDamage * 100
        ));

        setType(TalentType.ENHANCE);
        setItem(Material.BEETROOT);

        setDurationSec(5.0f);
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
        player.setOutline(Outline.RED);

        player.schedule(() -> {
            player.setOutline(Outline.CLEAR);
        }, getDuration());

        // Fx
        soundFx.play(player.getPlayer());

        return Response.OK;
    }
}
