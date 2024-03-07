package me.hapyl.fight.game.heroes.archive.dark_mage;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.heroes.archive.witcher.WitherData;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.talents.archive.dark_mage.DarkMageTalent;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DarkMageSpell extends PlayerData {

    private static final long TIMEOUT = 2000L;

    private SpellButton first;
    private SpellButton second;
    private long lastUsed;

    public DarkMageSpell(GamePlayer player) {
        super(player);
        this.first = null;
        this.second = null;
        this.lastUsed = 0L;
    }

    @Override
    public void remove() {
        this.first = null;
        this.second = null;
        this.lastUsed = 0L;
    }

    public GamePlayer getPlayer() {
        return this.player;
    }

    public void addButton(SpellButton button) {
        markUsed();

        if (this.first == null) {
            this.first = button;
        }
        else {
            this.second = button;
        }

        display();
    }

    public void display() {
        player.sendSubtitle("%s %s".formatted(nonnullButton(first), nonnullButton(second)), 0, 40, 5);

        if (first == null) {
            player.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.25f);
        }
        else {
            player.playSound(Sound.BLOCK_LEVER_CLICK, 1.0f);
        }
    }

    public boolean isTimeout() {
        return (System.currentTimeMillis() - this.lastUsed) >= TIMEOUT;
    }

    @Nullable
    public SpellButton getFirst() {
        return first;
    }

    @Nullable
    public SpellButton getSecond() {
        return second;
    }

    public void cast(@Nonnull DarkMageData data) {
        final DarkMageTalent talent = getTalent();

        if (talent == null) {
            return;
        }

        // Assist
        talent.executeDarkMage(player);
        remove();
    }

    @Nullable
    public DarkMageTalent getTalent() {
        for (Talent talent : Heroes.DARK_MAGE.getHero().getTalents()) {
            if (talent instanceof DarkMageTalent darkMageTalent && darkMageTalent.test(this)) {
                return darkMageTalent;
            }
        }

        return null;
    }

    public boolean isFull() {
        return first != null && second != null;
    }

    public boolean isEmpty() {
        return first == null && second == null && lastUsed == 0L;
    }

    public void markUsed() {
        lastUsed = System.currentTimeMillis();
        player.setCooldown(Heroes.DARK_MAGE.getHero().getWeapon().getMaterial(), 1);
    }

    private String nonnullButton(SpellButton button) {
        return button == null ? "&8_" : "&a&n" + button + "&r";
    }

}
