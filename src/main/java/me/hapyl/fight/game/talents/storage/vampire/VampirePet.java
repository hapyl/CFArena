package me.hapyl.fight.game.talents.storage.vampire;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.LinkedKeyValMap;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;

public class VampirePet extends Talent {

    private final LinkedKeyValMap<Player, Bat> pets;
    private final int ATTACK_PERIOD = 30;

    public VampirePet() {
        super("Dracula Jr.");
        setItem(Material.BAT_SPAWN_EGG);

        setDescription("Call upon the lord of bats to aid you in battle, summoning a bat that will periodically nearby attack opponents.");
        setDurationSec(10);
        setCd(getDuration() + (8 * 20));

        pets = LinkedKeyValMap.of();
    }

    public Bat getPet(Player player) {
        return pets.getValue(player);
    }

    @Override
    public void onDeath(Player player) {
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
                    final Entity nearestEntity = Utils.getNearestEntity(player.getLocation(), 20.0d, entity -> {
                        return Utils.isEntityValid(entity, player) && !entity.equals(bat);
                    });

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
                    projectile.setShooter(player);
                });
            }
        }.runTaskTimer(0, ATTACK_PERIOD);
    }

    @Override
    public Response execute(Player player) {
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
        Chat.sendMessage(player, "&a*bat noises*!");
        PlayerLib.playSound(player, Sound.ENTITY_BAT_TAKEOFF, 0.0f);

        GameTask.runLater(() -> {
            if (pet.isDead()) {
                return;
            }

            pets.useValueAndRemove(player, Bat::remove);

            PlayerLib.playSound(player, Sound.ENTITY_BAT_DEATH, 0.0f);
            Chat.sendMessage(player, "&c*bat noises*...");
        }, getDuration());

        return Response.OK;
    }
}
