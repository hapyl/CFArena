package me.hapyl.fight.game.talents.himari;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.registry.Key;
import org.bukkit.Material;
import org.bukkit.Sound;
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
            private int currentChar = 0;

            @Override
            public void run(int tick) {
                if (tick >= chargeTime) {
                    deadShot(player);
                    cancel();
                    return;
                }
                if (currentChar < charge.length) {
                    player.sendSubtitle(Character.toString(charge[currentChar]), 1, 5, 2);
                    player.playSound(Sound.BLOCK_LEVER_CLICK, 2);
                    currentChar++;
                }
            }
        }.runTaskTimer(0, 9);
        return Response.ok();
    }

    public void deadShot(@NotNull GamePlayer player) {
        GameEntity target = player.getTargetEntity();
        if (!isTargetInSight(player, target)) {
            player.sendMessage("&cThe target is no longer in your sight!");
            return;
        }

        // 1 > Calculate damage

        // 2 > Apply the damage

        // 3 > Play hit sound or whatever you want
        player.playSound(Sound.ENTITY_ARROW_HIT_PLAYER, 2);

    }

    private boolean isTargetInSight(GamePlayer player, GameEntity target) {
        // Logic to check if the target is in the player's line of sight

        return false;
    }

}
