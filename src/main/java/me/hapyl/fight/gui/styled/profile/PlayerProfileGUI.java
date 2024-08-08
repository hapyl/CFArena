package me.hapyl.fight.gui.styled.profile;

import me.hapyl.fight.game.cosmetic.gui.CollectionGUI;
import me.hapyl.fight.game.experience.ExperienceGUI;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.gui.styled.hotbar.HotbarLoadoutGUI;
import me.hapyl.fight.gui.SettingsGUI;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.gui.styled.StyledItem;
import me.hapyl.fight.gui.styled.profile.achievement.AchievementGUI;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PlayerProfileGUI extends StyledGUI {
    public PlayerProfileGUI(Player player) {
        super(player, "Your Profile", Size.FOUR);

        setOpenEvent(fn -> {
            PlayerLib.playSound(player, Sound.ITEM_BOOK_PAGE_TURN, 0.0f);
        });

        openInventory();
    }

    @Override
    public void onUpdate() {
        final PlayerProfile profile = PlayerProfile.getProfile(player);

        if (profile == null) {
            return;
        }

        setHeader(new ItemBuilder(Material.PLAYER_HEAD)
                .setSkullOwner(getPlayer().getName())
                .setName("&aProfile")
                .setSmartLore("&8Oh hey, it's you!")
                .asIcon());

        setItem(
                20,
                StyledItem.ICON_COSMETICS.asButton("browse cosmetics"),
                CollectionGUI::new
        );

        setItem(
                22,
                StyledItem.ICON_LEVELLING.asButton("open leveling"),
                ExperienceGUI::new
        );

        setItem(
                24,
                StyledItem.ICON_ACHIEVEMENTS.asButton("browse achievements!"),
                AchievementGUI::new
        );

        // Settings
        setPanelItem(
                6,
                StyledItem.ICON_SETTINGS.asButton("modify settings"),
                SettingsGUI::new
        );

        // Loadout
        setPanelItem(
                7,
                StyledItem.ICON_LOADOUT.asButton("customize hotbar"),
                HotbarLoadoutGUI::new
        );

    }

}
