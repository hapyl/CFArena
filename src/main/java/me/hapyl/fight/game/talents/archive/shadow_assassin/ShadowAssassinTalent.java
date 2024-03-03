package me.hapyl.fight.game.talents.archive.shadow_assassin;

import me.hapyl.fight.game.HeroReference;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.shadow_assassin.AssassinMode;
import me.hapyl.fight.game.heroes.archive.shadow_assassin.Data;
import me.hapyl.fight.game.heroes.archive.shadow_assassin.ShadowAssassin;
import me.hapyl.fight.game.talents.archive.techie.Talent;

import javax.annotation.Nonnull;

public abstract class ShadowAssassinTalent extends Talent implements HeroReference<ShadowAssassin> {

    protected ShadowAssassinModeSpecificTalent stealthTalent;
    protected FuryTalent furyTalent;

    public ShadowAssassinTalent(@Nonnull String name) {
        super(name);
    }

    public final Response execute(@Nonnull GamePlayer player) {
        final Data data = getData(player);
        final AssassinMode mode = data.getMode();
        final ShadowAssassin hero = getHero();

        final Response response = mode == AssassinMode.STEALTH ? stealthTalent.execute1(player, hero) : furyTalent.execute1(player, hero);

        if (response != null && response.isError()) {
            return response;
        }

        // Don't return OK because of custom cooldown
        return Response.AWAIT;
    }

    @Nonnull
    @Override
    public ShadowAssassin getHero() {
        return Heroes.SHADOW_ASSASSIN.getHero(ShadowAssassin.class);
    }

    @Nonnull
    public Data getData(GamePlayer player) {
        return getHero().getData(player);
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
