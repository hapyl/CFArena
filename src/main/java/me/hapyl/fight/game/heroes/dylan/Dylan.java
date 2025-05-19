package me.hapyl.fight.game.heroes.dylan;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.loadout.HotBarSlot;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.dylan.*;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.terminology.EnumTerm;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Map;

public class Dylan extends Hero implements Listener, PlayerDataHandler<DylanData>, UIComponent {
    
    public static final String familiarName = "E'zel";
    
    private final PlayerDataMap<DylanData> playerDataMap = PlayerMap.newDataMap(DylanData::new);
    
    public Dylan(@Nonnull Key key) {
        super(key, "D'lan");
        
        setDescription("""
                       An undead servant of hells and his familiar - %s.
                       """.formatted(familiarName));
        
        setItem("54d91cfcc614fb0723ce6f6b448e6ffd166bd05bce6f8fb286fc939492146cf2");
        
        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.DEFENSE, Archetype.TALENT_DAMAGE, Archetype.POWERFUL_ULTIMATE, Archetype.MELEE, Archetype.STRATEGY);
        profile.setAffiliation(Affiliation.HELL);
        profile.setRace(Race.HUMAN);
        profile.setGender(Gender.MALE);
        
        final HeroEquipment equipment = getEquipment();
        equipment.setChestPlate(59, 47, 47, TrimPattern.TIDE, TrimMaterial.IRON);
        equipment.setLeggings(Material.IRON_LEGGINGS, TrimPattern.TIDE, TrimMaterial.NETHERITE);
        equipment.setBoots(59, 47, 47, TrimPattern.EYE, TrimMaterial.IRON);
        
        setWeapon(Weapon.builder(Material.TALL_DRY_GRASS, Key.ofString("dylan_weapon"))
                        .name("Vox")
                        .description("""
                                     A piece of nether grass, which is surprisingly sharp...
                                     """)
                        .damage(5)
        );
        
        setUltimate(new DylanUltimate());
        
        setEventHandler(new HeroEventHandler(this) {
            @Override
            public void handlePlayerSwapHandItemsEvent(@Nonnull GamePlayer player) {
                final DylanData data = getPlayerData(player);
                
                // If familiar exists and in COMBUST state then explode right away
                if (data.familiar != null && data.familiar.selfDestruct() == DylanFamiliar.SelfDestructState.COMBUST) {
                    data.familiar.selfDestruct(DylanFamiliar.SelfDestructState.SELF_DESTRUCT);
                    data.familiar = null;
                    return;
                }
                
                super.handlePlayerSwapHandItemsEvent(player);
            }
        });
    }
    
    @Nonnull
    @Override
    public DylanUltimate getUltimate() {
        return (DylanUltimate) super.getUltimate();
    }
    
    @Override
    public SummonWhelp getFirstTalent() {
        return TalentRegistry.SUMMON_WHELP;
    }
    
    @Override
    public HellfireWard getSecondTalent() {
        return TalentRegistry.HELLFIRE_WARD;
    }
    
    @Override
    public WhelpAttack getThirdTalent() {
        return TalentRegistry.WHELP_ATTACK;
    }
    
    @Override
    public Blightwhirl getFourthTalent() {
        return TalentRegistry.BLIGHTWHIRL;
    }
    
    @Override
    public DylanPassive getPassiveTalent() {
        return TalentRegistry.DYLAN_PASSIVE;
    }
    
    @Override
    public void onStart(@Nonnull GameInstance instance) {
        new TickingGameTask() {
            @Override
            public void run(int tick) {
                playerDataMap.values().forEach(DylanData::tick);
            }
        }.runTaskTimer(0, 1);
    }
    
    @Override
    public void onStart(@Nonnull GamePlayer player) {
        // Yucky 1 tick delay because execution order ):
        player.schedule(() -> whelpTalents(player, false), 1);
    }
    
    @EventHandler
    public void handlePlayerToggleSneakEvent(PlayerToggleSneakEvent ev) {
        final GamePlayer player = CF.getPlayer(ev);
        
        if (!validatePlayer(player) || !ev.isSneaking()) {
            return;
        }
        
        final DylanData data = getPlayerData(player);
        
        // Didn't take damage
        if (data.rebuke == null) {
            return;
        }
        
        getPassiveTalent().rebuke(data.rebuke);
    }
    
    @Override
    public void processDamageAsVictim(@Nonnull DamageInstance instance) {
        final LivingGameEntity entity = instance.getEntity();
        final LivingGameEntity damager = instance.getDamager();
        
        // Only allow direct damage
        if (damager == null || !instance.getCause().isDirectDamage() || !(entity instanceof GamePlayer player)) {
            return;
        }
        
        final DylanPassive rebuke = getPassiveTalent();
        final DylanData data = getPlayerData(player);
        
        if (data.rebuke != null || rebuke.isOnCooldown(player)) {
            return;
        }
        
        data.allowRebuke(damager, instance);
    }
    
    @Nonnull
    @Override
    public PlayerDataMap<DylanData> getDataMap() {
        return playerDataMap;
    }
    
    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
        final DylanData data = getPlayerData(player);
        
        if (data.familiar == null) {
            return "";
        }
        
        return data.familiar.toString();
    }
    
    public void whelpTalents(@Nonnull GamePlayer player, boolean b) {
        for (Map.Entry<Talent, HotBarSlot> entry : talentsMapped.entrySet()) {
            final Talent talent = entry.getKey();
            final HotBarSlot slot = entry.getValue();
            
            if (!(talent instanceof WhelpTalent whelpTalent)) {
                continue;
            }
            
            whelpTalent.giveItem(player, slot, b);
        }
    }
    
    public class DylanUltimate extends UltimateTalent {
        
        @DisplayField public final double instanceRadius = 6;
        @DisplayField(percentage = true) public final double damageOfHealth = 1.5;
        @DisplayField public final double maxSpreadDistance = 5;
        @DisplayField public final short damageInstances = 10;
        
        @DisplayField private final double rushSpeed = 0.75;
        
        private final double vexScale = 1.75;
        
        public DylanUltimate() {
            super(Dylan.this, "Hellfire Combustion", 60);
            
            setDescription("""
                           Command &3%1$s&7 to &ecombust&7 and rush forward, following your &ecrosshair&7.
                           
                           &3%1$s&7 will &4self-destruct&7 upon hitting an &cenemy&7, dealing multiple instances of %2$s in large AoE based on it's current &c❤ health&7.
                           &8&o;;Press the ultimate key to self-destruct early.
                           """.formatted(familiarName, EnumTerm.TRUE_DAMAGE));
            
            setTexture("8287b397daf9516a0bd76f5f1b7bf979515df3d5d833e0635fa68b37ee082212");
            
            setCastDurationSec(0.75f);
            setDurationSec(3);
            
            setSound(Sound.ENTITY_WITHER_HURT, 1.75f);
        }
        
        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player, boolean isFullyCharged) {
            final DylanData data = getPlayerData(player);
            final DylanFamiliar familiar = data.familiar;
            
            final Response response = WhelpTalent.validateEzel(data);
            
            if (!response.isOk()) {
                return error(response.reason());
            }
            
            // Prepare self destruct
            familiar.selfDestruct(DylanFamiliar.SelfDestructState.PREPARE);
            
            final DylanFamiliar.FamiliarEntity entity = familiar.entity();
            
            return new UltimateInstance() {
                @Override
                public void onCastStart() {
                    // Fx
                    entity.playWorldSound(Sound.ENTITY_BREEZE_INHALE, 0.75f);
                }
                
                @Override
                public void onCastTick(int tick) {
                    final double newScale = 1 + (vexScale - 1) * ((double) tick / castDuration);
                    
                    entity.setAttributeValue(Attribute.SCALE, newScale);
                }
                
                @Override
                public void onExecute() {
                    familiar.selfDestruct(DylanFamiliar.SelfDestructState.COMBUST);
                    
                    // Fx
                    entity.playWorldSound(Sound.ENTITY_BREEZE_DEFLECT, 0.75f);
                }
                
                @Override
                public void onTick(int tick) {
                    // If already self-destructed, then end ultimate
                    if (familiar.selfDestruct() != DylanFamiliar.SelfDestructState.COMBUST) {
                        forceEndUltimate();
                        return;
                    }
                    
                    // Follow crosshair
                    final Vector direction = player.getLocation().getDirection().normalize();
                    direction.multiply(rushSpeed);
                    
                    final Location location = entity.getLocation();
                    location.add(direction);
                    
                    // Collision
                    if (!Collect.nearbyEntities(location, 0.75, player::isNotSelfOrTeammate).isEmpty()) {
                        forceEndUltimate();
                        onEnd();
                        return;
                    }
                    
                    // Vexes can go through blocks, so no need to check for blocks because that's intended and not a bug, yeah..., that!
                    entity.teleport(location);
                    
                    // Display duration
                    final int barAmount = 25;
                    final int durationLeftPercent = (int) ((double) barAmount * tick / duration);
                    
                    player.sendSubtitle("&4\uD83D\uDC7E " + "&c|".repeat(barAmount - durationLeftPercent) + "&8|".repeat(durationLeftPercent), 0, 5, 2);
                }
                
                @Override
                public void onEnd() {
                    familiar.selfDestruct(DylanFamiliar.SelfDestructState.SELF_DESTRUCT);
                    data.familiar = null;
                }
            };
        }
    }
}
