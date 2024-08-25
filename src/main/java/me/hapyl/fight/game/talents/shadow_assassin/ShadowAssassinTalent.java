package me.hapyl.fight.game.talents.shadow_assassin;


import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.shadow_assassin.AssassinMode;
import me.hapyl.fight.game.heroes.shadow_assassin.ShadowAssassin;
import me.hapyl.fight.game.heroes.shadow_assassin.ShadowAssassinData;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.registry.Key;

import javax.annotation.Nonnull;

public abstract class ShadowAssassinTalent extends Talent {

    protected ShadowAssassinModeSpecificTalent stealthTalent;
    protected FuryTalent furyTalent;

    public ShadowAssassinTalent(@Nonnull Key key, @Nonnull String name) {
        super(key, name);
    }

    public final Response execute(@Nonnull GamePlayer player) {
        final ShadowAssassinData data = getData(player);
        final AssassinMode mode = data.getMode();
        final ShadowAssassin hero = HeroRegistry.SHADOW_ASSASSIN;

        final Response response = mode == AssassinMode.STEALTH ? stealthTalent.execute1(player, hero) : furyTalent.execute1(player, hero);

        if (response != null && response.isError()) {
            return response;
        }

        // Don't return OK because of custom cooldown
        return Response.AWAIT;
    }

    @Nonnull
    public ShadowAssassinData getData(GamePlayer player) {
        return HeroRegistry.SHADOW_ASSASSIN.getData(player);
    }

    protected void setTalents(@Nonnull ShadowAssassinModeSpecificTalent stealthTalent, @Nonnull FuryTalent furyTalent) {
        this.stealthTalent = stealthTalent;
        this.furyTalent = furyTalent;

        setDescription("""
                &9&lWhile in Stealth mode:
                %s
                &b• Cooldown: &f%s
                
                &c&lWhile in Fury mode:
                %s
                &b• Cooldown: &f%s
                &b• Energy Cost: &f%s
                """.formatted(
                // Stealth
                stealthTalent.getDescription(),
                stealthTalent.getCooldownFormatted(),

                // Fury
                furyTalent.getDescription(),
                furyTalent.getCooldownFormatted(),
                furyTalent.furyCost
        ));
    }


}
