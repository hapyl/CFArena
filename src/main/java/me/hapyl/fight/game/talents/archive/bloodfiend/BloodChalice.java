package me.hapyl.fight.game.talents.archive.bloodfiend;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import javax.annotation.Nullable;
import java.util.Map;

public class BloodChalice extends Talent implements Listener {

    @DisplayField protected final double chaliceHealth = 5.0d;
    @DisplayField protected final double damage = 2.0d;
    @DisplayField protected final int interval = 40;
    @DisplayField protected final double maxDistance = 30.0d;

    private final Map<Player, Chalice> chalice;

    public BloodChalice() {
        super("Blood Chalice");

        setTexture("492f08c1294829d471a8e0109a06fb6ae717e5faf3e0808408a66d889227dac7");
        setDescription("""
                Place a blood chalice near you for {duration}.
                                
                &cBitten&7 players will suffer damage every &b{interval}&7, unless the chalice is broken.
                                
                If the duration expires and the chalice is not broken, all &cbitten&7 players will &4die&7.
                                
                &e;;Only one chalice may exist at the same time.
                &6;;The chalice will break if you stray too far away!
                """);

        setDurationSec(30);
        setCooldownSec(20);

        chalice = Maps.newHashMap();
    }

    @Nullable
    public Chalice getChalice(Player player) {
        return chalice.get(player);
    }

    @Override
    public void onDeath(Player player) {
        final Chalice chalice = this.chalice.remove(player);

        if (chalice != null) {
            chalice.remove();
        }
    }

    @Override
    public void onStop() {
        chalice.values().forEach(Chalice::remove);
        chalice.clear();
    }

    @Override
    public Response execute(Player player) {
        final Chalice remove = chalice.remove(player);

        if (remove != null) {
            remove.remove();
            Chat.sendMessage(player, "&aYour previous chalice was removed!");
        }

        chalice.put(player, new Chalice(this, player, player.getLocation().subtract(0, 1.4f, 0)) {
            @Override
            public void onTaskStop() {
                chalice.remove(player);
            }
        });

        return Response.OK;
    }
}
