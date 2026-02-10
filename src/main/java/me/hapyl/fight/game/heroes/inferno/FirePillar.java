package me.hapyl.fight.game.heroes.inferno;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.component.ComponentList;
import me.hapyl.eterna.module.hologram.Hologram;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.WarningType;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.terminology.EnumTerm;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Collect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import javax.annotation.Nonnull;
import java.util.List;

public class FirePillar extends TickingGameTask {
    
    private static final Material[] PILLAR_MATERIALS = {
            Material.RED_TERRACOTTA,
            Material.YELLOW_TERRACOTTA
    };
    
    private static final long ICD = 200L;
    
    private final InfernoUltimate ultimate;
    private final GamePlayer player;
    private final Location location;
    private final List<Block> affectedBlocks;
    private final Hologram hologram;
    
    private int hitsLeft;
    private long lastHit;
    private double theta;
    
    public FirePillar(InfernoUltimate ultimate, GamePlayer player, Location location) {
        this.ultimate = ultimate;
        this.player = player;
        this.location = location;
        this.affectedBlocks = Lists.newArrayList();
        this.hitsLeft = ultimate.pillarHealth;
        
        this.hologram = Hologram.ofArmorStand(location);
        this.hologram.showAll();
        
        makePillar(ultimate.pillarHeight);
        
        // Fx
        player.playWorldSound(Sound.ENTITY_IRON_GOLEM_DEATH, 0.0f);
        
        // Notify enemies
        Collect.enemyPlayers(player).forEach(enemy -> {
            enemy.sendMessage("&c&l\uD83D\uDDFC &eA fire pillar has spawned nearby! &e&lBreak it or &4&ldie&e&l!");
            enemy.sendWarning(WarningType.DANGER, 40);
        });
    }
    
    public boolean hasBlock(@Nonnull Block block) {
        return affectedBlocks.contains(block);
    }
    
    private void makePillar(int height) {
        // Reset previous blocks
        affectedBlocks.forEach(block -> block.setType(Material.AIR, false));
        affectedBlocks.clear();
        
        final Block block = location.getBlock();
        
        for (int i = 0; i < height; i++) {
            final Block relative = block.getRelative(BlockFace.UP, i);
            final int iModTwo = i % 2;
            
            relative.setType(PILLAR_MATERIALS[height % 2 == 0 ? 1 - iModTwo : iModTwo], false);
            affectedBlocks.add(relative);
        }
        
        // Update hologram
        hologram.teleport(location.clone().add(0.0d, height - 0.5d, 0.0d));
    }
    
    protected void hitPillar(GamePlayer player) {
        if (this.player.isSelfOrTeammate(player)) {
            player.sendMessage("&c&l\uD83D\uDDFC &6Cannot damage allied pillar!");
            player.playSound(Sound.ENTITY_CAT_HISS, 2.0f);
            player.setVelocity(player.getDirection().multiply(-1).multiply(0.5d).setY(0.3d));
            return;
        }
        
        final long currentTimeMillis = System.currentTimeMillis();
        final long timeSinceLastHit = currentTimeMillis - lastHit;
        
        // Icd
        if (timeSinceLastHit < ICD) {
            return;
        }
        
        lastHit = currentTimeMillis;
        
        if (--hitsLeft > 0) {
            // Decrease size every 2 hits
            if (hitsLeft % ultimate.pillarHealthHeightDifference == 0) {
                final int newSize = hitsLeft / ultimate.pillarHealthHeightDifference;
                
                makePillar(newSize);
            }
        }
        else {
            breakPillar();
            return;
        }
        
        // Fx
        player.playWorldSound(Sound.ENTITY_WITHER_BREAK_BLOCK, 2.0f);
        player.playWorldSound(Sound.ENTITY_IRON_GOLEM_DAMAGE, 0.75f);
    }
    
    private void breakPillar() {
        ultimate.removePillar(this);
        
        // Fx
        player.playWorldSound(location, Sound.ENTITY_WITHER_BREAK_BLOCK, 0.75f);
        player.playWorldSound(location, Sound.ENTITY_WITHER_BREAK_BLOCK, 1.25f);
        player.playWorldSound(location, Sound.ENTITY_WITHER_BREAK_BLOCK, 2.0f);
    }
    
    @Override
    public void run(int tick) {
        // If player is no longer alive, remove the pillar
        if (player.isDeadOrRespawning()) {
            breakPillar();
            return;
        }
        
        final List<LivingGameEntity> victims = Collect.nearbyEntities(location, ultimate.explosionRadius, player::isNotSelfOrTeammate);
        
        if (tick > ultimate.explosionDelay) {
            breakPillar();
            
            // Boom
            victims.forEach(enemy -> {
                final double damage = enemy.getMaxHealth() * 2.0d;
                
                enemy.sendMessage("&c&l\uD83D\uDDFC &4A fire pillar exploded and damaged you for &f&l%.0f %s&4!".formatted(damage, EnumTerm.TRUE_DAMAGE));
                enemy.damageNoKnockback(damage, player, DamageCause.FIRE_PILLAR);
            });
            
            // Fx
            player.spawnWorldParticle(location, Particle.LAVA, 100, 5d, 0.2d, 5d, 0.01f);
            player.spawnWorldParticle(location, Particle.EXPLOSION_EMITTER, 1);
            
            player.playWorldSound(location, Sound.ENTITY_BLAZE_DEATH, 0.0f);
            player.playWorldSound(location, Sound.ENTITY_WITHER_HURT, 0.0f);
            return;
        }
        
        // Update hologram
        this.hologram.setLines(pl -> ComponentList.ofLegacy(
                "&c&l\uD83D\uDDFC &6&lFIRE PILLAR &c&l\uD83D\uDDFC",
                "&e&l%.1fs &e\uD83D\uDCA5  &c&l%s &c❤".formatted((ultimate.explosionDelay - tick) / 20d, hitsLeft)
        ));
        
        // Notify victims
        victims.stream()
               .filter(GamePlayer.class::isInstance)
               .map(GamePlayer.class::cast)
               .forEach(gameplayer -> gameplayer.sendWarning(WarningType.DANGER, 15));
        
        // Fx
        final double height = (double) hitsLeft / ultimate.pillarHeight;
        
        final double x = Math.sin(theta) * 1.025d;
        final double y = Math.cos(Math.toRadians(tick) * 10) * height + (height * 0.7d);
        final double z = Math.cos(theta) * 1.025d;
        
        LocationHelper.offset(location, x, y, z, () -> player.spawnWorldParticle(location, Particle.FLAME, 1));
        LocationHelper.offset(location, z, y, x, () -> player.spawnWorldParticle(location, Particle.FLAME, 1));
        
        theta += Math.PI / 32;
        
        // Some lava particles
        if (modulo(3)) {
            player.spawnWorldParticle(location, Particle.LAVA, 5, 0.1d, 1d, 0.1d, 0.02f);
            player.playWorldSound(location, Sound.BLOCK_LAVA_POP, 0.5f + (((float) tick / ultimate.explosionDelay)));
        }
    }
    
    @Override
    public void onTaskStop() {
        CFUtils.clearCollectionAnd(affectedBlocks, block -> block.setType(Material.AIR, false));
        
        hologram.destroy();
    }
}
