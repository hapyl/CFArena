package me.hapyl.fight.game.entity.commission;

import me.hapyl.eterna.module.player.PlayerSkin;
import me.hapyl.eterna.module.reflect.npc.ClickType;
import me.hapyl.eterna.module.reflect.npc.HumanNPC;
import me.hapyl.eterna.module.reflect.npc.NPCAnimation;
import me.hapyl.fight.event.DamageInstance;
import org.bukkit.Sound;
import org.bukkit.entity.Husk;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * Overlay entities are special entity type consisting of an actual entity (always a Husk) and
 * a {@link Overlay} that follows it the "mimics" the entity actions.
 */
public class CommissionOverlayEntity extends CommissionEntity {

    private final Overlay overlay;

    public CommissionOverlayEntity(@Nonnull CommissionOverlayEntityType type, @Nonnull Husk husk) {
        super(type, husk);

        this.overlay = new Overlay(this);
    }

    @Nonnull
    @Override
    public CommissionOverlayEntityType type() {
        return (CommissionOverlayEntityType) super.type();
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void onTick(int tick) {
        // Sync the overlay to the entity
        overlay.teleport(getLocation());

        // Sync equipment
        overlay.setEquipment(getEquipment());
    }

    @OverridingMethodsMustInvokeSuper
    @Override
    public void onRemove() {
        super.onRemove();

        overlay.remove();
    }

    @Override
    public void onDamageDealt(@Nonnull DamageInstance instance) {
        // Simulate swing
        overlay.swingMainHand();
    }

    @OverridingMethodsMustInvokeSuper
    @Override
    public void onDamageTaken(@Nonnull DamageInstance instance) {
        super.onDamageTaken(instance);

        // Play damage animation & sound
        overlay.playAnimation(NPCAnimation.TAKE_DAMAGE);

        playWorldSound(Sound.ENTITY_PLAYER_HURT, 1.0f);
    }

    @Nonnull
    public Overlay overlay() {
        return this.overlay;
    }

    @Nonnull
    @Override
    public String getScoreboardName() {
        return overlay.getHexName();
    }

    public static class Overlay extends HumanNPC {

        private final CommissionOverlayEntity entity;

        private Overlay(@Nonnull CommissionOverlayEntity entity) {
            super(entity.getLocation(), "");

            this.entity = entity;

            // Make sure we can always damage the entity
            setInteractionDelay(0);

            // Apply skin
            final PlayerSkin skin = entity.type().skin();
            setSkin(skin.getTexture(), skin.getSignature());

            // Show to everyone by default
            showAll();
        }

        @Override
        public void onClick(@Nonnull Player player, @Nonnull ClickType type) {
            if (type != ClickType.ATTACK) {
                return;
            }

            player.attack(entity.entity());
        }
    }

}
