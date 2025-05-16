package me.hapyl.fight.game.heroes.bloodfield;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.google.common.collect.Sets;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.entity.EntityUtils;
import me.hapyl.eterna.module.player.PlayerSkin;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.event.custom.TalentUseEvent;
import me.hapyl.fight.fx.EntityFollowingParticle;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.bloodfiend.TwinClaws;
import me.hapyl.fight.game.talents.bloodfiend.candlebane.CandlebaneTalent;
import me.hapyl.fight.game.talents.bloodfiend.chalice.BloodChaliceTalent;
import me.hapyl.fight.game.talents.bloodfiend.taunt.Taunt;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComplexComponent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Bat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public class Bloodfiend extends Hero implements PlayerDataHandler<BloodfiendData>, Listener, UIComplexComponent {
    
    private final PlayerDataMap<BloodfiendData> playerData = PlayerMap.newDataMap(player -> new BloodfiendData(this, player));
    
    public Bloodfiend(@Nonnull Key key) {
        super(key, "Bloodfiend");
        
        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.DAMAGE, Archetype.SELF_SUSTAIN, Archetype.TALENT_DAMAGE, Archetype.POWERFUL_ULTIMATE);
        profile.setAffiliation(Affiliation.CHATEAU);
        profile.setGender(Gender.MALE);
        profile.setRace(Race.VAMPIRE);
        
        setDescription("""
                       A vampire prince with a sunscreen.
                       """);
        
        setItem("5aa29ea961757dc3c90bfabf302c5abe9d308fb4a7d3864e5788ad2cc9160aa2");
        setSkin(new PlayerSkin(
                "ewogICJ0aW1lc3RhbXAiIDogMTY2MTQwMDI0MTc2NiwKICAicHJvZmlsZUlkIiA6ICI4YTg3NGJhNmFiZDM0ZTc5OTljOWM1ODMwYWYyY2NmNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJSZXphMTExIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzVhYTI5ZWE5NjE3NTdkYzNjOTBiZmFiZjMwMmM1YWJlOWQzMDhmYjRhN2QzODY0ZTU3ODhhZDJjYzkxNjBhYTIiCiAgICB9CiAgfQp9",
                "u7oD4NYj9J7UzMV/GZ3oScp3E6ci7+YI3DsDlTzVfsHKB5yWNEyZPttL09dMDWyJY1kdC8hsK8i5xhF5BaC/2pj/f3SNndzkrflEYrwUr8/1GVXpejIEVpb+SNqImpjsxWY3bLVQHaQ47WjMzvfrQ/gaEMKp3vDmjqST4gWKPxyk6hEHAudA1evE95QSjKX+ayMc822WQPOlPsqcFIZ/f/HYivYl9FQ4HbSyRfK2iI3Ibb0Mwg7BDcJuvkxdnIpkwBz1Hu3SH77dcpXZtLvIBc7dy41zJOMhUzyqkFFVrid5GvgTgb2o+iJ9mSNfVxN9khpG2q15lofdfIseijpq3QP2rAhdl3uX7DqT/CzOzfXP/9FGQaGuYySkNRlbt1WLfWJN9sHWK/jyz1nhV+JwJvwg/uV4Cor9q1jr01cv/FsWIUwSHLnXndIOEileCKnqlo6G/FtTU4Rgd1C5CryBUhY1WMc+HPk38wmWo6HzNOlhT1HltiPjb4kpSUP+vz5LTtplOqwomw/XBp/wuXuS2ijzCVo6lovtUzra5lsGa9EijHPreXt2dEHy68bTZBt2Os4BeWCMTz58d4wvSvC/hHNXdd/asx1CcW288HFxWRxoNLLawanDILCZLdRln4MwlGP1IruOuK0wJOkP3kxqHJdCL51psBPWDpPTzW0VC9c="
        ));
        
        final HeroAttributes attributes = getAttributes();
        attributes.setMaxHealth(80);
        attributes.setAttackSpeed(150);
        
        final HeroEquipment equipment = getEquipment();
        
        equipment.setChestPlate(99, 8, 16, TrimPattern.SILENCE, TrimMaterial.NETHERITE);
        equipment.setLeggings(28, 3, 7);
        equipment.setBoots(5, 3, 23, TrimPattern.HOST, TrimMaterial.NETHERITE);
        
        setWeapon(Weapon.createBuilder(Material.GHAST_TEAR, Key.ofString("vampires_fang_bloodfiend"))
                        .name("Vampire's Fang")
                        .description("""
                                     A sharp fang.
                                     """)
                        .damage(5.0d)
                        .damageCause(DamageCause.VAMPIRE_BITE)
        );
        
        setUltimate(new BloodfiendUltimate());
    }
    
    @Nonnull
    @Override
    public BloodfiendUltimate getUltimate() {
        return (BloodfiendUltimate) super.getUltimate();
    }
    
    @Override
    public void onStart(@Nonnull GameInstance instance) {
        new GameTask() {
            @Override
            public void run() {
                playerData.forEach((player, data) -> {
                    data.tick();
                });
            }
        }.runTaskTimer(1, 1);
    }
    
    @Override
    public void onRespawn(@Nonnull GamePlayer player) {
        onPlayersRevealed(player);
    }
    
    @Override
    public void onStart(@Nonnull GamePlayer player) {
        super.onStart(player);
    }
    
    @EventHandler()
    public void handleTalentUse(TalentUseEvent ev) {
        final GamePlayer player = ev.getPlayer();
        
        workImpel(
                player, (impel, gp) -> {
                    impel.complete(player, ImpelType.USE_ABILITY);
                }
        );
    }
    
    @Override
    public void processDamageAsDamager(@Nonnull DamageInstance instance) {
        final GamePlayer player = instance.getDamagerAsPlayer();
        final LivingGameEntity entity = instance.getEntity();
        final DamageCause cause = instance.getCause();
        
        if (player == null || cause != DamageCause.VAMPIRE_BITE) {
            return;
        }
        
        final BloodfiendData data = getPlayerData(player);
        
        data.addSucculence(entity);
        
        // Blood Chalice
        final BloodChaliceTalent chalice = getThirdTalent();
        final Taunt taunt = chalice.getTaunt(player);
        
        if (taunt == null) {
            return;
        }
        
        if (taunt.isSuckedEntityAndWithinRange(entity)) {
            final double damage = instance.getDamage();
            final double healing = damage * chalice.healingPercent;
            
            player.heal(healing);
        }
    }
    
    @Override
    public TwinClaws getFirstTalent() {
        return TalentRegistry.TWIN_CLAWS;
    }
    
    @Override
    public CandlebaneTalent getSecondTalent() {
        return TalentRegistry.CANDLEBANE;
    }
    
    @Override
    public BloodChaliceTalent getThirdTalent() {
        return TalentRegistry.BLOOD_CHALICE;
    }
    
    @Override
    public Talent getFourthTalent() {
        return TalentRegistry.SPECTRAL_FORM;
    }
    
    @Override
    public Talent getPassiveTalent() {
        return TalentRegistry.SUCCULENCE;
    }
    
    // impel handles
    @EventHandler()
    public void handleImpelClick(PlayerInteractEvent ev) {
        final GamePlayer player = CF.getPlayer(ev.getPlayer());
        
        if (player == null) {
            return;
        }
        
        final float pitch = player.getLocation().getPitch();
        
        workImpel(
                player, (impel, gamePlayer) -> {
                    if (pitch <= -50) {
                        impel.complete(gamePlayer, ImpelType.CLICK_UP);
                    }
                    else if (pitch >= 40) {
                        impel.complete(gamePlayer, ImpelType.CLICK_DOWN);
                    }
                }
        );
        
    }
    
    @EventHandler()
    public void handleImpelSneak(PlayerToggleSneakEvent ev) {
        final GamePlayer player = CF.getPlayer(ev.getPlayer());
        
        if (player == null) {
            return;
        }
        
        workImpel(
                player, (impel, gamePlayer) -> {
                    impel.complete(gamePlayer, ImpelType.SNEAK);
                }
        );
    }
    
    @EventHandler()
    public void handleImpelJump(PlayerJumpEvent ev) {
        final GamePlayer player = CF.getPlayer(ev.getPlayer());
        
        if (player == null) {
            return;
        }
        
        workImpel(
                player, (impel, gamePlayer) -> impel.complete(gamePlayer, ImpelType.JUMP)
        );
    }
    
    @Nullable
    public ImpelInstance getPlayerImpel(GamePlayer player) {
        for (BloodfiendData data : playerData.values()) {
            final ImpelInstance impel = data.getImpelInstance();
            
            if (impel == null || !impel.isPlayer(player)) {
                continue;
            }
            
            return impel;
        }
        
        return null;
    }
    
    public void workImpel(GamePlayer player, BiConsumer<Impel, GamePlayer> consumer) {
        if (player == null) {
            return;
        }
        
        final ImpelInstance impelInstance = getPlayerImpel(player);
        
        if (impelInstance == null) {
            return;
        }
        
        final Impel impel = impelInstance.getImpel();
        
        if (impel == null) {
            return;
        }
        
        consumer.accept(impel, player);
    }
    
    @EventHandler()
    public void handleFlight(PlayerToggleFlightEvent ev) {
        final GamePlayer player = CF.getPlayer(ev.getPlayer());
        
        if (!validatePlayer(player)) {
            return;
        }
        
        // Don't allow changing flight
        ev.setCancelled(true);
    }
    
    @Nullable
    @Override
    public List<String> getStrings(@Nonnull GamePlayer player) {
        final BloodfiendData data = getPlayerData(player);
        final int succulencePlayers = data.getSuckedCount();
        
        final CandlebaneTalent twinClaws = getSecondTalent();
        final BloodChaliceTalent bloodChalice = getThirdTalent();
        
        final Taunt pillar = twinClaws.getTaunt(player);
        final Taunt chalice = bloodChalice.getTaunt(player);
        
        return List.of(
                succulencePlayers > 0 ? "&c&l🦇 &f" + succulencePlayers : "",
                pillar != null ? "&6&lⅡ &f" + CFUtils.formatTick(pillar.getTimeLeft()) : "",
                chalice != null ? "&4&l🍷 &f" + CFUtils.formatTick(chalice.getTimeLeft()) : ""
        );
    }
    
    @Override
    public boolean processInvisibilityDamage(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity, double damage) {
        if (player.isUsingUltimate()) {
            return false;
        }
        
        return super.processInvisibilityDamage(player, entity, damage);
    }
    
    @Nonnull
    @Override
    public PlayerDataMap<BloodfiendData> getDataMap() {
        return playerData;
    }
    
    public class BloodfiendUltimate extends UltimateTalent {
        
        @DisplayField public final short impelTimes = 3;
        @DisplayField public final double impelNonPlayerDamage = 50;
        @DisplayField public final double impelDamage = 25;
        
        @DisplayField public final int impelDuration = 30;
        @DisplayField public final int impelCd = 15;
        
        public BloodfiendUltimate() {
            super(Bloodfiend.this, "Impel", 50);
            
            setDescription("""
                           After a short casting time, impel all &cbitten &cenemies&7 for {duration}.
                           &8&o;;While casting, transform into a bat and fly freely.
                           
                           While impelled, &nplayers &nmust&7 obey &b&l%s &7of your commands.
                           &8&oEntities other than players take damage {impelNonPlayerDamage} damage.
                           
                           If &4failed&7 to obey a command, they suffer &c{impelDamage} ❤&7 damage.
                           """.formatted(CFUtils.toWord(impelTimes)));
            
            setType(TalentType.IMPAIR);
            setMaterial(Material.LEAD);
            
            setDuration(impelDuration * impelTimes);
            setCastDuration(30);
        }
        
        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player, boolean isFullyCharged) {
            final BloodfiendData data = getPlayerData(player);
            final Set<LivingGameEntity> suckedEntities = data.getSuckedEntities();
            final Location location = player.getLocation().add(0.0d, 0.5d, 0.0d);
            
            // Draw particles
            suckedEntities.forEach(entity -> new EntityFollowingParticle(2, location, entity) {
                @Override
                public void draw(int tick, @Nonnull Location location) {
                    player.spawnWorldParticle(location, Particle.LAVA, 1, 0.1d, 0.1d, 0.1d, 0.0f);
                    player.spawnWorldParticle(location, Particle.FLAME, 1);
                    player.spawnWorldParticle(location, Particle.SMALL_FLAME, 3, 0.1d, 0.1d, 0.1d, 0.05f);
                }
                
                @Override
                public void onHit(@Nonnull Location location) {
                    if (target instanceof GamePlayer) {
                        return;
                    }
                    
                    target.damage(impelNonPlayerDamage, player, DamageCause.IMPEL);
                }
            }.runTaskTimer(0, 1));
            
            // Spawn bats
            final Set<Bat> fxBats = Sets.newHashSet();
            final Location eyeLocation = player.getEyeLocation();
            
            for (int i = 0; i < 10; i++) {
                fxBats.add(Entities.BAT.spawn(
                        eyeLocation, self -> {
                            self.setAwake(true);
                            self.setInvulnerable(true);
                        }
                ));
            }
            
            player.addEffect(EffectType.INVISIBLE, 10000);
            
            final Bat playerBat = Entities.BAT.spawn(
                    eyeLocation, self -> {
                        self.setAwake(true);
                        self.setInvulnerable(true);
                        
                        EntityUtils.setCollision(self, EntityUtils.Collision.DENY, player.getEntity());
                    }
            );
            
            final float flySpeed = player.getFlySpeed();
            
            player.teleport(eyeLocation);
            
            player.setFlySpeed(0.05f);
            player.setAllowFlight(true);
            player.setFlying(true);
            
            player.playWorldSound(location, Sound.ENTITY_BAT_TAKEOFF, 0.25f);
            player.playWorldSoundAtTicks(
                    location, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.75f, 1.55f,
                    0, 2, 3, 5, 6, 9, 10, 12
            );
            
            return builder()
                    .onCastTick(tick -> {
                        playerBat.teleport(player.getLocation());
                        
                        // Fx
                        player.spawnWorldParticle(player.getLocation(), Particle.SMOKE, 2, 0.15d, 0.15d, 0.15d, 0.0f);
                    })
                    .onCastEnd(() -> {
                        getPlayerData(player).newImpelInstance().nextImpel(2);
                        
                        fxBats.forEach(Bat::remove);
                        fxBats.clear();
                        
                        player.setFlySpeed(flySpeed);
                        player.setAllowFlight(false);
                        player.setFlying(false);
                        
                        player.removeEffect(EffectType.INVISIBLE);
                        
                        playerBat.remove();
                    });
        }
    }
    
}
