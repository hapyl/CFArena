package me.hapyl.fight.game.cosmetic.gadget.guesswho;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.inventory.Response;
import me.hapyl.eterna.module.inventory.SignGUI;
import me.hapyl.eterna.module.inventory.SignType;
import me.hapyl.eterna.module.inventory.gui.StrictAction;
import me.hapyl.fight.CF;
import me.hapyl.fight.Message;
import me.hapyl.fight.activity.ActivityHandler;
import me.hapyl.fight.database.entry.GuessWhoEntry;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.gui.styled.StyledTexture;
import me.hapyl.fight.invite.Acceptable;
import me.hapyl.fight.invite.Identifier;
import me.hapyl.fight.invite.PlayerInvite;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class GuessWhoLobbyGUI extends StyledGUI {
    public GuessWhoLobbyGUI(@Nonnull Player player) {
        super(player, "Guess Who", Size.FOUR);
        
        openInventory();
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        setHeader(StyledTexture.ICON_GUESS_WHO.asIcon());
        
        setItem(
                21, new ItemBuilder(Material.SPRUCE_SIGN)
                        .setName("Invite Player")
                        .addTextBlockLore("""
                                          Send an invite to a player to play a game of Guess Who!
                                          
                                          %sLeft-Click to select player
                                          %sRight-Click to enter username
                                          """.formatted(Color.BUTTON, Color.BUTTON_DARKER))
                        .asIcon(), makeAction()
        );
        
        setItem(23, makeStatsItem());
    }
    
    private ItemStack makeStatsItem() {
        final GuessWhoEntry entry = CF.getDatabase(player).guessWhoEntry;
        
        final ItemBuilder builder = new ItemBuilder(Material.FLOW_BANNER_PATTERN)
                .setName("Statistics")
                .addTextBlockLore("""
                                  Displays your statistics.
                                  """)
                .addLore();
        
        for (GuessWhoEntry.StatType stat : GuessWhoEntry.StatType.values()) {
            builder.addLore("%s: &a%s".formatted(stat.label(), stat.asString(entry)));
        }
        
        return builder.asIcon();
    }
    
    private StrictAction makeAction() {
        return new StrictAction() {
            @Override
            public void onLeftClick(@Nonnull Player player) {
                new GuessWhoLobbyPlayerInviteSelectionGUI(player);
            }
            
            @Override
            public void onRightClick(@Nonnull Player player) {
                new SignGUI(player, SignType.SPRUCE, "Enter Username") {
                    @Override
                    public void onResponse(Response response) {
                        final String username = response.getAsString();
                        final Player target = Bukkit.getPlayer(username);
                        
                        // TODO @Jun 07, 2025 (xanyjl) -> All player search will require permission and vanish check
                        
                        if (target == null) {
                            Message.error(player, "This player is not online!");
                            return;
                        }
                        
                        invite(player, target);
                    }
                };
            }
        };
    }
    
    static void invite(Player player, Player target) {
        PlayerInvite.send(
                player, target,
                Identifier.GUESS_WHO,
                new Acceptable() {
                    @Nonnull
                    @Override
                    public me.hapyl.fight.game.Response onAccept(@Nonnull Player whoAccepted) {
                        if (ActivityHandler.hasActivity(whoAccepted)) {
                            return me.hapyl.fight.game.Response.error("You already playing an activity!");
                        }
                        
                        ActivityHandler.startActivity(() -> new GuessWhoActivity(player, target));
                        
                        return me.hapyl.fight.game.Response.ok();
                    }
                    
                    @Override
                    public void onDecline(@Nonnull Player whoDeclined) {
                    }
                }
        );
    }
}
