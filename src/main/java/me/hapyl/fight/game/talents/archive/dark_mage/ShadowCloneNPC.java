package me.hapyl.fight.game.talents.archive.dark_mage;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.reflect.npc.ClickType;
import me.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import me.hapyl.spigotutils.module.reflect.npc.NPCPose;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.bukkit.Sound.*;

public class ShadowCloneNPC extends HumanNPC {

    public final Player player;
    public boolean ultimate;

    private boolean valid;

    public ShadowCloneNPC(Player player) {
        super(player.getLocation(), "", player.getName());

        this.player = player;

        // Spawn
        GamePlayer.getPlayer(player).addEffect(GameEffectType.INVISIBILITY, getTalent().getDuration());
        showAll();
        setEquipment(player.getEquipment());

        if (player.isSwimming()) {
            this.setPose(NPCPose.SWIMMING);
        }
        else if (player.isSneaking()) {
            this.setPose(NPCPose.CROUCHING);
        }

        valid = true;
    }

    @Nonnull
    public ShadowClone getTalent() {
        return Talents.SHADOW_CLONE.getTalent(ShadowClone.class);
    }

    @Override
    public void onClick(Player clicker, HumanNPC npc, ClickType clickType) {
        if (clicker == player) {
            return;
        }

        if (ultimate) {
            blind(clicker);
        }
        else {
            explode(clicker);
        }
    }

    public void remove(int delay) {
        if (delay <= 0) {
            remove();
            return;
        }

        GameTask.runLater(this::remove, delay);
    }

    @Override
    @Deprecated
    public void remove() {
        super.remove();
    }

    public void blind(@Nonnull Player clicker) {
        this.lookAt(clicker.getLocation());

        // Fx
        PlayerLib.addEffect(clicker, PotionEffectType.SLOW, 60, 4);
        PlayerLib.addEffect(clicker, PotionEffectType.DARKNESS, 60, 4);

        PlayerLib.playSound(ENTITY_WITCH_CELEBRATE, 2.0f);
        PlayerLib.playSound(ENTITY_WITHER_SHOOT, 0.75f);
    }

    public void explode(@Nullable Player clicker, int delay) {
        valid = false;

        if (delay <= 0) {
            explode(clicker);
            return;
        }

        GameTask.runLater(() -> explode(clicker), delay);
    }

    public void explode(@Nullable Player clicker) {
        if (!valid) {
            return;
        }

        valid = false;

        final ShadowClone talent = getTalent();
        final Location location = this.getLocation();

        // Turn towards target
        if (clicker != null) {
            blind(clicker);

            // Add a little delay before removing hit NPC, so it doesn't look weird
            remove(30);
        }
        else {
            remove();
        }

        PlayerLib.spawnParticle(location.add(0.0d, 0.5d, 0.0d), Particle.SQUID_INK, 30, 0.1, 0.5, 0.1, 0.05f);
        PlayerLib.playSound(location, ENTITY_SQUID_SQUIRT, 0.25f);

        Collect.nearbyPlayers(location, talent.damageRadius).forEach(target -> {
            if (target.is(player)) {
                return; // don't damage self
            }

            target.damage(talent.damage, target);
            target.addPotionEffect(PotionEffectType.SLOW, 60, 2);
            target.addPotionEffect(PotionEffectType.BLINDNESS, 60, 2);
        });
    }


}
