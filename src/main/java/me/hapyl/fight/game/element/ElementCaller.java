package me.hapyl.fight.game.element;

import me.hapyl.fight.CF;
import me.hapyl.fight.event.custom.game.GameEndEvent;
import me.hapyl.fight.event.custom.game.GameStartEvent;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.cosmetic.crate.Crates;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.PlayerDataHandler;
import me.hapyl.fight.game.heroes.TickingHero;
import me.hapyl.fight.game.maps.Level;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.util.EnumIterators;
import me.hapyl.fight.util.Materials;

import javax.annotation.Nonnull;

/**
 * A single instanced caller responsible for operation of {@link StrictElementHandler} and {@link StrictPlayerElementHandler}.
 * <br>
 * The caller calls in specific order.
 */
public final class ElementCaller implements StrictElementHandler, StrictPlayerElementHandler {

    /**
     * The sole {@link ElementCaller} instance.
     */
    public static final ElementCaller CALLER = new ElementCaller();

    private ElementCaller() { // There should only be one instance of the caller!
    }

    @Override
    public void onStart(@Nonnull GameInstance instance) {
        // Game mode is a special boy
        instance.getMode().onStart(instance);

        // Call heroes
        for (Hero hero : HeroRegistry.values()) {
            hero.onStart(instance);

            final Weapon weapon = hero.getWeapon();
            weapon.onStart(instance);

            // Schedule task if applicable
            if (hero instanceof TickingHero tickingHero) {
                new TickingGameTask() {
                    @Override
                    public void run(int tick) {
                        tickingHero.tick(tick);
                    }
                }.runTaskTimer(1, 1);
            }
        }

        // Call level
        instance.currentLevel().onStart(instance);

        // Call talents
        EnumIterators.ofWrapper(Talents.class, Talents::getTalent, talent -> {
            talent.onStart(instance);
        });

        // Call event
        new GameStartEvent(instance).call();
    }

    @Override
    public void onStop(@Nonnull GameInstance instance) {
        // Reset material cooldowns
        Materials.setItemCooldowns(0);

        // Call heroes
        for (Hero hero : HeroRegistry.values()) {
            hero.onStop(instance);

            // Reset player data if applicable
            if (hero instanceof PlayerDataHandler<?> handler) {
                handler.resetPlayerData();
            }

            // Call weapons
            final Weapon weapon = hero.getWeapon();

            weapon.onStop(instance);
            weapon.getAbilities().forEach(Ability::clearCooldowns);
        }

        // Call level
        instance.currentLevel().onStop(instance);

        // Call talents
        EnumIterators.ofWrapper(Talents.class, Talents::getTalent, talent -> {
            talent.onStop(instance);
        });

        // Call players
        CF.getPlayers().forEach(player -> {
            CALLER.onStop(player);

            if (player.isSpectator()) {
                return;
            }

            // Give crates to players
            Crates.grant(player, Crates.randomCrate());
        });

        // Call event
        new GameEndEvent(instance).call();
    }

    @Override
    public void onPlayersRevealed(@Nonnull GameInstance instance) {
        // Call heroes
        for (Hero hero : HeroRegistry.values()) {
            hero.onPlayersRevealed(instance);

            final Weapon weapon = hero.getWeapon();
            weapon.onPlayersRevealed(instance);
        }

        // Call level
        instance.currentLevel().onPlayersRevealed(instance);

        // Call talents
        EnumIterators.ofWrapper(Talents.class, Talents::getTalent, talent -> {
            talent.onPlayersRevealed(instance);
        });

        // Call players
        CF.getPlayers().forEach(CALLER::onPlayersRevealed);
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        player.getAttributes().onStart(player);

        final Hero hero = player.getHero();
        hero.onStart(player);

        final Level level = Manager.current().currentLevel();
        level.onStart(player);

        level.getFeatures().forEach(feature -> {
            feature.onStart(player);
        });

        hero.getNullSafeTalents().forEach(talent -> {
            talent.onStart(player);
        });

        final Weapon weapon = hero.getWeapon();
        weapon.onStart(player);
    }

    @Override
    public void onStop(@Nonnull GamePlayer player) {
        player.getAttributes().onStop(player);

        final Hero hero = player.getHero();
        hero.onStop(player);

        final Level level = Manager.current().currentLevel();
        level.onStop(player);

        level.getFeatures().forEach(feature -> {
            feature.onStop(player);
        });

        hero.getNullSafeTalents().forEach(talent -> {
            talent.onStop(player);
        });

        final Weapon weapon = hero.getWeapon();
        weapon.onStop(player);
    }

    @Override
    public void onPlayersRevealed(@Nonnull GamePlayer player) {
        player.getAttributes().onPlayersRevealed(player);

        final Hero hero = player.getHero();
        hero.onPlayersRevealed(player);

        final Level level = Manager.current().currentLevel();
        level.onPlayersRevealed(player);

        level.getFeatures().forEach(feature -> {
            feature.onPlayersRevealed(player);
        });

        hero.getNullSafeTalents().forEach(talent -> {
            talent.onPlayersRevealed(player);
        });

        final Weapon weapon = hero.getWeapon();
        weapon.onPlayersRevealed(player);
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        player.getAttributes().onDeath(player);

        final Hero hero = player.getHero();
        hero.onDeath(player);

        final Level level = Manager.current().currentLevel();
        level.onDeath(player);

        level.getFeatures().forEach(feature -> {
            feature.onDeath(player);
        });

        hero.getNullSafeTalents().forEach(talent -> {
            talent.onDeath(player);
        });

        final Weapon weapon = hero.getWeapon();
        weapon.onDeath(player);
        weapon.getAbilities().forEach(ability -> ability.stopCooldown(player));

        player.executeTalentsOnDeath();

        // Reset data if applicable
        if (hero instanceof PlayerDataHandler<?> handler) {
            handler.removePlayerData(player);
        }
    }

}
