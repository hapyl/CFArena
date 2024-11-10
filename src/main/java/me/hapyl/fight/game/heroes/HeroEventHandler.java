package me.hapyl.fight.game.heroes;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.fight.event.PlayerHandler;
import me.hapyl.fight.event.custom.TalentPreconditionEvent;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.loadout.HotBarSlot;
import me.hapyl.fight.game.talents.ChargedTalent;
import me.hapyl.fight.game.talents.InputTalent;
import me.hapyl.fight.game.talents.Talent;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class HeroEventHandler {

    private final static Response ERROR_PREGAME = Response.error("The game hasn't started yet!");
    private final static Response ERROR_STUNNED = Response.error("Cannot use ultimate while stunned!");
    private final static Response ERROR_NOT_READY = Response.error("Your ultimate isn't ready!");

    private final Hero hero;

    public HeroEventHandler(@Nonnull Hero hero) {
        this.hero = hero;
    }

    public void handlePlayerSwapHandItemsEvent(@Nonnull GamePlayer player) {
        if (!player.isAbleToUseAbilities()) {
            return;
        }

        final TalentPreconditionEvent event = new TalentPreconditionEvent(player);

        if (event.call()) {
            player.sendMessage("&4&l※ &c" + event.getReason());
            return;
        }

        // Ultimate is not ready
        if (!player.isUltimateReady()) {
            player.sendTitle("&4&l※", "&c" + ERROR_NOT_READY, 5, 15, 5);
            player.sendMessage("&4&l※ &c" + ERROR_NOT_READY);

            return;
        }

        hero.getUltimate().execute(player);
    }

    public boolean handlePlayerClick(@Nonnull GamePlayer player, @Nonnull HotBarSlot slot) {
        final Talent talent = hero.getTalent(slot);

        if (talent == null) {
            return false;
        }

        // Check talent lock
        final int lock = player.getTalentLock(slot);

        if (lock > 0) {
            player.sendMessage("&cTalent is locked for %ss!".formatted(Tick.round(lock)));
            player.playSound(Sound.ENTITY_ENDERMAN_SCREAM, 0.0f);
            player.snapToWeapon();
            return false;
        }

        // Execute talent
        if (!checkAndExecuteTalent(player, talent, slot)) {
            player.snapToWeapon();
            return false;
        }

        if (talent instanceof InputTalent inputTalent) {
            player.setInputTalent(inputTalent);
        }
        else {
            player.snapToWeapon();
            player.cancelInputTalent();
            return true;
        }

        return false;
    }

    private boolean checkAndExecuteTalent(GamePlayer player, Talent talent, HotBarSlot slot) {
        if (!PlayerHandler.checkTalent(player, talent)) {
            return false;
        }

        // Execute talent and get response
        final Response response = talent.execute0(player);

        // If not error, add to the queue
        // Yeah I know two of the 'same' checks, but I'll
        // make it look good later maybe or not or I don't care
        if (!response.isError()) {
            player.getTalentQueue().add(talent);
        }

        if (!PlayerHandler.checkResponse(player, response)) {
            return false;
        }

        // \/ Talent executed \/
        if (talent instanceof ChargedTalent chargedTalent) {
            chargedTalent.getData(player).setLastKnownSlot(slot);
            chargedTalent.removeChargeAndStartCooldown(player);
        }

        final int point = talent.getPoint();

        if (point > 0) {
            player.addEnergy(point);
        }

        talent.startCd(player);
        return true;
    }

}
