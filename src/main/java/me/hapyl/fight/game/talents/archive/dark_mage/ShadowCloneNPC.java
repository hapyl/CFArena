package me.hapyl.fight.game.talents.archive.dark_mage;

import me.hapyl.fight.game.TalentReference;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.CFUtils;
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

public class ShadowCloneNPC extends HumanNPC implements TalentReference<ShadowClone> {

    public final GamePlayer player;
    private final ShadowClone talent;
    protected boolean ultimate;

    private boolean valid;

    public ShadowCloneNPC(ShadowClone talent, GamePlayer player) {
        super(CFUtils.anchorLocation(player.getLocation()), "", player.getName());

        this.talent = talent;
        this.player = player;

        // Spawn
        player.addEffect(GameEffectType.INVISIBILITY, talent.getDuration());
        showAll();
        setEquipment(player.getEquipment());

        if (player.isSwimming()) {
            setPose(NPCPose.SWIMMING);
        }
        else if (player.isSneaking()) {
            setPose(NPCPose.CROUCHING);
        }

        valid = true;
    }

    public boolean isUltimate() {
        return ultimate;
    }

    @Override
    public void onClick(@Nonnull Player player, @Nonnull ClickType type) {
        if (this.player.is(player)) {
            return;
        }

        if (ultimate) {
            blind(player);
        }
        else {
            explode(player);
        }
    }

    public void remove(int delay) {
        if (delay <= 0) {
            remove();
            return;
        }

        GameTask.runLater(this::remove, delay);
    }

    @Nonnull
    public ShadowClone getTalent() {
        return talent;
    }

    @Override
    @Deprecated
    public void remove() {
        super.remove();
    }

    public void blind(@Nonnull Player clicker) {
        lookAt(clicker.getLocation());

        // Fx
        PlayerLib.addEffect(clicker, PotionEffectType.SLOW, 60, 4);
        PlayerLib.addEffect(clicker, PotionEffectType.DARKNESS, 60, 4);

        final Location location = getLocation();

        player.playWorldSound(location, ENTITY_WITCH_CELEBRATE, 2.0f);
        player.playWorldSound(location, ENTITY_WITHER_SHOOT, 0.75f);
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
        final Location location = getLocation();

        // Turn towards target
        if (clicker != null) {
            blind(clicker);

            // Add a little delay before removing hit NPC, so it doesn't look weird
            remove(30);
        }
        else {
            remove();
        }

        // Damage
        Collect.nearbyEntities(location, talent.damageRadius).forEach(target -> {
            if (target.equals(player)) {
                return; // don't damage self
            }

            target.damage(talent.damage, target);
            target.addPotionEffect(PotionEffectType.SLOW, 60, 2);
            target.addPotionEffect(PotionEffectType.BLINDNESS, 60, 2);
        });

        // Fx
        player.spawnWorldParticle(location.add(0.0d, 1.0d, 0.0d), Particle.SMOKE_LARGE, 30, 0.25d, 0.5d, 0.25d, 0.05f);
        player.playWorldSound(location, ENTITY_SQUID_SQUIRT, 0.25f);
    }


}
