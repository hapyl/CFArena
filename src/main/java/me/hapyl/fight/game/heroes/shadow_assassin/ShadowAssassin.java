package me.hapyl.fight.game.heroes.shadow_assassin;

import me.hapyl.eterna.module.particle.ParticleBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.loadout.HotBarSlot;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.shadow_assassin.DarkCover;
import me.hapyl.fight.game.talents.shadow_assassin.PlayerCloneList;
import me.hapyl.fight.game.talents.shadow_assassin.ShadowAssassinClone;
import me.hapyl.fight.game.talents.shadow_assassin.ShadowSwitch;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ShadowAssassin extends Hero implements Listener, UIComponent, PlayerDataHandler<ShadowAssassinData> {
    
    public final HeroEquipment furyEquipment = new HeroEquipment();
    
    public final ModifierSource modifierSource = new ModifierSource(Key.ofString("shadow_mode"), true);
    public final double attackIncrease = 0.4285714285714286d; // Mimics the old 30 flat attack
    
    private final PlayerDataMap<ShadowAssassinData> playerData = PlayerMap.newDataMap(player -> new ShadowAssassinData(player, this));
    
    public ShadowAssassin(@Nonnull Key key) {
        super(key, "Shadow Assassin");
        
        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.DAMAGE, Archetype.STRATEGY, Archetype.MELEE, Archetype.SELF_BUFF, Archetype.POWERFUL_ULTIMATE);
        profile.setGender(Gender.UNKNOWN);
        profile.setRace(Race.UNKNOWN);
        
        setDescription("""
                       An assassin with anger management issues from dimension of shadows.
                       
                       Capable of switching between being Stealthy and Furious.
                       """);
        setItem("d7fcfa5b0af855f314606a5cd2b597475286a152d1ee08d9949a6386cbc46a8e");
        
        final HeroAttributes attributes = getAttributes();
        attributes.setAttack(70);
        attributes.setSpeed(120);
        
        final HeroEquipment equipment = getEquipment();
        equipment.setChestPlate(14, 23, 41);
        equipment.setLeggings(7, 12, 23);
        equipment.setBoots(Color.BLACK);
        
        furyEquipment.setTexture("2bbc217afc3a10cb268fec0426ccdee2b83906a8162d69f8c6d065b5aebc119c");
        furyEquipment.setChestPlate(54, 22, 22);
        furyEquipment.setLeggings(28, 10, 10);
        furyEquipment.setBoots(Color.BLACK);
        
        setWeapon(new ShadowAssassinWeapon(this));
        setUltimate(new ShadowAssassinUltimate());
    }
    
    @Nonnull
    public ShadowAssassinData getData(@Nonnull GamePlayer player) {
        return playerData.computeIfAbsent(player, fn -> new ShadowAssassinData(player, this));
    }
    
    @Nonnull
    @Override
    public ShadowAssassinUltimate getUltimate() {
        return (ShadowAssassinUltimate) super.getUltimate();
    }
    
    @EventHandler()
    public void handleUltimate(PlayerInteractEvent ev) {
        final GamePlayer player = CF.getPlayer(ev.getPlayer());
        final ShadowAssassinWeapon weapon = getWeapon();
        
        if (ev.getHand() == EquipmentSlot.OFF_HAND
                || ev.getAction() == Action.PHYSICAL
                || !validatePlayer(player)
                || !player.isUsingUltimate()
                || player.cooldownManager.hasCooldown(weapon)) {
            return;
        }
        
        final LivingGameEntity livingEntity = getNearestEntity(player);
        
        if (livingEntity == null) {
            player.sendMessage("&cNo valid opponent!");
            player.playSound(Sound.ENTITY_SILVERFISH_AMBIENT, 0.0f);
            return;
        }
        
        final ShadowAssassinUltimate ultimate = getUltimate();
        
        livingEntity.damage(weapon.getDamage(), player, DamageCause.NEVERMISS);
        player.cooldownManager.setCooldown(weapon, ultimate.hitCooldown);
        
        // Fx
        player.playWorldSound(Sound.BLOCK_NETHER_ORE_BREAK, 1.75f);
    }
    
    @Override
    public void onStart(@Nonnull GameInstance instance) {
        new GameTask() {
            @Override
            public void run() {
                getAlivePlayers().forEach(player -> {
                    if (!player.hasEffect(EffectType.INVISIBLE)) {
                        return;
                    }
                    
                    player.spawnWorldParticle(Particle.ENCHANT, 5, 0.5, 1, 0.5, 1f);
                });
            }
        }.runTaskTimer(0, 5);
    }
    
    @Override
    public boolean processInvisibilityDamage(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity, double damage) {
        getSecondTalent().onDamage(player, entity, damage);
        return false;
    }
    
    @Override
    public void processDamageAsVictim(@Nonnull DamageInstance instance) {
        final GamePlayer player = instance.getEntityAsPlayer();
        final DarkCover darkCover = getSecondTalent();
        
        if (darkCover.isInDarkCover(player)) {
            instance.setCancelled(true);
        }
    }
    
    @Override
    public void processDamageAsDamager(@Nonnull DamageInstance instance) {
        final GamePlayer player = instance.getDamagerAsPlayer();
        
        if (player == null || !instance.isDirectDamage()) {
            return;
        }
        
        final LivingGameEntity entity = instance.getEntity();
        
        if (!validateCanBackStab(player, entity)) {
            return;
        }
        
        final Vector playerDirection = player.getLocation().getDirection();
        final Vector entityDirection = entity.getLocation().getDirection();
        
        if (playerDirection.dot(entityDirection) > 0) {
            getWeapon().performBackStab(player, entity);
        }
    }
    
    public void displayFootprints(Location location) {
        ParticleBuilder
                .blockDust(location.getBlock().getRelative(BlockFace.DOWN).getType())
                .display(location, 3, 0.25d, 0.0d, 0.25d, 1.0f);
    }
    
    @Nonnull
    @Override
    public ShadowAssassinWeapon getWeapon() {
        return (ShadowAssassinWeapon) super.getWeapon();
    }
    
    @EventHandler()
    public void handlePlayerSneakEvent(PlayerToggleSneakEvent ev) {
        final GamePlayer player = CF.getPlayer(ev.getPlayer());
        
        if (!validatePlayer(player)) {
            return;
        }
        
        final ShadowAssassinClone talent = getThirdTalent();
        final PlayerCloneList playerClones = talent.getPlayerClones(player);
        
        playerClones.linkToClone();
    }
    
    @Override
    public ShadowSwitch getFirstTalent() {
        return TalentRegistry.SHADOW_SWITCH;
    }
    
    @Override
    public DarkCover getSecondTalent() {
        return TalentRegistry.DARK_COVER;
    }
    
    @Override
    public ShadowAssassinClone getThirdTalent() {
        return TalentRegistry.SHADOW_ASSASSIN_CLONE;
    }
    
    @Override
    public Talent getPassiveTalent() {
        return TalentRegistry.SHADOW_ENERGY;
    }
    
    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
        final int energy = getData(player).getEnergy();
        return ChatColor.DARK_PURPLE + Named.SHADOW_ENERGY.getPrefix() + energy +
                (energy == ShadowAssassinData.MAX_ENERGY ? " &lMAX!" : "");
    }
    
    @Nonnull
    @Override
    public PlayerDataMap<ShadowAssassinData> getDataMap() {
        return playerData;
    }
    
    @Nullable
    private LivingGameEntity getNearestEntity(GamePlayer player) {
        return Collect.targetEntityDot(player, getUltimate().maxDistance, 0.5d, t -> !player.isSelfOrTeammate(t) && t.hasLineOfSight(player));
    }
    
    private boolean validateCanBackStab(GamePlayer player, LivingGameEntity entity) {
        return entity != null
                && !player.isUsingUltimate()
                && player != entity
                && !player.cooldownManager.hasCooldown(getWeapon()) && player.isHeldSlot(HotBarSlot.WEAPON);
    }
    
    public class ShadowAssassinUltimate extends UltimateTalent {
        
        @DisplayField private final int hitCooldown = 20;
        @DisplayField(suffix = " blocks") private final double maxDistance = 10;
        
        private ShadowAssassinUltimate() {
            super(ShadowAssassin.this, "Extreme Focus", 80);
            
            setDescription("""
                           Enter {name} for {duration}.
                           
                           While active, your &amelee&7 attacks will &nnot&7 miss if an &cenemy&7 is within your line of sight.
                           
                           You cannot perform &eShadow Stab&7 while {name} is active.
                           """
            );
            
            setType(TalentType.ENHANCE);
            setMaterial(Material.GOLDEN_CARROT);
            setDurationSec(10);
            setCooldownSec(40);
        }
        
        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player, boolean isFullyCharged) {
            return builder()
                    .onExecute(() -> {
                        // FIXME (hapyl): 001, Mar 1: Change for reach attribute if it's 1.21 or whatever the fuck it is
                        player.cooldownManager.setCooldown(getWeapon(), 0);
                        
                        // Fx
                        player.playWorldSound(Sound.BLOCK_BEACON_ACTIVATE, 1.75f);
                        player.playWorldSound(Sound.BLOCK_BEACON_AMBIENT, 1.75f);
                    })
                    .onEnd(() -> {
                        player.playWorldSound(Sound.BLOCK_BEACON_DEACTIVATE, 1.85f);
                    });
        }
    }
}
