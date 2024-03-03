package me.hapyl.fight.game.heroes.archive.shaman;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.UltimateResponse;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Tick;
import org.bukkit.*;

import javax.annotation.Nonnull;

public class ShamanUltimate extends UltimateTalent {

    @DisplayField private final double increaseRadius = 7.5d;
    @DisplayField private final double effectResIncrease = 0.5d;
    @DisplayField private final int effectResIncreaseDuration = Tick.fromSecond(12);

    private final TemperInstance temperInstance = Temper.SPIRITUAL_CLEANSING
            .newInstance()
            .increase(AttributeType.EFFECT_RESISTANCE, effectResIncrease);

    public ShamanUltimate() {
        super("Spiritual Cleansing", 45);

        setDescription("""
                Instantly cleanse all &cnegative&7 effects from nearby &aallies&7.
                       
                Also increase their %s for &b{effectResIncreaseDuration}&7.
                """.formatted(AttributeType.EFFECT_RESISTANCE));

        setType(Type.SUPPORT);
        setItem(Material.MILK_BUCKET);
        setSound(Sound.ENTITY_GOAT_SCREAMING_MILK, 0.0f);
        setCooldownSec(30);
    }

    @Nonnull
    @Override
    public UltimateResponse useUltimate(@Nonnull GamePlayer player) {
        Collect.nearbyEntities(player.getLocation(), increaseRadius).forEach(entity -> {
            if (!player.isSelfOrTeammate(entity)) {
                return;
            }

            // Remove effects
            player.removeEffectsByType(EffectType.NEGATIVE);

            temperInstance.temper(entity, effectResIncreaseDuration);

            // Fx
            final Location location = entity.getLocation();

            entity.spawnWorldParticle(location, Particle.SPELL_MOB, 20, 0.25d, 0.5d, 0.25d, 0.7f);
            entity.playWorldSound(Sound.ENTITY_WITCH_DRINK, 0.0f);

            if (player == entity) {
                player.sendMessage(AttributeType.EFFECT_RESISTANCE.getCharacter() + " You cleansed yourself!");
            }
            else {
                entity.sendMessage(AttributeType.EFFECT_RESISTANCE.getCharacter() + " &d%s cleansed you!".formatted(player.getName()));
            }

        });

        // Fx
        player.playWorldSound(Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1.25f);
        player.playWorldSound(Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 1.25f);

        return UltimateResponse.OK;
    }
}
