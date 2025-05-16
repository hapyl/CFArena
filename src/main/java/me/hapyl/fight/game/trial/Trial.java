package me.hapyl.fight.game.trial;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.scoreboard.Scoreboarder;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.loadout.HotBarSlot;
import me.hapyl.fight.game.lobby.LobbyItems;
import me.hapyl.fight.game.maps.EnumLevel;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.trial.objecitive.*;
import me.hapyl.fight.util.Lifecycle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Husk;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Function;

/**
 * This is a repayable trial as a {@link HeroRegistry#TUTORIAL_ARCHER}
 */
public class Trial extends TickingGameTask implements Lifecycle {

    private static final Hero trialHero = HeroRegistry.TUTORIAL_ARCHER;
    private static final ItemStack lockedTalent = new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE)
            .setName("&cLocked Talent")
            .setSmartLore("Progress the tutorial to unlock this talent!")
            .asIcon();

    protected final Set<TrialEntity> entities;

    private final PlayerProfile profile;
    private final Hero previousHero;
    private final GamePlayer player;
    private final LinkedList<TrialObjective> objectives;
    private int stage;
    private int husksThisStage;

    public Trial(PlayerProfile profile) {
        this.profile = profile;
        this.previousHero = profile.getHero();

        profile.setSelectedHero(trialHero, false);

        this.player = profile.createGamePlayer();

        this.objectives = Lists.newLinkedList();
        initObjectives();

        this.entities = Sets.newHashSet();
    }

    @Nonnull
    public PlayerProfile getProfile() {
        return profile;
    }

    @Nonnull
    public GamePlayer getPlayer() {
        return player;
    }

    public int huskCount() {
        return entities.size();
    }

    @Nonnull
    public String getHuskString() {
        return "&c%s&7/&a%s".formatted(husksThisStage - huskCount(), husksThisStage);
    }

    @Override
    public void onStart() {
        player.teleport(EnumLevel.TRAINING_GROUNDS.getLevel().getLocation());

        player.resetPlayer();
        player.prepare(trialHero);

        player.hide();

        // Remove talents because they will be explained one by one
        player.setItem(HotBarSlot.TALENT_1, lockedTalent);
        player.setItem(HotBarSlot.TALENT_2, lockedTalent);

        showObjective();

        runTaskTimer(0, 1);
    }

    @Override
    public void onStop() {
        trialHero.onStop(player);

        profile.setSelectedHero(previousHero);
        profile.resetGamePlayer();

        final Player bukkitPlayer = player.getEntity();

        player.getInventory().clear();
        player.teleport(EnumLevel.SPAWN.getLevel().getLocation());

        LobbyItems.giveAll(bukkitPlayer);

        entities.forEach(LivingGameEntity::forceRemove);
        entities.clear();

        cancel();
    }

    @Nonnull
    public TrialEntity spawnEntity(@Nonnull Location location, @Nonnull Function<Husk, TrialEntity> consumer) {
        final TrialEntity gameEntity = CF.createEntity(location, Entities.HUSK, entity -> {
            // The entity should only be visible to the player whose trial it is
            entity.setVisibleByDefault(false);

            // Make sure it ain't a baby
            entity.setAdult();

            return consumer.apply(entity);
        });

        player.showEntity(gameEntity);
        entities.add(gameEntity);

        husksThisStage++;
        return gameEntity;
    }

    @Override
    public void run(int tick) {
        final TrialObjective currentObjective = getCurrentObjective();

        if (currentObjective != null) {
            currentObjective.tick();
        }
    }

    public void updateScoreboard(@Nonnull Scoreboarder builder) {
        final TrialObjective objective = getCurrentObjective();

        builder.addLine("&b&l❓ Trial:");
        builder.addLine(" &6ᴏʙᴊᴇᴄᴛɪᴠᴇ: &f" + (objective != null ? objective.getName() : "&8None!"));

        if (objective != null) {
            final String[] scoreboardStrings = objective.getScoreboardStrings();

            for (String string : scoreboardStrings) {
                ItemBuilder.splitString(string, 25).forEach(line -> {
                    builder.addLine("&7&o  " + line);
                });
            }
        }

        builder.addLine("");
    }

    @Nullable
    public TrialObjective getCurrentObjective() {
        return objectives.peek();
    }

    public void nextObjective() {
        final TrialObjective currentObjective = getCurrentObjective();

        if (currentObjective != null) {
            player.sendMessage("");
            player.sendMessage("&2&lOBJECTIVE COMPLETE!");
            player.sendMessage("&f " + currentObjective.getName());
            player.sendMessage("");

            player.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 0.0f);

            currentObjective.onStop();
        }

        stage++;
        husksThisStage = 0;

        objectives.poll();
        showObjective();
    }

    public int getStage() {
        return stage;
    }

    public void complete() {
    }

    private void initObjectives() {
        objectives.add(new TrialObjectiveShootHusks(this));
        objectives.add(new TrialObjectiveTripleShot(this));
        objectives.add(new TrialObjectiveShock(this));
        objectives.add(new TrialObjectiveHealth(this));
        objectives.add(new TrialObjectiveCritical(this));

        // Block all paths
        objectives.forEach(objective -> {
            final TrialObjectivePath path = objective.getPath();

            if (path != null) {
                path.onStart();
            }
        });
    }

    private void showObjective() {
        final TrialObjective objective = getCurrentObjective();

        if (objective == null) {
            complete();
            return;
        }

        // Display the current objective
        GameTask.runLater(() -> {
            player.sendMessage("");
            player.sendMessage("&6&lNEW OBJECTIVE!");
            player.sendMessage(" " + objective.getName());

            objective.getDescriptionSplit(50).forEach(string -> {
                player.sendMessage("  &7&o" + string);
            });

            player.sendMessage("");

            player.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.75f);
        }, 20);

        // Actually call onStart right away
        objective.onStart();
    }

}
