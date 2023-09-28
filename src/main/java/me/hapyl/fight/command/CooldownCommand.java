package me.hapyl.fight.command;

import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.ChargedTalent;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import org.bukkit.entity.Player;

public class CooldownCommand extends SimplePlayerAdminCommand {
    public CooldownCommand(String name) {
        super(name);
        setAliases("cd");
    }

    @Override
    protected void execute(Player player, String[] args) {
        final GamePlayer gamePlayer = GamePlayer.getExistingPlayer(player);

        if (!Manager.current().isDebug()) {
            Chat.sendMessage(player, "&cNot in debug mode!");
            return;
        }

        if (gamePlayer == null) {
            Chat.sendMessage(player, "&cCannot use this command outside a game!");
            return;
        }

        final Weapon weapon = gamePlayer.getHero().getWeapon();
        GamePlayer.setCooldown(player, weapon.getMaterial(), 0);

        weapon.getAbilities().forEach(ability -> {
            ability.stopCooldown(player);
        });

        for (Talent talent : gamePlayer.getHero().getTalents()) {
            if (talent == null) {
                continue;
            }

            if (talent instanceof PassiveTalent) {
                continue;
            }

            talent.stopCd(player);
            if (talent instanceof ChargedTalent chargedTalent) {
                chargedTalent.grantAllCharges(player);
            }
        }

        gamePlayer.getUltimate().stopCd(player);
        gamePlayer.getHero().setUsingUltimate(player, false);
        Chat.sendMessage(player, "&aReset cooldowns.");
    }
}
