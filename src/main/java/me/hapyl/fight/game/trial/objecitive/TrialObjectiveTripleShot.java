package me.hapyl.fight.game.trial.objecitive;

import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.loadout.HotbarLoadout;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.trial.Trial;
import me.hapyl.fight.util.collection.CacheSet;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.entity.ProjectileHitEvent;

import javax.annotation.Nonnull;
import java.util.Set;

public class TrialObjectiveTripleShot extends TrialObjective {

    private static final Set<Location> targetLocations;

    static {
        targetLocations = Set.of(
                BukkitUtils.defLocation(-208.0, 66.0, 250.0),
                BukkitUtils.defLocation(-209.0, 66.0, 248.0),
                BukkitUtils.defLocation(-209.0, 66.0, 252.0)
        );
    }

    private final CacheSet<Location> cache = new CacheSet<>(500);
    private int tries;

    public TrialObjectiveTripleShot(Trial trial) {
        super(trial, "Triple Shot", "Hit all the targets at the same time using your Triple Shot talent!");

        setPath(new TrialObjectivePath(trial, Material.CRACKED_STONE_BRICKS, -229, 64, 240, -227, 67, 240));
    }

    @Override
    public void onStart() {
        super.onStart();

        final PlayerProfile profile = trial.getProfile();
        final HotbarLoadout loadout = profile.getHotbarLoadout();
        final GamePlayer player = trial.getPlayer();

        player.sendTextBlockMessage("""
                &a&lᴛᴀʟᴇɴᴛs
                Each &6hero&7 has at least &ntwo&7 combat &btalents&7 that can be used to aid you in battle.
                                
                In this case, you just &bunlocked&7 your first talent: &aTriple Shot&7, which shoots &nthree&7 &narrows&7 at once!
                Try hitting &nall&7 &nthree&7 &etarget blocks&7 at the &nsame&7 time!
                                
                &aPress &l&n%s&a on the keyboard to use the talent!
                """.formatted(loadout.getInventorySlotBySlot(HotbarSlots.TALENT_1) + 1));

        player.giveTalentItem(HotbarSlots.TALENT_1);
    }

    @Override
    public <T extends Event> void handle(@Nonnull T event) {
        if (!(event instanceof ProjectileHitEvent ev)) {
            return;
        }

        final Block hitBlock = ev.getHitBlock();

        if (hitBlock == null) {
            return;
        }

        cache.add(hitBlock.getLocation());
        tries++;

        if (cache.containsAll(targetLocations)) {
            trial.nextObjective();

            // Achievement
            if (((double) tries / 3.0d) == 1.0d) {
                Achievements.FIRST_TRY.complete(trial.getPlayer());
            }
        }
    }
}
