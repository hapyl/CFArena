package me.hapyl.fight.game.heroes.juju;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.Timed;
import me.hapyl.fight.game.talents.juju.PoisonZone;
import me.hapyl.fight.game.talents.juju.TricksOfTheJungle;
import me.hapyl.fight.util.Described;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.math.Tick;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BFormat;
import me.hapyl.spigotutils.module.util.SmallCaps;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public enum ArrowType implements Described {

    ELUSIVE("Elusive Arrows", "Bloom upon impact into &a&omultiple&7&o arrows, dealing &c&oAoE &c&odamage&7&o.") {
        @Override
        public void onTick(GamePlayer player, Arrow arrow) {
            PlayerLib.spawnParticle(arrow.getLocation(), Particle.TOTEM_OF_UNDYING, 3, 0, 0, 0, 0);
        }

        @Override
        public void onHit(GamePlayer player, Arrow arrow) {
            bloomArrow(player, arrow.getLocation());
        }

        private TricksOfTheJungle getTalent() {
            return Talents.TRICKS_OF_THE_JUNGLE.getTalent(TricksOfTheJungle.class);
        }

        private void bloomArrow(GamePlayer player, Location location) {
            final TricksOfTheJungle talent = getTalent();
            final double horizontalSpread = talent.horizontalSpread;
            final double ySpread = talent.ySpread;

            location.add(0, 2, 0);

            spawnArrow(player, location, new Vector(-horizontalSpread, ySpread, 0));
            spawnArrow(player, location, new Vector(horizontalSpread, ySpread, 0));
            spawnArrow(player, location, new Vector(0, ySpread, horizontalSpread));
            spawnArrow(player, location, new Vector(0, ySpread, -horizontalSpread));
            spawnArrow(player, location, new Vector(horizontalSpread, ySpread, horizontalSpread));
            spawnArrow(player, location, new Vector(horizontalSpread, ySpread, -horizontalSpread));
            spawnArrow(player, location, new Vector(-horizontalSpread, ySpread, horizontalSpread));
            spawnArrow(player, location, new Vector(-horizontalSpread, ySpread, -horizontalSpread));
        }

        private void spawnArrow(GamePlayer player, Location location, Vector vector) {
            if (location.getWorld() == null || !location.getBlock().getType().isAir()) {
                return;
            }

            final Arrow arrow = location.getWorld().spawnArrow(location, vector, 1.5f, 0.25f);
            final double damage = getTalent().damage;

            arrow.setDamage(damage);
            arrow.setShooter(player.getPlayer());
        }
    },
    POISON_IVY(
            "Poison Ivy Arrow", """
            Creates a &a&otoxic zone&7&o upon impact that reduces %s&7&o and deals &brapid&7&o damage.
            """.formatted(AttributeType.DEFENSE)

    ) {
        @Override
        public void onHit(GamePlayer player, Arrow arrow) {
            Talents.POISON_ZONE.getTalent(PoisonZone.class).execute(player, arrow.getLocation());
        }

        @Override
        public void onShoot(GamePlayer player, Arrow arrow) {
            final JuJu juju = Heroes.JUJU.getHero(JuJu.class);

            juju.unequipArrow(player, this);
            player.setUsingUltimate(false);
        }
    };

    private final String name;
    private final String description;
    private final String smallCaps;

    ArrowType(String name, String description) {
        this.name = name;
        this.description = description;
        this.smallCaps = SmallCaps.format(this.name());
    }

    public void onShoot(GamePlayer player, Arrow arrow) {
    }

    public void onHit(GamePlayer player, Arrow arrow) {
    }

    public void onTick(GamePlayer player, Arrow arrow) {
    }

    public void onEquip(GamePlayer player) {
        final Location location = player.getLocation();

        player.sendTitle("&aEquipped", smallCaps, 5, 15, 10);

        player.playWorldSound(location, Sound.ITEM_BONE_MEAL_USE, 0.0f);
        player.playWorldSound(location, Sound.BLOCK_GRASS_PLACE, 0.0f);
        player.playWorldSound(location, Sound.ENTITY_HORSE_SADDLE, 0.75f);
    }

    public void onUnequip(GamePlayer player) {
        final Location location = player.getLocation();

        player.sendTitle("&cUnequipped", smallCaps, 5, 15, 10);

        player.playWorldSound(location, Sound.ITEM_BONE_MEAL_USE, 0.0f);
        player.playWorldSound(location, Sound.BLOCK_GRASS_PLACE, 0.0f);
        player.playWorldSound(location, Sound.ENTITY_HORSE_SADDLE, 0.75f);
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }

    @Nonnull
    public String getTalentDescription(@Nonnull Timed timed) {
        return BFormat.format("""
                Equip {name} for &b{duration}s&7.
                                
                &a&l{name}
                &7&o;;{description}
                """, getName(), Tick.round(timed.getDuration()), getName(), getDescription());
    }

}
