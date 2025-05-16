package me.hapyl.fight.game.skin;

import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.entry.SkinEntry;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.MoveType;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Lifecycle;
import org.bukkit.event.Listener;

public class SkinEffectManager implements Listener, Lifecycle {

    public static final long STANDING_STILL_THRESHOLD = 1_000L;

    public SkinEffectManager(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @Override
    public void onStart() {
        new TickingGameTask() {
            @Override
            public void run(int tick) {
                for (final GamePlayer player : CF.getAlivePlayers()) {
                    final PlayerProfile profile = CF.getProfile(player.getEntity());

                    if (!player.isValidForCosmetics()) {
                        continue;
                    }

                    final SkinEntry skinEntry = profile.getDatabase().skinEntry;
                    final Skins selectedSkin = skinEntry.getSelected(player.getHero());

                    if (selectedSkin == null) {
                        continue;
                    }

                    final Skin skin = selectedSkin.getSkin();

                    if (!player.hasMovedInLast(MoveType.KEYBOARD, STANDING_STILL_THRESHOLD)) {
                        skin.onStandingStill(player);
                    }

                    skin.onTick(player, tick);
                }
            }
        }.runTaskTimer(1, 1);
    }

    @Override
    public void onStop() {
    }

}
