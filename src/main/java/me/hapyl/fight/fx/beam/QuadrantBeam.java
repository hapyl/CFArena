package me.hapyl.fight.fx.beam;

import me.hapyl.eterna.module.entity.packet.PacketEntity;
import me.hapyl.eterna.module.entity.packet.PacketGuardian;
import me.hapyl.eterna.module.entity.packet.PacketSquid;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public class QuadrantBeam {

    private final PacketEntity<?>[] entities;

    public QuadrantBeam(@Nonnull Location location) {
        this.entities = new PacketEntity[4];

        for (int i = 0; i < 4; i++) {
            final PacketSquid squid = new PacketSquid(location);
            final PacketGuardian guardian = new PacketGuardian(location);

            squid.showGlobally();
            guardian.showGlobally();

            guardian.setMarker();
            squid.setMarker();

            guardian.setCollision(false);
            squid.setCollision(false);

            guardian.setBeamTarget(squid);

            entities[i] = guardian;
            entities[++i] = squid;
        }
    }

    public void remove() {
        CFUtils.forEach(entities, PacketEntity::destroy);
    }

    public void setGuardianLocation(int index, Location location) {
        setLocation(index, location);
    }

    public void setSquidLocation(int index, Location location) {
        setLocation(index + 1, location);
    }

    private void setLocation(int index, Location location) {
        final PacketEntity<?> entity = entities[index];

        entity.teleport(location);
    }
}
