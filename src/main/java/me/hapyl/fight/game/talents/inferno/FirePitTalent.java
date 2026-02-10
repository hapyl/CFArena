package me.hapyl.fight.game.talents.inferno;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.terminology.EnumTerm;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByBlockEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class FirePitTalent extends Talent implements Listener {
    
    protected final int[][] firePitsSpawnOffsets = {
            { 0, 0 },
            { 3, 3 },
            { 3, -3 },
            { -3, 3 },
            { -3, -3 }
    };
    
    protected final int[][] firePitsOffsets = {
            { 0, 0 },
            { 1, 0 },
            { 0, 1 },
            { -1, 0 },
            { 0, -1 }
    };
    
    protected final BlockData[] firePitsMaterials = {
            Material.BLACK_TERRACOTTA.createBlockData(),
            Material.BROWN_TERRACOTTA.createBlockData(),
            Material.ORANGE_TERRACOTTA.createBlockData(),
            Material.RED_TERRACOTTA.createBlockData()
    };
    
    @DisplayField protected final short pitCount = 5;
    @DisplayField protected final int transformDelay = 20;
    @DisplayField(percentage = true) protected final double damage = 2d;
    
    @DisplayField private final short totalStages = (short) firePitsMaterials.length;
    @DisplayField private final int delayPerStage = transformDelay / totalStages;
    
    protected final Set<InfernoFire> infernoFires = Sets.newHashSet();
    
    public FirePitTalent(@Nonnull Key key) {
        super(key, "Fire Pit");
        
        setDescription("""
                       Summon &6{pitCount}&7 fire pits around you which transform to soul fire after &b{transformDelay}&7.
                       
                       Stepping in fire deals &4{damage}&7 of &cenemy&7's %s as %s.
                       """.formatted(AttributeType.MAX_HEALTH, EnumTerm.TRUE_DAMAGE));
        
        setMaterial(Material.FIRE_CHARGE);
        setType(TalentType.DAMAGE);
        
        setDurationSec(2.5f);
        setCooldownSec(20.0f);
    }
    
    @EventHandler
    public void handleEntityCombustEvent(EntityCombustByBlockEvent ev) {
        final LivingGameEntity entity = CF.getEntity(ev.getEntity());
        final Block combuster = ev.getCombuster();
        
        if (entity == null || combuster == null) {
            return;
        }
        
        final InfernoFire fire = infernoFires.stream()
                                             .filter(f -> CFUtils.compareBlockLocation(f.location, combuster.getLocation()))
                                             .findFirst()
                                             .orElse(null);
        
        if (fire == null) {
            return;
        }
        
        fire.touch(entity);
        ev.setCancelled(true);
    }
    
    @Nullable
    public InfernoFire createFire(@Nonnull Location location, @Nonnull InfernoFire.Type type, @Nonnull Consumer<LivingGameEntity> touch) {
        if (!location.getBlock().isEmpty()) {
            return null;
        }
        
        final InfernoFire fire = new InfernoFire(location, type) {
            @Override
            public void touch(@Nonnull LivingGameEntity entity) {
                touch.accept(entity);
            }
        };
        infernoFires.add(fire);
        
        return fire;
    }
    
    @Override
    public void onStop(@Nonnull GameInstance instance) {
        infernoFires.forEach(InfernoFire::extinguish);
        infernoFires.clear();
    }
    
    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        new FirePitHandler(player);
        return Response.OK;
    }
    
    private class FirePitHandler extends TickingGameTask {
        
        private final GamePlayer player;
        private final List<FirePit> firePits;
        
        FirePitHandler(@Nonnull GamePlayer player) {
            this.player = player;
            this.firePits = Lists.newArrayList();
            
            // Create fire pits
            for (int[] offset : firePitsSpawnOffsets) {
                firePits.add(new FirePit(FirePitTalent.this, player, player.getLocation().add(offset[0], 0, offset[1])));
            }
            
            runTaskTimer(0, 1);
        }
        
        @Override
        public void run(int tick) {
            final int stage = tick / delayPerStage;
            
            if (player.isDeadOrRespawning()) {
                extinguish();
                return;
            }
            
            // Increment stage
            if (tick % delayPerStage == 0 && stage < totalStages) {
                firePits.forEach(pit -> {
                    pit.transform(firePitsMaterials[stage]);
                    
                    // Fx
                    player.playWorldSound(pit.centre(), Sound.ITEM_FLINTANDSTEEL_USE, 0.75f + (0.75f * stage / totalStages));
                });
            }
            
            // Create inferno fire
            if (tick == transformDelay) {
                firePits.forEach(FirePit::lightTheFire);
            }
            
            // Extinguish
            if (tick >= getDuration() + transformDelay) {
                extinguish();
            }
        }
        
        private void extinguish() {
            cancel();
            
            firePits.forEach(FirePit::remove);
            firePits.clear();
        }
    }
    
}
