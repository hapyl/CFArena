package me.hapyl.fight.game.heroes.archive.doctor;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.RightClickable;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class PhysGun extends Weapon implements RightClickable {

    private final PlayerMap<CaptureData> capturedEntity = PlayerMap.newMap();

    public PhysGun() {
        super(Material.GOLDEN_HORSE_ARMOR);
        setId("dr_ed_gun_2");
        setName("Upgraded Dr. Ed's Gravity Energy Capacitor Mk. 4");

        setAbility(AbilityType.RIGHT_CLICK, Ability.of("Harvest V2", "", this));
        GameTask.scheduleCancelTask(capturedEntity::clear);
    }

    @Override
    public void onRightClick(@Nonnull GamePlayer player, @Nonnull ItemStack item) {
        // Throw
        if (capturedEntity.containsKey(player)) {
            final CaptureData data = capturedEntity.remove(player);
            final Location location = player.getLocation().add(player.getLocation().getDirection().multiply(2.0d));
            final LivingGameEntity entity = data.getCaptured();

            entity.asPlayer(targetPlayer -> {
                targetPlayer.setAllowFlight(data.isFlight());
            });

            entity.setVelocity(player.getLocation().getDirection().multiply(2.0d));
            entity.spawnWorldParticle(location, Particle.EXPLOSION_NORMAL, 10, 0.2, 0.05, 0.2, 0.02f);
            entity.playWorldSound(location, Sound.ITEM_CROSSBOW_SHOOT, 0.5f);
            return;
        }

        // Get the target entity
        final LivingGameEntity target = Collect.targetEntity(player, 3.0d, e -> !e.equals(player));

        if (target == null) {
            player.sendMessage("&cNo valid target!");
            return;
        }

        capturedEntity.put(player, new CaptureData(player, target,
                target instanceof GamePlayer targetPlayer && targetPlayer.getAllowFlight()
        ));

        // Tick entity
        new GameTask() {
            @Override
            public void run() {
                final CaptureData data = capturedEntity.get(player);

                if (data == null) {
                    cancel();
                    return;
                }

                if (!player.isHeldSlot(HotbarSlots.HERO_ITEM) || data.check(target)) {
                    data.dismount();
                    cancel();
                    return;
                }

                final Location playerLocation = player.getLocation();
                final Location location = target.getLocation();
                Location finalLocation = playerLocation.add(0.0d, 1.0d, 0.0d).add(playerLocation.getDirection().multiply(2.0d));

                finalLocation.setYaw(location.getYaw());
                finalLocation.setPitch(location.getPitch());

                if (!finalLocation.getBlock().getType().isAir() || !finalLocation.getBlock().getRelative(BlockFace.UP).getType().isAir()) {
                    finalLocation = playerLocation;
                }

                target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 1, true));
                target.sendSubtitle("&f&lCaptured by &a%s&f&l!".formatted(player.getName()), 0, 10, 0);

                target.teleport(finalLocation);
                player.sendSubtitle("&f&lCarrying &a%s".formatted(target.getName()), 0, 10, 0);

            }
        }.runTaskTimer(0, 1);
    }

}
