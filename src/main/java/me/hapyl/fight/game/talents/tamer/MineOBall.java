package me.hapyl.fight.game.talents.tamer;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.tamer.TamerData;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.tamer.pack.ActiveTamerPack;
import me.hapyl.fight.game.talents.tamer.pack.TamerPack;
import me.hapyl.fight.game.talents.tamer.pack.TamerPacks;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MineOBall extends Talent implements Listener, TamerTimed {

    public MineOBall(@Nonnull Key key) {
        super(key, "Mine 'o Ball");

        setDescription("""
                Summon a random pack of creatures that will aid you in battle.
                
                &6Creatures:
                """
        );

        for (TamerPacks enumPack : TamerPacks.values()) {
            final TamerPack pack = enumPack.getPack();

            addDescription(
                    """
                            &f&l%s
                            &8%s
                            %s
                            """.formatted(pack.getName(), pack.getTypeString(), pack.getDescription())
            );

            copyDisplayFieldsFrom(pack);
        }

        setCooldownSec(10);
        setTexture("5fe47640843744cd5796979d1196fb938317ec42b09fccb2c545ee4c925ac2bd");
    }

    @Nonnull
    @Override
    public String getTypeFormattedWithClassType() {
        return "Summon Talent";
    }

    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        final TamerData data = HeroRegistry.TAMER.getPlayerData(player);
        TamerPack previousPack = null;

        if (data.activePack != null) {
            data.activePack.recall();
            previousPack = data.activePack.getPack();;
        }

        final TamerPacks randomPack = TamerPacks.random(previousPack);
        final ActiveTamerPack tamerPack = new ActiveTamerPack(randomPack.getPack(), player);

        tamerPack.spawn();
        data.activePack = tamerPack;

        // Fx
        player.sendMessage("&c\uD83D\uDD34 &eYou just summoned a &6%s&e!".formatted(tamerPack.getName()));

        return Response.OK;
    }

}
