package me.hapyl.fight.game.talents.dylan;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayData;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Vectors;
import me.hapyl.fight.game.Callback;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.Shield;
import me.hapyl.fight.game.entity.cooldown.EntityCooldown;
import me.hapyl.fight.game.heroes.dylan.Dylan;
import me.hapyl.fight.game.heroes.dylan.DylanFamiliar;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class HellfireWard extends Talent {
    
    @DisplayField(percentage = true) private final double shieldStrength = 0.6;
    @DisplayField(percentage = true) private final float shieldCapacity = 0.6f;
    
    @DisplayField(percentage = true) private final double scorchingRingDamage = 0.3;
    @DisplayField private final int scorchingRingCooldown = 30;
    
    @DisplayField private final double maxLookupDistance = 20;
    
    private final EntityCooldown cooldown = EntityCooldown.of("hellfire_ward", scorchingRingCooldown * 50L);
    
    public HellfireWard(@Nonnull Key key) {
        super(key, "Hellfire Ward");
        
        setDescription("""
                       Cast a protective ward on a &ateammate &8(excluding &3%s&8)&7, applying &eHellfire Shield&7 for up to &b{duration}&7.
                       
                       &6Hellfire Shield
                        &8•&7 Absorbs &e{shieldStrength}&7 of the damage taken.
                        &8•&7 Triggers a &eScorching Ring&7 on damage, dealing &4{scorchingRingDamage}&7 of the &cdamage&7 taken to nearby &cenemies&7 and sets them on &efire&7.
                       """.formatted(Dylan.familiarName));
        
        setType(TalentType.DEFENSE);
        setMaterial(Material.HONEYCOMB);
        
        setDurationSec(12.5f);
        setCooldownSec(22);
    }
    
    @Nullable
    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final LivingGameEntity target = Collect.targetEntityRayCast(
                player, maxLookupDistance, 1.25, entity -> !(entity instanceof DylanFamiliar.FamiliarEntity) && player.isTeammate(entity)
        );
        
        if (target == null) {
            return Response.error("Not targeting a teammate!");
        }
        
        applyShield(player, target);
        return Response.OK;
    }
    
    public void applyShield(@Nonnull GamePlayer player, @Nonnull LivingGameEntity target) {
        target.setShield(new HellfireShield(
                player,
                target, target.getMaxHealth() * shieldCapacity, callback -> callback
                .duration(duration)
                .strength(shieldCapacity)
        ));
    }
    
    public class HellfireShield extends Shield {
        
        private static final DisplayData model = BDEngine.parse(
                "{Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:netherite_block\",Properties:{}},transformation:[-0.0113233332f,0f,-0.2112962745f,0.0138178968f,0f,0.30625f,0f,0.2668655053f,0.0422592549f,0f,-0.0566166661f,0.0009086237f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:netherite_block\",Properties:{}},transformation:[0.0113233332f,0f,-0.2112962745f,0.1940678968f,0f,0.30625f,0f,0.2668655053f,0.0422592549f,0f,0.0566166661f,-0.0539976263f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:netherite_block\",Properties:{}},transformation:[0.0096404143f,0.0640658185f,-0.1538368658f,0.1328178968f,-0.0029448669f,0.2091581492f,0.0471299738f,0.0881467553f,0.0375194972f,-0.0000447264f,0.0432266618f,-0.0533413763f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:netherite_block\",Properties:{}},transformation:[-0.0098141154f,-0.0640658185f,-0.1538368658f,0.0186303968f,-0.0029979276f,0.2091581492f,-0.0471299738f,0.1351780053f,0.0381955242f,-0.0000447264f,-0.0432266618f,-0.0113413763f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;653113882,882463709,1572010999,-1752428094],properties:[{name:\"textures\",value:\"ewogICJ0aW1lc3RhbXAiIDogMTc0ODEwNzkyMDI0MiwKICAicHJvZmlsZUlkIiA6ICIwNDg2YWUwMWI4Y2I0OWUzODMyZDcwOTNmMWJlNzI3NyIsCiAgInByb2ZpbGVOYW1lIiA6ICJfcGFrbWFuXyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS80YzE1YTQ5YjQyMzdkNTY1ZjE2ODc4ZmY1ZTE1NDNkZTZkM2E5MGM5ODg0ZjkxZGY1ODAxOWI0ZDAxNTYxMGMyIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=\"}]}}},item_display:\"none\",transformation:[-0.0148030303f,0f,-0.1276333353f,-0.0292758532f,0.0060307252f,0.2332276249f,-0.0015320731f,0.4554280053f,0.0860276156f,-0.0163497697f,-0.0218548498f,0.0267211237f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:netherite_block\",Properties:{}},transformation:[0.0000051001f,-0.0004280167f,-0.0699995215f,0.0345991468f,-0.0000533569f,0.1189944421f,-0.0002523026f,0.1539905053f,0.0059497586f,0.0010674983f,0.0000577408f,0.0094398737f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:netherite_block\",Properties:{}},transformation:[-0.0093633238f,0.0509921443f,-0.1352590607f,-0.0021508532f,-0.0030530053f,0.0786052781f,0.0938402797f,0.0030530053f,0.038304175f,0.0187300285f,-0.0255841437f,-0.0288413763f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:netherite_block\",Properties:{}},transformation:[0.0093633238f,-0.0509921443f,-0.1352590607f,0.1371928968f,-0.0030530053f,0.0786052781f,-0.0938402797f,0.0968967553f,0.038304175f,0.0187300285f,0.0255841437f,-0.0550913763f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:netherite_block\",Properties:{}},transformation:[0f,0f,-0.0875f,0.0426928968f,0f,0.284375f,0f,0.2804280053f,0.065625f,0f,0f,-0.0179038763f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:netherite_block\",Properties:{}},transformation:[0f,0f,-0.0875f,0.0426928968f,-0.0055826615f,0.2833441553f,0f,0.0128967553f,0.0653871128f,0.0241915332f,0f,-0.0415288763f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-994881397,784856179,551120226,-1346562376],properties:[{name:\"textures\",value:\"ewogICJ0aW1lc3RhbXAiIDogMTc0ODEwNzkyOTM1NSwKICAicHJvZmlsZUlkIiA6ICJhODEzMzJmNjA5MmE0NDQ1Yjk1YzVhZGFjNDA2OTQzYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJTbmFwcHliM2FzdCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9iZjY5YjkxMzAxNDI1NDBlN2M4OWIzYWYwM2NjOThhNzliN2JhZTAwYWU2MjQzMDY1YWQzZDkxYTZkZjBkOTkwIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=\"}]}}},item_display:\"none\",transformation:[0.0148030303f,0f,-0.1248736957f,0.0267241468f,0.0060307252f,0.2332276249f,0.0014989472f,0.4554280053f,0.0860276156f,-0.0163497697f,0.0213823125f,0.0267211237f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-453329952,-854092510,353702174,601715541],properties:[{name:\"textures\",value:\"ewogICJ0aW1lc3RhbXAiIDogMTc0ODEwNzk0MDkzNSwKICAicHJvZmlsZUlkIiA6ICI5ZjJiY2M1M2U4YzM0OTY4YTc5Yzc0NTExYWQ2NmQyYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJLYWJveWlvIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Q2MWUzNTkwNjNhYTRhYWE2YmYyYjU1ZDk0MjUxODA2ZmNjODAyZjJmNjliYjhjYWRmYTdlMDRlMGVlNDk5NGEiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==\"}]}}},item_display:\"none\",transformation:[-0.0148030303f,0f,-0.1276333353f,-0.0292758532f,-0.0074862125f,0.2329174191f,0.0019018318f,0.3473655053f,0.0859131941f,0.0202957104f,-0.0218257817f,0.0345961237f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;2025410943,-2003344517,-847677110,134923698],properties:[{name:\"textures\",value:\"ewogICJ0aW1lc3RhbXAiIDogMTc0ODEwNzk0NjE2NiwKICAicHJvZmlsZUlkIiA6ICIzMzU3MWJiY2UyMDE0MTRiYmNkMDYyMjEyZTI4MjBlMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGFkb21JbmF0b3I0NzgiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjRkM2JkZDFkNTRlNjVkY2MxYTgwZjg5NmVjY2ZjNDYzMjZjNzI4OGFmN2ZjZTAyYmMxZjJiM2E5MDA5MzJjOCIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9\"}]}}},item_display:\"none\",transformation:[0.0148030303f,0f,-0.1248736957f,0.0267241468f,-0.0074862125f,0.2329174191f,-0.0018607111f,0.3473655053f,0.0859131941f,0.0202957104f,0.0213538729f,0.0345961237f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_wall\",Properties:{up:\"true\"}},transformation:[-0.0240054664f,0f,-0.1267777647f,-0.1038696032f,0f,0.30625f,0f,0.2701467553f,0.0895896204f,0f,-0.0339699997f,-0.0566226263f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_wall\",Properties:{up:\"true\"}},transformation:[-0.0235365069f,-0.0616056911f,-0.1237805034f,-0.0487446032f,-0.0071897202f,0.2011264763f,-0.0379218067f,0.1146155053f,0.0916016554f,-0.0000430089f,-0.0347811166f,-0.0574976263f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_wall\",Properties:{up:\"true\"}},transformation:[-0.0226625582f,0.0368899212f,-0.2418181947f,0.0310991468f,-0.0073893535f,0.0568664558f,0.1677690714f,-0.0332594947f,0.0927096626f,0.0135501122f,-0.0457397191f,-0.0513726263f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_wall\",Properties:{up:\"true\"}},transformation:[0.0240054664f,0f,-0.1267777647f,0.2340991468f,0f,0.30625f,0f,0.2683967553f,0.0895896204f,0f,0.0339699997f,-0.0905288763f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_wall\",Properties:{up:\"true\"}},transformation:[0.0235365069f,0.0616056911f,-0.1237805034f,0.1759116468f,-0.0071897202f,0.2011264763f,0.0379218067f,0.0767717553f,0.0916016554f,-0.0000430089f,0.0347811166f,-0.0922788763f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_wall\",Properties:{up:\"true\"}},transformation:[0.0226625582f,-0.0368899212f,-0.2418181947f,0.2141928968f,-0.0073893535f,0.0568664558f,-0.1677690714f,0.1345217553f,0.0927096626f,0.0135501122f,0.0457397191f,-0.0970913763f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_wall\",Properties:{up:\"true\"}},transformation:[-0.0266164084f,0.2168792347f,0.0101967701f,-0.2003383532f,0.0003776329f,-0.0181457371f,0.1308294515f,0.5201780053f,0.0888481103f,0.0650480768f,0.0024986001f,-0.0826538763f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_wall\",Properties:{up:\"true\"}},transformation:[0.0266182557f,-0.2169889659f,0.0093745485f,0.1980053968f,0.0002103907f,-0.0167826954f,-0.1308909368f,0.6514280053f,0.0888481103f,0.0650480768f,-0.0024986001f,-0.0802476263f,0f,0f,0f,1f]}]}"
        );
        
        private static final double defaultRadius = 0.8;
        private static final int damageDuration = 10;
        
        private final GamePlayer player;
        private final List<DisplayEntity> models;
        
        private double radius;
        
        HellfireShield(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity, double maxCapacity, @Nonnull Callback<Builder> callback) {
            super(entity, maxCapacity, callback);
            
            this.player = player;
            this.models = createModels(3);
            this.radius = defaultRadius;
        }
        
        @Override
        public void onHit(double amount, @Nullable LivingGameEntity damager) {
            super.onHit(amount, damager);
            
            // Create a ring
            if (entity.hasCooldown(cooldown)) {
                return;
            }
            
            entity.startCooldown(cooldown);
            
            final double damage = amount * scorchingRingDamage;
            final Location location = entity.getMidpointLocation();
            
            // Affect
            new TickingGameTask() {
                private final Set<LivingGameEntity> tookDamage = Sets.newHashSet();
                
                @Override
                public void run(int tick) {
                    if (tick > damageDuration) {
                        radius = defaultRadius;
                        cancel();
                        return;
                    }
                    
                    // Calculate new radius
                    radius = defaultRadius + Math.sin(Math.PI * tick / damageDuration);
                    
                    // Go full circle
                    for (double d = 0; d < Math.PI * 2; d += Math.PI * 0.2 / radius * 0.7) {
                        final double x = Math.sin(d) * radius;
                        final double z = Math.cos(d) * radius;
                        
                        LocationHelper.offset(
                                location, x, 0, z, () ->
                                        Collect.nearbyEntities(location, 1, entity -> !tookDamage.contains(entity) && HellfireShield.this.entity.isNotSelfOrTeammate(entity))
                                               .forEach(entity -> {
                                                   entity.damageNoKnockback(damage, entity, DamageCause.SCORCHING_RING);
                                                   entity.addAssistingPlayer(player);
                                                   
                                                   tookDamage.add(entity);
                                               })
                        );
                    }
                }
            }.runTaskTimer(0, 1);
        }
        
        @Override
        public void onRemove(@Nonnull Cause cause) {
            models.forEach(Entity::remove);
            
            entity.playWorldSound(Sound.BLOCK_FIRE_EXTINGUISH, 0.75f);
        }
        
        @Override
        public void tick() {
            super.tick();
            
            // Fx
            final double rad = Math.toRadians(duration * 6);
            final double offset = Math.PI * 2 / models.size();
            
            final Location location = entity.getLocation();
            final Location centre = entity.getLocation().add(0, 0.5, 0);
            
            for (int i = 0; i < models.size(); i++) {
                final double x = Math.sin(rad + offset * i) * radius;
                final double y = Math.sin(rad * i) * 0.1;
                final double z = Math.cos(rad + offset * i) * radius;
                
                final DisplayEntity display = models.get(i);
                
                LocationHelper.offset(
                        location, x, y + 0.5, z, () -> {
                            final Vector vector = Vectors.directionTo(centre, display.getLocation()).multiply(-1);
                            vector.setY(0); // Don't care about pitch
                            location.setDirection(vector);
                            
                            display.teleport(location);
                            
                            // Fx
                            if (duration % 3 == 0) {
                                entity.spawnWorldParticle(location, Particle.LAVA, 1, 0.1, 0.05, 0.1, 0.0f);
                            }
                        }
                );
            }
        }
        
        private List<DisplayEntity> createModels(int amount) {
            final Location location = player.getLocation();
            location.setYaw(0.f);
            location.setPitch(0.f);
            
            final List<DisplayEntity> entities = Lists.newArrayList();
            
            for (int i = 0; i < amount; i++) {
                entities.add(model.spawn(
                        location, self -> {
                            self.setTeleportDuration(1);
                            self.setBillboard(Display.Billboard.FIXED);
                        }
                ));
            }
            
            return entities;
        }
    }
}
