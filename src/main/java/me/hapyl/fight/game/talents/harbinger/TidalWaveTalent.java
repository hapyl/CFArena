package me.hapyl.fight.game.talents.harbinger;

import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayData;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.TalentLock;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.harbinger.HarbingerData;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.DirectionalMatrix;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TidalWaveTalent extends Talent {
    
    @DisplayField private final int talentLockDuration = Tick.fromSeconds(3);
    
    @DisplayField private final double arrowSpeed = 0.5;
    @DisplayField private final double speedDecrease = -10;
    @DisplayField private final double radius = 3;
    @DisplayField private final short riptideAmount = 200;
    @DisplayField private final int impairDuration = Tick.fromSeconds(3);
    
    private final DisplayData display = BDEngine.parse(
            "/summon block_display ~-0.5 ~ ~-0.5 {Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:tube_coral_fan\",Properties:{}},transformation:[2f,0f,0f,-1f,0f,0f,-2f,1.0625f,0f,2f,0f,0f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:tube_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[1.7320508076f,1f,0f,-0.49125f,0f,0f,-2f,1.0625f,-1f,1.7320508076f,0f,-0.70625f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:tube_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[-1.7320508076f,1f,0f,-0.509375f,0f,0f,-2f,1.0625f,-1f,-1.7320508076f,0f,1.025625f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:frogspawn\",Properties:{}},transformation:[1f,0f,0f,-0.5f,0f,0f,1f,-0.4375f,0f,-1f,0f,0.265625f,0f,0f,0f,1f]}]}"
    );
    
    private final ModifierSource modifierSource = new ModifierSource(Key.ofString("tidal_wave"));
    
    public TidalWaveTalent(@Nonnull Key key) {
        super(key, "Tidal Vortex");
        
        setDescription("""
                       Launch a giant vortex in front of you.
                       
                       The vortex constantly rushes forward, applying %s, &dlocking&7 enemy &atalents&7 and &eimpairing&7 movement.
                       &8&o;;The vortex can pass through solid blocks.
                       """.formatted(Named.RIPTIDE)
        );
        
        setType(TalentType.IMPAIR);
        setMaterial(Material.PRISMARINE_CRYSTALS);
        setDurationSec(3);
        setCooldownSec(12);
        setPoint(0);
    }
    
    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocationInFrontFromEyes(1.0);
        final DirectionalMatrix matrix = player.getLookAlongMatrix();
        
        final HarbingerData data = HeroRegistry.HARBINGER.getPlayerData(player);
        final Particle.DustTransition dustTransition = new Particle.DustTransition(Color.fromRGB(91, 148, 240), Color.fromRGB(14, 27, 173), 1);
        
        new TickingGameTask() {
            private double theta;
            
            @Override
            public void run(int tick) {
                final double progress = (double) tick / getDuration();
                
                if (progress >= 1.0) {
                    cancel();
                    return;
                }
                
                final double halfRadius = radius * 0.5;
                final double radius = halfRadius + (TidalWaveTalent.this.radius - halfRadius) * Math.sin(Math.toRadians(tick));
                
                for (int i = 0; i < 4; i++) {
                    final double x = Math.sin(theta) * radius;
                    final double y = Math.cos(theta) * radius;
                    final double z = (double) tick * arrowSpeed;
                    
                    // Only collide at first tick
                    if (i == 0) {
                        matrix.transformLocation(
                                location, x, y, z, () -> {
                                    Collect.nearbyEntities(location, radius, player::isNotSelfOrTeammate)
                                           .forEach(entity -> {
                                               final EntityAttributes attributes = entity.getAttributes();
                                               final boolean newModifier = attributes.hasModifier(modifierSource);
                                               
                                               // The collect check called every tick, so we check for modifier
                                               if (!newModifier) {
                                                   attributes.addModifier(
                                                           modifierSource, impairDuration, player, modifier -> modifier
                                                                   .of(AttributeType.SPEED, ModifierType.FLAT, speedDecrease)
                                                   );
                                                   
                                                   if (entity instanceof GamePlayer playerEntity) {
                                                       final TalentLock talentLock = playerEntity.getTalentLock();
                                                       
                                                       talentLock.setLockAll(talentLockDuration);
                                                       
                                                       entity.playSound(Sound.BLOCK_GLASS_BREAK, 0.5f);
                                                       entity.playSound(Sound.ENTITY_WITCH_HURT, 0.75f);
                                                       entity.playSound(Sound.ENTITY_WITCH_DEATH, 1.25f);
                                                   }
                                                   
                                                   data.setRiptide(entity, riptideAmount);
                                               }
                                               
                                           });
                                    
                                    // Sfx
                                    if (modulo(3)) {
                                        player.playWorldSound(location, Sound.AMBIENT_UNDERWATER_ENTER, (float) (0.5f + 0.7 * progress));
                                    }
                                }
                        );
                    }
                    
                    drawParticles(x, y, z);
                    drawParticles(y, x, z);
                    
                    drawParticles(-x, -y, z);
                    drawParticles(-y, -x, z);
                    
                    theta += Math.PI / 64;
                }
            }
            
            private void drawParticles(double x, double y, double z) {
                matrix.transformLocation(location, x, y, z, this::drawParticles);
            }
            
            private void drawParticles(Location location) {
                player.spawnWorldParticle(location, Particle.DUST_COLOR_TRANSITION, 0, 0, 0, 0, 1.0f, dustTransition);
            }
            
        }.runTaskTimer(0, 1);
        
        return Response.OK;
    }
    
}
