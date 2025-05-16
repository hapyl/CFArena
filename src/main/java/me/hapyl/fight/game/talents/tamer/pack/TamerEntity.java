package me.hapyl.fight.game.talents.tamer.pack;

import me.hapyl.eterna.module.ai.AI;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.util.IndexedTicking;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.team.Entry;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.util.Collect;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

public class TamerEntity extends LivingGameEntity implements IndexedTicking {
    
    private static final double MAX_DISTANCE_FROM_OWNER = 30;
    
    protected final ActiveTamerPack pack;
    protected final GamePlayer player;
    protected final GameTeam team;
    protected final AI ai;
    protected int tick;
    protected boolean targetClosestEntities;
    
    public TamerEntity(@Nonnull ActiveTamerPack pack, @Nonnull LivingEntity entity) {
        super(entity, pack.getPack().attributes);
        
        this.pack = pack;
        this.player = pack.player;
        this.team = player.getTeam();
        this.targetClosestEntities = true;
        
        // Add helmet if possible
        final EntityEquipment equipment = getEquipment();
        equipment.setHelmet(ItemBuilder.leatherHat(team.getColorAsBukkitColor()).setUnbreakable().asIcon());
        
        ai = getMobAI();
    }
    
    @Override
    @OverridingMethodsMustInvokeSuper
    public void tick(int index) {
        tick++;
        
        final double distanceFromOwner = getLocation().distance(player.getLocation());
        
        // Teleport if too far away
        if (distanceFromOwner > MAX_DISTANCE_FROM_OWNER) {
            teleport(player);
            playWorldSound(Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f);
        }
        
        if (!(entity instanceof Creature creature) || !targetClosestEntities) {
            return;
        }
        
        final LivingEntity target = creature.getTarget();
        
        // Target the closest entity
        if ((target == null || team.isTeammates(Entry.of(this), Entry.of(target)))) {
            final LivingGameEntity nearestTarget = findNearestTarget();
            
            if (nearestTarget == null || nearestTarget.getEntity().equals(target)) {
                return; // Don't care
            }
            
            creature.setTarget(nearestTarget.getEntity());
            creature.setAware(true);
            
            // Fx
            playWorldSound(entity.getHurtSound(), 0.5f);
            spawnWorldParticle(getEyeLocation(), Particle.LAVA, 5, 0.1, 0.3, 0.1, 0.025f);
        }
        
    }
    
    @Nullable
    private LivingGameEntity findNearestTarget() {
        return Collect.nearestEntity(getLocation(), 30, this::isNotSelfOrTeammate);
    }
    
}
