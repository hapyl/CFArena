package me.hapyl.fight.game.talents.archive.techie;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.ChargedTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TrapCage extends ChargedTalent implements Listener {

    @DisplayField private final double cageRadius = 2.0d;
    @DisplayField public final double cageDamage = 5.0d;
    @DisplayField public final int vulnerabilityDuration = 80;
    @DisplayField public final int windupTime = 20;
    @DisplayField private final int rechargeCd = 80;

    private final Map<Player, Set<CyberCage>> cageMap = new HashMap<>();

    public TrapCage() {
        super("CYber Cage", """
                Toss a cage in front of you, masking itself upon landing as a block below it.
                                
                Activates upon opponents touch and explodes in small AoE applying &b&lCYber &b&lHack&7.
                                
                &e&lSNEAK &7near your cage to pick it up.
                """, 3);

        setItem(Material.IRON_TRAPDOOR);
        setCooldownSec(2);
    }

    @Override
    public void onDeathCharged(@Nonnull GamePlayer player) {
        //getCages(player).forEach(CyberCage::remove);
        //cageMap.remove(player);
    }

    @Override
    public void onStopCharged() {
        cageMap.values().forEach(set -> {
            set.forEach(CyberCage::remove);
            set.clear();
        });
        cageMap.clear();
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        //getCages(player).add(new CyberCage(player));
        return Response.OK;
    }

    @EventHandler()
    public void handleMovement(PlayerMoveEvent ev) {
        //final Player player = ev.getPlayer();
        //if (!Manager.current().isPlayerInGame(player)) {
        //    return;
        //}
        //
        //getNearbyCages(player, cageRadius)
        //        .forEach(cage -> {
        //            if (cage.isOwner(player)) {
        //                return;
        //            }
        //
        //            cage.activate(player);
        //
        //            // Remove cage and grant charge
        //            removeCage(cage);
        //            grantCharge(cage.getPlayer(), rechargeCd);
        //        });
    }

    @Override
    public void onStartCharged() {
        new GameTask() {
            @Override
            public void run() {
                cageMap.values().forEach(set -> set.forEach(CyberCage::drawParticle));
            }
        }.runTaskTimer(10, 10);
    }

    @EventHandler()
    public void handleSneaking(PlayerToggleSneakEvent ev) {
        //final Player player = ev.getPlayer();
        //if (!Manager.current().isPlayerInGame(player)) {
        //    return;
        //}
        //
        //final Set<CyberCage> cages = getCages(player);
        //
        //cages.forEach(cage -> {
        //    if (cage.compareDistance(player.getLocation(), cageRadius)) {
        //        removeCage(cage);
        //
        //        // Fx
        //        Chat.sendMessage(player, "&aPicked up cage.");
        //        PlayerLib.playSound(player, Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 0.5f);
        //        grantCharge(player, rechargeCd);
        //    }
        //});
    }

    private void removeCage(CyberCage cage) {
        final Player player = cage.getPlayer();
        getCages(player).remove(cage);
        //grantCharge(player);
        cage.remove();
    }

    private Set<CyberCage> getNearbyCages(Player player, double distance) {
        final Set<CyberCage> cages = new HashSet<>();
        final Location location = player.getLocation();

        cageMap.values().forEach(hashSet -> hashSet.stream()
                .filter(cage -> cage.compareDistance(location, distance))
                .forEach(cages::add));

        return cages;
    }

    public Set<CyberCage> getCages(Player player) {
        return cageMap.computeIfAbsent(player, k -> Sets.newConcurrentHashSet());
    }
}
