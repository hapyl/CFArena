package me.hapyl.fight.game.loadout;

import me.hapyl.fight.event.PlayerHandler;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.talents.ChargedTalent;
import me.hapyl.fight.game.talents.InputTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.spigotutils.module.math.Tick;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.Nonnull;

public class HotbarTalentSlot extends HotbarSlot {

    private final int talentIndex;

    public HotbarTalentSlot(String name, String description, int talentIndex) {
        super(Material.CREEPER_BANNER_PATTERN, name, description);
        this.talentIndex = talentIndex;
    }

    public int getTalentIndex() {
        return talentIndex;
    }

    @Override
    public boolean handle(@Nonnull GamePlayer player, int slot) {
        final Hero hero = player.getHero();
        final PlayerInventory inventory = player.getInventory();
        final Talent talent = hero.getTalent(talentIndex);
        final ItemStack itemOnNewSlot = inventory.getItem(slot);

        if (talent == null || !isValidItem(talent, itemOnNewSlot)) {
            return false;
        }

        // Check talent lock
        final int lock = player.getTalentLock(getHandle());

        if (lock > 0) {
            player.sendMessage("&cTalent is locked for %ss!", Tick.round(lock));
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

    private boolean isValidItem(Talent talent, ItemStack stack) {
        return stack != null && !stack.getType().isAir();
    }

    private boolean checkAndExecuteTalent(GamePlayer player, Talent talent, int slot) {
        if (!PlayerHandler.checkTalent(player, talent)) {
            return false;
        }

        // Make sure the talent item is still in the slot
        final ItemStack itemInSlot = player.getInventory().getItem(slot);
        if (itemInSlot == null || itemInSlot.getType() != talent.getMaterial()) {
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
            chargedTalent.setLastKnownSlot(player, slot);
            chargedTalent.removeChargeAndStartCooldown(player);
        }

        final int point = talent.getPoint();

        if (point > 0) {
            player.addUltimatePoints(point);
        }

        talent.startCd(player);
        return true;
    }
}
