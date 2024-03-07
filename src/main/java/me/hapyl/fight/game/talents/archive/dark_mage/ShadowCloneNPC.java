package me.hapyl.fight.game.talents.archive.dark_mage;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.spigotutils.module.reflect.npc.ClickType;
import me.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import me.hapyl.spigotutils.module.reflect.npc.NPCPose;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class ShadowCloneNPC extends HumanNPC {

    private final GamePlayer player;
    private final ShadowClone talent;
    private final GameTask task;

    private final double health;
    private final int energy;
    private final int[] cooldowns;

    private int lifeTime;

    public ShadowCloneNPC(ShadowClone talent, GamePlayer player) {
        super(CFUtils.anchorLocation(player.getLocation()), "", player.getName());

        this.talent = talent;
        this.player = player;

        // Store data
        this.health = player.getHealth();
        this.energy = player.getUltPoints();

        final Hero hero = player.getHero();

        this.cooldowns = new int[] {
                hero.getFirstTalent().getCdTimeLeft(player),
                hero.getSecondTalent().getCdTimeLeft(player),
                hero.getThirdTalent().getCdTimeLeft(player)
        };

        this.lifeTime = talent.getDuration();
        this.task = new GameTask() {
            @Override
            public void run() {
                if (lifeTime-- > 0) {
                    return;
                }

                talent.clones.remove(player, ShadowCloneNPC.this);

                player.sendSubtitle("&cYour clone dissipated!", 0, 20, 0);
                player.playWorldSound(getLocation(), Sound.ENTITY_GHAST_SCREAM, 0.75f);

                remove();
                playRemoveFx();
            }
        }.runTaskTimer(0, 1);

        // Spawn
        showAll();
        setEquipment(player.getEquipment());

        if (player.isSwimming()) {
            setPose(NPCPose.SWIMMING);
        }
        else if (player.isSneaking()) {
            setPose(NPCPose.CROUCHING);
        }

        // Fx
        player.playWorldSound(Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 0.25f);
        player.playWorldSound(Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 1.25f);
    }

    @Override
    public String toString() {
        return "&3\uD83E\uDE9E &b" + CFUtils.decimalFormatTick(lifeTime);
    }

    @Override
    public void onClick(@Nonnull Player click, @Nonnull ClickType type) {
        final GamePlayer clickedPlayer = CF.getPlayer(click);

        if (player.isSelfOrTeammate(clickedPlayer)) {
            return;
        }

        if (type != ClickType.ATTACK) {
            return;
        }

        remove();

        talent.clones.remove(player, this);
        talent.startCd(player);

        // Fx
        final Location location = getLocation();

        player.playWorldSound(location, Sound.BLOCK_GLASS_BREAK, 0.0f);
        player.playWorldSound(location, Sound.ENTITY_PLAYER_HURT, 0.75f);

        playRemoveFx();

        player.sendSubtitle("&cYou clone was destroyed!", 0, 20, 0);
    }

    @Override
    public void remove() {
        super.remove();
        task.cancel();
    }

    public void playRemoveFx() {
        player.spawnWorldParticle(getLocation(), Particle.SMOKE_LARGE, 20, 0.1, 0.5, 0.1, 0.5f);
    }

    @Nonnull
    public ShadowClone getTalent() {
        return talent;
    }

    public void teleport() {
        player.teleport(getLocation());

        player.setHealth(health);
        player.setUltPoints(energy);

        final Hero hero = player.getHero();

        hero.getFirstTalent().startCd(player, cooldowns[0]);
        hero.getSecondTalent().startCd(player, cooldowns[1]);
        hero.getThirdTalent().startCd(player, cooldowns[2]);

        remove();

        // Fx
        player.playWorldSound(Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 0.75f);
        player.playWorldSound(Sound.ENTITY_ENDERMAN_TELEPORT, 1.25f);

        player.addEffect(Effects.BLINDNESS, 20);
    }

}
