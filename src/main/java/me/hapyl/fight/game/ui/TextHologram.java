package me.hapyl.fight.game.ui;

import com.google.common.collect.Lists;
import me.hapyl.spigotutils.module.hologram.Holo;
import me.hapyl.spigotutils.module.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

import java.util.List;

public class TextHologram implements Holo {

    private final List<String> strings;
    private TextDisplay display;

    public TextHologram() {
        strings = Lists.newArrayList();
    }

    @Override
    public Hologram addLine(String line) {
        strings.add(line);
        return null;
    }

    @Override
    public Hologram addLines(String... lines) {
        return null;
    }

    @Override
    public Hologram setLines(String... lines) {
        return null;
    }

    @Override
    public Hologram removeLine(int index) throws IndexOutOfBoundsException {
        return null;
    }

    @Override
    public Hologram setLine(int index, String line) {
        return null;
    }

    @Override
    public Hologram clear() {
        return null;
    }

    @Override
    public Hologram setPersistent(boolean persistent) {
        return null;
    }

    @Override
    public boolean isPersistent() {
        return false;
    }

    @Override
    public Hologram setRemoveWhenFarAway(int hideWhenFurtherThan) {
        return null;
    }

    @Override
    public int getRemoveWhenFarAway() {
        return 0;
    }

    @Override
    public boolean isShowingTo(Player player) {
        return false;
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public boolean destroy() {
        return false;
    }

    @Override
    public Hologram showAll() {
        return null;
    }

    @Override
    public Hologram show(Player... players) {
        return null;
    }

    @Override
    public Hologram hide(boolean flag, Player... players) {
        return null;
    }

    @Override
    public Hologram hide(Player... players) {
        return null;
    }

    @Override
    public Hologram teleport(Location location) {
        return null;
    }

    @Override
    public Hologram updateLines(boolean keepListSorted) {
        return null;
    }

    @Override
    public Hologram updateLines() {
        return null;
    }

    @Override
    public Hologram create(Location location) {
        return null;
    }

    @Override
    public Hologram move(Location location, Player... players) {
        return null;
    }
}
