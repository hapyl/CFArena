package me.hapyl.fight.game.talents.zealot;


import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.Shield;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MalevolentHitshieldTalent extends Talent {

    @DisplayField private final short shieldStrength = 7;
    @DisplayField private final int cooldown = Tick.fromSeconds(30);

    public MalevolentHitshieldTalent(@Nonnull Key key) {
        super(key, "Malevolent Hitshield");

        setDescription("""
                Gain a &eshield&7 for &b{shieldStrength}&7 hits, reduced on hit &nregardless&7 of damage.
                """
        );

        setType(TalentType.DEFENSE);
        setMaterial(Material.ENDER_EYE);
    }

    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        startCdIndefinitely(player); // icd

        player.setShield(new MalevolentHitshield(player));

        // Fx
        player.playWorldSound(Sound.ENTITY_ENDER_DRAGON_HURT, 0.0f);
        player.sendMessage("&5🛡 &dShield activated!");

        return Response.OK;
    }
    
    private class MalevolentHitshield extends Shield {
        
        public MalevolentHitshield(@Nonnull LivingGameEntity entity) {
            super(entity, shieldStrength);
        }
        
        @Override
        public void takeDamage(double damage) {
            capacity--;
        }
        
        @Override
        public boolean canShield(@Nullable DamageCause cause) {
            return true; // blocks any damage
        }
        
        @Override
        public void onRemove(@Nonnull Cause cause) {
            if (cause != Cause.BROKEN) {
                return;
            }
         
            // Fx
            entity.playWorldSound(Sound.ENTITY_ENDERMAN_HURT, 0.0f);
            entity.playWorldSound(Sound.ENTITY_ENDERMAN_DEATH, 1.25f);
            
            final Location location = entity.getMidpointLocation();
         
            entity.spawnWorldParticle(location, Particle.WITCH, 50, 0,0,0, 0.7f);
        }
        
        @Override
        public void onHit(double amount, @Nullable LivingGameEntity damager) {
            // Fx
            entity.playWorldSound(Sound.ENTITY_ENDERMAN_TELEPORT, (float) (2.0f - (1.5f / shieldStrength * capacity)));
            entity.spawnWorldParticle(Particle.PORTAL, 10, 0, 0, 0, 1.0f);
            entity.spawnWorldParticle(Particle.REVERSE_PORTAL, 10, 0, 0, 0, 1.0f);
        }
    }

}
