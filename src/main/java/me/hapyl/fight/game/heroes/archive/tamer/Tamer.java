package me.hapyl.fight.game.heroes.archive.tamer;

import me.hapyl.fight.CF;
import me.hapyl.fight.event.io.DamageInput;
import me.hapyl.fight.event.io.DamageOutput;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.DisabledHero;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.UltimateCallback;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.tamer.MineOBall;
import me.hapyl.fight.game.talents.archive.tamer.TamerPack;
import me.hapyl.fight.game.talents.archive.tamer.TamingTheWind;
import me.hapyl.fight.game.weapons.Weapon;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import javax.annotation.Nonnull;

public class Tamer extends Hero implements Listener, DisabledHero {

    private final double WEAPON_DAMAGE = 8.0d; // since it's a fishing rod, we're storing the damage here
    private final int WEAPON_COOLDOWN = 10;

    public Tamer() {
        super("Tamer");

        setDescription("""
                A former circus pet trainer who gained the ability to tame the elements.
                """);

        setItem("fbad693d041db13ff36b81480b06456cd0ad6a57655338b956ea015a150516e2");

        setArchetype(Archetype.STRATEGY);

        final HeroAttributes attributes = getAttributes();
        attributes.setSpeed(70);

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(222, 35, 22);
        equipment.setLeggings(48, 119, 227);
        equipment.setBoots(38, 0, 0);

        setWeapon(new Weapon(Material.FISHING_ROD)
                .setName("Lash")
                .setDescription("An old lash used to train beasts and monsters.")
                .setId("tamer_weapon")
                .setDamage(2.0d)); // This is melee damage, weapon damage is handled in the event

        setUltimate(new UltimateTalent("Improve! Overcome!", """
                Improve the &bduration&7 and &cdamage&7 of your talents.
                """, 50)
                .setType(Talent.Type.ENHANCE)
                .setItem(Material.LINGERING_POTION, builder -> {
                    builder.setPotionColor(Color.ORANGE);
                })
                .setCooldownSec(70)
                .setDurationSec(60)
        );
    }

    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        // TODO -> Add FX
        return UltimateCallback.OK;
    }

    // Cleaned up the code a little
    public void executeTamerPackOnUltimateEnd(GamePlayer player) {
        final TamerPack pack = getPlayerPack(player);

        if (pack == null) {
            return;
        }

        pack.getPack().onUltimateEnd(player, pack);
        pack.removeAll();
    }

    public TamerPack getPlayerPack(GamePlayer player) {
        return getFirstTalent().getPack(player);
    }

    @Override
    public DamageOutput processDamageAsDamager(DamageInput input) {
        final GamePlayer player = input.getDamagerAsPlayer();
        final LivingGameEntity entity = input.getEntity();

        if (player == null) {
            return null;
        }

        if (getFirstTalent().isPackEntity(player, entity.getEntity())) {
            player.sendMessage("&cYou cannot damage your own minion!");
            return DamageOutput.CANCEL;
        }

        return null;
    }

    // prevent pack members from damaging each other and the owner
    @EventHandler()
    public void handleMinionDamage(EntityDamageByEntityEvent ev) {
        final Entity entity = ev.getEntity();
        Entity damager = ev.getDamager();

        // root to shooter
        if (damager instanceof Projectile projectile && projectile.getShooter() instanceof LivingEntity shooter) {
            damager = shooter;
        }

        // Only allow living<->living damage
        if (!(entity instanceof LivingEntity livingEntity) || !(damager instanceof LivingEntity livingDamager)) {
            return;
        }

        final MineOBall mineOBall = getFirstTalent();
        if (!mineOBall.isPackEntity(livingDamager)) {
            return;
        }

        // Cancel event, set damage using GamePlayer
        final double finalDamage = ev.getFinalDamage();

        ev.setCancelled(true);
        ev.setDamage(0.0d);

        // cancel if friendly
        if (mineOBall.isInSamePackOrOwner(entity, damager)) {
            if (damager instanceof Creature creature) {
                creature.setTarget(null);
            }
            return;
        }

        CF.getEntityOptional(livingEntity).ifPresent(gameEntity -> {
            gameEntity.damage(finalDamage, mineOBall.getOwner(livingDamager), EnumDamageCause.MINION);
        });
    }

    @EventHandler()
    public void handleLash(ProjectileHitEvent ev) {
        if (!(ev.getEntity() instanceof FishHook hook) || !(hook.getShooter() instanceof Player player)) {
            return;
        }

        final GamePlayer gamePlayer = CF.getPlayer(player);

        if (gamePlayer == null || !validatePlayer(player) || player.hasCooldown(Material.FISHING_ROD)) {
            return;
        }

        if (ev.getHitBlock() != null) {
            hook.remove();
            return;
        }

        if (ev.getHitEntity() instanceof LivingEntity living) {
            CF.getEntityOptional(living).ifPresent(gameEntity -> {
                gameEntity.damage(WEAPON_DAMAGE, gamePlayer, EnumDamageCause.LEASHED);
            });
            hook.remove();
        }

        gamePlayer.setCooldown(Material.FISHING_ROD, WEAPON_COOLDOWN);
    }

    @Override
    public MineOBall getFirstTalent() {
        return (MineOBall) Talents.MINE_O_BALL.getTalent();
    }

    @Override
    public TamingTheWind getSecondTalent() {
        return (TamingTheWind) Talents.TAMING_THE_WIND.getTalent();
    }

    @Override
    public Talent getThirdTalent() {
        return Talents.TAMING_THE_EARTH.getTalent();
    }

    @Override
    public Talent getFourthTalent() {
        return Talents.TAMING_THE_TIME.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return null;
    }
}
