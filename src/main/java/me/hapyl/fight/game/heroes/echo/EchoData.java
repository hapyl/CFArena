package me.hapyl.fight.game.heroes.echo;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffectType;

public class EchoData extends PlayerData {

    protected EchoWorld echoWorld;

    public EchoData(GamePlayer player) {
        super(player);
    }

    @Override
    public void remove() {
        if (echoWorld != null) {
            echoWorld.cancel();
            echoWorld = null;
        }
    }

    public void toggleEchoWorld() {
        // Apply blindness first to not see the changes
        player.addPotionEffect(PotionEffectType.BLINDNESS, 20, 30);

        if (this.echoWorld != null) {
            remove();
        }
        else {
            this.echoWorld = new EchoWorld(this, this.player);
            this.echoWorld.createEchoWorld();
        }

        // Fx
        player.playWorldSound(Sound.ENTITY_WARDEN_HURT, 0.75f);
        player.playWorldSound(Sound.ENTITY_GOAT_RAM_IMPACT, 0.5f);
    }

    public boolean isInEchoWorld() {
        return this.echoWorld != null;
    }

}
