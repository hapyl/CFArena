package me.hapyl.fight.game.cosmetic.gadget.guesswho;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.gui.styled.ReturnData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledPageGUI;
import me.hapyl.fight.gui.styled.StyledTexture;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GuessWhoLobbyPlayerInviteSelectionGUI extends StyledPageGUI<PlayerProfile> {
    public GuessWhoLobbyPlayerInviteSelectionGUI(@Nonnull Player player) {
        super(player, "Select Player", Size.FIVE);
        
        setContents(CF.streamProfiles()
                      .filter(profile -> !profile.getPlayer().equals(player) && !profile.isHidden())
                      .toList()
        );
        
        openInventory();
    }
    
    @Nullable
    @Override
    public ReturnData getReturnData() {
        return ReturnData.of("Guess Who", GuessWhoLobbyGUI::new);
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        
        setHeader(StyledTexture.ICON_GUESS_WHO.asIcon());
    }
    
    @Nonnull
    @Override
    public ItemStack asItem(@Nonnull Player player, PlayerProfile profile, int index, int page) {
        final Player target = profile.getPlayer();
        final String name = target.getName();
        
        return new ItemBuilder(Material.PLAYER_HEAD)
                .setName(profile.display().toString())
                .addLore(Color.BUTTON + "Click to invite %s!".formatted(name))
                .setSkullOwner(name)
                .asIcon();
    }
    
    @Override
    public void onClick(@Nonnull Player player, @Nonnull PlayerProfile content, int index, int page, @Nonnull ClickType clickType) {
        GuessWhoLobbyGUI.invite(player, content.getPlayer());
        player.closeInventory();
    }
}
