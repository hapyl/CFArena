package me.hapyl.fight.game.talents.himari;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.shield.Shield;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SpikeBarrier extends HimariTalent {

    @DisplayField private final double shieldStrength = 0.5d;

    public SpikeBarrier(@Nonnull Key key) {
        super(key,"Spike Barrier");
        setDescription("""
                &8;;This Talent is unlocked only if you roll it out from Lucky Day.
                
                Creates a spike barrier around Himari, absorbing 50 percent of incoming damage.
                Additionally, it reflects 50 percent of blocked damage back to the sender.
                Shield is disabled after 20 seconds of usage.
                """);

        setItem(Material.SHIELD);
        setType(TalentType.DEFENSE);
        setDurationSec(20);
    }

    @Nonnull
    @Override
    public Response executeHimari(@NotNull GamePlayer player) {
        player.setShield(new SpikeShield(player));
        player.schedule(
                () -> {
                    player.setShield(null);
                    player.sendMessage("&eSpike Shield is gone!");
                }, getDuration()
        );

        return Response.ok();
    }

    private class SpikeShield extends Shield {

        public SpikeShield(@Nonnull GamePlayer player) {
            super(player, INFINITE_SHIELD, INFINITE_SHIELD, SpikeBarrier.this.shieldStrength);
        }

        @Override
        public void onHit(double amount, @Nullable LivingGameEntity damager) {
            if (damager == null || player.isSelfOrTeammate(damager)) {
                return;
            }

            damager.damage(amount, player, DamageCause.SPIKE_SHIELD);

            // Fx
        }
    }

}
