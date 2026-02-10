package me.hapyl.fight.game.talents.echo;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.echo.EchoData;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EchoWorldTalent extends Talent {

    @DisplayField public final short radius = 40;

    public EchoWorldTalent(@Nonnull Key key) {
        super(key, "Echo World");

        setDescription("""
                Calm your spirit and enter the %1$s.
                &8&o;;Use again while in the %2$s to return to your body.
                
                While in the %1$s, you can fly freely and use your talents.
                """.formatted(Named.ECHO_WORLD, Named.ECHO_WORLD.getName())
        );

        setMaterial(Material.NETHERITE_INGOT);
        setType(TalentType.ENHANCE);

        setCooldownSec(2);
        setDurationSec(12);
    }

    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        final EchoData data = HeroRegistry.ECHO.getPlayerData(player);

        data.toggleEchoWorld();
        return Response.OK;
    }

}
