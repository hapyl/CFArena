package me.hapyl.fight.game.trial.objecitive;

import me.hapyl.fight.Main;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.trial.Trial;
import me.hapyl.fight.game.trial.TrialEntity;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class TrialObjectiveShootHusks extends TrialObjective {

    public TrialObjectiveShootHusks(Trial trial) {
        super(trial, "Shoot the Husks", "Shoot the husks with your bow!");

        setPath(new TrialObjectivePath(trial, Material.CRACKED_STONE_BRICKS, -233, 64, 249, -231, 66, 251));
    }

    @Nonnull
    @Override
    public String[] getScoreboardStrings() {
        return new String[] {
                getDescription(),
                "Husks Killed: " + trial.getHuskString()
        };
    }

    @Override
    public void onStart() {
        super.onStart();

        final GamePlayer player = trial.getPlayer();
        player.setInvulnerable(true);

        // Spawn husks
        spawnHusk(-240, 66, 244);
        spawnHusk(-240, 66, 256);

        player.sendTitle(Main.GAME_NAME, "&7Tutorial", 10, 50, 10);

        player.sendTextBlockMessage("""
                &a&lWelcome to the game!
                Here's a little tutorial that will explain the basics of the game.
                                    
                &a&lᴀᴄᴛɪᴏɴʙᴀʀ
                In the actionbar &8(above your inventory)&7, you can see some &nimportant&7 information about your character, like your &cHealth ❤&7 and &bEnergy &l※&7!
                                    
                &b&lsᴄᴏʀᴇʙᴏᴀʀᴅ
                In the scoreboard &8(right side of the screen)&7 you can see information of the current game &8(or lobby)&7.
                As an example, for the &bTutorial&7, you can see the current &6objective&7!
                
                &6Speaking of... go shoot some husks!
                """);
    }

    private void spawnHusk(double x, double y, double z) {
        trial.spawnEntity(BukkitUtils.defLocation(x, y, z), husk -> new TrialEntity(trial, husk));
    }
}
