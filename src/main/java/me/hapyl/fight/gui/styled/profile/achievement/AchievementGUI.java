package me.hapyl.fight.gui.styled.profile.achievement;

import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.gui.styled.ReturnData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.gui.styled.StyledItem;
import me.hapyl.fight.gui.styled.profile.PlayerProfileGUI;
import me.hapyl.fight.util.NoProfileException;
import me.hapyl.fight.ux.Notifier;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.player.PlayerLib;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class AchievementGUI extends StyledGUI {

    private final PlayerProfile profile;

    public AchievementGUI(Player player) {
        super(player, "Achievements", Size.FOUR);

        profile = PlayerProfile.getProfile(player);

        if (profile == null) {
            throw new NoProfileException(player);
        }

        openInventory();
    }

    @Nullable
    @Override
    public ReturnData getReturnData() {
        return ReturnData.of("Profile", PlayerProfileGUI::new);
    }

    @Override
    public void onUpdate() {
        final Heroes hero = profile.getHero();

        setHeader(StyledItem.ICON_ACHIEVEMENTS.asIcon());

        setItem(20, StyledItem.ICON_ACHIEVEMENTS_GENERAL.asButton("view"), AchievementGeneralGUI::new);
        setItem(22, StyledItem.ICON_ACHIEVEMENTS_TIERED.asButton("view"), AchievementTieredGUI::new);
        setItem(
                24,
                new ItemBuilder(hero.getHero().getItem())
                        .setName("Hero Achievements")
                        .addLore()
                        .addSmartLore("Play as a hero to progress these achievements.")
                        .addLore()
                        .addLore(Color.ERROR + "Hero Achievements are coming soon!")
                        .addSmartLore("But don't worry, your progress is being tracked!", Color.ERROR_DARKER.toString())
                        .asIcon(),
                this::comingSoon
        );

        // TODO (hapyl): 006, Sep 6: Use different chests (iron/gold) texture for one time reward!!! they look COOL!
        // Rewards
        setItem(
                31,
                ItemBuilder.playerHeadUrl("d1eb3a9c2a647c6808a88c692896f48294d3b59c9ca9e8ca6265b84840d1fe9a")
                        .setName(Color.SUCCESS + "Rewards")
                        .addLore()
                        .addSmartLore("Claim &6unique&7 rewards by accumulating %s&7.".formatted(Currency.ACHIEVEMENT_POINT.getFormatted()))
                        .addLore()
                        .addLore(Color.ERROR + "Rewards are coming soon!")
                        .asIcon(),
                this::comingSoon
        );
    }

    private void comingSoon(Player player) {
        Notifier.error(player, "This feature is coming soon!");
        PlayerLib.villagerNo(player);
    }
}
