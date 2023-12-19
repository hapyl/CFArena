package me.hapyl.fight.game.talents.archive.bloodfiend;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.TalentReference;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.bloodfield.Bloodfiend;
import me.hapyl.fight.game.heroes.archive.bloodfield.BloodfiendData;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.profile.data.AchievementData;
import me.hapyl.fight.game.profile.data.Type;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.module.block.display.BlockStudioParser;
import me.hapyl.spigotutils.module.block.display.DisplayData;
import me.hapyl.spigotutils.module.block.display.DisplayEntity;
import me.hapyl.spigotutils.module.entity.Entities;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class TwinClaw extends TickingGameTask implements TalentReference<TwinClaws> {

    private final static DisplayData DISPLAY_DATA = BlockStudioParser.parse(
            "{Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:orange_terracotta\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,-0.5000f,0.0000f,1.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f,-0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:orange_terracotta\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,-0.5000f,0.0000f,1.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f,-0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:orange_terracotta\",Properties:{}},transformation:[0.6250f,0.0000f,0.0000f,-0.3125f,0.0000f,0.6875f,0.0000f,2.0000f,0.0000f,0.0000f,0.5000f,0.1250f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:red_terracotta\",Properties:{}},transformation:[0.7500f,0.0000f,0.0000f,-0.3750f,0.0000f,1.0000f,0.0000f,1.0000f,0.0000f,0.0000f,0.8125f,-0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:terracotta\",Properties:{}},transformation:[0.5000f,0.0000f,0.0000f,-0.2500f,0.0000f,0.2210f,-0.4419f,2.8750f,0.0000f,0.2210f,0.4419f,-0.1250f,0.0000f,0.0000f,0.0000f,1.0000f]}]}"
    );

    private final GamePlayer player;
    private final Vector vector;
    private final ArmorStand entity;
    private final int duration;
    private final DisplayEntity displayEntity;

    public TwinClaw(GamePlayer player, Location startLocation, Vector vector, int duration) {
        this.player = player;
        this.vector = vector;
        this.duration = duration;

        final Vector direction = startLocation.getDirection();

        //direction.setY(0.0d);
        startLocation.setDirection(direction);

        entity = Entities.ARMOR_STAND_MARKER.spawn(startLocation, self -> {
            self.setGravity(false);
            self.setSmall(true);
            self.setInvulnerable(true);
            self.setInvisible(true);
        });

        displayEntity = DISPLAY_DATA.spawn(startLocation);

        runTaskTimer(0, 1);

        // Fx
        player.playWorldSound(Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.75f);
    }

    @Override
    public void run(int tick) {
        if (tick > duration) {
            remove();
            return;
        }

        final Location location = entity.getLocation();
        location.add(vector);

        if (location.getBlock().getType().isOccluding()) {
            remove();
            return;
        }

        entity.teleport(location);
        displayEntity.teleport(location);

        // Damage
        final LivingGameEntity nearestEntity = Collect.nearestEntity(location, 1, entity -> !player.isSelfOrTeammate(entity));

        if (nearestEntity == null) {
            return;
        }

        final TwinClaws talent = getTalent();
        double damage = talent.twinClawDamage;

        if (nearestEntity instanceof GamePlayer gamePlayer) {
            final Bloodfiend bloodfiend = Heroes.BLOODFIEND.getHero(Bloodfiend.class);
            final BloodfiendData data = bloodfiend.getData(player);

            if (data.isBitten(gamePlayer)) {
                damage += damage * talent.bittenDamageIncrease;
            }
        }

        nearestEntity.damage(damage, player, EnumDamageCause.TWINCLAW);
        remove();

        // Achievement
        final PlayerProfile profile = player.getProfile();
        final AchievementData data = profile.getPlayerData().getAchievementData(Achievements.THEY_ARE_TWINS_ALRIGHT);

        final int useTime = data.checkExpire(5000).increment(Type.USE_TIME, 1);

        if (useTime >= 2) {
            data.completeAchievement();
        }
    }

    public void remove() {
        final Location location = entity.getLocation().add(0.0d, 1.5d, 0.0d);

        entity.remove();
        displayEntity.remove();
        cancel();

        // Fx
        player.spawnWorldParticle(location, Particle.EXPLOSION_NORMAL, 20, 0.5d, 0.6d, 0.5d, 0.1f);
    }

    @Nonnull
    @Override
    public TwinClaws getTalent() {
        return Talents.TWIN_CLAWS.getTalent(TwinClaws.class);
    }
}
