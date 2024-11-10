package me.hapyl.fight.game.heroes.echo;

import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Constants;
import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.WarningType;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.echo.EchoTrapTalent;
import me.hapyl.fight.game.talents.echo.EchoWorldTalent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import javax.annotation.Nonnull;
import java.util.Set;

public class Echo extends Hero implements Disabled, PlayerDataHandler<EchoData>, Listener {

    private final PlayerDataMap<EchoData> echoData = PlayerMap.newDataMap(EchoData::new);

    public Echo(@Nonnull Key key) {
        super(key, "Echo");

        setDescription("""
                A spirit stuck between the %s and "reality".
                """.formatted(Named.ECHO_WORLD.getName())
        );

        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.HEXBANE, Archetype.POWERFUL_ULTIMATE);

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(28, 34, 38, TrimPattern.RAISER, TrimMaterial.IRON);
        equipment.setLeggings(28, 34, 38, TrimPattern.SNOUT, TrimMaterial.IRON);

        setItem("906b574d7ab53d5337915e3071e6cfcff6039dfb3f81b5ab66baf2380c962829");

        setUltimate(new EchoUltimate());
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        player.addEffect(Effects.INVISIBILITY, Constants.INFINITE_DURATION);
    }

    @Override
    public void onRespawn(@Nonnull GamePlayer player) {
        onStart(player);
    }

    @Override
    public boolean processInvisibilityDamage(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity, double damage) {
        return false;
    }

    @Override
    public boolean isValidIfInvisible(@Nonnull GamePlayer player) {
        return true;
    }

    @Override
    public EchoWorldTalent getFirstTalent() {
        return TalentRegistry.ECHO_WORLD;
    }

    @Override
    public EchoTrapTalent getSecondTalent() {
        return TalentRegistry.ECHO_TRAP;
    }

    @Override
    public Talent getPassiveTalent() {
        return null;
    }

    @Nonnull
    @Override
    public EchoUltimate getUltimate() {
        return (EchoUltimate) super.getUltimate();
    }

    @Nonnull
    @Override
    public PlayerDataMap<EchoData> getDataMap() {
        return echoData;
    }

    public final class EchoUltimate extends UltimateTalent {

        @DisplayField public final double nullSpaceRadius = 20;

        EchoUltimate() {
            super(Echo.this, "Nullspace", 60);

            setDescription("""
                    While in the %s, summon a &8null space&7 at your current location, pulling yourself and &7&nall&7 &4&nenemies&7 into it.
                    &8&o;;You can leave the null space anytime.
                    
                    All &4enemies&7 will leave their bodies and become an &fecho spirit&7.
                    
                    The &fspirits&7 &nmust&7 find and return into their body within {duration} or &4die&7.
                    &8&o;;You can also destroy enemy bodies, forcing them to die.
                    
                    &8&o;;You cannot enter %s while the ultimate is active.
                    """.formatted(Named.ECHO_WORLD, Named.ECHO_WORLD.getName())
            );

            setItem(Material.FIREWORK_STAR);
            setType(TalentType.IMPAIR);

            setCastDuration(20);

            setDurationSec(10);
            setCooldownSec(20);
        }

        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player) {
            final Location targetLocation = player.getLocationAnchored();

            return builder()
                    .onCastTick(tick -> {
                        final double d = Math.PI * 2 * ((double) tick / getCastDuration());
                        final double x = Math.sin(d) * nullSpaceRadius;
                        final double z = Math.cos(d) * nullSpaceRadius;

                        LocationHelper.offset(targetLocation, x, 0, z, () -> {
                            player.spawnWorldParticle(targetLocation, Particle.FLAME, 1);
                        });

                        LocationHelper.offset(targetLocation, z, 0, x, () -> {
                            player.spawnWorldParticle(targetLocation, Particle.FLAME, 1);
                        });

                        // Warn players
                        Collect.nearbyPlayers(targetLocation, nullSpaceRadius)
                                .forEach(target -> target.sendWarning(WarningType.WARNING));
                    })
                    .onCastEnd(() -> {
                        final EchoData data = getPlayerData(player);
                        final Set<GamePlayer> targets = Collect.enemyPlayers(player);

                        targets.add(player); // We're also in the null world
                    });
        }
    }

}
