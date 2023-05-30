package me.hapyl.fight.game.talents.storage.witcher;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Kven extends Talent {

    private final Map<Player, Integer> shieldCharges = new HashMap<>();

    public Kven() {
        super("Quen", "Applies two charges of Quen shield that blocks damage.");

        setItem(Material.FLOWER_POT);
        setCooldownSec(30);
    }

    @Override
    public void onStop() {
        shieldCharges.clear();
    }

    public int getShieldCharge(Player player) {
        return shieldCharges.getOrDefault(player, 0);
    }

    public void removeShieldCharge(Player player) {
        shieldCharges.put(player, getShieldCharge(player) - 1);
        if (getShieldCharge(player) <= 0) {
            shieldCharges.remove(player);
            Chat.sendMessage(player, "&aYour &l%s &ashield has broke!", this.getName());
        }
        else {
            Chat.sendMessage(player, "&aOne of your &l%s &ashields broke!", this.getName());
        }

        // fx
        PlayerLib.playSound(player, Sound.ITEM_SHIELD_BREAK, 0.75f);
    }

    @Override
    public Response execute(Player player) {
        if (getShieldCharge(player) != 0) {
            return Response.error("Already have shield applied!");
        }

        new GameTask() {
            private double theta = 0.0d;

            @Override
            public void run() {

                final Location location = player.getLocation().add(0.0d, 1.0d, 0.0d);
                final int shieldCharge = getShieldCharge(player);

                if (shieldCharge <= 0) {
                    this.cancel();
                    return;
                }

                final double x = 0.75d * Math.sin(theta);
                final double z = 0.75d * Math.cos(theta);

                double offset = 0.0d;
                for (int i = 0; i < shieldCharge; i++) {
                    boolean swapSide = i % 2 == 0;
                    location.add(swapSide ? x : z, offset, swapSide ? z : x);
                    PlayerLib.spawnParticle(location, Particle.ENCHANTMENT_TABLE, 1, 0, 0, 0, 0);
                    location.subtract(swapSide ? x : z, offset, swapSide ? z : x);
                    offset += 0.2d;
                }

                theta = theta >= 36 ? 0 : theta + 0.1d;

            }
        }.runTaskTimer(0, 1);

        // fx
        Chat.sendMessage(player, "&a%s Shields have been activated!", this.getName());
        PlayerLib.playSound(player, Sound.BLOCK_BELL_RESONATE, 2.0f);
        shieldCharges.put(player, 2);

        return Response.OK;
    }
}
