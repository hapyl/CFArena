package me.hapyl.fight.game.trial.objecitive;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.trial.Trial;
import me.hapyl.fight.game.trial.TrialEntity;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.util.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class TrialObjectiveHealth extends TrialObjective {

    private static final ItemStack playerHead = ItemBuilder.playerHeadUrl("9e5b4994ffc2d320d0a460c571ae13b56c696c3194615d31a4d7ad0510839c45")
            .asIcon();

    public TrialObjectiveHealth(Trial trial) {
        super(trial, "Importance of Health", "Take damage and heal yourself.");

        setPath(new TrialObjectivePath(trial, Material.CRACKED_STONE_BRICKS, -212, 64, 233, -212, 67, 235));
    }

    @Override
    public void onStart() {
        super.onStart();

        final GamePlayer player = trial.getPlayer();
        player.setInvulnerable(false);

        player.sendTextBlockMessage("""
                &c&lʜᴇᴀʟᴛʜ
                Health is the most important resource in the game. Without it, you die! 😲
                                
                You &n&cdon't&7 naturally in a game &8(unless stated otherwise in a game mode)&7, but there are ways to heal yourself:
                └ &7Picking up a &aHealth Supply Pack&7 restores health.
                └ &7Killing a &a&nplayer&7 restores &a%.0f%%&7 of your max health.
                └ &7Certain &btalents&7 may heal you.
                                
                Unfortunately, there are no players nearby, so let's pretend that husk is a player!
                                
                &aDamage yourself with a &bShock Dart&a and then heal by killing the 'player'.
                """.formatted(GamePlayer.HEALING_AT_KILL * 100));

        // Spawn the "player"
        trial.spawnEntity(BukkitUtils.defLocation(-216.5, 64.0, 228.5), husk -> {
            husk.setAI(false);
            husk.setCustomName("\"Player\"");
            husk.setCustomNameVisible(true);

            final EntityEquipment equipment = husk.getEquipment();

            if (equipment != null) {
                equipment.setHelmet(playerHead);
            }

            return new TrialEntity(trial, husk) {
                @Override
                public void onDeath() {
                    super.onDeath();

                    player.heal(player.getMaxHealth() * GamePlayer.HEALING_AT_KILL);
                }
            };
        });
    }
}
