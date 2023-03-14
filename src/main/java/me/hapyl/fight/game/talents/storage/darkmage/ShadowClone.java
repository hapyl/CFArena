package me.hapyl.fight.game.talents.storage.darkmage;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.heroes.HeroHandle;
import me.hapyl.fight.game.heroes.storage.extra.DarkMageSpell;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.reflect.npc.ClickType;
import me.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import me.hapyl.spigotutils.module.reflect.npc.NPCPose;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

import static org.bukkit.Sound.ENTITY_SQUID_SQUIRT;

public class ShadowClone extends DarkMageTalent {

    public ShadowClone() {
        super(
                "Shadow Clone",
                "Creates a shadow clone of you at your current location and completely hides you. After a brief delay or whenever enemy hits the clone, it explodes, stunning, blinding and dealing damage to nearby players.",
                Material.NETHERITE_SCRAP
        );

        this.setCd(300);
    }

    @Nonnull
    @Override
    public DarkMageSpell.SpellButton first() {
        return DarkMageSpell.SpellButton.LEFT;
    }

    @Nonnull
    @Override
    public DarkMageSpell.SpellButton second() {
        return DarkMageSpell.SpellButton.RIGHT;
    }

    public void explode(HumanNPC npc, Player clicker) {
        if (!npc.isAlive()) {
            return;
        }

        final Location location = npc.getLocation();

        // Turn towards target
        if (clicker != null) {
            npc.lookAt(clicker.getLocation());

            PlayerLib.addEffect(clicker, PotionEffectType.SLOW, 60, 4);
            PlayerLib.addEffect(clicker, PotionEffectType.DARKNESS, 60, 4);

            // Add a little delay before removing hit NPC, so it doesn't look weird
            GameTask.runLater(npc::remove, 20);
        }
        else {
            npc.remove();
        }

        PlayerLib.spawnParticle(location.add(0.0d, 0.5d, 0.0d), Particle.SQUID_INK, 30, 0.1, 0.5, 0.1, 0.05f);
        PlayerLib.playSound(location, ENTITY_SQUID_SQUIRT, 0.25f);

        Utils.getPlayersInRange(location, 3.0d).forEach(target -> {
            GamePlayer.damageEntity(target, 3.0d, target);
            PlayerLib.addEffect(target, PotionEffectType.SLOW, 60, 2);
            PlayerLib.addEffect(target, PotionEffectType.BLINDNESS, 60, 2);
        });
    }

    @Override
    public Response execute(Player player) {
        if (HeroHandle.DARK_MAGE.isUsingUltimate(player)) {
            return Response.error("Unable to use while in ultimate form!");
        }

        final HumanNPC shadowClone = new HumanNPC(player.getLocation(), "", player.getName()) {
            @Override
            public void onClick(Player clicker, HumanNPC npc, ClickType clickType) {
                if (clicker == player) {
                    return;
                }

                explode(npc, clicker);
            }
        };

        GamePlayer.getPlayer(player).addEffect(GameEffectType.INVISIBILITY, 60);
        shadowClone.showAll();
        shadowClone.setEquipment(player.getEquipment());

        if (player.isSwimming()) {
            shadowClone.setPose(NPCPose.SWIMMING);
        }
        else if (player.isSneaking()) {
            shadowClone.setPose(NPCPose.CROUCHING);
        }

        new GameTask() {
            @Override
            public void run() {
                if (!HeroHandle.DARK_MAGE.isUsingUltimate(player)) {
                    Utils.showPlayer(player);
                }

                explode(shadowClone, null);
            }
        }.runTaskLater(60);

        return Response.OK;
    }

}
