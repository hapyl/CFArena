package me.hapyl.fight.game.cosmetic.gadget.guesswho;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.fight.CF;
import me.hapyl.fight.activity.ActivityHandler;
import me.hapyl.fight.game.challenge.ChallengeType;
import me.hapyl.fight.registry.Registries;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public enum GameState {
    
    HERO_SELECTION {
        @Override
        public void onStart(@Nonnull GuessWhoActivity game) {
            game.promptPlayers();
        }
    },
    IN_GAME {
        @Override
        public void onStart(@Nonnull GuessWhoActivity game) {
            game.sendMessage("Both players have selected a hero!");
            
            game.promptPlayers();
        }
    },
    REVEALING_GUESS {
        @Override
        public void onStart(@Nonnull GuessWhoActivity game) {
            final GuessWhoSuspenseGuessReveal suspense = game.suspense();
        }
    },
    POST_GAME {
        @Override
        public void onStart(@Nonnull GuessWhoActivity game) {
            final GuessWhoPlayer winner = game.getWinner();
            
            // Display winners
            game.asBothPlayers(gwPlayer -> {
                final Player player = gwPlayer.getPlayer();
                final GuessWhoPlayer opponent = gwPlayer.opponent();
                
                // Result chat message
                Chat.sendMessage(player, CFUtils.strikethroughText(ChatColor.YELLOW));
                Chat.sendCenterMessage(player, gwPlayer.isWinner() ? "&6&lᴠɪᴄᴛᴏʀʏ" : "&c&lᴅᴇꜰᴇᴀᴛ");
                Chat.sendMessage(player, "");
                Chat.sendCenterMessage(player, "&6&lᴡɪɴɴᴇʀ");
                Chat.sendCenterMessage(player, winner.toString());
                Chat.sendCenterMessage(player, "&8&o%s".formatted(game.result().toString(game)));
                Chat.sendMessage(player, "");
                Chat.sendCenterMessage(player, "&aYour hero was &6%s!".formatted(gwPlayer.getGuessHeroName()));
                Chat.sendCenterMessage(player, "%s's&a hero was &6%s!".formatted(opponent.toString(), opponent.getGuessHeroName()));
                Chat.sendMessage(player, CFUtils.strikethroughText(ChatColor.YELLOW));
                
                // Title
                if (gwPlayer.isWinner()) {
                    gwPlayer.title("&6&lᴠɪᴄᴛᴏʀʏ", "&aYou won, good job!", 10, 50, 10);
                    
                    // Trigger achievement for winning
                    Registries.achievements().WIN_GUESS_WHO.complete(player);
                }
                else {
                    gwPlayer.title("&c&lᴅᴇғᴇᴀᴛ", "&cBetter luck next time!", 10, 50, 10);
                }
                
                // Progress bond
                ChallengeType.PLAY_GUESS_WHO.progress(player);
                
                // Free activity
                ActivityHandler.stopActivity(game);
            });
        }
    };
    
    @EventLike
    public void onStart(@Nonnull GuessWhoActivity game) {
    }
    
}
