package me.hapyl.fight.game.heroes.doctor;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.loadout.HotBarSlot;
import me.hapyl.fight.game.talents.Cooldown;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PhysGun extends Weapon {

    public final double maxDistance = 2.5d;
    public final double shift = 0.1d;
    public final double throwMagnitude = 2.0d;

    protected final PlayerMap<CaptureData> capturedEntity = PlayerMap.newMap();

    public PhysGun() {
        super(Material.GOLDEN_HORSE_ARMOR, Key.ofString("dr_ed_gun_2"));

        setName("Upgraded Dr. Ed's Gravity Energy Capacitor Mk. 4");

        setAbility(AbilityType.RIGHT_CLICK, new Harvest());
    }

    public void stop(@Nonnull GamePlayer player) {
        final CaptureData data = capturedEntity.remove(player);

        player.setItem(HotBarSlot.HERO_ITEM, null);
        player.setUsingUltimate(false);
        player.snapToWeapon();

        if (data == null) {
            return;
        }

        data.cancel();
    }

    public class Harvest extends Ability {

        public Harvest() {
            super("Harvest V2", "harvest v2");

            setCooldownSec(2);
            GameTask.scheduleCancelTask(capturedEntity::clear);
        }

        @Nullable
        @Override
        public Response execute(@Nonnull GamePlayer player, @Nonnull ItemStack item) {
            final CaptureData data = capturedEntity.remove(player);

            // Throw
            if (data != null) {
                final Location location = player.getLocation().add(player.getLocation().getDirection().multiply(throwMagnitude));
                final LivingGameEntity entity = data.getEntity();

                data.cancel();
                stop(player);

                entity.setVelocity(player.getLocation().getDirection().multiply(2.0d));
                entity.spawnWorldParticle(location, Particle.POOF, 10, 0.2, 0.05, 0.2, 0.02f);
                entity.playWorldSound(location, Sound.ITEM_CROSSBOW_SHOOT, 0.5f);
                return Response.OK;
            }

            // Get the target entity
            final LivingGameEntity target = Collect.targetEntityRayCast(player, 3.0d, 1.25f, entity -> {
                return !player.isSelfOrTeammateOrHasEffectResistance(entity);
            });

            if (target == null) {
                return Response.error("&cNo valid target!");
            }

            capturedEntity.put(player, new CaptureData(PhysGun.this, player, target));
            return Response.AWAIT;
        }

        @Override
        public Cooldown setCooldownSec(float cooldownSec) {
            return super.setCooldownSec(cooldownSec);
        }
    }


}
