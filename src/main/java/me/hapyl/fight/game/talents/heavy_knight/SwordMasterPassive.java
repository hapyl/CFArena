package me.hapyl.fight.game.talents.heavy_knight;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.event.custom.AttributeModifyEvent;
import me.hapyl.fight.game.attribute.AttributeModifier;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.talents.PassiveTalent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public class SwordMasterPassive extends PassiveTalent implements Listener {
    public SwordMasterPassive(@Nonnull Key key) {
        super(key, "Enchanted Armor");
        
        setDescription("""
                       Your enchanted armor is undestructible, preventing your %s from decreasing.
                       """.formatted(AttributeType.DEFENSE)
        );
        
        setMaterial(Material.NETHERITE_CHESTPLATE);
    }
    
    @EventHandler
    public void handleAttributeChangeEvent(AttributeModifyEvent ev) {
        final LivingGameEntity entity = ev.getEntity();
        final AttributeModifier modifier = ev.modifier();
        
        if (!(entity instanceof GamePlayer player)) {
            return;
        }
        
        if (!HeroRegistry.SWORD_MASTER.validatePlayer(player)) {
            return;
        }
        
        if (modifier.remove(entry -> entry.attributeType() == AttributeType.DEFENSE && entry.value() < 0)) {
            player.spawnBuffDisplay("&2\uD83D\uDEE1 ᴇɴᴄʜᴀɴᴛᴇᴅ ᴀʀᴍᴏʀ", 30);
            player.playWorldSound(Sound.ITEM_ARMOR_EQUIP_NETHERITE, 0.75f);
        }
    }
    
}
