package me.hapyl.fight.game.heroes.archive.mage;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.HeroReference;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.weapons.RightClickable;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class MageWeapon extends Weapon implements RightClickable, HeroReference<Mage> {

    private final Mage hero;

    public MageWeapon(Mage hero) {
        super(Material.IRON_HOE);

        this.hero = hero;

        setDamage(8.0d);
        setName("Soul Eater");
        setDescription("""
                A weapon capable of absorbing soul fragments and convert them into fuel.
                """);
        setId("soul_eater");

        setAbility(AbilityType.RIGHT_CLICK, Ability.of("Soul Whisper", """
                Launch a laser of souls that damages the first enemy it hits.
                """, this));
    }

    @Nonnull
    @Override
    public Mage getHero() {
        return hero;
    }

    @Override
    public void onRightClick(@Nonnull GamePlayer player, @Nonnull ItemStack item) {
        if (player.hasCooldown(Material.IRON_HOE)) {
            return;
        }

        final Mage hero = getHero();
        final int souls = hero.getSouls(player);

        if (souls <= 0) {
            player.playSound(Sound.ENTITY_PLAYER_BURP, 2.0f);
            return;
        }

        hero.addSouls(player, -1);
        player.setCooldown(Material.IRON_HOE, 10);
        player.playSound(Sound.BLOCK_SOUL_SAND_BREAK, 0.75f);
        CFUtils.rayTraceLine(player, 50, 0.5, -1.0d, this::spawnParticles, entity -> hitEnemy(entity, player));
    }

    private void spawnParticles(Location location) {
        PlayerLib.spawnParticle(location, Particle.SOUL, 1, 0.1d, 0.0d, 0.1d, 0.035f);
    }

    private void hitEnemy(LivingEntity livingEntity, GamePlayer player) {
        final Location location = livingEntity.getLocation();
        livingEntity.addScoreboardTag("LastDamage=Soul");

        CF.getEntityOptional(livingEntity).ifPresent(entity -> {
            entity.damage(getDamage() / 2, player, EnumDamageCause.SOUL_WHISPER);
        });

        PlayerLib.spawnParticle(location, Particle.SOUL, 8, 0, 0, 0, 0.10f);
        PlayerLib.spawnParticle(location, Particle.SOUL_FIRE_FLAME, 10, 0, 0, 0, 0.25f);
    }

}
