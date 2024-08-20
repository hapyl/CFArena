package me.hapyl.fight.game.heroes;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.TalentRegistry;
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
        // THIS IS NOT HARDCODED STUN SYSTEM IS JUST IN AXII! -h
        if (TalentRegistry.AKCIY.isStunned(player)) {
            player.sendMessage("&4&l※ &cCannot use ultimate while stunned!");
            return;
        }

        // Ultimate is not ready
        if (!player.isUltimateReady()) {
            player.sendTitle("&4&l※", "&cYour ultimate isn't ready!", 5, 15, 5);
            player.sendMessage("&4&l※ &cYour ultimate isn't ready!");
            return;
        }

        hero.getUltimate().execute(player);
    }

}
