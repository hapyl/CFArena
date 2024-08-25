package me.hapyl.fight.game.talents.hercules;

import me.hapyl.eterna.module.player.PlayerLib;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.registry.Key;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class HerculesJump extends Talent {
    public HerculesJump(@Nonnull Key key) {
        super(key, "Updraft");

        setDescription("""
                Instantly propel yourself high up to perform plunging attack.
                """
        );

        setCooldownSec(10);
        setItem(Material.SLIME_BALL);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        player.setVelocity(new Vector(0.0d, 1.05d, 0.0d));

        PlayerLib.playSound(player.getLocation(), Sound.ENTITY_SLIME_JUMP, 0.5f);
        PlayerLib.playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1.25f);

        return Response.OK;
    }
}
