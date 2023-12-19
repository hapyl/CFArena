package me.hapyl.fight.game.talents.archive.tamer;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.talents.archive.tamer.pack.ActiveTamerPack;
import me.hapyl.fight.game.talents.archive.tamer.pack.TamerPack;
import me.hapyl.fight.game.talents.archive.tamer.pack.TamerPacks;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Nulls;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayFieldData;
import me.hapyl.fight.util.displayfield.DisplayFieldDataProvider;
import me.hapyl.fight.util.displayfield.DisplayFieldSerializer;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class MineOBall extends Talent implements Listener, TamerTimed, DisplayFieldDataProvider {

    private final PlayerMap<ActiveTamerPack> tamerPackMap = PlayerMap.newConcurrentMap();
    private final List<DisplayFieldData> displayFieldData = Lists.newArrayList();

    public MineOBall() {
        super("Mine 'o Ball", """
                Summon a random pack of creatures that will aid you in battle.
                                
                &6Creatures:
                """);

        for (TamerPacks enumPack : TamerPacks.values()) {
            final TamerPack pack = enumPack.getPack();

            addDescription("""
                    &f&l%s
                    &8%s
                    %s
                    """, pack.getName(), pack.getTypeString(), pack.getDescription());

            // Copy display fields
            DisplayFieldSerializer.copy(pack, this);
        }

        setCooldownSec(10);
        setTexture("5fe47640843744cd5796979d1196fb938317ec42b09fccb2c545ee4c925ac2bd");
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        tamerPackMap.removeAnd(player, ActiveTamerPack::recall);
    }

    @Override
    public void onStop() {
        tamerPackMap.forEachAndClear(ActiveTamerPack::remove);
    }

    @Nullable
    public ActiveTamerPack getPack(GamePlayer player) {
        return player == null ? null : tamerPackMap.get(player);
    }

    @Override
    public void onStart() {
        new GameTask() {
            @Override
            public void run() {
                tamerPackMap.forEach((player, pack) -> {
                    if (pack.isOver()) {
                        pack.recall();
                        tamerPackMap.remove(player);
                        return;
                    }

                    pack.tick();
                });
            }
        }.runTaskTimer(0, 1);
    }

    @Nonnull
    @Override
    public String getTypeFormattedWithClassType() {
        return "Summon Talent";
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final ActiveTamerPack oldPack = getPack(player);

        if (oldPack != null) {
            oldPack.recall();
        }

        final TamerPacks randomPack = TamerPacks.random(Nulls.getOrNull(oldPack, ActiveTamerPack::getPack));
        final ActiveTamerPack tamerPack = new ActiveTamerPack(randomPack.getPack(), player);

        tamerPack.spawn();
        tamerPackMap.put(player, tamerPack);

        // Fx
        player.sendMessage("&c\uD83D\uDD34 &eYou just summoned a &6%s&e!", tamerPack.getName());

        return Response.OK;
    }

    @Nonnull
    @Override
    public List<DisplayFieldData> getDisplayFieldData() {
        return displayFieldData;
    }
}
