package me.hapyl.fight.game.heroes.harbinger;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.event.custom.GameDeathEvent;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.harbinger.MeleeStance;
import me.hapyl.fight.game.talents.harbinger.RiptidePassive;
import me.hapyl.fight.game.talents.harbinger.TidalWaveTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.game.weapons.BowWeapon;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Harbinger extends Hero implements Listener, UIComponent, PlayerDataHandler<HarbingerData> {
    
    private final PlayerDataMap<HarbingerData> playerData = PlayerMap.newDataMap(HarbingerData::new);
    
    public Harbinger(@Nonnull Key key) {
        super(key, "Harbinger");
        
        setDescription("""
                       A strict and reliable bounty hunter.
                       
                       Always gets what she wants.
                       """);
        
        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.STRATEGY, Archetype.RANGE, Archetype.POWERFUL_ULTIMATE, Archetype.SELF_BUFF);
        profile.setAffiliation(Affiliation.MERCENARY);
        profile.setGender(Gender.FEMALE);
        
        setMinimumLevel(5);
        setItem("22a1ac2a8dd48c371482806b3963571952997a5712806e2c8060b8e7777754");
        
        final HeroEquipment equipment = getEquipment();
        equipment.setChestPlate(82, 82, 76);
        equipment.setLeggings(54, 48, 48);
        equipment.setBoots(183, 183, 180);
        
        setWeapon(BowWeapon.of(Key.ofString("harbinger_bow"), "Rust", "A rusty but reliable bow.", 2.0d));
        setUltimate(new HarbingerUltimate(this));
    }
    
    @Override
    public void processDamageAsDamager(@Nonnull DamageInstance instance) {
        final GamePlayer player = instance.getDamagerAsPlayer();
        
        if (player == null || !instance.isDirectDamage()) {
            return;
        }
        
        if (!instance.isCrit()) {
            return;
        }
        
        final HarbingerData data = getPlayerData(player);
        final RiptidePassive passive = getPassiveTalent();
        
        final boolean isStanceActive = data.stance != null;
        final LivingGameEntity entity = instance.getEntity();
        
        data.executeRiptideSlashIfPossible(entity);
        data.setRiptide(entity, isStanceActive ? passive.meleeRiptideAmount : passive.rangeRiptideAmount);
    }
    
    @EventHandler()
    public void handleGameDeathEvent(GameDeathEvent ev) {
        final LivingGameEntity entity = ev.getEntity();
        
        playerData.values().forEach(data -> {
            data.riptideMap.remove(entity);
        });
    }
    
    @Override
    public void onStart(@Nonnull GamePlayer player) {
        player.setArrowItem();
    }
    
    @Override
    public void onStart(@Nonnull GameInstance instance) {
        new GameTask() {
            @Override
            public void run() {
                playerData.values().forEach(HarbingerData::tick);
            }
        }.runTaskTimer(0, 1);
    }
    
    @Override
    public MeleeStance getFirstTalent() {
        return TalentRegistry.STANCE;
    }
    
    @Override
    public TidalWaveTalent getSecondTalent() {
        return TalentRegistry.TIDAL_WAVE;
    }
    
    @Override
    public RiptidePassive getPassiveTalent() {
        return TalentRegistry.RIPTIDE;
    }
    
    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
        @Nullable final StanceData data = getPlayerData(player).stance;
        
        if (data == null) {
            return "";
        }
        
        final MeleeStance talent = getFirstTalent();
        
        final long activeFor = (System.currentTimeMillis() - data.usedAt()) / 50;
        final int durationLeft = (int) (talent.maxDuration - activeFor);
        final int cooldown = talent.calculateCooldown(data);
        
        return "&2⚔ &l%s &8(%s)".formatted(CFUtils.formatTick(durationLeft), CFUtils.formatTick(cooldown));
    }
    
    @Nonnull
    @Override
    public PlayerDataMap<HarbingerData> getDataMap() {
        return playerData;
    }
    
}
