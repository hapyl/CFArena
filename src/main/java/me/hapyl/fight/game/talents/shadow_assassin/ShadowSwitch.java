package me.hapyl.fight.game.talents.shadow_assassin;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.shadow_assassin.AssassinMode;
import me.hapyl.fight.game.talents.TalentType;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class ShadowSwitch extends ShadowAssassinTalent {

    public ShadowSwitch(@Nonnull Key key) {
        super(key, "Shadow Switch");

        setType(TalentType.ENHANCE);
        setItem(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);

        // Make sure setTalents is last
        setTalents(new Stealth(), new Fury(1));
    }

    private boolean isInDarkCover(GamePlayer player) {
        return HeroRegistry.SHADOW_ASSASSIN.getSecondTalent().isInDarkCover(player);
    }

    private class Stealth extends StealthTalent {

        public Stealth() {
            super(ShadowSwitch.this);

            setDescription("""
                    Deal small &cAoE damage&7 and switch to &cFury&7 mode.
                    
                    &8;;Your damage capabilities are enhanced while in Fury mode!
                    """);
            setCooldownSec(2);
        }

        @Override
        public Response execute(@Nonnull GamePlayer player) {
            if (isInDarkCover(player)) {
                return Response.error("Cannot switch while in Dark Cover!");
            }

            getData(player).switchMode(AssassinMode.FURY);
            return Response.OK;
        }
    }

    private class Fury extends FuryTalent {

        public Fury(int furyCost) {
            super(ShadowSwitch.this, furyCost);

            setDescription("""
                    Deal small &cAoE damage&7 and switch to &9Stealth&7 mode.
                    """);
            setCooldownSec(2);
        }

        @Override
        public Response execute(@Nonnull GamePlayer player) {
            if (isInDarkCover(player)) {
                return Response.error("Cannot switch while in Dark Cover!");
            }

            getData(player).switchMode(AssassinMode.STEALTH);
            return Response.OK;
        }
    }

}
