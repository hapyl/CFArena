package me.hapyl.fight.game.heroes.mage;

import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MageWeapon extends Weapon {

    private final Mage hero;

    public MageWeapon(Mage hero) {
        super(Material.IRON_HOE, Key.ofString("soul_eater"));

        this.hero = hero;

        setDamage(8.0d);
        setName("Soul Eater");
        setDescription("""
                A weapon capable of absorbing soul fragments and convert them into fuel.
                """);

        setAbility(AbilityType.RIGHT_CLICK, new SoulWhisper());
    }

    public class SoulWhisper extends Ability {

        public SoulWhisper() {
            super("Soul Whisper", "Launch a laser of souls that damages the first enemy it hits.");

            setCooldownSec(1);
        }

        @Nullable
        @Override
        public Response execute(@Nonnull GamePlayer player) {
            final int souls = hero.getSouls(player);

            if (souls <= 0) {
                player.playSound(Sound.ENTITY_PLAYER_BURP, 2.0f);
                return null;
            }

            CFUtils.rayTraceLine(player, 50, 0.5, -1.0d, this::spawnParticles, entity -> hitEnemy(entity, player));

            HeroRegistry.MAGE.addSouls(player, -1);

            player.setCooldownInternal(getMaterial(), 10);
            player.playSound(Sound.BLOCK_SOUL_SAND_BREAK, 0.75f);

            return Response.OK;
        }

        private void spawnParticles(Location location) {
            PlayerLib.spawnParticle(location, Particle.SOUL, 1, 0.1d, 0.0d, 0.1d, 0.035f);
        }

        private void hitEnemy(LivingGameEntity entity, GamePlayer player) {
            final Location location = entity.getLocation();

            entity.addTag("LastDamage=Soul");

            // Fx
            entity.spawnWorldParticle(location, Particle.SOUL, 8, 0, 0, 0, 0.10f);
            entity.spawnWorldParticle(location, Particle.SOUL_FIRE_FLAME, 10, 0, 0, 0, 0.25f);
        }
    }


}
