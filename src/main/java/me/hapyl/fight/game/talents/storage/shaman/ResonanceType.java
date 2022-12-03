package me.hapyl.fight.game.talents.storage.shaman;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.talents.storage.extra.ActiveTotem;
import me.hapyl.fight.game.talents.storage.extra.ActiveTotemResonance;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.gometry.Quality;
import me.hapyl.spigotutils.module.math.gometry.WorldParticle;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffectType;

public enum ResonanceType {
    STANDBY(Material.STONE, ChatColor.BLACK, ChatColor.DARK_GRAY, "", 0, 0, null),

    SLOWING_AURA(Material.BLUE_DYE, ChatColor.AQUA, ChatColor.BLUE, "Slows all enemies around the totem.", 3.0d, 10, (totem) -> {
        // Heal
        totem.getPlayerInRange().forEach(player -> PlayerLib.addEffect(player, PotionEffectType.SLOW, 10, 0));

        // Fx
        Geometry.drawCircle(
                totem.getLocationCentered(),
                totem.getResonanceType().getRange(),
                Quality.HIGH,
                new WorldParticle(Particle.SPELL_MOB)
        );
    }),

    HEALING_AURA(
            Material.LARGE_FERN,
            ChatColor.GREEN,
            ChatColor.DARK_GREEN,
            "Continuously heals all players around the totem.",
            2.5d,
            20,
            (totem) -> {
                // Heal
                totem.getPlayerInRange().forEach(player -> {
                    GamePlayer.getPlayer(player).heal(2.0d);
                    PlayerLib.spawnParticle(
                            player.getLocation().add(0.0d, player.getEyeHeight(), 0.0d),
                            Particle.HEART,
                            5,
                            0.1d,
                            0.1d,
                            0.1d,
                            0.0f
                    );
                });

                // Fx
                Geometry.drawCircle(
                        totem.getLocationCentered(),
                        totem.getResonanceType().getRange(),
                        Quality.HIGH,
                        new WorldParticle(Particle.VILLAGER_HAPPY)
                );
            }
    ),

    CYCLONE_AURA(
            Material.PHANTOM_MEMBRANE,
            ChatColor.YELLOW,
            ChatColor.GOLD,
            "Continuously pulls all enemies to the center of the totem.",
            4.25d,
            5,
            (totem) -> {

            }
    ),

    ACCELERATING_AURA(
            Material.RED_MUSHROOM,
            ChatColor.RED,
            ChatColor.DARK_RED,
            "Accelerates cooldowns and ultimate point generation of all players around the totem.",
            3.0d,
            10,
            (totem) -> {

            }
    ),


    ;

    private final Material material;
    private final String about;
    private final double range;
    private final int interval;
    private final ChatColor[] colors;
    private final ActiveTotemResonance resonance;

    ResonanceType(Material material, ChatColor color, ChatColor activeColor, String about, double range, int interval, ActiveTotemResonance resonance) {
        this.material = material;
        this.about = about;
        this.range = range;
        this.interval = interval;
        this.colors = new ChatColor[] { color, activeColor };
        this.resonance = resonance;
    }

    public void resonate(ActiveTotem totem) {
        if (totem == null || resonance == null) {
            return;
        }
        resonance.resonate(totem);
    }

    public int getInterval() {
        return interval;
    }

    public String getAbout() {
        return about;
    }

    public ChatColor getColor() {
        return colors[0];
    }

    public ChatColor getActiveColor() {
        return colors[1];
    }

    public Material getMaterial() {
        return material;
    }

    public String getName() {
        return Chat.capitalize(name());
    }

    public double getRange() {
        return range;
    }
}
