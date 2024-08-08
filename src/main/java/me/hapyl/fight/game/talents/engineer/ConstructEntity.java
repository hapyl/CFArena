package me.hapyl.fight.game.talents.engineer;

import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.Removable;
import me.hapyl.fight.util.Ticking;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class ConstructEntity implements Removable, Ticking {

    private final GamePlayer player;
    private final Construct construct;

    // This entity is used as a "collision" and damage check.
    private final LivingGameEntity entity;

    // Armor stand is used to display health, time, etc.
    private final ArmorStand stand;

    // Current display entity
    private DisplayEntity displayEntity;

    public ConstructEntity(GamePlayer player, Construct construct) {
        this.player = player;
        this.construct = construct;

        final Location location = construct.location;

        this.entity = CF.createEntity(
                LocationHelper.addAsNew(location, 0, construct.talent.yOffset / 2, 0),
                Entities.SLIME, self -> {
                    final double health = construct.healthScaled().get(0, 10.0d);

                    self.setSilent(true);
                    self.setInvisible(true);
                    self.setAI(false);
                    self.setSize(3);
                    self.setMaxHealth(health);
                    self.setHealth(health);

                    final LivingGameEntity entity = new LivingGameEntity(self) {
                        @Override
                        public void onDamageTaken(@Nonnull DamageInstance instance) {
                            player.playWorldSound(location, Sound.ENTITY_IRON_GOLEM_HURT, 1.5f);
                        }

                        @Override
                        public void onTeammateDamage(@Nonnull LivingGameEntity lastDamager) {
                            lastDamager.sendMessage("&cCannot damage allied Construct!");
                        }
                    };
                    entity.setImmune(EnumDamageCause.SUFFOCATION);
                    entity.setInformImmune(false);
                    entity.setCustomName(construct.getName());

                    player.getTeam().addEntry(entity.getEntry());

                    return entity;
                }
        );

        final EngineerTalent talent = construct.talent;

        this.stand = Entities.ARMOR_STAND_MARKER.spawn(location.clone().add(0, talent.yOffset, 0), self -> {
            self.setInvisible(true);
            self.setSmall(true);
            self.setGravity(false);
        });

        setDisplayEntity(0);
    }

    @Nonnull
    public LivingGameEntity getEntity() {
        return entity;
    }

    @Nonnull
    public ArmorStand getStand() {
        return stand;
    }

    @Nonnull
    public DisplayEntity getDisplayEntity() {
        return displayEntity;
    }

    public void setDisplayEntity(int level) {
        if (displayEntity != null) {
            displayEntity.remove();
        }

        displayEntity = construct.talent.getDisplayData(level).spawnInterpolated(construct.location);
    }

    @Override
    public void tick() {
        stand.setCustomName(Chat.format(
                "&8[&f&l%s&8] &a%s &c&l%.0f &c❤ &b%ss".formatted(
                        construct.getLevelRoman(),
                        construct.getName(),
                        entity.getHealth(),
                        construct.getDurationLeft()
                )
        ));
        stand.setCustomNameVisible(true);
    }

    @Override
    public void remove() {
        entity.remove();
        stand.remove();
        displayEntity.remove();
    }

    public boolean isDead() {
        return entity.isDead();
    }

    @Nonnull
    public Location getLocation() {
        return displayEntity.getHead().getLocation();
    }

    public void lookAt(@Nonnull Location location) {
        final Location entityLocation = displayEntity.getHead().getLocation();
        final Vector vector = location.toVector().subtract(entityLocation.toVector());

        entityLocation.setDirection(vector);
        displayEntity.teleport(entityLocation);
    }
}
