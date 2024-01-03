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

public class VortexStar extends Talent {

    @DisplayField private final short maximumStars = 7;
    @DisplayField private final int pickupCooldown = 30;
    @DisplayField private final int summonCooldown = 100;

    private final PlayerMap<AstralStars> stars = PlayerMap.newMap();

    public VortexStar() {
        super("Astral Star");

        setDescription("""
                Summon an &eAstral Star&7 at your current location.
                &8&o;;If used nearby a star, it will be picked up.
                                
                &7&o;;The stars are your guide! But only &b&o{maximumStars}&7&o can exist at the same time.
                """);

        setType(Type.CREATABLE);
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
        return getStars(player).getStarAmount();
    }

    @Nonnull
    public AstralStars getStars(GamePlayer player) {
        return stars.computeIfAbsent(player, AstralStars::new);
    }

    @Override
    public void onStart() {
        new GameTask() {
            @Override
            public void run() {
                stars.values().forEach(AstralStars::tick);
            }
        }.runTaskTimer(0, 3);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final int starsAmount = getStarAmount(player);
        final AstralStars stars = getStars(player);
        final AstralStar nearestStar = stars.getFirstStarToPickup();

        if (nearestStar != null) {
            stars.removeStar(nearestStar);
            startCd(player, pickupCooldown);

            // Fx
            player.playSound(Sound.BLOCK_BELL_RESONATE, 1.95f);
            player.sendMessage("&e⭐ &aPicked up an Astral Star.");

            return Response.AWAIT;
        }

        if (starsAmount >= maximumStars) {
            player.playSound(Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
            return Response.error("Out of Astral Stars!");
        }

        stars.summonStar(player.getEyeLocation());
        startCd(player, summonCooldown);

        // Fx
        player.playSound(Sound.BLOCK_BELL_USE, 1.75f);
        player.sendMessage("&e⭐ &aCreated new Astral Star.");

        return Response.OK;
    }
}
