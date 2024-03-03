package me.hapyl.fight.game.cosmetic.skin;

import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.entry.SkinEntry;
import me.hapyl.fight.game.GameElement;
import me.hapyl.fight.game.IGameInstance;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.MoveType;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.TickingGameTask;
import org.bukkit.event.Listener;
import org.checkerframework.checker.units.qual.K;

public class SkinEffectManager implements Listener, GameElement {

    public SkinEffectManager(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @Override
    public void onStart() {
        new TickingGameTask() {
            @Override
            public void run(int tick) {
                for (final GamePlayer player : CF.getAlivePlayers()) {
                    final PlayerProfile profile = PlayerProfile.getProfile(player.getPlayer());

                    if (profile == null) {
                        continue;
                    }

                    final SkinEntry skinEntry = profile.getDatabase().skinEntry;
                    final Skins selectedSkin = skinEntry.getSelected(player.getEnumHero());

                    if (selectedSkin == null) {
                        continue;
                    }

                    final Skin skin = selectedSkin.getSkin();

                    if (skin instanceof SkinEffectHandler handler) {
                        if (!player.hasMovedInLast(MoveType.KEYBOARD, 1000)) {
                            handler.onStandingStill(player);
                        }

                        handler.onTick(player, tick);
                    }

                }
            }
        }.runTaskTimer(1, 1);
    }

    @Override
    public void onStop() {

    }
}
