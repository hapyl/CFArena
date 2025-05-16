package me.hapyl.fight.game.talents.engineer;

import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.reflect.glowing.Glowing;
import me.hapyl.eterna.module.reflect.glowing.GlowingColor;
import me.hapyl.fight.Message;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.engineer.EngineerData;
import me.hapyl.fight.util.hitbox.HitboxEntity;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class ConstructEntity extends HitboxEntity {
    
    private final GamePlayer player;
    private final Construct construct;
    
    private final ArmorStand stand;
    private final DisplayEntity displayEntity;
    
    ConstructEntity(@Nonnull Slime slime, double scale, @Nonnull GamePlayer player, @Nonnull Construct construct) {
        super(slime, scale);
        
        this.player = player;
        this.construct = construct;
        
        final Location location = construct.location;
        final EngineerConstructTalent talent = construct.talent;
        
        this.stand = Entities.ARMOR_STAND_MARKER.spawn(
                LocationHelper.addAsNew(location, 0, talent.yOffset, 0), self -> {
                    self.setInvisible(true);
                    self.setSmall(true);
                    self.setGravity(false);
                }
        );
        
        this.displayEntity = construct.talent.model().spawn(
                construct.location, self -> {
                    self.setTeleportDuration(3);
                    
                    // Glow model for self only
                    Glowing.setGlowing(player.getEntity(), self, GlowingColor.GREEN);
                }
        );
        
        player.getTeam().addEntry(getEntry());
    }
    
    @Nonnull
    public ArmorStand getStand() {
        return stand;
    }
    
    @Nonnull
    public DisplayEntity getDisplayEntity() {
        return displayEntity;
    }
    
    @Override
    public void tick() {
        super.tick();
        
        stand.setCustomName(Chat.format(
                "&8[&7Lv%s&8] &a%s %s".formatted(
                        construct.getLevelRoman(),
                        construct.getName(),
                        construct.toString()
                )
        ));
        stand.setCustomNameVisible(true);
    }
    
    @Nonnull
    public Location getLocation() {
        return displayEntity.getHead().getLocation();
    }
    
    public void lookAt(@Nonnull Location location) {
        final Location entityLocation = displayEntity.getHead().getLocation();
        final Vector vector = location.toVector().subtract(entityLocation.toVector());
        
        entityLocation.setDirection(vector);
        displayEntity.teleport(entityLocation);
    }
    
    @Override
    public void onInteract(@Nonnull GamePlayer whoClicked) {
        doInteract(player, false);
    }
    
    @Override
    public void onDespawn() {
        stand.remove();
        displayEntity.remove();
    }
    
    @Override
    public void onDamageTaken(@Nonnull DamageInstance instance) {
        playWorldSound(Sound.ENTITY_IRON_GOLEM_HURT, 1.5f);
    }
    
    @Override
    public void onTeammateDamage(@Nonnull LivingGameEntity lastDamager) {
        if (lastDamager instanceof GamePlayer teammate && player.equals(teammate)) {
            doInteract(teammate, true);
        }
        else {
            lastDamager.sendMessage(Message.ERROR, "Cannot damage allied construct!");
        }
    }
    
    private void doInteract(GamePlayer whoClicked, boolean isHealing) {
        // Check for upgrade
        if (!player.equals(whoClicked)) {
            return;
        }
        
        // Make sure the player is holding Iron
        final ItemStack heldItem = player.getHeldItem();
        
        if (heldItem.getType() != HeroRegistry.ENGINEER.getPassiveTalent().getMaterial()) {
            return;
        }
        
        final EngineerData data = HeroRegistry.ENGINEER.getPlayerData(player);
        final int iron = data.getIron();
        final int cost = isHealing ? Construct.HEALING_COST : construct.talent.upgradeCost();
        
        if (iron < cost) {
            construct.message("&4Not enough resources!");
            return;
        }
        
        // Left click to heal
        if (isHealing) {
            if (construct.isFullHealth()) {
                construct.message("&4Already at full health!");
                return;
            }
            
            construct.heal();
        }
        // Right click to upgrade
        else {
            if (construct.isMaxLevel()) {
                construct.message("&4Already at max level!");
                return;
            }
            
            construct.levelUp();
        }
        
        data.setIron(iron - cost);
    }
}
