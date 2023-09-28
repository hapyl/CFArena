package me.hapyl.fight.game.talents.archive.zealot;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.Compute;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.math.Tick;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Map;

public class MalevolentHitshield extends Talent {

    public final Material cooldownItem = Material.ENDERMAN_SPAWN_EGG;

    @DisplayField private final short shieldStrength = 10;
    @DisplayField private final int cooldown = Tick.fromSecond(30);

    private final Map<Player, Integer> playerShield;

    public MalevolentHitshield() {
        super("Malevolent Hitshield");

        setDescription("""
                Gain a shield for &b{shieldStrength}&7 hits, reduced on hit &nregardless&7 of damage.
                                
                &b;;The shield has infinite duration and the cooldown stars after the shield is broken.
                """);

        playerShield = Maps.newHashMap();

        setItem(Material.ENDER_EYE);
    }

    @Override
    public void onStop() {
        playerShield.clear();
    }

    @Override
    public void onDeath(Player player) {
        playerShield.remove(player);
    }

    @Override
    public Response execute(Player player) {
        playerShield.put(player, (int) shieldStrength);

        // Fx
        player.setGlowing(true);
        PlayerLib.playSound(player, Sound.ENTITY_ENDER_DRAGON_HURT, 0.0f);
        Chat.sendMessage(player, "&5🛡 &dShield activated!");
        startCd(player, 10000);

        return Response.OK;
    }

    public boolean hasCharge(Player player) {
        return playerShield.containsKey(player);
    }

    public void reduce(Player player) {
        final int shield = playerShield.compute(player, Compute.intSubtract());
        final Location location = player.getLocation();

        if (shield > 0) {
            player.setCooldown(cooldownItem, 2); // internal cooldown

            // Fx
            PlayerLib.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, (2.0f - (1.5f / shieldStrength * shield)));
            PlayerLib.spawnParticle(location, Particle.PORTAL, 10, 0, 0, 0, 1.0f);
            PlayerLib.spawnParticle(location, Particle.REVERSE_PORTAL, 10, 0, 0, 0, 1.0f);

            return;
        }

        // Shield broke
        playerShield.remove(player);

        player.setGlowing(false);
        startCd(player, cooldown);

        // Fx
        PlayerLib.playSound(location, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.75f);
        PlayerLib.playSound(location, Sound.ENTITY_ENDERMAN_HURT, 0.25f);
        PlayerLib.spawnParticle(location, Particle.SPELL_WITCH, 25, 0.5d, 0.5d, 0.5d, 1.0f);

        Chat.sendMessage(player, "&5🛡 &dYour shield broke!");
    }
}
