package me.hapyl.fight.game.talents.archive.dark_mage;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.dark_mage.DarkMage;
import me.hapyl.fight.game.heroes.archive.dark_mage.DarkMageSpell;
import me.hapyl.fight.game.heroes.archive.dark_mage.SpellButton;
import me.hapyl.fight.game.heroes.archive.witcher.WitherData;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.util.SmallCaps;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public abstract class DarkMageTalent extends Talent {

    private final String USAGE_REMINDER = SmallCaps.format("Usage Reminder");

    public DarkMageTalent(String name, String description) {
        this(name, description, Material.BEDROCK);
    }

    public DarkMageTalent(String name, String description, Material material) {
        super(name, description, material);

        addDescription("""
                                
                %sWitherborn Assist
                %s
                                
                %s
                                
                &8;;You must use your wand to cast this spell!
                """, Color.WITHERS.bold(), getAssistDescription(), getUsage());
    }

    @Nonnull
    public abstract String getAssistDescription();

    @Nonnull
    public abstract SpellButton first();

    @Nonnull
    public abstract SpellButton second();

    public abstract Response executeSpell(@Nonnull GamePlayer player);

    public void assist(@Nonnull WitherData data) {
    }

    @Override
    public final Response execute(@Nonnull GamePlayer player) {
        player.sendTitle(getUsageRaw(), null, 10, 30, 10);
        player.playSound(Sound.ENTITY_GLOW_SQUID_DEATH, 0.75f);
        player.playSound(Sound.ITEM_BOOK_PAGE_TURN, 0.0f);
        return Response.AWAIT;
    }

    public final Response executeDarkMage(GamePlayer player) {
        if (hasCd(player)) {
            player.sendSubtitle("&cSpell on cooldown for %ss!".formatted(BukkitUtils.roundTick(getCdTimeLeft(player))), 0, 20, 5);
            return Response.ERROR;
        }

        final Response response = Talent.preconditionTalent(player);

        if (!response.isOk()) {
            player.sendTitle("&c" + response.getReason(), null, 0, 20, 5);
            return response;
        }

        final Response spellResponse = executeSpell(player);

        if (!spellResponse.isOk()) {
            player.sendTitle("&c" + spellResponse.getReason(), null, 0, 20, 5);
            return spellResponse;
        }

        startCd(player);
        postProcessTalent(player);

        player.sendTitle("&aCasted: %s&a!".formatted(getName()), null, 0, 10, 5);
        return response;
    }

    public final boolean test(DarkMageSpell darkMageSpell) {
        return darkMageSpell.getFirst() == first() && darkMageSpell.getSecond() == second();
    }

    protected boolean hasWither(GamePlayer player) {
        return getWither(player) != null;
    }

    protected WitherData getWither(GamePlayer player) {
        return Heroes.DARK_MAGE.getHero(DarkMage.class).getPlayerData(player).getWitherData();
    }

    private String getUsageRaw() {
        return "%s &7➠ %s".formatted(first(), second());
    }

    private String getUsage() {
        return String.format("&f&lUsage: " + getUsageRaw());
    }
}

