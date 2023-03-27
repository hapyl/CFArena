package me.hapyl.fight.game.heroes.storage.extra;

import me.hapyl.fight.game.heroes.HeroHandle;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.storage.darkmage.DarkMageTalent;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class DarkMageSpell {

    private final Player player;
    private SpellButton first;
    private SpellButton second;
    private long lastUsed;

    public DarkMageSpell(Player player) {
        this.player = player;
        this.first = null;
        this.second = null;
        this.lastUsed = 0L;
    }

    public Player getPlayer() {
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
        Chat.sendTitle(player, "", "%s %s".formatted(nonnullButton(first), nonnullButton(second)), 0, 40, 5);

        if (first == null) {
            PlayerLib.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.25f);
        }
        else {
            PlayerLib.playSound(player, Sound.BLOCK_LEVER_CLICK, 1.0f);
        }
    }

    private String nonnullButton(SpellButton button) {
        return button == null ? "&8_" : "&a&n" + button.getName() + "&r";
    }

    public boolean isTimeout() {
        return (System.currentTimeMillis() - this.lastUsed) >= 2000L;
    }

    @Nullable
    public SpellButton getFirst() {
        return first;
    }

    @Nullable
    public SpellButton getSecond() {
        return second;
    }

    public void cast() {
        // Cast spell
        for (Talent talent : Heroes.DARK_MAGE.getHero().getTalents()) {
            if (talent instanceof DarkMageTalent darkMageTalent) {
                if (darkMageTalent.test(this)) {
                    darkMageTalent.executeDarkMage(player);
                    break;
                }
            }
        }

        // Reset buttons
        clear();
    }

    public boolean isFull() {
        return first != null && second != null;
    }

    public boolean isEmpty() {
        return first == null && second == null && lastUsed == 0L;
    }

    public void clear() {
        this.first = null;
        this.second = null;
        this.lastUsed = 0L;
    }

    public void markUsed() {
        this.lastUsed = System.currentTimeMillis();
        player.setCooldown(HeroHandle.DARK_MAGE.getWeapon().getType(), 1);
    }

    public enum SpellButton {
        LEFT,
        RIGHT;

        public String getName() {
            return this == LEFT ? "L" : "R";
        }

    }

}
