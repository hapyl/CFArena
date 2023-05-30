package me.hapyl.fight.game.talents.storage.heavy_knight;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.storage.SwordMaster;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentQueue;
import me.hapyl.fight.util.Utils;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.math.Tick;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;

public class Slash extends Talent {

    @DisplayField private final double distance = 3.0d;
    @DisplayField private final int effectDuration = Tick.fromSecond(4);

    public Slash() {
        super("Slash");

        setItem(Material.QUARTZ);
        setCooldownSec(8);

        setDescription("""
                Perform a slash in front of you damaging all enemies in small AoE.
                                
                &e;;You talents are executed in the right order, apply additional debuffs.
                """);
    }

    @Override
    public Response execute(Player player) {
        final Location location = player.getLocation();
        final Vector direction = location.getDirection().normalize().setY(0.0d);

        location.add(direction.multiply(distance));

        final List<LivingEntity> entitiesHit = Utils.getEntitiesInRange(
                location,
                distance,
                entity -> Utils.isEntityValid(entity, player)
        );

        entitiesHit.forEach(entity -> {
            GamePlayer.damageEntity(entity, 10.0d, player);
        });

        // Fx
        PlayerLib.spawnParticle(location, Particle.SWEEP_ATTACK, 10, distance, 0.5d, distance, 0.0f);
        PlayerLib.playSound(location, Sound.BLOCK_ANVIL_PLACE, 0.75f);

        // Check for talent order
        if (checkTalentOrder(player)) {
            entitiesHit.forEach(entity -> {
                entity.addPotionEffect(PotionEffectType.SLOW.createEffect(effectDuration, 1));
                entity.addPotionEffect(PotionEffectType.WEAKNESS.createEffect(effectDuration, 1));

                Chat.sendMessage(entity, "");
            });
            return Response.AWAIT;
        }

        return Response.OK;
    }

    private boolean checkTalentOrder(Player player) {
        final Hero hero = Heroes.SWORD_MASTER.getHero(SwordMaster.class);
        final TalentQueue talentQueue = GamePlayer.getPlayer(player).getTalentQueue();

        // have to check for 2 talents, since talent is added AFTER it's executed
        // but since last 2 talents are a and b, this is the third one
        return talentQueue.checkTalents(hero.getFirstTalent(), hero.getSecondTalent());
    }
}
