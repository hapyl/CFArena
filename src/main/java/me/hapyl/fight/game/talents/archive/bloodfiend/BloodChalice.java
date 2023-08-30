package me.hapyl.fight.game.talents.archive.bloodfiend;

import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class BloodChalice extends TauntTalent<ChaliceTaunt> implements Listener {

    @DisplayField protected final double chaliceHealth = 5.0d;
    @DisplayField protected final double damage = 2.0d;
    @DisplayField protected final int interval = 40;
    @DisplayField protected final double maxDistance = 30.0d;

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
    }

    @Override
    public ChaliceTaunt createTaunt(Player player) {
        return new ChaliceTaunt(this, player, player.getLocation());
    }

}
