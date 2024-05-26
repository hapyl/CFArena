package me.hapyl.fight.game.heroes.bloodfield.impel;

import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.bloodfield.Bloodfiend;
import org.bukkit.Sound;

import javax.annotation.Nullable;
import java.util.Set;

public class ImpelInstance extends Instance<Bloodfiend> {

    private final GamePlayer player;
    private final Set<GamePlayer> enemyPlayers;
    private Impel impel;
    private int impelCount;

    public ImpelInstance(Bloodfiend instance, GamePlayer player, Set<GamePlayer> targets) {
        super(instance);
        this.player = player;
        this.impelCount = 0;
        this.enemyPlayers = targets;

        if (targets.isEmpty()) {
            player.sendMessage("&cNo one to impel!");
        }
        else {
            player.sendMessage("&eImpelled %s enemies!", targets.size());
        }
    }

    public boolean isPlayer(GamePlayer player) {
        if (impel == null) {
            return false;
        }

        for (GamePlayer gamePlayer : impel.getPlayers()) {
            if (gamePlayer.equals(player)) {
                return true;
            }
        }

        return false;
    }

    public void nextImpel(int cd) {
        if (impelCount >= instance.impelTimes) {
            return;
        }

        enemyPlayers.removeIf(GamePlayer::isDeadOrRespawning);

        impelCount++;
        impel = new Impel(Type.random(), instance.impelDuration) {
            @Override
            public void onFail(GamePlayer player) {
                player.damage(instance.impelDamage, ImpelInstance.this.player, EnumDamageCause.IMPEL);

                player.sendMessage(
                        "&6&l🦇 &eFailed to obey %s's command! &c-%s &c❤",
                        ImpelInstance.this.player.getName(),
                        (int) instance.impelDamage
                );

                player.sendSubtitle("&eImpel: &4&lFAILED! &c-%s ❤".formatted((int) instance.impelDamage), 0, 20, 5);

                player.playSound(Sound.ENTITY_BLAZE_HURT, 0.25f);
                player.playSound(Sound.ENTITY_ZOMBIE_HURT, 0.25f);
                player.playSound(Sound.ENTITY_PLAYER_BREATH, 1.0f);
            }

            @Override
            public void onImpelStop() {
                nextImpel(instance.impelCd);
            }
        };

        enemyPlayers.forEach(player -> {
            impel.addTargetPlayer(player);
        });

        impel.start(cd);
    }

    public void stop() {
        if (impel == null) {
            return;
        }

        impel.stop();
        impel = null;
    }

    public GamePlayer getPlayer() {
        return player;
    }

    @Nullable
    public Impel getImpel() {
        return impel;
    }

}
