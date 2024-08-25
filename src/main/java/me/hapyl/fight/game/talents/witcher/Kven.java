package me.hapyl.fight.game.talents.witcher;


import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.registry.Key;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.eterna.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class Kven extends Talent {

    private final PlayerMap<Integer> shieldCharges = PlayerMap.newMap();

    public Kven(@Nonnull Key key) {
        super(key, "Quen");

        setDescription("""
                Applies &ntwo charges&7 of Quen shield that &8blocks&7 any damage.
                """
        );

        setType(TalentType.DEFENSE);
        setItem(Material.FLOWER_POT);
        setCooldownSec(30);
    }

    @Override
    public void onStop(@Nonnull GameInstance instance) {
        shieldCharges.clear();
    }

    public int getShieldCharge(GamePlayer player) {
        return shieldCharges.getOrDefault(player, 0);
    }

    public void removeShieldCharge(GamePlayer player) {
        shieldCharges.put(player, getShieldCharge(player) - 1);
        if (getShieldCharge(player) <= 0) {
            shieldCharges.remove(player);

            player.sendMessage("&aYour &l%s &ashield has broke!".formatted(getName()));
            player.playWorldSound(Sound.ITEM_SHIELD_BREAK, 0.5f);
        }
        else {
            player.sendMessage("&aOne of your &l%s &ashields broke!".formatted(getName()));
            player.playWorldSound(Sound.ITEM_SHIELD_BREAK, 0.75f);
        }
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        if (getShieldCharge(player) != 0) {
            return Response.error("Already have shield applied!");
        }

        shieldCharges.put(player, 2);

        new GameTask() {
            private double theta = 0.0d;

            @Override
            public void run() {
                final Location location = player.getLocation().add(0.0d, 1.0d, 0.0d);
                final int shieldCharge = getShieldCharge(player);

                if (shieldCharge <= 0) {
                    cancel();
                    return;
                }

                final double x = 0.75d * Math.sin(theta);
                final double z = 0.75d * Math.cos(theta);

                double offset = 0.0d;
                for (int i = 0; i < shieldCharge; i++) {
                    boolean swapSide = i % 2 == 0;
                    location.add(swapSide ? x : z, offset, swapSide ? z : x);
                    PlayerLib.spawnParticle(location, Particle.ENCHANT, 1, 0, 0, 0, 0);
                    location.subtract(swapSide ? x : z, offset, swapSide ? z : x);
                    offset += 0.2d;
                }

                theta = theta >= 36 ? 0 : theta + 0.1d;

            }
        }.runTaskTimer(0, 1);

        // Fx
        player.sendMessage("&a%s Shields have been activated!".formatted(getName()));
        player.playSound(Sound.BLOCK_BELL_RESONATE, 2.0f);

        return Response.OK;
    }
}
