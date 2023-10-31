package me.hapyl.fight.game.maps.features;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.game.maps.MapFeature;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.spigotutils.module.math.Numbers;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class LibraryFeature extends MapFeature implements Listener {

    private final LibraryKeyport portals;
    private final PlayerMap<Integer> voidMap = PlayerMap.newMap();
    private final char[] chars = { 'ᛈ', 'ᚢ', 'ᛋ', 'ᛏ', 'ᛟ', 'ᛏ', 'ᚨ' };

    public LibraryFeature() {
        super(
                "The Void",
                "A chunk of void that can transport you anywhere. But be aware that continuous usage may as well consume you..."
        );
        portals = new LibraryKeyport();
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        voidMap.remove(player);
    }

    @Override
    public void onStop() {
        voidMap.clear();
    }

    @Override
    public void onStart() {
        new GameTask() {
            private int tick = 0;

            @Override
            public void run() {
                portals.getEntrances().forEach(blockLoc -> {
                    final Location location = blockLoc.toLocation().add(0.0d, 1.0d, 0.0d);

                    PlayerLib.spawnParticle(location, Particle.PORTAL, 20, 0.1d, 1.5d, 0.1d, 1.0f);
                    PlayerLib.spawnParticle(location, Particle.ENCHANTMENT_TABLE, 10, 0.1d, 1.5d, 0.1d, 1.0f);
                });

                if (tick % 200 == 0) {
                    CF.getAlivePlayers().forEach(player -> removeVoidValue(player));
                }

                tick += 5;
            }
        }.runTaskTimer(5, 5);
    }

    private void removeVoidValue(GamePlayer player) {
        voidMap.computeIfPresent(player, (pl, a) -> Numbers.clamp(a - 1, 0, 7));
        displayVoidValues(player);
    }

    @EventHandler()
    public void handleMovement(PlayerMoveEvent ev) {
        if (!validateGameAndMap(GameMaps.LIBRARY)) {
            return;
        }

        final GamePlayer player = CF.getPlayer(ev.getPlayer());

        if (player == null) {
            return;
        }

        if (!portals.testPlayer(player)) {
            return;
        }

        voidMap.compute(player, (pl, old) -> old == null ? 1 : Numbers.clamp(old + 1, 0, 7));
        displayVoidValues(player);
    }

    private void displayVoidValues(GamePlayer player) {
        final int current = voidMap.getOrDefault(player, 0);
        if (current <= 0) {
            return;
        }
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            builder.append(i < current ? "&d&n" : "&8").append(chars[i]);
        }

        String subtitle = "";
        switch (current) {
            case 5 -> subtitle = "Void is Watching...";
            case 6 -> subtitle = "Vulnerable to Void";
            case 7 -> {
                subtitle = "Void Consuming You";
                player.damage(30, EnumDamageCause.LIBRARY_VOID);
                player.addPotionEffect(PotionEffectType.WITHER, 20, 0);
            }
        }

        player.sendTitle(builder.toString(), "&6" + subtitle, 0, 20, 5);
        player.playSound(Sound.AMBIENT_SOUL_SAND_VALLEY_MOOD, 2.0f);
    }

    @Override
    public void tick(int tick) {
    }

}
