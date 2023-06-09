package me.hapyl.fight.game.talents.archive.dark_mage;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.heroes.archive.dark_mage.DarkMageSpell;
import me.hapyl.fight.game.heroes.archive.witcher.WitherData;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.SmallCaps;
import me.hapyl.spigotutils.module.chat.Chat;
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

        final Response response = Talent.preconditionTalent(player);

        if (!response.isOk()) {
            Chat.sendTitle(player, "", "&c" + response.getReason(), 0, 20, 5);
            return response;
        }

        final Response spellResponse = executeSpell(player);

        if (!spellResponse.isOk()) {
            Chat.sendTitle(player, "", "&c" + spellResponse.getReason(), 0, 20, 5);
            return spellResponse;
        }

        startCd(player);
        postProcessTalent(player);

        Chat.sendTitle(player, "", "&aCasted: &l%s&a!".formatted(getName()), 0, 20, 5);
        return response;
    }

    public final boolean test(DarkMageSpell darkMageSpell) {
        return darkMageSpell.getFirst() == first() && darkMageSpell.getSecond() == second();
    }

    private String getUsageRaw() {
        return "&e&l%s &f➠ &6&l%s".formatted(first().name(), second().name());
    }

    private String getUsage() {
        return String.format("&f&lUsage: " + getUsageRaw());
    }
}
