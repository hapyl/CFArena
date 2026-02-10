package me.hapyl.fight.game.talents.dark_mage;

import me.hapyl.eterna.module.inventory.Equipment;
import me.hapyl.eterna.module.npc.ClickType;
import me.hapyl.eterna.module.npc.Npc;
import me.hapyl.eterna.module.npc.NpcPose;
import me.hapyl.eterna.module.npc.appearance.AppearanceBuilder;
import me.hapyl.eterna.module.npc.appearance.AppearanceHumanoid;
import me.hapyl.eterna.module.reflect.Skin;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.CFUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class ShadowCloneNPC extends Npc {
    
    private final GamePlayer player;
    private final ShadowClone talent;
    private final GameTask task;
    
    private final double health;
    private final double energy;
    private final int[] cooldowns;
    
    private int lifeTime;
    
    public ShadowCloneNPC(ShadowClone talent, GamePlayer player) {
        super(BukkitUtils.anchorLocation(player.getLocation()), Component.empty(), AppearanceBuilder.ofMannequin(Skin.ofPlayer(player.getEntity())));
        
        this.talent = talent;
        this.player = player;
        
        // Store data
        this.health = player.getHealth();
        this.energy = player.getEnergy();
        
        final Hero hero = player.getHero();
        
        this.cooldowns = new int[] {
                hero.getFirstTalent().getCooldownTimeLeft(player),
                hero.getSecondTalent().getCooldownTimeLeft(player),
                hero.getThirdTalent().getCooldownTimeLeft(player)
        };
        
        this.lifeTime = talent.getDuration(player);
        this.task = new GameTask() {
            @Override
            public void run() {
                if (lifeTime-- > 0) {
                    return;
                }
                
                talent.clones.remove(player, ShadowCloneNPC.this);
                
                player.sendSubtitle("&cYour clone dissipated!", 0, 20, 0);
                player.playWorldSound(getLocation(), Sound.ENTITY_GHAST_SCREAM, 0.75f);
                
                destroy();
                playRemoveFx();
            }
        }.runTaskTimer(0, 1);
        
        getAppearance(AppearanceHumanoid.class).setEquipment(Equipment.of(player.getEntity()));
        
        showAll();
        
        if (player.isSwimming()) {
            setPose(NpcPose.SWIMMING);
        }
        else if (player.isSneaking()) {
            setPose(NpcPose.CROUCHING);
        }
        
        // Fx
        player.playWorldSound(Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 0.25f);
        player.playWorldSound(Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 1.25f);
    }
    
    @Override
    public String toString() {
        return "&3\uD83E\uDE9E &b" + CFUtils.formatTick(lifeTime);
    }
    
    @Override
    public void onClick(@Nonnull Player click, @Nonnull ClickType type) {
        final GamePlayer clickedPlayer = CF.getPlayer(click);
        
        if (player.isSelfOrTeammate(clickedPlayer)) {
            return;
        }
        
        if (type != ClickType.ATTACK) {
            return;
        }
        
        destroy();
        
        talent.clones.remove(player, this);
        talent.startCooldown(player);
        
        // Fx
        final Location location = getLocation();
        
        player.playWorldSound(location, Sound.BLOCK_GLASS_BREAK, 0.0f);
        player.playWorldSound(location, Sound.ENTITY_PLAYER_HURT, 0.75f);
        
        playRemoveFx();
        
        player.sendSubtitle("&cYou clone was destroyed!", 0, 20, 0);
    }
    
    @Override
    public void destroy() {
        super.destroy();
        task.cancel();
    }
    
    public void playRemoveFx() {
        player.spawnWorldParticle(getLocation(), Particle.LARGE_SMOKE, 20, 0.1, 0.5, 0.1, 0.5f);
    }
    
    @Nonnull
    public ShadowClone getTalent() {
        return talent;
    }
    
    public void teleport() {
        player.teleport(getLocation());
        
        player.setHealth(health);
        player.setEnergy(energy);
        
        final Hero hero = player.getHero();
        
        hero.getFirstTalent().startCooldown(player, cooldowns[0] + 1);
        hero.getSecondTalent().startCooldown(player, cooldowns[1] + 1);
        hero.getThirdTalent().startCooldown(player, cooldowns[2] + 1);
        
        destroy();
        
        // Fx
        player.playWorldSound(Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 0.75f);
        player.playWorldSound(Sound.ENTITY_ENDERMAN_TELEPORT, 1.25f);
        
        player.addEffect(EffectType.BLINDNESS, 20);
    }
    
}
