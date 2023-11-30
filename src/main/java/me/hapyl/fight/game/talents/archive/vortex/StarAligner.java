package me.hapyl.fight.game.talents.archive.vortex;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.vortex.Vortex;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class StarAligner extends Talent {

    public StarAligner() {
        super(
                "Star Aligner", """
                        Align with your target &eAstral Star&7, &bteleporting&7 to it and launching an &6Astral Slash&7.
                                                
                        &8;;The star is consumed on teleport.
                        """);

        setType(Type.DAMAGE);
        setItem(Material.BEETROOT_SEEDS);
        setCooldownSec(1);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final AstralStars stars = Talents.VORTEX_STAR.getTalent(VortexStar.class).getStars(player);
        final AstralStar targetStar = stars.getTargetStar();

        if (targetStar == null) {
            return Response.error("Not targeting any astral stars!");
        }

        final Location previousLocation = player.getEyeLocation();

        stars.removeStar(targetStar);
        targetStar.teleport(player);

        final Vortex hero = Heroes.VORTEX.getHero(Vortex.class);

        hero.performStarSlash(previousLocation, targetStar.getLocation(), player);
        hero.addDreamStack(player);

        PlayerLib.playSound(Sound.ENTITY_ENDERMAN_TELEPORT, 1.75f);
        return Response.OK;
    }
}
