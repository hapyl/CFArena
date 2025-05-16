package me.hapyl.fight.game.heroes.zealot;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.loadout.HotBarSlot;
import me.hapyl.fight.game.task.player.PlayerGameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class ZealotWeapon extends Weapon {
    
    private final Zealot zealot;
    
    public ZealotWeapon(Zealot zealot) {
        super(Material.DIAMOND_SWORD, Key.ofString("zealot_weapon"));
        this.zealot = zealot;
        
        setDamage(2.0d);
        
        setName("Psionic Blade");
        setDescription("""
                       An ordinary space katana.
                       """);
        
        setAbility(AbilityType.RIGHT_CLICK, new SoulCryAbility());
    }
    
    public class SoulCryAbility extends Ability {
        
        @DisplayField(scale = 100) private final double ferocityIncrease = 1.0;
        @DisplayField(scale = 100) private final double speedIncrease = 0.2;
        
        private final ModifierSource modifierSource = new ModifierSource(Key.ofString("soul_cry"));
        
        public SoulCryAbility() {
            super(
                    "Soul Cry",
                    "Gain &a{ferocityIncrease}%% %s and &b{speedIncrease}%% %s for {duration}.".formatted(
                            AttributeType.FEROCITY,
                            AttributeType.SPEED
                    )
            );
            
            setDurationSec(3);
            setCooldownSec(13);
        }
        
        @Override
        public Response execute(@Nonnull GamePlayer player) {
            final ItemStack weapon = player.getItem(HotBarSlot.WEAPON);
            
            if (weapon == null || weapon.getType() == Material.GOLDEN_SWORD) {
                return Response.OK;
            }
            
            player.getAttributes().addModifier(
                    modifierSource, this, modifier -> modifier
                            .of(AttributeType.FEROCITY, ModifierType.MULTIPLICATIVE, ferocityIncrease)
                            .of(AttributeType.SPEED, ModifierType.MULTIPLICATIVE, speedIncrease)
            );
            
            zealot.abilityEquipment.equip(player);
            player.setItem(HotBarSlot.WEAPON, weapon.withType(Material.GOLDEN_SWORD));
            
            new PlayerGameTask(player) {
                @Override
                public void run() {
                    zealot.getEquipment().equip(player);
                    player.setItem(HotBarSlot.WEAPON, createItem());
                    
                    // Fx
                    player.playWorldSound(Sound.ENTITY_ELDER_GUARDIAN_AMBIENT_LAND, 1.0f);
                }
            }.runTaskLater(getDuration());
            
            // Fx
            player.playWorldSound(Sound.ENTITY_ELDER_GUARDIAN_AMBIENT_LAND, 0.0f);
            
            return Response.OK;
        }
    }
}
