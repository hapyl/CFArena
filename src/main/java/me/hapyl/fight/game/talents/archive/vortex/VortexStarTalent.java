package me.hapyl.fight.game.talents.archive.vortex;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class VortexStarTalent extends Talent {

    @DisplayField private final short maximumStars = 5;
    @DisplayField public final double healthSacrificePerStar = 80d / maximumStars;

    private final PlayerMap<AstralStarList> stars = PlayerMap.newMap();

    public VortexStarTalent() {
        super("Astral Star");

        setDescription("""
                &4Sacrifice &c{healthSacrificePerStar} ❤&7 to summon an &eAstral Star&7 at your &ncurrent&7 location.
                                
                The &estar&7 inherits the &4sacrificed &c❤&7 and &ncan&7 be destroyed.
                                
                &c;;If the star is destroyed, the sacrificed health will &nnot&c be returned!
                                
                &8;;Up to {maximumStars} stars can exist simultaneously.
                """);

        setType(Type.CREATABLE);
        setItem(Material.NETHER_STAR);
        setCooldown(100);
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        stars.removeAnd(player, AstralStarList::clear);
    }

    @Override
    public void onStop() {
        stars.forEachAndClear(AstralStarList::clear);
    }

    public int getStarAmount(GamePlayer player) {
        return getStars(player).getStarAmount();
    }

    @Nonnull
    public AstralStarList getStars(GamePlayer player) {
        return stars.computeIfAbsent(player, AstralStarList::new);
    }

    @Override
    public void onStart() {
        new GameTask() {
            @Override
            public void run() {
                stars.values().forEach(AstralStarList::tick);
            }
        }.runTaskTimer(0, 1);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final int starsAmount = getStarAmount(player);
        final AstralStarList stars = getStars(player);

        final double health = player.getHealth();

        if (health < healthSacrificePerStar + 1) {
            return Response.error("Not enough health!");
        }

        if (starsAmount >= maximumStars) {
            player.playSound(Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
            return Response.error("Out of Astral Stars!");
        }

        stars.summonStar(player.getEyeLocation(), this);

        // Fx
        player.playSound(Sound.BLOCK_BELL_USE, 1.75f);
        player.sendMessage("&e⭐ &aCreated new Astral Star.");

        return Response.OK;
    }
}
