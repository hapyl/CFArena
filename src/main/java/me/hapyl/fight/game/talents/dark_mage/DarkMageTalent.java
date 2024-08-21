package me.hapyl.fight.game.talents.dark_mage;

import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.dark_mage.DarkMage;
import me.hapyl.fight.game.heroes.dark_mage.DarkMageData;
import me.hapyl.fight.game.heroes.dark_mage.DarkMageSpell;
import me.hapyl.fight.game.heroes.dark_mage.SpellButton;
import me.hapyl.fight.game.heroes.witcher.WitherData;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.talents.Talent;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public abstract class DarkMageTalent extends Talent {

    public DarkMageTalent(@Nonnull DatabaseKey key, @Nonnull String name, @Nonnull String description) {
        super(key, name);

        setDescription("""
                %s
                %s
                
                &8;;You must use your wand to cast this spell!
                """.formatted(description, getUsage())
        );
    }

    @Nonnull
    public abstract SpellButton first();

    @Nonnull
    public abstract SpellButton second();

    public abstract Response executeSpell(@Nonnull GamePlayer player);

    public int getDuration(@Nonnull GamePlayer player) {
        final int duration = super.getDuration();

        if (!hasWither(player)) {
            return duration;
        }

        return (int) (duration * (1 + getUltimate().durationIncrease));
    }

    @Override
    @Deprecated
    public int getDuration() {
        return super.getDuration();
    }

    @Override
    public void startCd(@Nonnull GamePlayer player) {
        if (!hasWither(player)) {
            super.startCd(player);
            return;
        }

        final DarkMage.DarkMageUltimate ultimate = getUltimate();

        startCd(player, (int) (getCooldown() * ultimate.cooldownReduction));
    }

    @Override
    public final Response execute(@Nonnull GamePlayer player) {
        player.sendTitle(getUsageRaw(), null, 5, 20, 5);

        player.playSound(Sound.ENTITY_GLOW_SQUID_DEATH, 0.75f);
        player.playSound(Sound.ITEM_BOOK_PAGE_TURN, 0.0f);

        return Response.AWAIT;
    }

    public final void executeDarkMage(@Nonnull GamePlayer player) {
        if (hasCd(player)) {
            player.sendSubtitle("&cSpell on cooldown for %ss!".formatted(BukkitUtils.roundTick(getCdTimeLeft(player))), 0, 20, 5);
            return;
        }

        final Response response = Talent.precondition(player);

        if (!response.isOk()) {
            player.sendTitle("&c" + response.getReason(), null, 0, 20, 5);
            return;
        }

        // Check for lock
        final HotbarSlots slot = HeroRegistry.DARK_MAGE.getTalentSlotByHandle(this);

        if (player.getTalentLock().isLocked(slot)) {
            player.sendTitle("&cTalent is locked!", null, 0, 20, 5);
            Response.error("Talent is locked!");
            return;
        }

        final Response spellResponse = executeSpell(player);

        if (spellResponse.isError()) {
            player.sendTitle("&c" + spellResponse.getReason(), null, 0, 20, 5);
            return;
        }

        if (!spellResponse.isAwait()) {
            startCd(player);
        }

        postProcessTalent(player);

        player.sendTitle("&aCasted: %s&a!".formatted(getName()), null, 0, 10, 5);
    }

    public final boolean test(DarkMageSpell darkMageSpell) {
        return darkMageSpell.getFirst() == first() && darkMageSpell.getSecond() == second();
    }

    protected DarkMage.DarkMageUltimate getUltimate() {
        return (DarkMage.DarkMageUltimate) HeroRegistry.DARK_MAGE.getUltimate();
    }

    protected boolean hasWither(@Nonnull GamePlayer player) {
        final DarkMage darkMage = HeroRegistry.DARK_MAGE;
        final DarkMageData data = darkMage.getPlayerData(player);

        return data.getWitherData() != null;
    }

    protected WitherData getWither(GamePlayer player) {
        return HeroRegistry.DARK_MAGE.getPlayerData(player).getWitherData();
    }

    private String getUsageRaw() {
        return "%s &7➠ %s".formatted(first(), second());
    }

    private String getUsage() {
        return String.format("&f&lUsage: " + getUsageRaw());
    }
}

