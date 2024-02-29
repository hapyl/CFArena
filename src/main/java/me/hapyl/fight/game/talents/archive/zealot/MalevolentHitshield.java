package me.hapyl.fight.game.talents.archive.zealot;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.shield.HitShield;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Tick;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MalevolentHitshield extends Talent {

    @DisplayField private final short shieldStrength = 7;
    @DisplayField private final int cooldown = Tick.fromSecond(30);

    public MalevolentHitshield() {
        super("Malevolent Hitshield");

        setDescription("""
                Gain a &eshield&7 for &b{shieldStrength}&7 hits, reduced on hit &nregardless&7 of damage.
                """);

        setType(Type.DEFENSE);
        setItem(Material.ENDER_EYE);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        startCd(player, 10000); // icd

        player.setShield(new HitShield(player, shieldStrength) {
            @Override
            public void onHit(double amount) {
                // Fx
                player.playWorldSound(Sound.ENTITY_ENDERMAN_TELEPORT, (float) (2.0f - (1.5f / shieldStrength * capacity)));
                player.spawnWorldParticle(Particle.PORTAL, 10, 0, 0, 0, 1.0f);
                player.spawnWorldParticle(Particle.REVERSE_PORTAL, 10, 0, 0, 0, 1.0f);
            }

            @Override
            public boolean canShield(@Nullable EnumDamageCause cause) {
                return true; // blocks any damage
            }

            @Override
            public void onBreak() {
                startCd(player, cooldown);

                player.playWorldSound(Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.75f);
                player.playWorldSound(Sound.ENTITY_ENDERMAN_HURT, 0.25f);
                player.spawnWorldParticle(Particle.SPELL_WITCH, 25, 0.5d, 0.5d, 0.5d, 1.0f);

                player.sendMessage("&5🛡 &dYour shield broke!");
            }
        });

        // Fx
        player.playWorldSound(Sound.ENTITY_ENDER_DRAGON_HURT, 0.0f);
        player.sendMessage("&5🛡 &dShield activated!");

        return Response.OK;
    }

}
