package me.hapyl.fight.game.talents.taker;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.taker.TakerData;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class FatalReap extends Talent {

    @DisplayField private final short spiritualBoneGeneration = 2;
    @DisplayField(suffix = " blocks") private final double length = 2.0d;
    @DisplayField(suffix = "%") private final double damagePercent = 20.0d;

    public FatalReap(@Nonnull Key key) {
        super(key, "Fatal Reap");

        setDescription("""
                Instantly swipe your scythe to unleash a &4devastating attack&7 that shatters your opponents' &fbones&7, dealing &c{damagePercent}&7 of their current health as &4damage&7.
                
                Also convert &b{spiritualBoneGeneration}&7 broken bones from each hit enemy directly into %s.
                """.formatted(Named.SPIRITUAL_BONES)
        );

        setMaterial(Material.NETHERITE_HOE);
        setCooldownSec(12);
    }

    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        final Set<LivingGameEntity> hitEntities = Sets.newHashSet();

        for (double d = length; d >= -length; d -= 0.2d) {
            final Location location = calculateLocation(player.getEyeLocation(), d);
            
            hitEntities.addAll(Collect.nearbyEntities(location, 1.5d, player::isNotSelfOrTeammate));

            player.spawnWorldParticle(location, Particle.SWEEP_ATTACK, 1, 0.0f, 0.0f, 0.0f, 0.0f);
        }

        final TakerData data = HeroRegistry.TAKER.getPlayerData(player);

        // Damage and give bones here
        hitEntities.forEach(entity -> {
            final double health = entity.getHealth();
            final double damage = Math.min(health * (damagePercent / 100), entity.getMaxHealth() / 2);
            
            entity.damage(damage, player, DamageCause.RIP_BONES);
            
            data.add(spiritualBoneGeneration, true);
        });
        hitEntities.clear();

        // Fx
        player.playWorldSound(Sound.ENTITY_CAT_HISS, 2.0f);

        return Response.OK;
    }

    private Location calculateLocation(Location location, double d) {
        location.setPitch(0.0f);

        final Vector vector = location.getDirection().normalize();

        location.add(0.0d, d / 2.0d, 0.0d);
        location.add(new Vector(-vector.getZ(), 0.0d, vector.getX()).multiply(Math.sin(d) * 1.5d));
        location.add(vector.multiply(length + (d / 2.0d)));

        return location;
    }

}
