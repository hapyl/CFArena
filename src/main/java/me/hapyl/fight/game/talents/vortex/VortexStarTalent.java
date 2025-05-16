package me.hapyl.fight.game.talents.vortex;


import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class VortexStarTalent extends Talent {

    @DisplayField private final short maximumStars = 5;
    @DisplayField(percentage = true) public final double healthSacrificePerStar = 1d / maximumStars;
    @DisplayField public final double maxStarDamage = healthSacrificePerStar / 5;

    private final PlayerMap<AstralStarList> stars = PlayerMap.newMap();

    public VortexStarTalent(@Nonnull Key key) {
        super(key, "Astral Star");

        setDescription("""
                Sacrifice &c{healthSacrificePerStar}&7 of %s to summon an &eAstral Star&7 at your &ncurrent&7 location.
                
                The &estar&7 inherits the &4sacrificed &c❤&7 and &ncan&7 be destroyed.
                
                &8&o;;If the star is destroyed, the sacrificed health will not be returned!
                
                &8&o;;Up to {maximumStars} stars can exist simultaneously.
                """.formatted(AttributeType.MAX_HEALTH)
        );

        setType(TalentType.MOVEMENT);
        setMaterial(Material.NETHER_STAR);
        setCooldown(50);
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        stars.removeAnd(player, AstralStarList::clear);
    }

    @Override
    public void onStop(@Nonnull GameInstance instance) {
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
    public void onStart(@Nonnull GameInstance instance) {
        new GameTask() {
            @Override
            public void run() {
                stars.values().forEach(AstralStarList::tick);
            }
        }.runTaskTimer(0, 1);
    }

    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        final int starsAmount = getStarAmount(player);
        final AstralStarList stars = getStars(player);
        
        final double maxHealth = player.getMaxHealth();
        final double health = player.getHealth();
        
        final double healthSacrifice = maxHealth * healthSacrificePerStar;

        if (health < healthSacrifice) {
            return Response.error("Not enough health!");
        }

        if (starsAmount >= maximumStars) {
            player.playSound(Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
            return Response.error("Out of Astral Stars!");
        }

        stars.summonStar(player.getEyeLocation(), this, healthSacrifice);
        
        final EntityAttributes attributes = player.getAttributes();
        attributes.subtract(AttributeType.MAX_HEALTH, healthSacrifice);

        // Fx
        player.playSound(Sound.BLOCK_BELL_USE, 1.75f);
        player.sendMessage("&e⭐ &aCreated new Astral Star.");

        return Response.OK;
    }
}
