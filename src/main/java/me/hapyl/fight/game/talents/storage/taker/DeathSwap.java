package me.hapyl.fight.game.talents.storage.taker;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.storage.extra.SpiritualBones;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.Utils;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class DeathSwap extends Talent {

    @DisplayField private final double damagePercent = 10.0d;
    @DisplayField private final short spiritualBoneCost = 1;

    public DeathSwap() {
        super("Death Swap");

        setDescription(
                "Instantly consume %s &eSpiritual Bone&7 to swap location with targeted entity and and deal &b%s%%&7 of their health as damage.",
                spiritualBoneCost,
                damagePercent
        );

        setItem(Material.ENDER_EYE);
        setCdSec(20);
    }

    @Override
    public Response execute(Player player) {
        final SpiritualBones bones = Heroes.Handle.TAKER.getBones(player);

        if (bones.getBones() < spiritualBoneCost) {
            return Response.error("Not enough &lSpiritual Bones&c!");
        }

        final LivingEntity target = Utils.getTargetEntity(player, 50.0d, 0.85d, entity -> true);

        if (target == null) {
            return Response.error("No target found!");
        }

        final double damage = Math.min(target.getHealth() * (damagePercent / 100), 100.0d);
        GamePlayer.damageEntity(target, damage, player, EnumDamageCause.RIP_BONES);

        // Swap
        final Location playerLocation = player.getLocation();
        final Location targetLocation = target.getLocation();

        final float playerYaw = playerLocation.getYaw();
        final float playerPitch = playerLocation.getPitch();

        playerLocation.setYaw(targetLocation.getYaw());
        playerLocation.setPitch(targetLocation.getPitch());

        targetLocation.setYaw(playerYaw);
        targetLocation.setPitch(playerPitch);

        player.teleport(targetLocation);
        target.teleport(playerLocation);

        PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.75f);
        Chat.sendMessage(player, "&aSwapped locations with %s!", target.getName());

        if (target instanceof Player playerTarget) {
            PlayerLib.playSound(playerTarget, Sound.ENTITY_ENDERMAN_TELEPORT, 0.75f);
            Chat.sendMessage(
                    playerTarget,
                    "&c%s swapped locations with you! This is a weird feeling for you, looks like you lost &l%s%%&c of your health...",
                    player.getName(),
                    damagePercent
            );
        }

        bones.remove(spiritualBoneCost);
        return Response.OK;
    }
}
