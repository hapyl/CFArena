package me.hapyl.fight.game.talents.vortex;

import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayData;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.entity.EntityUtils;
import me.hapyl.eterna.module.reflect.glow.Glowing;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.Event;
import me.hapyl.fight.game.attribute.BaseAttributes;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.Outline;
import me.hapyl.fight.game.heroes.HeroRegistry;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AstralStar implements Ticking {

    private static final DisplayData model = BDEngine.parse(
            "/summon block_display ~-0.5 ~-0.5 ~-0.5 {Passengers:[{id:\"minecraft:item_display\",item:{id:\"minecraft:nether_star\",Count:1},item_display:\"none\",transformation:[1.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f,0.0000f,0.0625f,0.0000f,0.0000f,1.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:nether_star\",Count:1},item_display:\"none\",transformation:[-0.0000f,0.0000f,-1.0000f,0.0000f,0.0000f,1.0000f,0.0000f,0.0625f,1.0000f,0.0000f,-0.0000f,0.0625f,0.0000f,0.0000f,0.0000f,1.0000f]}]}");

    protected final LivingGameEntity entity;
    protected final DisplayEntity displayEntity;

    private final GamePlayer player;
    private final VortexStarTalent talent;

    private ChatColor currentColor;
    private StarState state;

    public AstralStar(GamePlayer player, Location location, VortexStarTalent talent) {
        this.player = player;
        this.talent = talent;

        location.setYaw(0.0f);
        location.setPitch(0.0f);

        this.displayEntity = model.spawnInterpolated(location);

        this.entity = CF.createEntity(location.subtract(0, 0.25, 0), Entities.SLIME, slime -> {
            slime.setGravity(false);
            slime.setSize(2);
            slime.setAI(false);
            slime.setSilent(true);
            slime.setInvisible(true);

            final BaseAttributes attributes = new BaseAttributes();
            attributes.setMaxHealth(talent.healthSacrificePerStar);

            final LivingGameEntity entity = new LivingGameEntity(slime, attributes) {
                @Override
                public void onDamageTaken(@Nonnull DamageInstance instance) {
                    player.setOutline(Outline.RED);

                    AstralStar.this.setState(StarState.BEING_ATTACKED);

                    player.sendSubtitle("&cA star is being attacked!", 1, 5, 1);

                    player.schedule(() -> {
                        AstralStar.this.setState(null);

                        player.setOutline(Outline.CLEAR);
                    }, 5);

                    // Fx
                    playWorldSound(Sound.BLOCK_BELL_USE, 1.25f);
                    playWorldSound(Sound.ENTITY_BLAZE_HURT, 0.75f);

                    instance.setDamage(Math.min(instance.getDamage(), talent.maxStarDamage));
                }

                @Override
                public void onDeath() {
                    super.onDeath();

                    playWorldSound(Sound.ENTITY_BLAZE_DEATH, 0.25f);
                    playWorldSound(Sound.ENTITY_PLAYER_BREATH, 0.25f);
                }

                @Override
                public void onTeammateDamage(@Nonnull LivingGameEntity lastDamager) {
                    lastDamager.sendMessage("&cCannot damage allied Astral Star!");
                }
            };

            entity.setCollision(EntityUtils.Collision.DENY);
            entity.setValidState(true);
            entity.setInformImmune(false);
            entity.setImmune(DamageCause.SUFFOCATION);

            player.getTeam().addEntry(entity.getEntry());

            return entity;
        });

        HeroRegistry.VORTEX.sacrificeHealth(player);
    }

    @Nonnull
    public VortexStarTalent getTalent() {
        return talent;
    }

    @Event
    public void onDeath() {
        player.playWorldSound(Sound.ENTITY_BLAZE_DEATH, 0.0f);
        player.sendTitle("&c⭐", "&4A star was destroyed!", 5, 10, 5);
    }

    @Override
    public void tick() {
        final int ticks = entity.aliveTicks();
        final Location location = entity.getLocation();

        final double y = Math.sin(Math.toRadians(ticks * 5)) * 0.02d;

        location.setYaw(location.getYaw() + 3);
        location.add(0, y, 0);

        // SFx
        if (ticks % 20 == 0) {
            player.getPlayer().playSound(location, Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.RECORDS, 0.5f, 0.75f);
        }

        entity.teleport(location);

        // Sync star entity
        location.add(0, 0.5, 0);
        location.setPitch(0f);

        displayEntity.teleport(location);
    }

    public void setColor(@Nonnull ChatColor color) {
        // don't allow changing if the state is not standby or already the same color
        if (state != null || currentColor == color) {
            return;
        }

        this.currentColor = color;

        this.displayEntity.forEach(entity -> {
            Glowing.glowInfinitely(entity, color, this.player.getPlayer());
        });
    }

    public void setColorGlobal(@Nonnull ChatColor chatColor) {
        this.displayEntity.forEach(entity -> {
            CF.getPlayers().forEach(player -> {
                Glowing.glowInfinitely(entity, chatColor, player.getPlayer());
            });
        });
    }

    public double dot() {
        return player.dot(entity.getLocation());
    }

    @Nonnull
    public Location getLocation() {
        return entity.getLocation();
    }

    public void remove() {
        this.entity.forceRemove();
        this.displayEntity.remove();
    }

    public void teleport(GamePlayer player) {
        final Location playerLocation = player.getLocation();
        final Location location = getLocation();

        location.setYaw(playerLocation.getYaw());
        location.setPitch(playerLocation.getPitch());

        player.teleport(location);
    }

    public double distance() {
        return entity.getLocation().distance(player.getLocation());
    }


    public boolean isDead() {
        return entity.isDead();
    }

    public double getHealth() {
        return entity.isDead() ? 0 : entity.getHealth();
    }

    @Nullable
    public StarState getState() {
        return state;
    }

    public void setState(@Nullable StarState newState) {
        StarState previousState = this.state;

        if (newState != null) {
            newState.onSet(this);
        }

        this.state = newState;

        if (previousState != null) {
            previousState.onUnSet(this);
        }
    }
}
