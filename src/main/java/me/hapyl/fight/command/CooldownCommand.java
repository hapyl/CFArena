package me.hapyl.fight.command;

import me.hapyl.eterna.module.command.SimplePlayerAdminCommand;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.fight.CF;
import me.hapyl.fight.Message;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.ChargedTalent;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.weapons.Weapon;
import org.bukkit.entity.Player;

public class CooldownCommand extends SimplePlayerAdminCommand {
    public CooldownCommand(String name) {
        super(name);
        setAliases("cd");
    }

    @Override
    protected void execute(Player player, String[] args) {
        final GamePlayer gamePlayer = CF.getPlayer(player);

        if (!Manager.current().isDebug()) {
            Message.error(player, "You must be in 'debug' environment to use this command!");
            return;
        }

        PlayerLib.stopCooldowns(player);

        if (gamePlayer == null) {
            Message.warning(player, "Only stopped vanilla cooldowns because you're not in a game!");
            return;
        }

        final Weapon weapon = gamePlayer.getHero().getWeapon();
        gamePlayer.cooldownManager.stopCooldown(weapon);

        weapon.getAbilities().forEach(ability -> ability.stopCooldown(gamePlayer));

        for (Talent talent : gamePlayer.getHero().getTalents()) {
            if (talent == null) {
                continue;
            }

            if (talent instanceof PassiveTalent) {
                continue;
            }

            talent.stopCooldown(gamePlayer);
            if (talent instanceof ChargedTalent chargedTalent) {
                chargedTalent.grantAllCharges(gamePlayer);
            }
        }

        gamePlayer.getUltimate().stopCooldown(gamePlayer);
        gamePlayer.setUsingUltimate(false);

        Message.success(player, "Stopped all cooldowns!");
    }
}
