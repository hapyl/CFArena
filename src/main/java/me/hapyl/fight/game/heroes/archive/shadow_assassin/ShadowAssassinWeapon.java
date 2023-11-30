package me.hapyl.fight.game.heroes.archive.shadow_assassin;

import me.hapyl.fight.game.HeroReference;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class ShadowAssassinWeapon extends Weapon implements HeroReference<ShadowAssassin> {

    @DisplayField private final int backstabCooldown = 400;
    @DisplayField private final double defenseReduction = 0.1d;
    @DisplayField private final int defenseReductionDuration = 60;

    private final ShadowAssassin hero;

    public ShadowAssassinWeapon(ShadowAssassin hero) {
        super(Material.IRON_SWORD);

        setName("Livid Dagger");
        setDescription("""
                A dagger made of bad memories.
                                
                &eAbility: Shadow Stab %sBack Stab
                Hit an enemy from behind to perform a shadow stab attack, reducing their %s and stunning them for a short time.
                                
                &f&m•&f &7Cooldown: &f&l%s
                """, Color.BUTTON.bold(), AttributeType.DEFENSE, CFUtils.decimalFormatTick(backstabCooldown));

        setDamage(8.0d);

        this.hero = hero;
    }

    public void performBackStab(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity) {
        final Location location = entity.getLocation();
        final Vector vector = location.getDirection();

        entity.setVelocity(new Vector(vector.getX(), 0.1d, vector.getZ()).multiply(2.13f));
        entity.addPotionEffect(PotionEffectType.SLOW, 40, 5);
        entity.addPotionEffect(PotionEffectType.CONFUSION, 40, 5);
        entity.getAttributes().decreaseTemporary(Temper.BACKSTAB, AttributeType.DEFENSE, defenseReduction, defenseReductionDuration);

        entity.sendMessage("&a%s stabbed you!", player.getName());
        player.setCooldown(getMaterial(), backstabCooldown);

        // Fx
        entity.playWorldSound(Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.65f);
        entity.spawnWorldParticle(Particle.CRIT, 10, 0.25d, 0.0d, 0.25d, 0.076f);
    }

    @Nonnull
    @Override
    public ShadowAssassin getHero() {
        return hero;
    }
}
