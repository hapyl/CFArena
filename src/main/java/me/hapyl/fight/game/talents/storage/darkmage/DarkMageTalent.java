package me.hapyl.fight.game.talents.storage.darkmage;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.heroes.storage.extra.DarkMageSpell;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.gui.GUI;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public abstract class DarkMageTalent extends Talent {

    public DarkMageTalent(String name, String description, Material material) {
        super(name, description, material);

        addDescription("__" + getUsage());
        setAutoAdd(false);
        setAltUsage("You must use your wand to cast this spell!");
    }

    @Nonnull
    public abstract DarkMageSpell.SpellButton first();

    @Nonnull
    public abstract DarkMageSpell.SpellButton second();

    public final void executeDarkMage(Player player) {
        if (hasCd(player)) {
            Chat.sendTitle(player, "", "&cSpell on cooldown for %ss!".formatted(BukkitUtils.roundTick(getCdTimeLeft(player))), 0, 20, 5);
            return;
        }

        final Response response = execute0(player);

        if (response.isOk()) {
            startCd(player);
            Chat.sendTitle(player, "", "&aCasted: &l%s&a!".formatted(getName()), 0, 20, 5);
            return;
        }

        Chat.sendTitle(player, "", "&c" + response.getReason(), 0, 20, 5);
    }

    public final boolean test(DarkMageSpell darkMageSpell) {
        return darkMageSpell.getFirst() == first() && darkMageSpell.getSecond() == second();
    }

    private String getUsage() {
        return String.format("&b&lUsage: &a&l%s &2%s &a&l%s", first().name(), GUI.ARROW_FORWARD, second().name());
    }
}
