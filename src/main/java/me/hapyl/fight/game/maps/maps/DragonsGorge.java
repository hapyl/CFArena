package me.hapyl.fight.game.maps.maps;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.maps.GameMap;
import me.hapyl.fight.game.maps.MapFeature;
import me.hapyl.fight.game.maps.Size;
import me.hapyl.fight.util.ProgressBarBuilder;
import me.hapyl.spigotutils.module.math.Numbers;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DragonsGorge extends GameMap {

    public DragonsGorge() {
        super("Dragon's Gorge");

        setDescription("A gorge with a dragon in it. What could go wrong?");
        setMaterial(Material.DARK_OAK_BOAT);
        setSize(Size.MEDIUM);
        setTicksBeforeReveal(100);

        addFeature(new MapFeature("Sheer Cold", "This water is so cold! Better keep an eye on your cold-o-meter!") {

            private final Map<Player, Float> coldMeter = new HashMap<>();
            private final float maxColdValue = 100.0f;

            @Override
            public void onStop() {
                coldMeter.clear();
            }

            @Override
            public void tick(int tick) {
                final Collection<GamePlayer> players = CF.getPlayers();

                players.forEach(gamePlayer -> {
                    final Player player = gamePlayer.getPlayer();

                    // Reset dead player
                    if (!gamePlayer.isAlive() && coldMeter.containsKey(player)) {
                        coldMeter.remove(player);
                        return;
                    }

                    final byte lightLevel = player.getLocation().getBlock().getLightFromBlocks();
                    final float newValue = addColdValue(player, player.isInWater() ? 0.25f : lightLevel >= 2 ? -0.3f : -0.1f);

                    if (newValue < 0) {
                        return;
                    }

                    // Punish
                    if (tick % 20 == 0) {
                        // Display cold meter
                        if (newValue > 0) {
                            gamePlayer.sendTitle("", ProgressBarBuilder.of("❄", ChatColor.AQUA, newValue, maxColdValue), 0, 25, 5);
                        }

                        // For FX
                        player.setFreezeTicks((int) Math.min(player.getMaxFreezeTicks(), newValue));

                        if (isBetween(newValue, 25, 50)) { // Low hitting ticks
                            gamePlayer.damage(4.0d, EnumDamageCause.COLD);
                        }
                        else if (isBetween(newValue, 50, 100)) { // High hitting ticks and warning
                            gamePlayer.damage(6.0d, EnumDamageCause.COLD);
                        }
                        else if (newValue >= maxColdValue) { // Instant Death
                            //GamePlayer.damageEntity(player, 1000.0d, null, EnumDamageCause.COLD);
                            // Replace 1000 damage to prevent achievement abuse
                            gamePlayer.setLastDamageCause(EnumDamageCause.COLD);
                            gamePlayer.die(true);
                        }

                        // Fx
                        if (newValue >= 60) {
                            PlayerLib.playSound(player, Sound.BLOCK_GLASS_BREAK, Numbers.clamp(1.0f - newValue / maxColdValue, 0.0f, 2.0f));
                        }
                    }
                });
            }

            private boolean isBetween(float value, float min, float max) {
                return value >= min && value < max;
            }

            private float addColdValue(Player player, float value) {
                coldMeter.put(player, Numbers.clamp(getColdValue(player) + value, 0.0f, maxColdValue));
                return getColdValue(player);
            }

            private float getColdValue(Player player) {
                return coldMeter.getOrDefault(player, 0.0f);
            }

        });

        //new Booster(-153, 64, 102, -1.9, 0.75, 1.9);
        //new Booster(-169, 64, 118, 1.9, 0.75, -1.9);

        this.addLocation(-143, 64, 86);
        this.addLocation(-150, 64, 100);
        this.addLocation(-172, 64, 119);
    }
}
