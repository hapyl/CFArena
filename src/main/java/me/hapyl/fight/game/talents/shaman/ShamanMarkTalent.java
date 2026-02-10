package me.hapyl.fight.game.talents.shaman;


import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.shaman.ShamanData;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.RaycastTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ShamanMarkTalent extends Talent {
    
    @DisplayField protected final int outOfSightDuration = Tick.fromSeconds(2);
    @DisplayField protected final double attackSpeedIncrease = 50;
    @DisplayField protected final double speedIncrease = 50;
    @DisplayField(percentage = true) protected final double attackIncrease = 0.5;
    
    @DisplayField private final double projectileStep = 0.5;
    private final double maxDistance = 30;
    
    // this is only for the display
    @DisplayField(suffix = " blocks") private final double maxProjectileDistance = maxDistance * projectileStep;
    
    public ShamanMarkTalent(@Nonnull Key key) {
        super(key, "Shaman's Mark");
        
        setDescription("""
                       Launch a projectile forward, that applies %s on the first &aally&7 it touches.
                       
                       &6%s
                       A mark that exists indefinitely as long as the &aally&7 is within the &bline of sight&7, and increases:
                        &8├&7 %s.
                        &8├&7 %s.
                        &8└&7 %s.
                       
                       &8&o;;Only one mark can exist on a single ally at any given time.
                       &8&o;;Marks from different Shamans don't stack.
                       """.formatted(Named.SHAMANS_MARK, Named.SHAMANS_MARK.getName(), AttributeType.ATTACK, AttributeType.ATTACK_SPEED, AttributeType.SPEED));
        
        setMaterial(Material.LILY_PAD);
        setType(TalentType.SUPPORT);
        setCooldownSec(16);
    }
    
    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        new RaycastTask(player.getEyeLocation()) {
            @Override
            public boolean step(@Nonnull Location location) {
                final LivingGameEntity nearestEntity = Collect.nearestEntity(location, 1.5, player::isTeammate);
                
                if (nearestEntity != null) {
                    final ShamanData data = HeroRegistry.SHAMAN.getPlayerData(player);
                    
                    if (data.mark != null) {
                        data.mark.cancel();
                    }
                    
                    data.mark = new ShamanMark(ShamanMarkTalent.this, data, nearestEntity);
                    return true;
                }
                
                // Fx
                player.spawnWorldParticle(location, Particle.ITEM, 3, 0.1, 0.1, 0.1, 0.1f, new ItemStack(Material.SLIME_BALL));
                player.playWorldSound(location, Sound.ENTITY_FROG_TONGUE, 1.25f);
                return false;
            }
        }.setStep(projectileStep)
         .setMax(maxDistance)
         .setIterations(2)
         .runTaskTimer(0, 1);
        
        return Response.OK;
    }
}
