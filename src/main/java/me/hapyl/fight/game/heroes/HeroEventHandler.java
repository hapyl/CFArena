package me.hapyl.fight.game.heroes;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.witcher.Akciy;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import javax.annotation.Nonnull;

public class HeroEventHandler {

    private final Hero hero;

    public HeroEventHandler(Hero hero) {
        this.hero = hero;
    }

    public void handlePlayerSwapHandItemsEvent(@Nonnull GamePlayer player, @Nonnull PlayerSwapHandItemsEvent ev) {
        if (!player.isAbleToUseAbilities()) {
            return;
        }

        // Check for stun
        if (Talents.AKCIY.getTalent(Akciy.class).isStunned(player)) {
            player.sendMessage("&4&l※ &cCannot use ultimate while stunned!");
            return;
        }

        // Ultimate is not ready
        if (!player.isUltimateReady()) {
            player.sendTitle("&4&l※", "&cYour ultimate isn't ready!", 5, 15, 5);
            player.sendMessage("&4&l※ &cYour ultimate isn't ready!");
            return;
        }


        final UltimateTalent ultimate = hero.getUltimate();

        ultimate.execute(player);
    }

}
