package me.hapyl.fight.game.talents.shadow_assassin;

import me.hapyl.eterna.module.inventory.Equipment;
import me.hapyl.eterna.module.npc.Npc;
import me.hapyl.eterna.module.npc.NpcAnimation;
import me.hapyl.eterna.module.npc.appearance.Appearance;
import me.hapyl.eterna.module.npc.appearance.AppearanceBuilder;
import me.hapyl.eterna.module.npc.appearance.AppearanceHumanoid;
import me.hapyl.eterna.module.npc.tag.TagLayout;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.reflect.EntityDataType;
import me.hapyl.eterna.module.reflect.Skin;
import me.hapyl.eterna.module.reflect.glowing.GlowingColor;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.TickingScheduler;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class CloneNPC extends Npc implements TickingScheduler {
    
    private static final ModifierSource modifierSource = new ModifierSource(Key.ofString("shadow_clone"));
    private static final Skin cloneSkin = Skin.of(
            "eyJ0aW1lc3RhbXAiOjE1ODUyNDc2MzIxNDYsInByb2ZpbGVJZCI6IjkxZjA0ZmU5MGYzNjQzYjU4ZjIwZTMzNzVmODZkMzllIiwicHJvZmlsZU5hbWUiOiJTdG9ybVN0b3JteSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzgwOTMyYWNmNDM1MGY2MDdhZTA5MmQ4ZTg4NTIzNTU0YjlkMzczMzRhMjFhOTkwMDZjYWVhZjU4YjAwNDIyMyIsIm1ldGFkYXRhIjp7Im1vZGVsIjoic2xpbSJ9fX19",
            "NaEFhxYKQCTjbjzriz2rgusEmIeUJweUIi1aBuw7PQ+csoWirXnoTZ8KwfRk2q2BxoY7v0edIE55pyE6Xmu5Pp+cp18rgVjSuqxr/CZE1BXVc+pefOG9Y8STqmjOYNaRwVJSavrCyrX9pksAI3jimDP+cwNUuC7vaDW8kj2WbnWho9kX4QMGqUQj1paNbHzXi1LwMyee5dKcR9Guzr9WKV/vzHtH5t+x4pPIT8t19aSSC1gkepJkgFstlrkMqo/yWDWyD0M5c8oDO4pYXKYBtcQtsiL1/7334sulmPwYM6nYi2H1I0ziNUX1THizvGp8Dzgu+3GBcjqoBncBmhYZba+4AcZgkFMXHylHIdCIzQgEl8lEiRI09VuAKp0TK8HGSBnfZOSxEnulVP7tbvODMygMHzkcaLW1QbghDt9oY1eTSZUqfCY3pEYMRyE7NeGN7w5FgF1yzD49xvi1IC+0lEK7ZmhHTHdrdCi6JYcc3s8AHPQ1N8SFUk6oBOVtTK4T4oOQG/spzdjD5i3rt1vZkW8pu2AAWbZC3VJDMwrtN1YbBjlYXR3puMUrKE7Zh//gxyr86ymV9ngcw0vTTwwNmQWwPuCjMALHiaw0nFQ9/EQtgXtAmKjmLwwCYjnYWQe0q0glxifVd5kdcMDY1FBn1HlOM872EiH2WB0bDm+T96A="
    );
    
    private final GamePlayer player;
    private final PlayerCloneList cloneList;
    private GameTask task;
    
    private boolean isAttacking;
    
    public CloneNPC(PlayerCloneList cloneList, Location location, GamePlayer owner) {
        super(location, Component.empty(), AppearanceBuilder.ofMannequin(cloneSkin));
        
        this.cloneList = cloneList;
        this.player = owner;
        
        setTagLayout(TagLayout.EMPTY);
        
        getAppearance(AppearanceHumanoid.class).setEquipment(
                Equipment.builder().mainHand(Material.IRON_SWORD).build()
        );
        
        showAll();
        
        // Glow for self
        glowColor(GlowingColor.BLACK);
        
        // Fx
        player.playWorldSound(location, Sound.ENTITY_SILVERFISH_AMBIENT, 0.0f);
    }
    
    public void glowColor(@Nonnull GlowingColor color) {
        getPacketTeam().color(player.getEntity(), color.bukkit);
        
        final Appearance appearance = getAppearance();
        final Byte existingBitMask = appearance.getEntityDataValue(EntityDataType.BYTE, 0);
     
        // Flip the glowing bit
        appearance.setEntityDataValue(EntityDataType.BYTE, 0, (byte) (existingBitMask | 0x40));
        appearance.updateEntityData();
    }
    
    @Nonnull
    @Override
    public GameTask schedule() {
        task = schedule(20, 5);
        return task;
    }
    
    public void disappear() {
        final Location location = getLocation().add(0.0d, 0.75d, 0.0d);
        
        PlayerLib.spawnParticle(location, Particle.LARGE_SMOKE, 20, 0.25d, 0.6d, 0.25d, 0.025f);
        destroy();
    }
    
    public void remove1() {
        if (task != null) {
            task.cancel();
        }
        
        if (cloneList != null) {
            cloneList.attackingMap.remove(this);
        }
        
        super.destroy();
    }
    
    @Override
    public void destroy() {
        cloneList.remove(this);
        remove1();
    }
    
    public void attack(@Nonnull LivingGameEntity entity, double damage) {
        final ShadowAssassinClone talent = TalentRegistry.SHADOW_ASSASSIN_CLONE;
        cloneList.attackingMap.put(this, entity);
        
        // Glow the clone
        glowColor(GlowingColor.LIGHT_PURPLE);
        
        lookAt(entity.getLocation());
        playAnimation(NpcAnimation.SWING_MAIN_HAND);
        
        // The damage is done without a damager to remove the knockback,
        // and the tick damage is, so when player teleports, they can instantly hit the enemy
        entity.setLastDamager(player);
        entity.damage(damage, DamageCause.SHADOW_CLONE);
        
        entity.addEffect(EffectType.BLINDNESS, 1, 20);
        entity.triggerDebuff(player);
        
        // Reduce defense
        entity.getAttributes().addModifier(modifierSource, talent.defenseReductionDuration, player, modifier -> modifier.of(AttributeType.DEFENSE, ModifierType.ADDITIVE, talent.defenseReduction));
        
        // Fx
        entity.playHurtSound(true);
        entity.playWorldSound(Sound.ENTITY_SQUID_SQUIRT, 0.0f);
        entity.playWorldSound(Sound.ENTITY_ENDER_DRAGON_HURT, 0.0f);
        entity.playWorldSound(Sound.ENTITY_ENDER_DRAGON_FLAP, 0.75f);
        
        entity.spawnWorldParticle(Particle.SWEEP_ATTACK, 1);
    }
    
    @Override
    public void tick() {
        if (isAttacking) {
            return;
        }
        
        Collect.nearbyEntities(getLocation(), 3).forEach(entity -> {
            if (isAttacking || player.isSelfOrTeammate(entity) || cloneList.isBeingAttacked(entity)) {
                return;
            }
            
            if (player.hasLineOfSight(entity)) {
                isAttacking = true;
                cloneList.createCloneLink(this);
                
                attack(entity, TalentRegistry.SHADOW_ASSASSIN_CLONE.cloneDamage);
                
                GameTask.runLater(this::disappear, PlayerCloneList.MAX_LINK_TIME);
            }
        });
    }
    
}
