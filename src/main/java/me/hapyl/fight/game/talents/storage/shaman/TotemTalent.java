package me.hapyl.fight.game.talents.storage.shaman;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentHandle;
import me.hapyl.fight.game.talents.storage.extra.ActiveTotem;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.entity.Player;

public class TotemTalent extends Talent {

    private final ResonanceType type;

    public TotemTalent(ResonanceType type, int cd) {
        super(
                type.getName(),
                "Target placed totem and use to switch it to %s.____%s".formatted(type.getName(), type.getAbout()),
                type.getMaterial()
        );
        this.type = type;

        addExtraInfo("Aura Range &l%s", type.getRange());
        addExtraInfo("Aura Interval &l%s", type.getInterval());
        setCdSec(cd);
    }

    @Override
    public Response execute(Player player) {
        final ActiveTotem totem = TalentHandle.TOTEM.getTargetTotem(player);
        if (totem == null) {
            return Response.error("Not targeting totem.");
        }

        totem.setResonanceType(type);
        Chat.sendMessage(player, "Switched totem to %s.", type.getName());

        return Response.OK;
    }
}
