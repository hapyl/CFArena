package me.hapyl.fight.game.talents.archive.zealot;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Tick;
import me.hapyl.spigotutils.module.util.Compute;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class MalevolentHitshield extends Talent {

    public final Material cooldownItem = Material.ENDERMAN_SPAWN_EGG;

    @DisplayField private final short shieldStrength = 10;
    @DisplayField private final int cooldown = Tick.fromSecond(30);

    private final PlayerMap<Integer> playerShield;

    public MalevolentHitshield() {
        super("Malevolent Hitshield");

        setDescription("""
                Gain a shield for &b{shieldStrength}&7 hits, reduced on hit &nregardless&7 of damage.
                                
                &b;;The shield has infinite duration and the cooldown stars after the shield is broken.
                """);

        playerShield = PlayerMap.newMap();

        setItem(Material.ENDER_EYE);
    }

    @Override
    public void onStop() {
        playerShield.clear();
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        playerShield.remove(player);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        playerShield.put(player, (int) shieldStrength);
        startCd(player, 10000);

        // Fx
        player.setGlowing(true);
        player.playSound(Sound.ENTITY_ENDER_DRAGON_HURT, 0.0f);
        player.sendMessage("&5🛡 &dShield activated!");

        return Response.OK;
    }

    public boolean hasCharge(GamePlayer player) {
        return playerShield.containsKey(player);
    }

    public void reduce(GamePlayer player) {
        final int shield = playerShield.compute(player, Compute.intSubtract());
        final Location location = player.getLocation();

        if (shield > 0) {
            player.setCooldown(cooldownItem, 2); // internal cooldown

            // Fx
            player.playWorldSound(Sound.ENTITY_ENDERMAN_TELEPORT, (2.0f - (1.5f / shieldStrength * shield)));
            player.spawnWorldParticle(Particle.PORTAL, 10, 0, 0, 0, 1.0f);
            player.spawnWorldParticle(Particle.REVERSE_PORTAL, 10, 0, 0, 0, 1.0f);

            return;
        }

        // Shield broke
        playerShield.remove(player);

        player.setGlowing(false);
        startCd(player, cooldown);

        // Fx
        player.playWorldSound(Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.75f);
        player.playWorldSound(Sound.ENTITY_ENDERMAN_HURT, 0.25f);
        player.spawnWorldParticle(Particle.SPELL_WITCH, 25, 0.5d, 0.5d, 0.5d, 1.0f);

        player.sendMessage("&5🛡 &dYour shield broke!");
    }
}
