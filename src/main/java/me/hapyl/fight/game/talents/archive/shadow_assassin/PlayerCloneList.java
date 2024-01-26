package me.hapyl.fight.game.talents.archive.shadow_assassin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.fight.game.TalentReference;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.task.player.PlayerGameTask;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.Map;

public class PlayerCloneList implements TalentReference<ShadowAssassinClone> {

    protected static final int MAX_LINK_TIME = 20;

    protected final Map<CloneNPC, LivingGameEntity> attackingMap;

    private final ShadowAssassinClone talent;
    private final GamePlayer player;
    private final LinkedList<CloneNPC> clones;

    private CloneNPC linkedClone;

    public PlayerCloneList(ShadowAssassinClone talent, GamePlayer player) {
        this.talent = talent;
        this.player = player;
        this.clones = Lists.newLinkedList();
        this.attackingMap = Maps.newHashMap();
    }

    public void linkToClone() {
        if (linkedClone == null) {
            return;
        }

        final Location location = player.getLocation();

        player.spawnWorldParticle(location, Particle.PORTAL, 10, 0.5, 1, 0.5, 0);
        player.spawnWorldParticle(location, Particle.REVERSE_PORTAL, 10, 0.5, 1, 0.5, 0);

        player.teleport(linkedClone.getLocation());
        player.playWorldSound(Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f);
        player.addEffect(Effects.BLINDNESS, 1, 20);

        getTalent().getData(player).addEnergy(talent.energyRegen);

        player.snapToWeapon();
        linkedClone = null;
    }

    @Nonnull
    @Override
    public ShadowAssassinClone getTalent() {
        return talent;
    }

    public void disappearAll() {
        clones.forEach(CloneNPC::remove0);
        clones.clear();
        linkedClone = null;
    }

    @Nonnull
    public CloneNPC createClone(Location location) {
        if (clones.size() >= talent.cloneLimit) {
            final CloneNPC firstClone = clones.pollFirst();

            if (firstClone != null) {
                firstClone.disappear();
            }
        }

        final CloneNPC npc = new CloneNPC(this, location, player);
        clones.add(npc);

        return npc;
    }

    public void remove(CloneNPC clone) {
        clones.remove(clone);
    }

    public boolean isBeingAttacked(LivingGameEntity entity) {
        return attackingMap.containsValue(entity);
    }

    protected void createCloneLink(CloneNPC clone) {
        if (linkedClone != null) { // don't allow a double link
            return;
        }

        linkedClone = clone;

        new PlayerGameTask(player) {
            private int tick = MAX_LINK_TIME;

            @Override
            public void run() {
                if (tick-- <= 0 || linkedClone == null) {
                    linkedClone = null;
                    cancel();
                    return;
                }

                final int progress = tick * 20 / MAX_LINK_TIME;

                player.sendTitle(
                        "&a|".repeat(progress) + "&8|".repeat(20 - progress),
                        "&bsɴᴇᴀᴋ ᴛᴏ ᴛᴇʟᴇᴘᴏʀᴛ",
                        0, 2, 0
                );

                player.playSound(Sound.BLOCK_LEVER_CLICK, 0.5f + (1.5f / MAX_LINK_TIME * tick));
            }
        }.runTaskTimer(0, 1);
    }
}
