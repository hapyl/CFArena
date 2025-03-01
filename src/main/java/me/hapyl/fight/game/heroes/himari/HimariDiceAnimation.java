package me.hapyl.fight.game.heroes.himari;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.himari.HimariAction;
import me.hapyl.fight.game.talents.himari.HimariActionList;
import me.hapyl.fight.game.task.player.PlayerTickingGameTask;
import me.hapyl.fight.util.CFUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.EulerAngle;

import javax.annotation.Nonnull;

public class HimariDiceAnimation {

    private static final ItemStack[] DICE_TEXTURE = {
            ItemBuilder.playerHeadUrl("4675996c9164cf409f9fc9024231ca301a4f024e3a306c8f3f4caa062a5576b8").asIcon(),
            ItemBuilder.playerHeadUrl("6c8c54dc6c2d40625a72e95d8a80f04a8f9fead318f370a97a82ab8872542477").asIcon(),
            ItemBuilder.playerHeadUrl("47ed98ce4e10b59767334789e33b112dafba93691eef64273288c2f1fd324e34").asIcon(),
            ItemBuilder.playerHeadUrl("9e6800bc7b0230694b1c0f24bfa9edb22f3478f9e4ee7fc4c4398a8799f3840e").asIcon(),
            ItemBuilder.playerHeadUrl("826aa157fe7680b3bd21d53c061e4a61c46a96078d641ca6bbfc604e219de19e").asIcon(),
            ItemBuilder.playerHeadUrl("586b745566284a05366baff2807d9d8f8344612aabddeb012c47c7252e34e731").asIcon()
    };

    private final GamePlayer player;
    private final HimariActionList actionList;

    public HimariDiceAnimation(@Nonnull GamePlayer player, @Nonnull HimariActionList actionList) {
        this.player = player;
        this.actionList = actionList;
    }

    public void play(int duration) {
        final ArmorStand diceStand = Entities.ARMOR_STAND.spawn(
                player.getLocationInFrontFromEyes(1.5d), self -> {
                    self.getEquipment().setHelmet(DICE_TEXTURE[0]);
                    self.setSmall(true);
                    self.setInvisible(true);
                    self.setInvulnerable(true);
                    self.setSilent(true);

                    CFUtils.lockArmorStand(self);
                }
        );

        new PlayerTickingGameTask(player) {
            @Override
            public void cancelBecauseOfDeath() {
                diceStand.remove();
            }

            @Override
            public void run(int tick) {
                final Location location = player.getLocationInFrontFromEyes(1.5d).subtract(0, 1, 0);

                // Apply slowness to player as well
                player.addPotionEffect(PotionEffectType.SLOWNESS, 2, 2);

                // Remove
                if (tick >= (duration + 25)) {
                    diceStand.remove();
                    cancel();
                    return;
                }

                // Reveal
                if (tick == duration) {
                    final HimariAction action = actionList.getRandomAction(player);
                    final int index = actionList.indexOf(action);

                    // Execute action
                    action.execute(player);

                    // Show correct dice
                    diceStand.getEquipment().setHelmet(DICE_TEXTURE[index]);
                    diceStand.setHeadPose(new EulerAngle(Math.toRadians(-70f), 0.0f, 0.0f));
                    diceStand.customName(Component.text(index + 1, NamedTextColor.GREEN, TextDecoration.BOLD));
                    diceStand.setCustomNameVisible(true);

                    location.setYaw(0.0f);
                    location.add(0, 0.75d, 0);

                    // Fx
                    player.spawnWorldParticle(location, Particle.LARGE_SMOKE, 5, 0.2d, 0.2d, 0.2d, 0.05f);
                }
                // Animate dice
                else if (tick < duration) {
                    diceStand.setHeadPose(randomAngle());

                    // Fx
                    if (modulo(4)) {
                        player.playWorldSound(Sound.BLOCK_WOOD_STEP, 0.5f + (1f / (tick % 4)));
                    }
                }

                diceStand.teleport(location);
            }
        }.runTaskTimer(0, 1);
    }

    private EulerAngle randomAngle() {
        return new EulerAngle(
                Math.toRadians(player.random.nextDouble(45, 90)),
                Math.toRadians(player.random.nextDouble(45, 90)),
                Math.toRadians(player.random.nextDouble(45, 90))
        );
    }
}
