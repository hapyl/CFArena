package me.hapyl.fight.game.talents.jester;

import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayData;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.jester.JesterData;
import me.hapyl.fight.game.talents.Talent;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public class MusicBoxTalent extends Talent {

    private final DisplayData model = BDEngine.parse(
            "/summon block_display ~-0.5 ~-0.5 ~-0.5 {Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:red_glazed_terracotta\",Properties:{facing:\"east\"}},transformation:[0.6875f,0f,0f,0f,0f,1f,0f,0f,0f,0f,1f,-0.4375f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:red_glazed_terracotta\",Properties:{facing:\"east\"}},transformation:[0.6875f,0f,0f,-0.6875f,0f,1f,0f,0f,0f,0f,1f,-0.4375f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:red_glazed_terracotta\",Properties:{facing:\"east\"}},transformation:[0.2578f,0f,0f,-0.125f,0f,0.1875f,0f,1f,0f,0f,1f,-0.8125f,0f,0f,0f,1f]}]}"
    );

    public MusicBoxTalent(@Nonnull Key key) {
        super(key, "Music Box");

        setDescription("""
                Place a Music Box nearby.
                
                
                """);

        setDurationSec(20.0f);
        setCooldownSec(15.0f);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final JesterData data = HeroRegistry.JESTER.getPlayerData(player);

        // Remove previous music box
        if (data.musicBox != null) {
            data.musicBox.remove();
        }

        final Location location = player.getLocation();
        location.setYaw(0.0f);
        location.setPitch(0.0f);

        data.musicBox = new MusicBox(player, location, model);
        return Response.OK;
    }
}
