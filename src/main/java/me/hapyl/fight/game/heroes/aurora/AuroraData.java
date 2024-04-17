package me.hapyl.fight.game.heroes.aurora;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.talents.aurora.AuroraArrowTalent;
import org.bukkit.Sound;

import javax.annotation.Nullable;

public class AuroraData extends PlayerData {

    protected AuroraArrowTalent arrow;
    protected int arrowCount;
    protected LivingGameEntity target;

    public AuroraData(GamePlayer player) {
        super(player);
    }

    @Override
    public void remove() {
    }

    public void setArrow(@Nullable AuroraArrowTalent arrow) {
        if (arrow == null) {
            // Start cooldown for the previous arrow
            if (this.arrow != null) {
                this.arrow.startCd(player);

                //player.sendSubtitle("&c- %s".formatted(this.arrow.getColor() + this.arrow.getName()), 5, 15, 5);
                player.playSound(Sound.ENTITY_HORSE_SADDLE, 1.25f);
            }

            this.arrow = null;
            this.arrowCount = 0;
            return;
        }

        this.arrow = arrow;
        this.arrowCount = arrow.getMaxArrows();

        player.sendSubtitle("&a+ %s".formatted(arrow.getColor() + arrow.getName()), 5, 15, 5);
        player.playSound(Sound.ENTITY_HORSE_SADDLE, 2.0f);
    }

}
