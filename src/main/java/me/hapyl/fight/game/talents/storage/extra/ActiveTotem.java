package me.hapyl.fight.game.talents.storage.extra;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.talents.TalentHandle;
import me.hapyl.fight.game.talents.storage.shaman.ResonanceType;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.math.geometry.WorldParticle;
import me.hapyl.spigotutils.module.reflect.visibility.Visibility;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class ActiveTotem {

    private final Player owner;
    private final Location location;
    private final Team shulkerTeam;
    private final Set<Shulker> shulkers; // shulkers are used for glowing effect and target detection
    private ResonanceType resonanceType;

    private final Consumer<Shulker> shulkerData = self -> {
        self.setAI(false);
        self.setInvulnerable(true);
        self.setInvisible(true);
    };

    public ActiveTotem(Player owner, Location location) {
        this.owner = owner;
        this.location = location;
        this.shulkers = Sets.newHashSet();
        this.resonanceType = ResonanceType.STANDBY;
        this.shulkerTeam = createTeam();
    }

    public void create() {
        final Block block = location.getBlock();
        final Block blockUp = block.getRelative(BlockFace.UP);

        block.setType(Material.OBSIDIAN, false);
        blockUp.setType(Material.OBSIDIAN, false);

        shulkers.add(Entities.SHULKER.spawn(location, shulkerData::accept));
        shulkers.add(Entities.SHULKER.spawn(blockUp.getLocation(), shulkerData::accept));

        shulkers.forEach(this::hideShulker);

        for (Shulker shulker : shulkers) {
            shulkerTeam.addEntry(shulker.getUniqueId().toString());
            shulker.setGlowing(true);
        }
    }

    public void destroy() {
        final Block block = location.getBlock();
        block.setType(Material.AIR, false);
        block.getRelative(BlockFace.UP).setType(Material.AIR, false);

        shulkers.forEach(Entity::remove);
        shulkerTeam.unregister();
    }

    public void setActive() {
        setGlowingColor(resonanceType.getActiveColor());
    }

    public void setGlowingColor(ChatColor color) {
        TalentHandle.TOTEM.defaultAllTotems(owner);
        shulkerTeam.setColor(color);
    }

    public ResonanceType getResonanceType() {
        return resonanceType;
    }

    public void setResonanceType(ResonanceType resonanceType) {
        this.resonanceType = resonanceType;
    }

    public Player getOwner() {
        return owner;
    }

    public Location getLocationCentered() {
        return BukkitUtils.centerLocation(location);
    }

    public Location getLocation() {
        return location;
    }

    public boolean isShulker(Shulker other) {
        for (Shulker shulker : shulkers) {
            if (shulker == other) {
                return true;
            }
        }

        return false;
    }

    private Team createTeam() {
        final Scoreboard scoreboard = owner.getScoreboard();
        final String teamName = UUID.randomUUID().toString();
        Team team = scoreboard.getTeam(teamName);

        if (team != null) {
            createTeam();
        }

        team = scoreboard.registerNewTeam(teamName);
        team.setColor(ChatColor.BLACK);
        return team;
    }

    private void hideShulker(Shulker shulker) {
        Visibility.of(shulker, owner);
    }

    public void defaultColor() {
        shulkerTeam.setColor(resonanceType.getColor());
    }

    public List<Player> getPlayerInRange() {
        return Utils.getPlayersInRange(getLocationCentered(), resonanceType.getRange());
    }

    public void drawCircle(Particle particle) {
        Geometry.drawCircle(
                getLocationCentered(),
                getResonanceType().getRange(),
                Quality.HIGH,
                new WorldParticle(particle)
        );
    }
}
