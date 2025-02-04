package me.hapyl.fight.game.talents.himari;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.TickingGameTask;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class DeadEye extends HimariTalent {

    private final char[] charge = {'ᚠ', 'ᛁ', 'ᚱ', 'ᛖ'};

    public DeadEye(@Nonnull Key key) {
        super(key, "Dead Eye");

        setDescription("""
                &8;;This Talent is unlocked only if you roll it out from Lucky Day.
                
                Charges "Dead Eye" effect.
                If the target you're pointing at is still in your sight after the duration ends,
                You'll deal %s that will ignore 80 percent of victim's %s.
                """.formatted(AttributeType.CRIT_DAMAGE, AttributeType.DEFENSE));

        setItem(Material.SPECTRAL_ARROW);
        setDurationSec(3);
        setCooldownSec(99999); //since not unlocked yet
        setType(TalentType.DAMAGE);
    }

    @Override
    public @Nullable Response executeHimari(@NotNull GamePlayer player) {
        new TickingGameTask() {
            private final int chargeTime = 3;

            @Override
            public void run(int tick) {
                if (tick >= chargeTime) {

                }
            }
        };
        return Response.OK;
    }
}
