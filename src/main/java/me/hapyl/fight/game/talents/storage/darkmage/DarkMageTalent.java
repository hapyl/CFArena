package me.hapyl.fight.game.talents.storage.darkmage;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.heroes.storage.extra.DarkMageSpell;
import me.hapyl.fight.game.heroes.storage.extra.WitherData;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.SmallCaps;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.gui.GUI;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public abstract class DarkMageTalent extends Talent {

    private final String USAGE_REMINDER = SmallCaps.format("Usage Reminder");

    public DarkMageTalent(String name, String description, Material material) {
        super(name, description, material);

        addDescription("""
                                
                &f&lWitherborn Assist
                %s
                                
                %s
                                
                &8;;You must use your wand to cast this spell! Please read wand's description.
                """, getAssistDescription(), getUsage());
    }

    @Nonnull
    public abstract String getAssistDescription();

    @Nonnull
    public abstract DarkMageSpell.SpellButton first();

    @Nonnull
    public abstract DarkMageSpell.SpellButton second();

    public abstract Response executeSpell(Player player);

    public void assist(WitherData data) {
        data.player.sendMessage("assisting talent " + getName());
    }

    @Override
    public final Response execute(Player player) {
        Chat.sendTitle(player, USAGE_REMINDER, getUsageRaw(), 10, 30, 10);
        return Response.AWAIT;
    }

    public final Response executeDarkMage(Player player) {
        if (hasCd(player)) {
            Chat.sendTitle(player, "", "&cSpell on cooldown for %ss!".formatted(BukkitUtils.roundTick(getCdTimeLeft(player))), 0, 20, 5);
            return Response.ERROR;
        }

        final Response response = precondition(player, executeSpell(player));

        if (response.isOk()) {
            startCd(player);
            Chat.sendTitle(player, "", "&aCasted: &l%s&a!".formatted(getName()), 0, 20, 5);
            return response;
        }

        Chat.sendTitle(player, "", "&c" + response.getReason(), 0, 20, 5);
        return response;
    }

    public final boolean test(DarkMageSpell darkMageSpell) {
        return darkMageSpell.getFirst() == first() && darkMageSpell.getSecond() == second();
    }

    private String getUsageRaw() {
        return "&a&l%s &2%s &a&l%s".formatted(first().name(), GUI.ARROW_FORWARD, second().name());
    }

    private String getUsage() {
        return String.format("&b&lUsage: " + getUsageRaw());
    }
}

