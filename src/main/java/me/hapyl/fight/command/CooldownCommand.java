package me.hapyl.fight.command;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.ChargedTalent;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.game.talents.archive.techie.Talent;
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
        final GamePlayer gamePlayer = CF.getPlayer(player);

        if (gamePlayer == null) {
            Chat.sendMessage(player, "&cNo handle.");
            return;
        }

        if (!Manager.current().isDebug()) {
            Chat.sendMessage(player, "&cNot in debug mode!");
            return;
        }

        final Weapon weapon = gamePlayer.getHero().getWeapon();
        gamePlayer.stopCooldown(weapon.getMaterial());

        weapon.getAbilities().forEach(ability -> {
            ability.stopCooldown(gamePlayer);
        });

        for (Talent talent : gamePlayer.getHero().getTalents()) {
            if (talent == null) {
                continue;
            }

            if (talent instanceof PassiveTalent) {
                continue;
            }

            talent.stopCd(gamePlayer);
            if (talent instanceof ChargedTalent chargedTalent) {
                chargedTalent.grantAllCharges(gamePlayer);
            }
        }

        gamePlayer.getUltimate().stopCd(gamePlayer);
        gamePlayer.setUsingUltimate(false);

        gamePlayer.sendMessage("&aReset cooldowns!");
    }
}
