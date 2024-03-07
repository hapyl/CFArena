package me.hapyl.fight.game.maps.maps;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.effect.Effects;
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

import java.awt.image.ShortLookupTable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DragonsGorge extends GameMap {

    private final ProgressBarBuilder progressBar = new ProgressBarBuilder("❄", ChatColor.AQUA, 15);

    public DragonsGorge() {
        super("Dragon's Gorge");

        setDescription("A gorge with a dragon in it. What could go wrong?");
        setMaterial(Material.DARK_OAK_BOAT);
        setSize(Size.SMALL);
        setTicksBeforeReveal(100);

        addFeature(new MapFeature("Sheer Cold", """
                This water is so cold! Better keep an eye on your cold-o-meter!
                """) {

            private final Map<GamePlayer, Float> coldMeter = new HashMap<>();

            private final float maxColdValue = 100.0f;
            private final double damage = 10.0d;

            private final float incrementDefault = 0.25f;
            private final float decrementDefault = 0.1f;
            private final float decrementInLight = 0.3f;

            @Override
            public void onStop() {
                coldMeter.clear();
            }

            @Override
            public void tick(int tick) {
                final Collection<GamePlayer> players = CF.getPlayers();

                players.forEach(player -> {
                    if (player.isDeadOrRespawning()) {
                        coldMeter.remove(player);
                        return;
                    }

                    final byte lightLevel = player.getLocation().getBlock().getLightFromBlocks();
                    final float newValue = coldMeter.compute(player, (p, v) -> {
                        final boolean inWater = p.isInWater();
                        v = v != null ? v : 0;

                        if (inWater) {
                            v += incrementDefault;
                        }
                        else {
                            v -= lightLevel >= 2 ? decrementInLight : decrementDefault;
                        }

                        return Numbers.clamp(v, 0, maxColdValue);
                    });

                    if (newValue <= 0) {
                        return;
                    }

                    final boolean isModulo = tick % 20 == 0;

                    // Display
                    if (isModulo) {
                        player.sendSubtitle(progressBar.build((int) newValue, (int) (maxColdValue)), 0, 25, 5);
                    }

                    player.setFreezeTicks((int) Math.min(player.getMaxFreezeTicks(), newValue));

                    if (isBetween(newValue, 25, 50)) {
                        player.addEffect(Effects.SLOW, 1, 5);
                    }
                    else if (isBetween(newValue, 50, 75)) {
                        player.addEffect(Effects.SLOW, 2, 5);
                    }
                    else if (isBetween(newValue, 75, 100)) {
                        player.addEffect(Effects.SLOW, 3, 5);
                    }
                    else if (newValue >= maxColdValue && isModulo) {
                        player.damage(damage, EnumDamageCause.COLD);

                        // Fx
                        player.playWorldSound(Sound.ENTITY_PLAYER_HURT_FREEZE, 1.5f - (1.0f / maxColdValue * newValue));
                        player.playWorldSound(Sound.BLOCK_GLASS_BREAK, 1.5f - (1.0f / maxColdValue * newValue));
                    }

                });
            }

            private boolean isBetween(float value, float min, float max) {
                return value >= min && value < max;
            }

            private float addColdValue(GamePlayer player, float value) {
                coldMeter.put(player, Numbers.clamp(getColdValue(player) + value, 0.0f, maxColdValue));
                return getColdValue(player);
            }

            private float getColdValue(GamePlayer player) {
                return coldMeter.getOrDefault(player, 0.0f);
            }

        });

        this.addLocation(4509, 64.5, 0, 90f, 0f);
        this.addLocation(4514, 65, -14, 90f, 0f);
        this.addLocation(4470, 64.5, 19, -90f, 0f);
    }
}
