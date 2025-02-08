package me.hapyl.fight.gui.styled.profile.achievement;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.fight.CF;
import me.hapyl.fight.Message;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.gui.styled.ReturnData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.gui.styled.StyledItem;
import me.hapyl.fight.gui.styled.profile.PlayerProfileGUI;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AchievementGUI extends StyledGUI {

    private final PlayerProfile profile;

    public AchievementGUI(Player player) {
        super(player, "Achievements", Size.FOUR);
        profile = CF.getProfile(player);

        openInventory();
    }

    @Nullable
    @Override
    public ReturnData getReturnData() {
        return ReturnData.of("Profile", PlayerProfileGUI::new);
    }

    public static ItemBuilder createHeroSpecificTexture(@Nonnull Hero hero) {
        final ItemBuilder builder = new ItemBuilder(hero.getItem());

        builder.setName(Color.SUCCESS + "Hero-Specific");
        builder.addLore();
        builder.addSmartLore("These achievements can only be completed while playing as %s.".formatted(hero.getName()));

        return builder;
    }

    @Override
    public void onUpdate() {
        final Hero hero = profile.getHero();

        setHeader(StyledItem.ICON_ACHIEVEMENTS.asIcon());

        setItem(20, StyledItem.ICON_ACHIEVEMENTS_GENERAL.asButton("view"), AchievementGeneralGUI::new);
        setItem(22, StyledItem.ICON_ACHIEVEMENTS_TIERED.asButton("view"), AchievementTieredGUI::new);
        setItem(
                24,
                createHeroSpecificTexture(hero).addLore().addLore(Color.BUTTON + "Click to view!").asIcon(),
                player -> new AchievementHeroGUI(player, hero)
        );

        // TODO (hapyl): 006, Sep 6: Use different chests (iron/gold) texture for one time reward!!! they look COOL!
        // Rewards
        setItem(
                31,
                ItemBuilder.playerHeadUrl("d1eb3a9c2a647c6808a88c692896f48294d3b59c9ca9e8ca6265b84840d1fe9a")
                        .setName(Color.SUCCESS + "Rewards")
                        .addLore()
                        .addSmartLore("Claim &6unique&7 rewards by accumulating %s&7.".formatted(Named.ACHIEVEMENT_POINT))
                        .addLore()
                        .addLore(Color.ERROR + "Rewards are coming soon!")
                        .asIcon(),
                this::comingSoon
        );
    }

    private void comingSoon(Player player) {
        Message.ERROR.send(player, "This feature is coming soon!");
        Message.ERROR.sound(player);
    }
}
