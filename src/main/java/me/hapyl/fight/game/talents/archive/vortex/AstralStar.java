package me.hapyl.fight.game.talents.archive.vortex;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.spigotutils.module.entity.Entities;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nonnull;

public class AstralStar {

    protected final LivingEntity star;
    private final GamePlayer player;
    private final Location location;

    public AstralStar(GamePlayer player, Location location) {
        this.player = player;
        this.location = location;
        this.star = Entities.BAT.spawn(location, self -> {
            self.setSilent(true);
            self.setInvulnerable(true);
            self.setInvisible(true);
            self.setAI(false);
            self.setAwake(false);
            self.setGlowing(true);
            self.setVisibleByDefault(false);
        });

        // Show bat to player
        player.showEntity(star);
    }

    public void setColor(@Nonnull ChatColor color) {
        final Team team = fetchTeam("batColor_" + color.name());

        team.setColor(color);
        team.addEntry(star.getUniqueId().toString());
    }

    public double dot() {
        return player.dot(star.getLocation());
    }

    @Nonnull
    public Location getLocation() {
        return location;
    }

    public void remove() {
        star.remove();
    }

    public void teleport(GamePlayer player) {
        final Location playerLocation = player.getLocation();

        location.setYaw(playerLocation.getYaw());
        location.setPitch(playerLocation.getPitch());

        player.teleport(location);
    }

    public double distance() {
        return star.getLocation().distance(player.getLocation());
    }

    private Team fetchTeam(String name) {
        final Scoreboard scoreboard = player.getScoreboard();
        Team team = scoreboard.getTeam(name);

        if (team == null) {
            team = scoreboard.registerNewTeam(name);
        }

        return team;
    }
}
