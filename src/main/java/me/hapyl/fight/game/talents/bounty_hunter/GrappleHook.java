package me.hapyl.fight.game.talents.bounty_hunter;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.entity.EntityUtils;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.bounty_hunter.BountyHunterData;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Blocks;
import me.hapyl.fight.util.Collect;
import org.bukkit.Input;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Slime;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GrappleHook extends TickingGameTask implements Removable {
    
    private final GrappleHookTalent talent;
    private final BountyHunterData data;
    private final GamePlayer player;
    private final Vector direction;
    
    private final Slime hook;
    
    private Anchor anchor;
    private int cuts;
    private int airTicks;
    
    GrappleHook(@Nonnull GrappleHookTalent talent, @Nonnull BountyHunterData data) {
        this.talent = talent;
        this.data = data;
        this.player = data.player;
        this.direction = player.getEyeLocation().getDirection().normalize().multiply(talent.hookExtendSpeed);
        
        this.hook = createEntity();
        this.hook.setLeashHolder(player.getEntity());
        
        this.runTaskTimer(0, 1);
    }
    
    @Override
    public void run(int tick) {
        final Location playerLocation = player.getLocation();
        
        // If we have an anchor means the hook hit something, pull towards it
        if (anchor != null) {
            @Nullable final LivingGameEntity hookedEntity = anchor.entity();
            
            // If we're close enough, then get off the hook
            if (playerLocation.distanceSquared(hook.getLocation()) <= 1.5) {
                remove();
                
                // Add a little boost for blocks
                if (hookedEntity == null) {
                    player.setVelocity(playerLocation.getDirection().normalize().multiply(0.25).setY(0.5));
                }
                
                return;
            }
            
            final Input input = player.input();
            final Location anchorLocation = anchor.location();
            final Location location = player.getLocation();
            
            final Vector toAnchor = anchorLocation.toVector().subtract(location.toVector()).normalize();
            
            // Forward/strafe directions
            final Vector look = location.getDirection().setY(0).normalize();
            final Vector strafe = look.clone().crossProduct(new Vector(0, 1, 0)).normalize();
            
            // Weaken look
            final Vector inputVector = new Vector(0, 0, 0);
            
            if (input.isLeft()) {
                inputVector.subtract(strafe);
            }
            if (input.isRight()) {
                inputVector.add(strafe);
            }
            
            // Normalize input to prevent boosting speed too much
            if (inputVector.lengthSquared() > 0) {
                inputVector.normalize().multiply(talent.strafeStrength);
            }
            
            // Final velocity is the anchor pull plus input influence
            final Vector velocity = toAnchor.add(inputVector).normalize().multiply(talent.hookPullSpeed);
            
            final Vector smoothen = velocity.clone().multiply(1 - talent.strafeSmoothFactor).add(velocity.clone().multiply(talent.strafeSmoothFactor));
            
            player.setVelocity(smoothen);
            
            // Sync hook to anchor
            hook.teleport(anchorLocation);
            
            // If an entity was hooked, notify them and show cut progress
            if (hookedEntity != null) {
                final String title = cuts == 0
                                     ? "&6ʏᴏᴜ'ʀᴇ ʜᴏᴏᴋᴇᴅ!"
                                     : "&6&l\uD83E\uDE9D".repeat(cuts) + "&8&l\uD83E\uDE9D".repeat(talent.cutsToRemove - cuts);
                
                final int ticks = hookedEntity.aliveTicks() % 40;
                final String subTitle = ticks <= 20 ? "&e&lSNEAK&6 to escape!" : "&6&lSNEAK&e to escape!";
                
                hookedEntity.sendTitle(title, subTitle, 0, 10, 0);
            }
            
            if (airTicks++ >= talent.maxAirTicks) {
                remove();
                
                message("&cYour hook broke because it got too tired!");
                player.playSound(Sound.ITEM_LEAD_BREAK, 0.0f);
                return;
            }
            
            // Play particle fx
            player.spawnWorldParticle(anchorLocation, Particle.ITEM, 5, 0.1, 0.1, 0.1, 0.05f, talent.getItem(player));
        }
        // Else extend the hook
        else {
            final Location location = hook.getLocation();
            
            // Max distance flown
            if (playerLocation.distance(location) >= talent.maxDistance) {
                remove();
                
                message("&cYou didn't hook anything!");
                return;
            }
            
            location.add(direction);
            
            // Collision detection
            final Block block = location.getBlock();
            
            if (isValidBlock(block)) {
                anchor = new Anchor() {
                    @Nonnull
                    @Override
                    public Location location() {
                        return block.getLocation().add(0.5, 0.5, 0.5);
                    }
                };
                return;
            }
            
            final LivingGameEntity entity = Collect.nearestEntity(location, 1.5, player::isNotSelfOrTeammate);
            
            if (entity != null) {
                // Separate check for cc because we need to break the hook
                if (entity.hasEffectResistanceAndNotify(player)) {
                    remove();
                    
                    message("&cYour hook broke!");
                    player.playSound(Sound.ITEM_LEAD_BREAK, 0.0f);
                    return;
                }
                
                anchor = new Anchor() {
                    @Nonnull
                    @Override
                    public LivingGameEntity entity() {
                        return entity;
                    }
                    
                    @Nonnull
                    @Override
                    public Location location() {
                        return entity.getMidpointLocation();
                    }
                };
                
                return;
            }
            
            // Sync hook
            hook.teleport(location);
        }
    }
    
    public void tryEscape(@Nonnull GamePlayer player) {
        if (anchor == null) {
            return;
        }
        
        if (!player.equals(anchor.entity())) {
            return;
        }
        
        cuts++;
        
        if (cuts >= talent.cutsToRemove) {
            remove();
            
            // Notify
            player.sendTitle("&aᴇꜱᴄᴀᴘᴇᴅ!", "&6&l\uD83E\uDE9D".repeat(talent.cutsToRemove), 5, 15, 5);
            
            this.player.sendTitle("&6&l\uD83E\uDE9D", "&c%s escaped!".formatted(player.getName()), 5, 15, 5);
            this.player.playSound(Sound.ENTITY_SHEEP_SHEAR, 0.75f);
            return;
        }
        
        // Fx
        player.playSound(Sound.ENTITY_SHEEP_SHEAR, 0.5f + (0.25f * cuts));
    }
    
    private boolean isValidBlock(Block block) {
        if (!block.isSolid()) {
            return false;
        }
        
        return Blocks.isValid(block);
    }
    
    @Override
    public void remove() {
        cancel();
        hook.remove();
        data.hook = null;
        
        // Always give fall damage resistance
        player.addEffect(EffectType.FALL_DAMAGE_RESISTANCE, 100);
    }
    
    private Slime createEntity() {
        return Entities.SLIME.spawn(
                player.getMidpointLocation(), self -> {
                    self.setSize(1);
                    self.setGravity(false);
                    self.setInvulnerable(true);
                    self.setInvisible(true);
                    self.setSilent(true);
                    self.setAI(false);
                    
                    EntityUtils.setCollision(self, EntityUtils.Collision.DENY);
                }
        );
    }
    
    protected void message(String message) {
        player.sendMessage("&6&l\uD83E\uDE9D &r" + message);
    }
    
    private interface Anchor {
        
        @Nullable
        default LivingGameEntity entity() {
            return null;
        }
        
        @Nonnull
        Location location();
    }
}
