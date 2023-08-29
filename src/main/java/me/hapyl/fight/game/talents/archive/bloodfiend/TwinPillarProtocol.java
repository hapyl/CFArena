package me.hapyl.fight.game.talents.archive.bloodfiend;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedEnumEntityUseAction;
import me.hapyl.fight.Main;
import me.hapyl.fight.game.TalentHandle;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.spigotutils.module.reflect.protocol.ProtocolListener;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;

public class TwinPillarProtocol extends ProtocolListener implements TalentHandle<TwinClaws> {
    public TwinPillarProtocol() {
        super(PacketType.Play.Client.USE_ENTITY);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        final Player player = event.getPlayer();
        final PacketContainer packet = event.getPacket();
        final Integer entityId = packet.getIntegers().read(0);
        final WrappedEnumEntityUseAction useAction = packet.getEnumEntityUseActions().read(0);
        final EnumWrappers.EntityUseAction action = useAction.getAction();

        if (action != EnumWrappers.EntityUseAction.ATTACK && action != EnumWrappers.EntityUseAction.INTERACT_AT) {
            return;
        }

        final TwinClaws talent = getTalent();

        for (Candlebane pillar : talent.getPillars().values()) {
            if (!pillar.isPart(entityId)) {
                continue;
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    pillar.click(player, action == EnumWrappers.EntityUseAction.ATTACK ? Candlebane.Click.LEFT : Candlebane.Click.RIGHT);
                }
            }.runTask(Main.getPlugin());
        }
    }

    @Override
    public void onPacketSending(PacketEvent event) {
    }

    @Nonnull
    @Override
    public TwinClaws getTalent() {
        return Talents.TWIN_CLAWS.getTalent(TwinClaws.class);
    }
}
