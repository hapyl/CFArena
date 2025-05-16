package me.hapyl.fight.game.trial.objecitive;

import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.loadout.HotBarLoadout;
import me.hapyl.fight.game.loadout.HotBarSlot;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.trial.Trial;
import me.hapyl.fight.game.trial.TrialEntity;
import org.bukkit.Material;
import org.bukkit.entity.Husk;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class TrialObjectiveShock extends TrialObjective {
    public TrialObjectiveShock(Trial trial) {
        super(trial, "Shocking Time", "Electrocute enemies using your Shock Dart.");

        setPath(new TrialObjectivePath(trial, Material.CRACKED_STONE_BRICKS, -222, 64, 233, -222, 67, 235));
    }

    @Override
    public void onStart() {
        super.onStart();

        final PlayerProfile profile = trial.getProfile();
        final HotBarLoadout loadout = profile.getHotbarLoadout();
        final GamePlayer player = trial.getPlayer();

        player.sendTextBlockMessage("""
                &a&lᴛᴀʟᴇɴᴛs Ⅱ
                                
                &a&lGreat work!
                Now about your second &btalent&7 — &aShock Dart&7 — that deals &cAoE&7 damage based on distance.
                                
                It's &nperfect&7 for enemies that are clamped together.
                                
                &aPress &l&n%s&a on the keyboard to use the talent!
                                
                &bGo electrocute some husks!
                """.formatted(loadout.getInventorySlotBySlot(HotBarSlot.TALENT_2) + 1));

        // Normal husks
        spawnHusk(-235, 64, 232, -64, false);
        spawnHusk(-238, 64, 233, -80, false);
        spawnHusk(-236, 64, 236, -115, false);

        // Flipped husks
        spawnHusk(-230, 68, 225, -15, true);
        spawnHusk(-228, 69, 223, 7, true);
        spawnHusk(-226, 69, 224, 25, true);

        // Give talent
        player.giveTalentItem(HotBarSlot.TALENT_2);
    }

    @Nonnull
    @Override
    public String[] getScoreboardStrings() {
        return new String[] {
                getDescription(),
                "Husks Electrocuted: " + trial.getHuskString()
        };
    }

    private void spawnHusk(int x, int y, int z, float yaw, boolean flipped) {
        trial.spawnEntity(BukkitUtils.defLocation(x + 0.5, y, z + 0.5, yaw, 0), new Function<>() {
            @Nonnull
            @Override
            public TrialEntity apply(@Nonnull Husk husk) {
                if (flipped) {
                    husk.setCustomName("Dinnerbone");
                    husk.setGravity(false);
                }
                
                husk.setAI(false);
                
                final TrialEntity entity = new TrialEntity(trial, husk);
                entity.setValidState(true);
                
                return entity;
            }
        });
    }

}
