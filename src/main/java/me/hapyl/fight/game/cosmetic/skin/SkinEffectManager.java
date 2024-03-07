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
                    final PlayerProfile profile = PlayerProfile.getProfile(player.getPlayer());

                    if (!player.isValidForCosmetics() || profile == null) {
                        continue;
                    }

                    final SkinEntry skinEntry = profile.getDatabase().skinEntry;
                    final Skins selectedSkin = skinEntry.getSelected(player.getEnumHero());

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
