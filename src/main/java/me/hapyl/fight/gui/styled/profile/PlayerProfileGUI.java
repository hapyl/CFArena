package me.hapyl.fight.gui.styled.profile;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.inventory.gui.GUIEventListener;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.fight.CF;
import me.hapyl.fight.Message;
import me.hapyl.fight.database.entry.MetadataEntry;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.CollectionGUI;
import me.hapyl.fight.game.experience.ExperienceGUI;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.gui.SettingsGUI;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.gui.styled.StyledTexture;
import me.hapyl.fight.gui.styled.eye.EyeGUI;
import me.hapyl.fight.gui.styled.hotbar.HotbarLoadoutGUI;
import me.hapyl.fight.gui.styled.profile.achievement.AchievementGUI;
import me.hapyl.fight.npc.TheEyeNPC;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

import javax.annotation.Nonnull;

public class PlayerProfileGUI extends StyledGUI implements GUIEventListener {
    public PlayerProfileGUI(Player player) {
        super(player, "Your Profile", Size.FOUR);
        
        openInventory();
    }
    
    @Override
    public void onOpen(@Nonnull InventoryOpenEvent event) {
        PlayerLib.playSound(player, Sound.ITEM_BOOK_PAGE_TURN, 0.0f);
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        
        final PlayerProfile profile = CF.getProfile(player);
        
        setHeader(new ItemBuilder(Material.PLAYER_HEAD)
                          .setSkullOwner(player.getName())
                          .setName("&aProfile")
                          .setSmartLore("&8Oh hey, it's you!")
                          .asIcon());
        
        setItem(
                20,
                StyledTexture.ICON_COSMETICS.asButton("browse cosmetics"),
                CollectionGUI::new
        );
        
        setItem(
                22,
                StyledTexture.ICON_LEVELLING.asButton("open leveling"),
                ExperienceGUI::new
        );
        
        setItem(
                24,
                StyledTexture.ICON_ACHIEVEMENTS.asButton("browse achievements!"),
                AchievementGUI::new
        );
        
        // Settings
        setPanelItem(
                6,
                StyledTexture.ICON_SETTINGS.asButton("modify settings"),
                SettingsGUI::new
        );
        
        // Loadout
        setPanelItem(
                7,
                StyledTexture.ICON_LOADOUT.asButton("customize hotbar"),
                HotbarLoadoutGUI::new
        );
        
        // The Eye Remote Communication
        if (MetadataEntry.isTrue(player, TheEyeNPC.HAS_UNLOCKED_REMOTE_GUI)) {
            setPanelItem(
                    2,
                    StyledTexture.THE_EYE.toBuilderClean()
                                         .setName("&aThe Eye Remote Communication")
                                         .addLore("&8/viewtheeyegui")
                                         .addLore()
                                         .addSmartLore("Allows for remote communication with &aThe Eye&7 by some &dmagic&7 &eblockchain&7, &d&l6G&7 technology.")
                                         .addLore()
                                         .addLore(Color.BUTTON.color("Click to communicate!"))
                                         .asIcon(),
                    player -> {
                        new EyeGUI(player);
                        
                        Message.sound(player, Sound.ENTITY_ENDERMAN_SCREAM, 0.75f);
                    }
            );
        }
    }
    
}
