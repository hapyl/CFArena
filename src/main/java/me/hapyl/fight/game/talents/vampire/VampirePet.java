package me.hapyl.fight.game.talents.vampire;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.util.LinkedKeyValMap;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;

import javax.annotation.Nonnull;

public class VampirePet extends Talent {

    private final LinkedKeyValMap<GamePlayer, Bat> pets;
    private final int ATTACK_PERIOD = 30;

    public VampirePet() {
        super(
                "Dracula Jr.",
                "Call upon the lord of bats to aid you in battle, summoning a bat that will periodically nearby attack opponents."
        );

        setItem(Material.BAT_SPAWN_EGG);
        setDurationSec(10);
        setCooldown(getDuration() + (8 * 20));

        pets = LinkedKeyValMap.of();
    }

    public Bat getPet(GamePlayer player) {
        return pets.getValue(player);
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        pets.useValueAndRemove(player, Bat::remove);
    }

    @Override
    public void onStop() {
        pets.forEach((p, b) -> b.remove());
        pets.clear();
    }

    @Override
    public void onStart() {
        new GameTask() {
            @Override
            public void run() {
                pets.forEach((player, bat) -> {
                    final LivingGameEntity nearestEntity = Collect.nearestEntityRaw(
                            player.getLocation(),
                            20.0d,
                            entity -> entity.isValid(player) && !entity.is(bat)
                    );

                    if (nearestEntity == null) {
                        return;
                    }

                    final Projectile projectile = bat.launchProjectile(
                            Snowball.class,
                            nearestEntity.getLocation()
                                    .toVector()
                                    .subtract(bat.getLocation().toVector())
                                    .normalize()
                                    .multiply(2.0d)
                    );

                    projectile.setGravity(false);
                    projectile.setShooter(player.getPlayer());
                });
            }
        }.runTaskTimer(0, ATTACK_PERIOD);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Bat oldPet = getPet(player);

        if (oldPet != null) {
            oldPet.remove();
        }

        final Bat pet = Entities.BAT.spawn(player.getLocation().add(0.0d, 1.0d, 0.0d), self -> {
            self.setAwake(true);
            self.setCustomName(Chat.format("&c" + getName()));
            self.setAI(false);
            self.setInvulnerable(true);
        });

        pets.put(player, pet);
        player.sendMessage("&a*bat noises*!");
        player.playSound(Sound.ENTITY_BAT_TAKEOFF, 0.0f);

        GameTask.runLater(() -> {
            if (pet.isDead()) {
                return;
            }

            pets.useValueAndRemove(player, Bat::remove);

            player.playSound(Sound.ENTITY_BAT_DEATH, 0.0f);
            player.sendMessage("&c*bat noises*...");
        }, getDuration());

        return Response.OK;
    }
}
