package me.hapyl.fight.command;

import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import me.hapyl.spigotutils.module.reflect.npc.Human;
import me.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.EntityEffect;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class TestNpcDeathAnimationCommand extends SimplePlayerAdminCommand {
    public TestNpcDeathAnimationCommand(@Nonnull String name) {
        super(name);
    }

    @Override
    protected void execute(Player player, String[] args) {
        final Human npc = HumanNPC.create(player.getLocation());
        final EntityPlayer human = npc.getHuman();

        npc.showAll();

        GameTask.runLater(() -> {
            npc.bukkitEntity().playEffect(EntityEffect.DEATH);
            Chat.sendMessage(player, "&eSent packet!");
        }, 10);

        GameTask.runLater(() -> {
            npc.remove();
            Chat.sendMessage(player, "&aFinished!");
        }, 60);
    }
}
