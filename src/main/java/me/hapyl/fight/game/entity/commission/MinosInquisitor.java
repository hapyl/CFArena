package me.hapyl.fight.game.entity.commission;

import me.hapyl.eterna.module.inventory.Equipment;
import me.hapyl.eterna.module.player.PlayerSkin;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.LowAttributes;
import me.hapyl.fight.game.entity.EntityType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Collect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Husk;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public class MinosInquisitor extends CommissionOverlayEntityType implements Listener {
    public MinosInquisitor(@Nonnull Key key) {
        super(
                key, "Minos Inquisitor", new PlayerSkin(
                        "ewogICJ0aW1lc3RhbXAiIDogMTczNzEyODQzNjIwNywKICAicHJvZmlsZUlkIiA6ICJjNWVmOGQ1NDIwOWY0OTdlYWYzYzA1NjA3MjZhYTMwNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNaXNoX0RheCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8xOWFhYzM1NmRiYmVmM2VhN2VlMzQ0MGQ3YjNhYjllZmMyOTY1ZTI1OWZhZGNiOWIwODUxYjBiMjQ0MzQ3YjdkIgogICAgfQogIH0KfQ==",
                        "oL3qtvO2Gqlgpsf3ZnKE6eLtxo2nVaHhcG+5iqc7a+dKjolHcCuxe6UT1iRL/Zc/4B8e6+zXwN692HavkZkXQIz04Mqs+CSyS1dwWsDRjQhYpQ5nNOQhQfWBL+F0w8dXGk7vFZz52iiHTE9wFrFVW5E34qDDP/aQt9sBlFVpIqjvF6v1LEvUp/YCTfFXv6j15cwwpK3jm94F+6Ig51FLdw15dXdGvnQxkQfESrKwC7WZiFEOXaDmnj7ru5lB070Wkm5JKA+LcaKhpm60ovk1qGdfofAt7jsrcY/LveFoPHXZgQ1gEq5JTjDSAbvUJoHdFy3Zb/eDN8wIZItZtvj3JOUfb3PinZzJyUpms/h85gkOkbOlro9EB2dcf0kYU0Z7VYxi5k9D2TZrRjyTAk/I1q8cQhsxFyqBW5h4YQy8JSkPd1Kl8W6QVU/KZ2yS5XbcvOFz8DXDS5RxC4h6iKbo7RqVpO9wYgYMF9pSl+WRhHI0d/dlLd5yyhI0CTXSFr6aa8QzILTnMrIVRrrpUTG4x3aAk8mLFFydn3yo4SHMmil6N/h6MbnLHPkNxL1NKdSxvw8mgEwkUw3quzSXvSnv6r7sD7EJ/ZYs7ixFo15ZBef4idRA90C6zwVAxnRPXkq5bTW0Z5ZNj0r/pcXbqmJ+sQovgRAol/8j7t1pZBMElP0="
                ),
                new LowAttributes()
                        .put(AttributeType.MAX_HEALTH, 1_000d)
                        .put(AttributeType.ATTACK, 2.5d)
                        .put(AttributeType.DEFENSE, 1.7d)
                        .put(AttributeType.SPEED, 0.6d)
        );

        setType(EntityType.MINIBOSS);

        equipment = Equipment.builder()
                             .mainHand(Weapon.builder(Material.ARMOR_STAND, Key.ofString("inquisitor_weapon"))
                                             .damage(10)
                                             .build()
                                             .createItem())
                             .build();
    }

    @Override
    public boolean isAttributesScalable() {
        return false;
    }

    @Nonnull
    @Override
    public CommissionOverlayEntity create(@Nonnull Location location) {
        return CF.createOverlayEntity(location, Entity::new);
    }

    private class Entity extends CommissionOverlayEntity {

        public Entity(@Nonnull Location location, @Nonnull Husk husk) {
            super(MinosInquisitor.this, husk);
        }

        @Override
        public void onTick(int tick) {
            super.onTick(tick);

            // Always attack the nearest target
            final LivingGameEntity target = Collect.nearestEntity(getLocation(), 3, entity -> entity instanceof GamePlayer);

            if (target != null) {
                entity.attack(target.getEntity());
            }
        }

        @Override
        public void onSpawn() {
            playWorldSound(Sound.ENTITY_WITHER_SPAWN, 2.0f);
            spawnWorldParticle(Particle.EXPLOSION_EMITTER, 1);
        }
    }
}
