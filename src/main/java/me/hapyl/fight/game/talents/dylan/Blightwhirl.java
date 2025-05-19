package me.hapyl.fight.game.talents.dylan;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.dylan.Dylan;
import me.hapyl.fight.game.heroes.dylan.DylanFamiliar;
import me.hapyl.fight.game.heroes.dylan.FamiliarAction;
import me.hapyl.fight.terminology.EnumTerm;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class Blightwhirl extends WhelpTalent {
    
    @DisplayField private final double maxDistance = 10;
    
    @DisplayField private final double attackDamage = 2;
    @DisplayField private final double attackPeriod = 10;
    @DisplayField private final double attackRadius = 4;
    
    private final ModifierSource modifierSource = new ModifierSource(Key.ofString("blightwhirl"));
    
    public Blightwhirl(@Nonnull Key key) {
        super(key, "Blightwhirl");
        
        setDescription("""
                       With your command, &3%1$s&7 floats towards the &etarget&7 location and starts channeling &4Blightwhirl&7.
                       
                       &6Blightwhirl
                       &3%1$s&7 continuously spawns soul fire around itself, dealing &cdamage&7 in small AoE.
                       &8&o%1$s cannot take other actions, including self-destruct, until channeling ends.
                       
                       After &b{duration}&7, &3%1$s&7 returns back and gains one stack of %3$s.
                       """.formatted(Dylan.familiarName, EnumTerm.TRUE_DAMAGE, Named.SCORCH));
        
        setTexture("c2ec5a516617ff1573cd2f9d5f3969f56d5575c4ff4efefabd2a18dc7ab98cd");
        
        setDurationSec(6);
        setCooldownSec(12);
    }
    
    @Nonnull
    @Override
    public ItemBuilder makeUnavailableBuilder(@Nonnull ItemBuilder builder) {
        return builder.setHeadTextureUrl("9137628276beb8c7064daff8fc12a59fe61381dd813939872f406ee295331695");
    }
    
    @Nonnull
    @Override
    public Response execute(@Nonnull GamePlayer player, @Nonnull DylanFamiliar familiar) {
        final Block block = player.getTargetBlockExact((int) maxDistance);
        
        if (block == null) {
            return Response.error("No valid block in sight!");
        }
        
        final Block groundBlock = block.getRelative(BlockFace.UP);
        final Block ezelLocation = groundBlock.getRelative(BlockFace.UP);
        
        // Make sure ezel can fit
        if (!groundBlock.isEmpty() || !ezelLocation.isEmpty()) {
            return Response.error("No valid block in sight!");
        }
        
        final Location location = groundBlock.getLocation().add(0.5, 0.5, 0.5);
        final Location centre = BukkitUtils.newLocation(location);
        
        final EntityAttributes attributes = familiar.entity().getAttributes();
        attributes.addModifier(
                modifierSource, duration, modifier -> modifier
                        .of(AttributeType.DEFENSE, ModifierType.FLAT, 20)
                        .of(AttributeType.EFFECT_RESISTANCE, ModifierType.FLAT, 100)
        );
        
        familiar.action(
                new FamiliarAction() {
                    @Nonnull
                    @Override
                    public Location destination() {
                        return location;
                    }
                    
                    @Override
                    public boolean isInterruptible() {
                        return false;
                    }
                    
                    @Override
                    public void tick(@Nonnull GamePlayer player, @Nonnull DylanFamiliar familiar) {
                        if (familiar.isMoving()) {
                            return;
                        }
                        
                        final double actionDuration = familiar.actionDuration();
                        
                        for (double z = -attackRadius; z <= attackRadius; z += 0.25) {
                            final double progress = z / (attackRadius * 2);
                            final double x = Math.cos(Math.PI * progress) * z;
                            final double angle = Math.toRadians(actionDuration * 5);
                            
                            // Rotate
                            final Vector vector = new Vector(x, 0, z);
                            vector.rotateAroundY(angle);
                            
                            LocationHelper.offset(
                                    location, vector.getX(), 0, vector.getZ(), () -> {
                                        // Fx
                                        final Vector towardsCentre = centre.toVector().subtract(location.toVector()).normalize();
                                        
                                        player.spawnWorldParticle(
                                                location, Particle.SOUL, 0,
                                                towardsCentre.getX() * 0.3,
                                                0,
                                                towardsCentre.getZ() * 0.3,
                                                0.3f
                                        );
                                    }
                            );
                        }
                        
                        // Damage is done by simple radius check
                        if (actionDuration % attackPeriod == 0) {
                            Collect.nearbyEntities(location, attackRadius, player::isNotSelfOrTeammate)
                                   .forEach(entity -> entity.damageNoKnockback(attackDamage, entity, DamageCause.WHELP_ATTACK));
                            
                            // Attack fx
                            player.playWorldSound(location, Sound.BLOCK_SOUL_SAND_BREAK, 1.0f);
                            player.playWorldSound(location, Sound.ENTITY_VEX_HURT, 0.75f);
                        }
                    }
                    
                }, duration
        );
        
        // Fx
        familiar.entity().playWorldSound(Sound.ENTITY_VEX_CHARGE, 0.75f);
        
        return Response.OK;
    }
}
