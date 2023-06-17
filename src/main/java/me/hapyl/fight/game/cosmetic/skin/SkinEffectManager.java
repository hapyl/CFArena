package me.hapyl.fight.game.cosmetic.skin;

import me.hapyl.fight.Main;
import me.hapyl.fight.game.GameElement;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.IGameInstance;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.task.GameTask;
import org.bukkit.event.Listener;

public class SkinEffectManager implements Listener, GameElement {

    public SkinEffectManager(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @Override
    public void onStart() {
        new GameTask() {
            private int tick = 0;

            @Override
            public void run() {
                final IGameInstance currentGame = Manager.current().getCurrentGame();

                for (GamePlayer player : currentGame.getAlivePlayers()) {
                    final PlayerProfile profile = PlayerProfile.getProfile(player.getPlayer());
                    final Skins enumSkin = null; // profile.getSkin();
                    if (enumSkin == null) {
                        continue;
                    }

                    final Skin skin = enumSkin.getSkin();

                    if (!player.hasMovedInLast(1000)) {
                        skin.onStandingStill(player.getPlayer());
                    }

                    skin.onTick(player.getPlayer(), tick);
                }

                tick++;
            }
        }.runTaskTimer(1, 1);
    }

    @Override
    public void onStop() {

    }
}
