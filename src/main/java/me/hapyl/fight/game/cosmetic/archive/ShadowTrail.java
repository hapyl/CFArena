package me.hapyl.fight.game.cosmetic.archive;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.contrail.ContrailCosmetic;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.reflect.npc.Human;
import me.hapyl.spigotutils.module.reflect.npc.NPCPose;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class ShadowTrail extends ContrailCosmetic implements Disabled {

    private final Map<Player, Queue<Human>> humans;

    public ShadowTrail() {
        super("Shadow Trail", "There is something trailing behind you!", Rarity.MYTHIC);

        setIcon(Material.PLAYER_HEAD);
        humans = Maps.newConcurrentMap();
    }

    @Override
    public void onMove(Display display) {
        final Player player = display.getPlayer();

        if (player == null) {
            return;
        }

        final Queue<Human> queue = humans.computeIfAbsent(player, q -> new LinkedList<>());

        if (queue.size() >= 3) {
            final Human poll = queue.poll();

            if (poll != null) {
                poll.remove();
            }
        }

        final Human npc = createNpc(display);
        queue.offer(npc);

        GameTask.runLater(() -> {
            if (!npc.isAlive()) {
                return;
            }

            queue.remove(npc);
            npc.remove();
        }, 20);
    }

    private Human createNpc(Display display) {
        final Player player = display.getPlayer();
        if (player == null) {
            throw new IllegalStateException("Player must be executor for contrail!");
        }

        final Location location = player.getLocation();
        location.subtract(location.getDirection().normalize().setY(0.0d).multiply(0.5d));

        final Human human = Human.create(location, "");

        human.setSkin(
                "ewogICJ0aW1lc3RhbXAiIDogMTY3ODY5NDEzOTEzOSwKICAicHJvZmlsZUlkIiA6ICIwNDNkZWIzOGIyNjE0MTg1YTIzYzU4ZmI2YTc5ZWZkYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJWaXRhbFNpZ256MiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS85NTlkOWE3YWUzZjE3OWVmMjU2YTFmOTdmMDNlZjNiYzYxOGZlNjE3ZTEyM2U4NDZmNTI4MjVlOWExYmNmNjA1IgogICAgfQogIH0KfQ==",
                "CfY7RICVgjl9QKz/dPAhvI93bSmFevNA4iQb69IAfwn4EFDmkcmXKe6I7L6/M25E+AWiiLmNOUt3k0ey1vt+vlUPZ2VU/rgbJQ/BX1RFH6NZvj0PpwFJGU2HyKnQgyEHJ4avPao/n5b5Pt8ABwlk3D0QgU8bfi6C/KKVs8GUfaSwXJ6FGpIXL357Liq/iowNWPt7aAUoQpzfoFHMG0dyT/b3lICP/lkodSi05GgwDv2weYcfMVNk0qqC57H8htpLW887juWezyhdDDoDA7/s2+8RpXlu9fRqBCmnD5VQivxZoHtB6dzP6sCniFPYEyR5dv+T8Sgud6CxJ337sDltdwQBp9xCH1tIpsd5bYxCbC9TC1UdLmfQkPnSYTk5GfAcsy9l7SkZsoZ9XWLmPhNQL+ATvIp7Q9h5gIre0xLKqQ40XSqbaX5IB1LYW36FzWJve/fqEPgey05CfQCjBE2M0mnSxYRzjh9JIcHB1+E5YfDtJnxg9UPJ61Frjban4FF6LFTR1Cf69B2QzuDwIq+8D7l0cGScpege8S5p6O4rFSF3FydL39kCnbGx6Cx7679BcLd+RYIC3MaDkDlRcVbLCBdo07JU6yxaPhBrbSF4/IwuDOywV4P96fVFzg1lO+GCC7Ny3dHcL8GVTaBNv7JPsndH53ktv6Zl7HH7ugrw0+k="
        );

        if (player.isSneaking()) {
            human.setPose(NPCPose.CROUCHING);
        }
        else if (player.isSwimming()) {
            human.setPose(NPCPose.SWIMMING);
        }
        else {
            human.setPose(NPCPose.SPIN_ATTACK);
        }

        human.setShaking(true);
        human.setCollision(false);

        for (Player canSee : display.getPlayersWhoCanSeeContrail()) {
            human.show(canSee);
        }

        return human;
    }

}
