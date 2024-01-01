package me.hapyl.fight.game.entity.overlay;

import me.hapyl.fight.game.Event;
import me.hapyl.fight.game.entity.NamedGameEntity;
import me.hapyl.fight.game.playerskin.PlayerSkin;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.ShutdownAction;
import me.hapyl.spigotutils.module.ai.AI;
import me.hapyl.spigotutils.module.ai.MobAI;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.reflect.npc.ClickType;
import me.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import me.hapyl.spigotutils.module.reflect.npc.NPCAnimation;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import javax.annotation.Nonnull;

public class OverlayNamedGameEntity extends NamedGameEntity<Zombie> implements Simulates {

    public static final String OVERLAY_TAG = "CF_OverlayEntity";

    private final OverlayGameEntityType type;
    private final HumanNPC npc;
    private final AI ai;

    private boolean dying;

    public OverlayNamedGameEntity(OverlayGameEntityType type, Zombie entity) {
        super(type, entity);

        this.type = type;
        this.npc = new HumanNPC(getLocation(), "", "") {
            @Override
            public void onClick(@Nonnull Player player, @Nonnull ClickType type) {
                if (type != ClickType.ATTACK) {
                    return;
                }

                if (entity.getNoDamageTicks() > (entity.getMaximumNoDamageTicks() / 2)) {
                    return;
                }

                final AttributeInstance instance = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
                double damage = 1.0;

                if (instance != null) {
                    damage = instance.getValue();
                }

                entity.damage(damage, player);
                simulateTakeDamage();
            }
        };

        this.entity.addScoreboardTag(OVERLAY_TAG);

        final PlayerSkin skin = type.getSkin();
        this.npc.setSkin(skin.getTexture(), skin.getSignature());

        this.npc.showAll();

        this.ai = MobAI.of(entity);
        //this.ai.removeAllGoals();
    }

    @Nonnull
    public OverlayGameEntityType getType() {
        return type;
    }

    @Nonnull
    public AI getAI() {
        return ai;
    }

    @Nonnull
    public HumanNPC getNpc() {
        return npc;
    }

    @Override
    public void kill() {
        if (dying) {
            return;
        }

        dying = true;

        super.kill();
        final Location location = entity.getLocation();

        // Since the entity isn't visible to the player, it has to be removed this way.
        entity.remove();

        // Death animation
        // I CANNOT figure out how to do the death animation so imma just fake it using elytra flying
        npc.playAnimation(NPCAnimation.TAKE_DAMAGE);
        npc.setDataWatcherByteValue(0, (byte) 0x80);
        npc.updateDataWatcher();

        playWorldSound(Sound.ENTITY_PLAYER_DEATH, 1.0f);

        GameTask.runLater(() -> {
            npc.remove();
            PlayerLib.spawnParticle(location.add(0.0d, 0.5d, 0.0d), Particle.EXPLOSION_NORMAL, 15, 0.1d, 0.33d, 0.1d, 0.0f);
        }, 15).setShutdownAction(ShutdownAction.IGNORE);
    }

    @Override
    public void onTick() {
        final Location location = entity.getLocation();

        npc.teleport(location);
    }

    @Override
    public void onTick10() {
        // Update equipment
        npc.setEquipment(entity.getEquipment());
    }

    @Event
    public void onAttack(EntityDamageByEntityEvent ev) {
    }

    @Override
    public void simulateAttack(EntityDamageByEntityEvent ev) {
        onAttack(ev);

        if (ev.isCancelled()) {
            return;
        }

        npc.playAnimation(NPCAnimation.SWING_MAIN_HAND);
    }

    @Override
    public void simulateTakeDamage() {
        npc.playAnimation(NPCAnimation.TAKE_DAMAGE);
        playWorldSound(Sound.ENTITY_PLAYER_HURT, 1.0f);
    }

    private double getHandItemDamage(Player player) {
        final AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);

        if (attribute == null) {
            return 1.0d;
        }

        return attribute.getValue();
    }

    private void playNpcDeathAnimation() {
        npc.remove();
    }
}
