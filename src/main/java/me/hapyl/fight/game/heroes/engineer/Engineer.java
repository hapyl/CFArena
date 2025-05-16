package me.hapyl.fight.game.heroes.engineer;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.engineer.EngineerDispenser;
import me.hapyl.fight.game.talents.engineer.EngineerSentry;
import me.hapyl.fight.game.talents.engineer.MagneticAttractionPassive;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.ui.UIComplexComponent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.fight.util.displayfield.DisplayFieldProvider;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class Engineer extends Hero implements Listener, PlayerDataHandler<EngineerData>, UIComplexComponent, DisplayFieldProvider {
    
    public final Weapon ironFist = Weapon.createBuilder(Material.IRON_BLOCK, Key.ofString("iron_fist"))
                                         .name("&6&lIron Fist")
                                         .damage(8.0d)
                                         .build();
    
    @DisplayField public final double ultimateInWaterDamage = 10;
    @DisplayField public final int ultimateHitCd = 5;
    
    private final PlayerDataMap<EngineerData> playerData = PlayerMap.newDataMap(player -> new EngineerData(this, player));
    
    public Engineer(@Nonnull Key key) {
        super(key, "Engineer");
        
        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.STRATEGY, Archetype.TALENT_DAMAGE, Archetype.SUPPORT);
        profile.setGender(Gender.MALE);
        
        setDescription("""
                       A Genius with 12 PhDs.
                       
                       Out of all his inventions, he only brought two to the fight, and your best hope is that they're pointing at you.
                       """);
        setItem("55f0bfea3071a0eb37bcc2ca6126a8bdd79b79947734d86e26e4d4f4c7aa9");
        
        setWeapon(Weapon.createBuilder(Material.IRON_HOE, Key.ofString("prototype_wrench"))
                        .name("Prototype Wrench")
                        .description("""
                                     A prototype wrench for all the needs.
                                     It... probably hurts to be hit with it.
                                     """
                        )
                        .damage(5.0d)
        );
        
        final HeroEquipment equipment = getEquipment();
        equipment.setChestPlate(255, 0, 0);
        equipment.setLeggings(0, 0, 0);
        equipment.setBoots(0, 0, 0);
        
        setUltimate(new EngineerUltimate());
    }
    
    @EventHandler()
    public void handlePlayerSwing(PlayerInteractEvent ev) {
        final GamePlayer player = CF.getPlayer(ev.getPlayer());
        final Action action = ev.getAction();
        
        if (!validatePlayer(player) || ev.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }
        
        if (action != Action.LEFT_CLICK_BLOCK && action != Action.LEFT_CLICK_AIR) {
            return;
        }
        
        swingMechaHand(player);
    }
    
    @Override
    public void processDamageAsDamager(@Nonnull DamageInstance instance) {
        final GamePlayer damager = instance.getDamagerAsPlayer();
        
        swingMechaHand(damager);
    }
    
    @Override
    public boolean processInvisibilityDamage(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity, double damage) {
        // Ultimate makes the player themselves invisible
        return !player.isUsingUltimate();
    }
    
    @Override
    public void onStart(@Nonnull GameInstance instance) {
        new TickingGameTask() {
            @Override
            public void run(int tick) {
                playerData.values().forEach(data -> data.tick(tick));
            }
        }.runTaskTimer(0, 1);
    }
    
    @Override
    public EngineerSentry getFirstTalent() {
        return TalentRegistry.ENGINEER_TURRET;
    }
    
    @Override
    public EngineerDispenser getSecondTalent() {
        return TalentRegistry.ENGINEER_DISPENSER;
    }
    
    @Override
    public MagneticAttractionPassive getPassiveTalent() {
        return TalentRegistry.ENGINEER_PASSIVE;
    }
    
    @Nonnull
    @Override
    public EngineerUltimate getUltimate() {
        return (EngineerUltimate) super.getUltimate();
    }
    
    @Nonnull
    @Override
    public PlayerDataMap<EngineerData> getDataMap() {
        return playerData;
    }
    
    @Nullable
    @Override
    public List<String> getStrings(@Nonnull GamePlayer player) {
        return getPlayerData(player).getStrings(player);
    }
    
    private void swingMechaHand(GamePlayer player) {
        getPlayerData(player).swingMechaIndustriesHand();
    }
    
    public class EngineerUltimate extends UltimateTalent {
        
        @DisplayField public final double mechaHealth = 100;
        @DisplayField public final double mechaDefense = 150;
        
        public EngineerUltimate() {
            super(Engineer.this, "Mecha-Industries", 50);
            
            setDescription("""
                           Instantly create a &fmech suit&7 and pilot it for {duration}.
                           
                           The suit provides &cattack&7 power, but you cannot use talents while piloting.
                           &8&o;;Looks like a wire sticking out of it, probably should keep away from water.
                           """);
            
            setType(TalentType.ENHANCE);
            setTexture("b69d0d4711153a089c5567a749b27879c769d3bdcea6fda9d6f66e93dd8c4512");
            
            setDurationSec(12);
            setCooldownSec(35);
            
            setSound(Sound.BLOCK_ANVIL_USE, 0.25f);
        }
        
        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player, boolean isFullyCharged) {
            return execute(() -> {
                final EngineerData data = getPlayerData(player);
                data.createMechaIndustries(Engineer.this);
                
                player.schedule(data::removeMechaIndustries, getUltimate().getDuration());
            });
        }
    }
}
