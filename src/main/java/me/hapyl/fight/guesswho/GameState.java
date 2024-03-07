package me.hapyl.fight.guesswho;

import me.hapyl.fight.game.Event;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.challenge.ChallengeType;
import me.hapyl.fight.game.color.Color;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public enum GameState {

    PREPARING,
    HERO_SELECTION,
    IN_GAME {
        @Override
        public void onStart(@Nonnull GuessWho game) {
            game.sendMessage("Both players have selected a hero!");

            game.promptPlayers();
        }
    },
    POST_GAME {
        @Override
        public void onStart(@Nonnull GuessWho game) {
            final GuessWhoPlayer winner = game.getWinner();
            final Player winnerPlayer = winner.getPlayer();

            // Actually just display this to everyone online why not
            Bukkit.getOnlinePlayers().forEach(player -> {
                Chat.sendMessage(player, "");
                Chat.sendCenterMessage(player, "&6&lɢᴀᴍᴇ ᴏᴠᴇʀ");
                Chat.sendCenterMessage(player, "&8Guess Who, %s rounds".formatted(game.getCurrentRound()));
                Chat.sendMessage(player, "");
                Chat.sendCenterMessage(player, "&6&lᴡɪɴɴᴇʀ");
                Chat.sendCenterMessage(player, winner.getProfileName());
                Chat.sendMessage(player, "");
                Chat.sendCenterMessage(player, "&6&lʀᴇᴀsᴏɴ");
                Chat.sendCenterMessage(player, Color.GRAY + game.result.getString(game));
                Chat.sendMessage(player, "");
                Chat.sendCenterMessage(
                        player,
                        "%s's &ahero was: &l%s&a!".formatted(game.player1.getProfileName(), game.player1.getGuessHeroName())
                );
                Chat.sendCenterMessage(
                        player,
                        "%s's &ahero was: &l%s&a!".formatted(game.player2.getProfileName(), game.player2.getGuessHeroName())
                );
                Chat.sendMessage(player, "");
            });

            game.asBothPlayers(player -> {
                final GuessWhoPlayer opponent = player.getOpponent();
                final Player bukkitPlayer = player.getPlayer();

                if (player.isWinner()) {
                    player.sendTitle(
                            "&6&lᴠɪᴄᴛᴏʀʏ",
                            "&aYou won, good job!",
                            5,
                            50,
                            5
                    );
                }
                else {
                    player.sendTitle("&c&lᴅᴇғᴇᴀᴛ", "&cBetter luck next time!", 5, 50, 5);
                }

                bukkitPlayer.closeInventory();
            });

            // Achievements
            Achievements.WIN_GUESS_WHO.complete(winnerPlayer);

            // Progress bond
            ChallengeType.PLAY_GUESS_WHO.progress(game.player1.getPlayer());
            ChallengeType.PLAY_GUESS_WHO.progress(game.player2.getPlayer());

            // Free manager
            Manager.current().stopGuessWhoGame();
        }
    };

    @Event
    public void onStart(@Nonnull GuessWho game) {
    }

}
