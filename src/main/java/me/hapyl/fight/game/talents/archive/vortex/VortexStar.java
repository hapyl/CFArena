package me.hapyl.fight.game.talents.archive.vortex;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nonnull;
import java.util.List;

public class VortexStar extends Talent {

    @DisplayField private final short maximumStars = 5;
    private final PlayerMap<AstralStars> stars = PlayerMap.newMap();

    public VortexStar() {
        super("Astral Star");
        setDescription("""
                Summons an Astral Star at your current location. If used nearby placed Astral Star, the star will be picked up.
                      
                You may have maximum of &b{maximumStars} &7stars at the same time.
                """);

        setItem(Material.NETHER_STAR);
        setCooldown(DYNAMIC);
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        getStars(player).clear();
        stars.remove(player);
    }

    @Override
    public void onStop() {
        stars.values().forEach(AstralStars::clear);
        stars.clear();
    }

    public int getStarAmount(GamePlayer player) {
        return getStars(player).getStarsAmount();
    }

    public AstralStars getStars(GamePlayer player) {
        return stars.computeIfAbsent(player, AstralStars::new);
    }

    @Override
    public void onStart() {
        new GameTask() {
            @Override
            public void run() {
                stars.values().forEach(as -> {
                    as.tickStars();
                    as.updateColors();
                });
            }
        }.runTaskTimer(0, 10);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final int starsAmount = getStarAmount(player);
        final AstralStars stars = getStars(player);
        final List<LivingEntity> twoStars = stars.getLastTwoStars();

        if (twoStars.size() >= 1) {
            final LivingEntity lastStar = twoStars.get(0);
            if (lastStar.getLocation().distance(player.getLocation()) <= stars.getPickupDistance()) {
                stars.removeStar(lastStar);
                startCd(player, 80);

                player.playSound(Sound.BLOCK_BELL_RESONATE, 1.95f);
                player.sendMessage("&aPick up an Astral Star.");

                return Response.OK;
            }
        }

        if (starsAmount >= maximumStars) {
            player.playSound(Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
            return Response.error("Out of Astral Stars!");
        }

        startCd(player, 200);
        stars.summonStar(player.getEyeLocation());

        player.playSound(Sound.BLOCK_BELL_USE, 1.75f);
        player.sendMessage("&aCreated new Astral Star.");

        return Response.OK;
    }
}
