package me.hapyl.fight.game.heroes.nyx;

import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayData;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.eterna.module.util.Tuple;
import me.hapyl.fight.event.custom.AttributeModifyEvent;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.EnumResource;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.talents.ChargeType;
import me.hapyl.fight.game.talents.OverchargeUltimateTalent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.nyx.ChaosGround;
import me.hapyl.fight.game.talents.nyx.NyxPassive;
import me.hapyl.fight.game.talents.nyx.WitherRosePath;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.util.ParticleDrawer;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

public class Nyx extends Hero implements Listener, PlayerDataHandler<NyxData>, UIComponent, ParticleDrawer {
    
    private final PlayerDataMap<NyxData> nyxDataMap = PlayerMap.newDataMap(NyxData::new);
    
    private final Particle.DustTransition dustData = new Particle.DustTransition(
            Color.fromRGB(66, 16, 181),
            Color.fromRGB(153, 62, 163),
            1
    );
    
    public Nyx(@Nonnull Key key) {
        super(key, "Nyx");
        
        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.SUPPORT, Archetype.HEXBANE, Archetype.DEFENSE, Archetype.POWERFUL_ULTIMATE);
        profile.setAffiliation(Affiliation.THE_WITHERS);
        profile.setGender(Gender.FEMALE);
        
        final HeroAttributes attributes = getAttributes();
        attributes.setDefense(80);
        attributes.setAttackSpeed(85);
        
        final HeroEquipment equipment = getEquipment();
        
        equipment.setChestPlate(38, 22, 38, TrimPattern.RAISER, TrimMaterial.NETHERITE);
        equipment.setLeggings(22, 28, 28, TrimPattern.DUNE, TrimMaterial.NETHERITE);
        equipment.setBoots(Material.NETHERITE_BOOTS, TrimPattern.DUNE, TrimMaterial.NETHERITE);
        
        setDescription("""
                       `Chaos... brings victory...`
                       """);
        
        setItem("e4e7d05432c07cbbe6414def96196f434ffc8759a528202463257f42f304670d");
        
        setWeapon(new NyxWeapon());
        setUltimate(new NyxUltimate());
    }
    
    @EventHandler()
    public void handleAttributeChange(AttributeModifyEvent ev) {
        final LivingGameEntity entity = ev.getEntity();
        final LivingGameEntity applier = ev.getApplier();
        
        if (!(applier instanceof GamePlayer playerApplier) || !ev.hasModification(AttributeModifyEvent.ModificationType.DEBUFF)) {
            return;
        }
        
        final GamePlayer nyx = getNyx(playerApplier);
        
        // No nyx on the team
        if (nyx == null) {
            return;
        }
        
        getPassiveTalent().execute(nyx, playerApplier, entity);
        
        // Decrease stack
        getPlayerData(nyx).decrementChaosStacks();
    }
    
    @Override
    public void onStart(@Nonnull GameInstance instance) {
        new TickingGameTask() {
            @Override
            public void run(int tick) {
                nyxDataMap.values().forEach(Ticking::tick);
            }
        }.runTaskTimer(0, 2);
    }
    
    @Nonnull
    @Override
    public PlayerDataMap<NyxData> getDataMap() {
        return nyxDataMap;
    }
    
    @Override
    public WitherRosePath getFirstTalent() {
        return TalentRegistry.WITHER_ROSE_PATH;
    }
    
    @Override
    public ChaosGround getSecondTalent() {
        return TalentRegistry.CHAOS_GROUND;
    }
    
    @Override
    public NyxPassive getPassiveTalent() {
        return TalentRegistry.NYX_PASSIVE;
    }
    
    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
        final NyxData data = getPlayerData(player);
        final int chaosStacks = data.getChaosStacks();
        
        return "&5%s &d%s".formatted(Named.THE_CHAOS.getPrefix(), chaosStacks);
    }
    
    public void drawParticle(@Nonnull Location location) {
        final World world = location.getWorld();
        
        world.spawnParticle(Particle.DUST, location, 1, dustData);
        world.spawnParticle(Particle.WITCH, location, 1);
    }
    
    @Nullable
    private GamePlayer getNyx(@Nonnull GamePlayer player) {
        if (validateNyx(player)) {
            return player;
        }
        
        return player.getTeam().getPlayers()
                     .stream()
                     .filter(this::validateNyx)
                     .findFirst()
                     .orElse(null);
    }
    
    private boolean validateNyx(GamePlayer player) {
        final NyxPassive passive = getPassiveTalent();
        
        return validatePlayer(player)
                && getPlayerData(player).getChaosStacks() > 0
                && player.isAlive() // Yeah, kinda make sure the player is alive
                && !passive.isOnCooldown(player);
    }
    
    public static class NyxTuple<V> extends Tuple<V, V> {
        
        public NyxTuple(@Nonnull V v, @Nonnull V v2) {
            super(v, v2);
        }
        
        @Nonnull
        public String getString(@Nonnull V v) {
            return String.valueOf(v);
        }
        
        @Nonnull
        public final String differenceInPercent() {
            if (a() instanceof Number numA && b() instanceof Number numB) {
                return "&b%.0f%%&7".formatted((1 - numA.doubleValue() / numB.doubleValue()) * 100);
            }
            
            return "&4not applicable";
        }
        
        @Override
        public final String toString() {
            return getString(a()) + "/" + getString(b());
        }
        
        @Nonnull
        public final V value(@Nonnull ChargeType type) {
            return type.value(a(), b());
        }
        
        @Nonnull
        public static <V> NyxTuple<V> of(@Nonnull V a, @Nonnull V b, @Nonnull Function<V, String> toString) {
            return new NyxTuple<>(a, b) {
                @Nonnull
                @Override
                public String getString(@Nonnull V v) {
                    return toString.apply(v);
                }
            };
        }
        
    }
    
    final class NyxUltimate extends OverchargeUltimateTalent {
        
        @DisplayField final NyxTuple<Double> distance = NyxTuple.of(4.0d, 5.5d, String::valueOf);
        @DisplayField final NyxTuple<Double> damage = NyxTuple.of(1.0d, 2.0d, String::valueOf);
        @DisplayField final NyxTuple<Double> energyDecrease = NyxTuple.of(1.0, 2.0d, String::valueOf);
        
        @DisplayField final NyxTuple<Integer> duration = NyxTuple.of(40, 55, v -> Tick.round(v) + "s");
        @DisplayField final NyxTuple<Integer> chaosRegen = NyxTuple.of(2, 3, String::valueOf);
        
        @DisplayField final int hitDelay = 2;
        @DisplayField final double overchargedEnergyDecrease = 40.0d;
        
        @DisplayField final int castDuration = 21; // not using cast duration because void portal handles it differently
        
        public DisplayData spear = BDEngine.parse(
                "/summon block_display ~-0.5 ~ ~-0.5 {Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone_wall\",Properties:{up:\"true\"}},transformation:[0.1875f,0f,0f,-0.095f,0f,3.6875f,0f,0.846875f,0f,0f,0.1875f,-0.101875f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1557317004,-389561592,-1124914678,-822040624],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzRkNDJmOWM0NjFjZWUxOTk3YjY3YmYzNjEwYzY0MTFiZjg1MmI5ZTVkYjYwN2JiZjYyNjUyN2NmYjQyOTEyYyJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,0f,0.1795f,-0.001875f,0f,1f,0f,2.970625f,-0.1875f,0f,0f,-0.0075f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;818785008,-1102424605,-1725003209,1182407917],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzRkNDJmOWM0NjFjZWUxOTk3YjY3YmYzNjEwYzY0MTFiZjg1MmI5ZTVkYjYwN2JiZjYyNjUyN2NmYjQyOTEyYyJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.1875f,0f,0f,-0.001875f,0f,1f,0f,2.44f,0f,0f,-0.1785f,-0.008125f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1172206389,-566905959,-69734431,-891956055],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzRkNDJmOWM0NjFjZWUxOTk3YjY3YmYzNjEwYzY0MTFiZjg1MmI5ZTVkYjYwN2JiZjYyNjUyN2NmYjQyOTEyYyJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.1795f,0f,0f,-0.001875f,0f,1f,0f,1.909375f,0f,0f,0.1885f,-0.01f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-768288044,-88546942,875754297,2074501220],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzRkNDJmOWM0NjFjZWUxOTk3YjY3YmYzNjEwYzY0MTFiZjg1MmI5ZTVkYjYwN2JiZjYyNjUyN2NmYjQyOTEyYyJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.25f,0f,0f,0f,0f,1.3125f,0f,4.110625f,0f,0f,0.1955f,-0.00875f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;498073589,-807031689,902695473,-1566760464],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzRkNDJmOWM0NjFjZWUxOTk3YjY3YmYzNjEwYzY0MTFiZjg1MmI5ZTVkYjYwN2JiZjYyNjUyN2NmYjQyOTEyYyJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.0195990869f,0f,-0.180505724f,0.00125f,0f,1f,0f,3.505625f,0.1864728554f,0f,-0.0189719161f,-0.008125f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;642598747,-223337188,1997633581,1487634616],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzRkNDJmOWM0NjFjZWUxOTk3YjY3YmYzNjEwYzY0MTFiZjg1MmI5ZTVkYjYwN2JiZjYyNjUyN2NmYjQyOTEyYyJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.1675f,0f,0f,0.001875f,0f,1f,0f,4.025f,0f,0f,-0.1795f,-0.0125f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:hopper\",Count:1},item_display:\"none\",transformation:[-0.449f,0f,0f,-0.00625f,0f,-1.943f,0f,4.641875f,0f,0f,0.837f,-0.010625f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:hopper\",Count:1},item_display:\"none\",transformation:[0f,0f,0.837f,-0.00625f,0f,-1.943f,0f,4.641875f,0.449f,0f,0f,-0.010625f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:flint\",Count:1},item_display:\"none\",transformation:[-0.4290488111f,-0.1344776353f,0f,0.048125f,0.085576677f,-0.6742195604f,0f,4.01875f,0f,0f,1f,-0.011875f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:flint\",Count:1},item_display:\"none\",transformation:[0f,0f,-1f,-0.00625f,0.085576677f,-0.6742195604f,0f,4.01875f,-0.4290488111f,-0.1344776353f,0f,0.025f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:flint\",Count:1},item_display:\"none\",transformation:[0.4290488111f,0.1344776353f,0f,-0.063125f,0.085576677f,-0.6742195604f,0f,4.01875f,0f,0f,-1f,-0.013125f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:flint\",Count:1},item_display:\"none\",transformation:[0f,0f,1f,-0.00875f,0.085576677f,-0.6742195604f,0f,4.01875f,0.4290488111f,0.1344776353f,0f,-0.05f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:flint\",Count:1},item_display:\"none\",transformation:[-0.2497120966f,0.0479781285f,0f,0.000625f,-0.0119945321f,-0.9988483865f,0f,0.791875f,0f,0f,1f,-0.013125f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:flint\",Count:1},item_display:\"none\",transformation:[-0.2380989499f,0.2095942056f,0f,0.035625f,-0.0762160748f,-0.6547721123f,0f,0.8325f,0f,0f,0.896f,-0.013125f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:flint\",Count:1},item_display:\"none\",transformation:[0.2443077766f,-0.1458721496f,0f,0.02125f,-0.053044418f,-0.6718463857f,0f,0.46875f,0f,0f,-0.843f,-0.013125f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1562835358,-851674999,-623966372,1423927731],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzRkNDJmOWM0NjFjZWUxOTk3YjY3YmYzNjEwYzY0MTFiZjg1MmI5ZTVkYjYwN2JiZjYyNjUyN2NmYjQyOTEyYyJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.25f,0f,0f,0f,0f,1.3125f,0f,1.44f,0f,0f,0.1885f,-0.00875f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:flint\",Count:1},item_display:\"none\",transformation:[0.1232964856f,0.0822655827f,0f,0.04375f,-0.0205663957f,0.4931859425f,0f,2.183125f,0f,0f,0.5625f,-0.013125f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:flint\",Count:1},item_display:\"none\",transformation:[-0.124392733f,-0.0492256811f,0f,-0.044375f,-0.0123064203f,0.4975709319f,0f,2.77375f,0f,0f,-0.5625f,-0.013125f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;567064690,-1832248891,393517656,-263464660],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzg3YTMyNjMzMWZlMjdmN2VlMDc0Zjk3NzI3NjA0YzQ5NWY5NWMzNzgxMjEzMDJlODc5ZTFmNDBiYTRkMjBhOCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.305897421f,0f,0.3238236721f,-0.005625f,0f,0.506f,0f,4.170625f,-0.2811885805f,0f,0.3522789794f,-0.013125f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:flint\",Count:1},item_display:\"none\",transformation:[1.9e-9f,0f,-0.5625f,0.00375f,-0.0205663957f,0.4931859425f,-1.4e-9f,1.669375f,0.1232964856f,0.0822655827f,8.3e-9f,0.038125f,0f,0f,0f,1f]}]}"
        );
        
        public NyxUltimate() {
            super(Nyx.this, "Impalement", 60, 100);
            
            setDescription("""
                           Summon a &8void portal&7 in front of you.
                           
                           After a short casting time, spears of chaos will rise from the portal, dealing &crapid damage&7 and reducing %s to &cenemies&7 within.
                           """.formatted(EnumResource.ENERGY)
            );
            
            setOverchargeDescription("""
                                     &8› &7Increases the range by %s.
                                     &8› &7Increases the damage by %s.
                                     &8› &7Increases the duration by %s.
                                     
                                     Adds a &cfinal slash&7, that decreases all &cenemies&7 %s by &b%.0f&7.
                                     &8&oThe slash is considered to be an effect.
                                     """.formatted(
                    distance.differenceInPercent(),
                    damage.differenceInPercent(),
                    duration.differenceInPercent(),
                    EnumResource.ENERGY,
                    overchargedEnergyDecrease
            ));
            
            setMaterial(Material.DRIED_KELP);
        }
        
        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player, @Nonnull ChargeType type) {
            return execute(() -> {
                final Location location = BukkitUtils.anchorLocation(
                        player.getLocation().add(player.getDirection().setY(0.0d).multiply(2))
                );
                
                location.setYaw(0.0f);
                location.setPitch(0.0f);
                
                new VoidPortal(player, location, this, type);
            });
        }
        
    }
}
