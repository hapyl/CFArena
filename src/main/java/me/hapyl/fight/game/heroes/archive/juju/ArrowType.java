package me.hapyl.fight.game.heroes.archive.juju;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.Timed;
import me.hapyl.fight.game.talents.archive.juju.PoisonZone;
import me.hapyl.fight.game.talents.archive.juju.TricksOfTheJungle;
import me.hapyl.fight.util.Described;
import me.hapyl.fight.util.SmallCaps;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.math.Tick;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BFormat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public enum ArrowType implements Described {

    ELUSIVE("Elusive Arrows", "Bloom upon impact into multiple arrows, dealing AoE damage.") {
        @Override
        public void onShoot(Player player, Arrow arrow) {
            player.setCooldown(Material.BOW, getTalent().cdBetweenShots);
        }

        @Override
        public void onTick(Player player, Arrow arrow) {
            PlayerLib.spawnParticle(arrow.getLocation(), Particle.TOTEM, 3, 0, 0, 0, 0);
        }

        @Override
        public void onHit(Player player, Arrow arrow) {
            bloomArrow(player, arrow.getLocation());
        }

        private TricksOfTheJungle getTalent() {
            return Talents.TRICKS_OF_THE_JUNGLE.getTalent(TricksOfTheJungle.class);
        }

        private void bloomArrow(Player player, Location location) {
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

        private void spawnArrow(Player player, Location location, Vector vector) {
            if (location.getWorld() == null || !location.getBlock().getType().isAir()) {
                return;
            }

            final Arrow arrow = location.getWorld().spawnArrow(location, vector, 1.5f, 0.25f);
            final double damage = getTalent().damage;

            arrow.setDamage(damage);
            arrow.setShooter(player);
        }
    },
    POISON_IVY(
            "Poison Ivy Arrow",
            Chat.bformat(
                    "Creates a toxic zone upon impact, that reduces {}&7&o and deals damage.",
                    AttributeType.DEFENSE
            )
    ) {
        @Override
        public void onHit(Player player, Arrow arrow) {
            Talents.POISON_ZONE.getTalent(PoisonZone.class).execute(player, arrow.getLocation());
        }

        @Override
        public void onShoot(Player player, Arrow arrow) {
            final JuJu juju = Heroes.JUJU.getHero(JuJu.class);

            juju.unequipArrow(player, this);
            juju.setUsingUltimate(player, false);
        }
    };

    private final String name;
    private final String description;

    ArrowType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void onShoot(Player player, Arrow arrow) {
    }

    public void onHit(Player player, Arrow arrow) {
    }

    public void onTick(Player player, Arrow arrow) {
    }

    public void onEquip(Player player) {
        final Location location = player.getLocation();

        Chat.sendTitle(player, "&aEquipped", SmallCaps.format(this.name()), 5, 15, 10);

        PlayerLib.playSound(location, Sound.ITEM_BONE_MEAL_USE, 0.0f);
        PlayerLib.playSound(location, Sound.BLOCK_GRASS_PLACE, 0.0f);
        PlayerLib.playSound(location, Sound.ENTITY_HORSE_SADDLE, 0.75f);
    }

    public void onUnequip(Player player) {
        final Location location = player.getLocation();

        Chat.sendTitle(player, "&cUnequipped", SmallCaps.format(this.name()), 5, 15, 10);

        PlayerLib.playSound(location, Sound.ITEM_BONE_MEAL_USE, 0.0f);
        PlayerLib.playSound(location, Sound.BLOCK_GRASS_PLACE, 0.0f);
        PlayerLib.playSound(location, Sound.ENTITY_HORSE_SADDLE, 0.75f);
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
