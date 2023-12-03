package me.hapyl.fight.game.heroes.archive.healer;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.heroes.DisabledHero;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.archive.healer.HealingOrb;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Healer extends Hero implements DisabledHero {

    public Healer() {
        super("Healer");

        setItem("null");
    }

    @Override
    public void useUltimate(Player player) {

    }

    public final Talent TALENT = new Talent("test talent", "test talent", Material.CARROT) {
        @Override
        public Response execute(Player player) {
            Chat.sendMessage(player, "&aOKOKOKOKKOKOKOKOKOKOKOKOKOKOKOKOKO");
            return Response.OK;
        }
    };

    @Override
    public HealingOrb getFirstTalent() {
        return (HealingOrb) Talents.HEALING_ORB.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return TALENT;
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.REVIVE.getTalent();
    }
}
