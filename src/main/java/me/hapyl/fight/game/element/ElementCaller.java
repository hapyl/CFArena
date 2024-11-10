package me.hapyl.fight.game.element;

import me.hapyl.fight.CF;
import me.hapyl.fight.event.custom.game.GameEndEvent;
import me.hapyl.fight.event.custom.game.GameStartEvent;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.PlayerDataHandler;
import me.hapyl.fight.game.heroes.TickingHero;
import me.hapyl.fight.game.maps.Level;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.util.Materials;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

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
        TalentRegistry.values().forEach(talent -> {
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
        TalentRegistry.values().forEach(talent -> {
            talent.onStop(instance);
        });

        // Call players
        CF.getPlayers().forEach(player -> {
            CALLER.onStop(player);

            if (player.isSpectator()) {
                return;
            }
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
        TalentRegistry.values().forEach(talent -> {
            talent.onPlayersRevealed(instance);
        });

        // Call players
        CF.getPlayers().forEach(CALLER::onPlayersRevealed);
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        forEachPlayerElementHandler(player, element -> {
            element.onStart(player);
        });
    }

    @Override
    public void onStop(@Nonnull GamePlayer player) {
        forEachPlayerElementHandler(player, element -> {
            element.onStop(player);
        });
    }

    @Override
    public void onPlayersRevealed(@Nonnull GamePlayer player) {
        forEachPlayerElementHandler(player, element -> {
            element.onPlayersRevealed(player);
        });
    }

    @Override
    public void onRespawn(@Nonnull GamePlayer player) {
        forEachPlayerElementHandler(player, element -> {
            element.onRespawn(player);
        });
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        forEachPlayerElementHandler(player, element -> {
            element.onDeath(player);
        });

        final Hero hero = player.getHero();
        final Weapon weapon = hero.getWeapon();

        // Stop cooldowns
        weapon.getAbilities().forEach(ability -> ability.stopCooldown(player));

        // Execute talents here
        player.executeTalentsOnDeath();

        // Reset data if applicable
        if (hero instanceof PlayerDataHandler<?> handler) {
            handler.removePlayerData(player);
        }
    }

    private void forEachPlayerElementHandler(GamePlayer player, Consumer<PlayerElementHandler> consumer) {
        consumer.accept(player.getAttributes());

        final Hero hero = player.getHero();
        consumer.accept(hero);

        final Level level = Manager.current().currentLevel();
        consumer.accept(level);

        level.getFeatures().forEach(consumer);

        hero.getNullSafeTalents().forEach(consumer);

        final Weapon weapon = hero.getWeapon();
        consumer.accept(weapon);
    }

}
