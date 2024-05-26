package me.hapyl.fight.game.heroes.moonwalker;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.talents.Removable;
import me.hapyl.fight.game.talents.moonwalker.MoonPillarZone;
import me.hapyl.fight.util.Ticking;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.Set;

public class MoonwalkerData extends PlayerData implements Ticking {

    public static final int MAX_ZONES = 3;

    private final LinkedList<MoonZone> moonZones;
    protected double weaponEnergy;

    public MoonwalkerData(GamePlayer player) {
        super(player);

        this.moonZones = Lists.newLinkedList();
    }

    @Override
    public void tick() {
        moonZones.removeIf(MoonZone::removeIf);
        moonZones.forEach(MoonZone::tick);
    }

    @Override
    public void remove() {
        moonZones.forEach(MoonZone::remove);
        moonZones.clear();
    }

    public void addZone(MoonPillarZone zone) {
        if (moonZones.size() >= MAX_ZONES) {
            final MoonZone last = moonZones.pollFirst();

            if (last != null) {
                last.remove();
            }
        }

        moonZones.add(zone);
    }

    @Nullable
    public MoonZone getZone(@Nonnull Location location) {
        for (MoonZone zone : moonZones) {
            if (zone.centre.distance(location) <= zone.size && zone.energy > 1) {
                return zone;
            }
        }

        return null;
    }

}
