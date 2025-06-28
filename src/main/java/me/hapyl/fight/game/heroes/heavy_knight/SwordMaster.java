package me.hapyl.fight.game.heroes.heavy_knight;

import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayData;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.loadout.HotBarSlot;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.heavy_knight.Slash;
import me.hapyl.fight.game.talents.heavy_knight.SwordMasterPassive;
import me.hapyl.fight.game.talents.heavy_knight.Updraft;
import me.hapyl.fight.game.talents.heavy_knight.Uppercut;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class SwordMaster extends Hero implements PlayerDataHandler<SwordMasterData> {
    
    private final PlayerDataMap<SwordMasterData> playerData = PlayerMap.newDataMap(player -> new SwordMasterData(this, player));
    
    public SwordMaster(@Nonnull Key key) {
        super(key, "Heavy Knight");
        
        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.DAMAGE, Archetype.TALENT_DAMAGE, Archetype.HEXBANE);
        profile.setAffiliation(Affiliation.KINGDOM);
        profile.setGender(Gender.MALE);
        
        setDescription("""
                       A royal knight with heavy armor used to fight the toughest beast and demons alike.
                       """);
        
        setItem("453f413a50bfc9d0d1817a5a67147b5f13436c2cbe86766c5f5f685285debd3a");
        
        final HeroAttributes attributes = getAttributes();
        attributes.setDefense(150);
        attributes.setSpeed(75); // 60 >> 75 = 0.25
        attributes.setAttackSpeed(50);
        
        final HeroEquipment equipment = getEquipment();
        equipment.setChestPlate(Material.NETHERITE_CHESTPLATE, TrimPattern.SHAPER, TrimMaterial.NETHERITE);
        equipment.setLeggings(Material.NETHERITE_LEGGINGS, TrimPattern.SILENCE, TrimMaterial.IRON);
        equipment.setBoots(Material.NETHERITE_BOOTS, TrimPattern.SILENCE, TrimMaterial.NETHERITE);
        
        setWeapon(new SwordMasterWeapon());
        setUltimate(new SwordMasterUltimate());
    }
    
    @Override
    public Uppercut getFirstTalent() {
        return TalentRegistry.UPPERCUT;
    }
    
    @Override
    public Updraft getSecondTalent() {
        return TalentRegistry.UPDRAFT;
    }
    
    @Override
    public Slash getThirdTalent() {
        return TalentRegistry.SLASH;
    }
    
    @Override
    public SwordMasterPassive getPassiveTalent() {
        return TalentRegistry.SWORD_MASTER_PASSIVE;
    }
    
    @Nonnull
    @Override
    public SwordMasterWeapon getWeapon() {
        return (SwordMasterWeapon) super.getWeapon();
    }
    
    @Nonnull
    @Override
    public PlayerDataMap<SwordMasterData> getDataMap() {
        return playerData;
    }
    
    public boolean addSuccessfulTalent(@Nonnull GamePlayer player, @Nonnull Talent talent) {
        return getPlayerData(player).buffer.offer(talent);
    }
    
    public void empowerWeapon(@Nonnull GamePlayer player) {
        getPlayerData(player).empowerWeapon();
    }
    
    private class SwordMasterUltimate extends UltimateTalent {
        
        private final DisplayData model = BDEngine.parse(
                "/summon block_display ~-0.5 ~ ~-0.5 {Passengers:[{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;569433531,1988961603,-257181581,1504653432],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWIxMzk1ZGIxNmJjMmVjMDIzMTIyZGE2ZmRkNGFjYzFkOGIyNDg0MjNlZTI3MTA1NTBlMDEzYTA5ZDQyOWM5MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.2559726548f,0f,0.2559726548f,0.2609854729f,-0.2556191014f,-0.8660254038f,0.2556191014f,-1.2482467012f,0.442745271f,-0.5f,-0.442745271f,-0.6805209252f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1292501586,1503627660,42103438,1707656210],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWIxMzk1ZGIxNmJjMmVjMDIzMTIyZGE2ZmRkNGFjYzFkOGIyNDg0MjNlZTI3MTA1NTBlMDEzYTA5ZDQyOWM5MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.2559726548f,0f,-0.2559726548f,0.2609854729f,0.2556191014f,-0.8660254038f,0.2556191014f,-1.6812594031f,-0.442745271f,-0.5f,-0.442745271f,-0.9305209252f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-548979353,-570627308,1129037930,1421801602],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWIxMzk1ZGIxNmJjMmVjMDIzMTIyZGE2ZmRkNGFjYzFkOGIyNDg0MjNlZTI3MTA1NTBlMDEzYTA5ZDQyOWM5MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.2559726548f,0f,0.2559726548f,0.2609854729f,-0.2556191014f,-0.8660254038f,0.2556191014f,-2.114272105f,0.442745271f,-0.5f,-0.442745271f,-1.1805209252f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;237912521,17618509,-1899136195,-1487034651],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWIxMzk1ZGIxNmJjMmVjMDIzMTIyZGE2ZmRkNGFjYzFkOGIyNDg0MjNlZTI3MTA1NTBlMDEzYTA5ZDQyOWM5MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.2559726548f,0f,0.2559726548f,0.2609854729f,-0.2556191014f,-0.8660254038f,-0.2556191014f,-2.5472848069f,0.442745271f,-0.5f,0.442745271f,-1.4305209252f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;434392480,1345503187,-2013909768,-1095869635],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWIxMzk1ZGIxNmJjMmVjMDIzMTIyZGE2ZmRkNGFjYzFkOGIyNDg0MjNlZTI3MTA1NTBlMDEzYTA5ZDQyOWM5MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.2559726548f,0f,0.2559726548f,0.2609854729f,-0.2556191014f,-0.8660254038f,0.2556191014f,-2.9802975088f,0.442745271f,-0.5f,-0.442745271f,-1.6805209252f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-741907683,-1459525903,-470167364,937720342],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E3YTExMjA4ZWRhNzQzMzFlNDZjODQ0M2Q3MGY5ZjM1MGMzNGI4YzI1MTk1MjFjOTc4YzVjYzU3Y2ZhNGYyNCJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.2982929956f,0f,0.2952595076f,0.2614123837f,-0.2085965005f,-0.549060106f,-0.2064751801f,-0.778210976f,0.3612997371f,-0.317f,0.3576255024f,-0.4094918605f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-856380195,1643825647,2128365470,-1584427887],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E3YTExMjA4ZWRhNzQzMzFlNDZjODQ0M2Q3MGY5ZjM1MGMzNGI4YzI1MTk1MjFjOTc4YzVjYzU3Y2ZhNGYyNCJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.2128497477f,0f,0.2108274223f,0.261496875f,0.1488459774f,0.4117950795f,0.1474317639f,-0.3574326905f,-0.2578087954f,0.23775f,-0.2553593057f,-0.1666918547f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;727262,-1391296254,-1170193277,247079235],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjk4MGZlNzYyZjQ4ODYxMzk3ZjBjOGRjMmY4ZDEzZjdjMTY2MTcwMDM4ZTk3MzAyMDY4OTE5MmE4OTMwOGYzZCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.2209708691f,0f,0.2209708691f,0.2611407742f,-0.2672863633f,-0.8660254038f,0.2672863633f,-1.2582984683f,0.4629535614f,-0.5f,-0.4629535614f,-0.6862565795f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;782842326,1270873871,-2046788928,1113430349],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjk4MGZlNzYyZjQ4ODYxMzk3ZjBjOGRjMmY4ZDEzZjdjMTY2MTcwMDM4ZTk3MzAyMDY4OTE5MmE4OTMwOGYzZCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.2209708691f,0f,0.2209708691f,0.2611407742f,-0.2672863633f,-0.8660254038f,0.2672863633f,-1.6913111702f,0.4629535614f,-0.5f,-0.4629535614f,-0.9362565795f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;126873606,-643065310,1553270655,359159670],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjk4MGZlNzYyZjQ4ODYxMzk3ZjBjOGRjMmY4ZDEzZjdjMTY2MTcwMDM4ZTk3MzAyMDY4OTE5MmE4OTMwOGYzZCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.2209708691f,0f,0.2209708691f,0.2611407742f,-0.2672863633f,-0.8660254038f,0.2672863633f,-2.1243238721f,0.4629535614f,-0.5f,-0.4629535614f,-1.1862565795f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1826298729,1576314949,-1782349690,-657344558],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjk4MGZlNzYyZjQ4ODYxMzk3ZjBjOGRjMmY4ZDEzZjdjMTY2MTcwMDM4ZTk3MzAyMDY4OTE5MmE4OTMwOGYzZCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.2209708691f,0f,0.2209708691f,0.2611407742f,-0.2672863633f,-0.8660254038f,0.2672863633f,-2.557336574f,0.4629535614f,-0.5f,-0.4629535614f,-1.4362565795f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-541740310,666590699,2086661133,-654935579],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjk4MGZlNzYyZjQ4ODYxMzk3ZjBjOGRjMmY4ZDEzZjdjMTY2MTcwMDM4ZTk3MzAyMDY4OTE5MmE4OTMwOGYzZCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.2209708691f,0f,0.2209708691f,0.2611407742f,-0.2672863633f,-0.8660254038f,0.2672863633f,-2.9903492759f,0.4629535614f,-0.5f,-0.4629535614f,-1.6862565795f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;411524349,-1312922487,1131480993,1410608319],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzFiMTg0NzY4ZDZmMTJlMzVhOWIyZTlkMmU5NmUxMTY2ODZiZmJmMGQ5MmZhYmJkNjJjODM1N2JhMjA1OTI3In19fQ==\"}]}}},item_display:\"none\",transformation:[0.1767766953f,0f,0.1767766953f,0.2614126194f,0.0883883476f,0.2706329387f,-0.0883883476f,-0.02789733f,-0.1530931089f,0.15625f,0.1530931089f,0.0243528796f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:iron_bars\",Properties:{east:\"false\",north:\"false\",south:\"false\",west:\"false\"}},transformation:[-0.4765899705f,0f,0.4716402231f,0.26375f,-0.2382949853f,-0.5945264397f,-0.2358201115f,0.16875f,0.4127390217f,-0.34325f,0.4084524146f,-0.410625f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1447170649,1455463372,1885873179,265222040],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY3OWVmYzU5ZTIyZmNmMzRmNzQ0OGJmN2FiNjY2NGY3OTljM2RmZjY1NmNmNDgzMDk4YmUzNmM5YWUxIn19fQ==\"}]}}},item_display:\"none\",transformation:[0.5f,0f,0f,0.26125f,0f,-0.1623797632f,0.5f,-0.839375f,0f,-0.09375f,-0.8660254038f,-0.445f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-36606703,-308046168,-136104975,675731982],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY3OWVmYzU5ZTIyZmNmMzRmNzQ0OGJmN2FiNjY2NGY3OTljM2RmZjY1NmNmNDgzMDk4YmUzNmM5YWUxIn19fQ==\"}]}}},item_display:\"none\",transformation:[0.438f,0f,0f,0.26125f,0f,-0.1623797632f,0.454f,-0.81875f,0f,-0.09375f,-0.7863510666f,-0.433125f,0f,0f,0f,1f]}]}"
        );
        
        @DisplayField private final double dashStrength = 0.6;
        
        @DisplayField private final int dazeDuration = Tick.fromSeconds(8);
        @DisplayField private final short dazeAmplifier = 2;
        
        @DisplayField private final double knockStrength = 0.375;
        
        public SwordMasterUltimate() {
            super(SwordMaster.this, "Siegebreaker", 60);
            
            setDescription("""
                           Summon a &emassive sword&7, sundering the ground with unstoppable force.
                           
                           After a short delay, &bdash&7 forward, knocking and applying %s to hit &cenemies&7.
                           &8&oYou are invulnerable while dashing.
                           """.formatted(EffectType.DAZE)
            );
            
            setMaterial(Material.GOLDEN_SWORD);
            setType(TalentType.IMPAIR);
            
            setCastDurationSec(0.75f);
            setDurationSec(0.8f);
        }
        
        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player, boolean isFullyCharged) {
            return new UltimateInstance() {
                private final DisplayEntity sword = model.spawnInterpolated(swordLocation());
                
                @Override
                public void onCastStart() {
                    player.setInvulnerable(true);
                    player.addEffect(EffectType.MOVEMENT_CONTAINMENT, duration + 5);
                    
                    // Fx
                    player.playWorldSound(Sound.ENTITY_IRON_GOLEM_HURT, 0.75f);
                    player.snapTo(HotBarSlot.HERO_ITEM);
                }
                
                @Override
                public void onExecute() {
                    // Modify players step height so we can properly "dash"
                    player.setAttributeValue(Attribute.STEP_HEIGHT, 1.2);
                    
                    // Fx
                    player.playWorldSound(Sound.BLOCK_GRINDSTONE_USE, 0.0f);
                }
                
                @Override
                public void onTick(int tick) {
                    final Vector direction = player.getDirection();
                    
                    direction.multiply(dashStrength);
                    direction.setY(-1);
                    
                    player.setVelocity(direction);
                    
                    // Collision
                    final Location location = player.getLocation();
                    
                    Collect.nearbyEntities(location, 2.25, player::isNotSelfOrTeammateOrHasEffectResistance)
                           .forEach(entity -> {
                               entity.addEffect(EffectType.DAZE, dazeAmplifier, dazeDuration);
                               
                               // Knock back
                               final Location entityLocation = entity.getLocation();
                               final Vector vector = entityLocation.toVector().subtract(location.toVector()).normalize().multiply(knockStrength * entityLocation.distanceSquared(location));
                               vector.setY(0.725);
                               
                               entity.setVelocity(vector);
                               
                               // Fx
                               entity.playWorldSound(Sound.ENTITY_IRON_GOLEM_STEP, 0.0f);
                               entity.playWorldSound(Sound.ENTITY_IRON_GOLEM_DAMAGE, 1.25f);
                           });
                    
                    // Sync sword
                    sword.teleport(swordLocation());
                    player.snapTo(HotBarSlot.HERO_ITEM);
                }
                
                @Override
                public void onEnd() {
                    sword.remove();
                    
                    // Cleanup
                    player.setInvulnerable(false);
                    player.resetAttributeValue(Attribute.STEP_HEIGHT);
                    player.snapToWeapon();
                }
                
                private Location swordLocation() {
                    final Location location = player.getLocation();
                    location.add(0, 1, 0);
                    location.setPitch(0);
                    
                    final Vector vector = LocationHelper.getVectorToTheRight(location);
                    vector.setY(0);
                    vector.multiply(0.6);
                    
                    location.add(vector);
                    return location;
                }
            };
        }
    }
}
