package me.hapyl.fight.game.heroes.aurora;

import com.google.common.collect.Sets;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.event.custom.ProjectilePostLaunchEvent;
import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.entity.EquipmentSlots;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.aurora.AuroraArrowTalent;
import me.hapyl.fight.game.talents.aurora.DivineIntervention;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.game.weapons.BowWeapon;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.util.CollectionUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Set;

public class Aurora extends Hero implements PlayerDataHandler<AuroraData>, Listener, UIComponent, Disabled {

    private final PlayerDataMap<AuroraData> auroraDataMap = PlayerMap.newDataMap(AuroraData::new);
    private final Set<AuroraArrowData> arrowDataSet = Sets.newHashSet();

    public Aurora(@Nonnull Heroes handle) {
        super(handle, "Aurora");

        setArchetype(Archetype.SUPPORT);
        setGender(Gender.FEMALE);

        final HeroAttributes attributes = getAttributes();

        setWeapon(new BowWeapon("Celestial", """
                A unique bow of celestial origins.
                """, 2));

        setUltimate(new AuroraUltimate());
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        player.setItem(EquipmentSlots.ARROW, new ItemStack(Material.ARROW));
    }

    @Override
    public void onStart() {
        new TickingGameTask() {
            @Override
            public void run(int tick) {
                // Tick arrows
                arrowDataSet.removeIf(data -> data.arrow().isDead() || data.player().isDeadOrRespawning());
                arrowDataSet.forEach(data -> data.type().onTick(data.player(), data.arrow(), tick));

                // Tick data
                auroraDataMap.values().forEach(data -> {
                    if (data.arrow == null) {
                        return;
                    }

                    final String string = data.arrow.getString(data.arrowCount);
                    data.player.sendSubtitle(string, 0, 5, 0);
                });

                // Tick teleport
                // Doing sqrt every tick is wack
                if (modulo(5)) {
                    getAlivePlayers().forEach(player -> {
                        final AuroraData data = getPlayerData(player);
                        final DivineIntervention passiveTalent = getPassiveTalent();

                        if (passiveTalent.hasCd(player)) {
                            data.target = null;
                            return;
                        }

                        data.target = Collect.targetEntityDot(
                                player,
                                passiveTalent.maxDistance,
                                0.95d,
                                player::isTeammate
                        );
                    });
                }
            }
        }.runTaskTimer(0, 1);
    }

    @Override
    public boolean processTeammateDamage(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity, DamageInstance instance) {
        instance.setDamage(0.0);
        instance.setCancelled(true);

        return false; // Don't cancel damage to not spam the "Cannot damage teammates."
    }

    @EventHandler()
    public void handleSneak(PlayerToggleSneakEvent ev) {
        final GamePlayer player = CF.getPlayer(ev);

        if (player == null || !validatePlayer(player)) {
            return;
        }

        final AuroraData data = getPlayerData(player);

        if (data.target == null) {
            return;
        }

        final DivineIntervention passiveTalent = getPassiveTalent();

        if (passiveTalent.hasCd(player)) {
            return;
        }

        final Location location = getTeleportLocation(data.target.getLocation());

        player.teleport(location);
        passiveTalent.startCd(player);

        // Fx
        player.playWorldSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.75f);
    }

    @EventHandler()
    public void handleShot(ProjectilePostLaunchEvent ev) {
        final GamePlayer player = ev.getShooter();

        if (!validatePlayer(player)) {
            return;
        }

        final Projectile projectile = ev.getProjectile();

        if (!(projectile instanceof Arrow arrow)) {
            return;
        }

        final AuroraData data = getPlayerData(player);
        final AuroraArrowTalent arrowType = data.arrow;

        if (arrowType == null) {
            return;
        }

        arrowDataSet.add(new AuroraArrowData(player, arrowType, arrow));
        arrowType.onShoot(player, arrow);

        data.arrowCount--;

        if (data.arrowCount <= 0) {
            data.setArrow(null);
        }
    }

    @Override
    public void processDamageAsDamagerProjectile(@Nonnull DamageInstance instance, @Nonnull Projectile projectile) {
        final GamePlayer player = instance.getDamagerAsPlayer();

        if (player == null || !(projectile instanceof Arrow arrow)) {
            return;
        }

        final AuroraArrowData data = CollectionUtils.findAndRemove(arrowDataSet, d -> d.arrow() == arrow);

        if (data == null) {
            return;
        }

        final LivingGameEntity entity = instance.getEntity();

        data.type().onHit(player, entity, instance);
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.CELESTE_ARROW.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.ETHEREAL_ARROW.getTalent();
    }

    @Override
    public DivineIntervention getPassiveTalent() {
        return (DivineIntervention) Talents.DIVINE_INTERVENTION.getTalent();
    }

    @Nonnull
    @Override
    public PlayerDataMap<AuroraData> getDataMap() {
        return auroraDataMap;
    }

    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
        final AuroraData data = getPlayerData(player);
        final DivineIntervention passiveTalent = getPassiveTalent();
        final LivingGameEntity target = data.target;

        if (passiveTalent.hasCd(player)) {
            return "&6\uD83D\uDC7C &f" + CFUtils.formatTick(passiveTalent.getCdTimeLeft(player));
        }

        return target != null ? "&6\uD83D\uDC7C &e%s".formatted(target.getName()) : "";
    }

    private Location getTeleportLocation(Location location) {
        location.add(1, 0, 1);

        for (int x = 0; x < 2; x++) {
            for (int z = 0; z < 2; z++) {
                location.subtract(x, 0, z);

                if (checkLocation(location)) {
                    return location;
                }

                location.add(x, 0, z);
            }
        }

        return location;
    }

    private boolean checkLocation(Location location) {
        final Block block = location.getBlock();

        if (!block.getRelative(BlockFace.DOWN).getType().isSolid()) {
            return false;
        }

        if (!block.isPassable()) {
            return false;
        }

        return block.getRelative(BlockFace.UP).isPassable();
    }

    private class AuroraUltimate extends UltimateTalent {

        @DisplayField private final double shieldCapacity = 1000;

        public AuroraUltimate() {
            super("Divine Arrow", 70);

            setDescription("""
                    Shoot a &bdivine arrow&7 up in the sky.
                                        
                    After a &nshort&7 &ndelay&7, the arrow &asplits&7 and rushes towards all &ateammates&7.
                                        
                    Upon &ncontact&7, it explodes in small AoE, dealing &cdamage&7 to &4enemies&7 and applying &bDivine Protection Seal&7 to any nearby &ateammates&7.
                                        
                    &6Divine Protection Seal
                    &8▷&7 Grants a &eshield&7 that &nconstantly&7 decreases its capacity.
                    &8▷&7 Grants a %s and %s boost &nidentical&7 to that of %s.
                                        
                    &2Aurora&7 becomes &binvulnerable&7 for the whole duration unless she &ndeals&7 damage.
                    &8;;Excluding teammate damage.
                    """.formatted(AttributeType.CRIT_CHANCE, AttributeType.CRIT_DAMAGE, getSecondTalent()));

            setType(TalentType.SUPPORT);
            setItem(Material.TIPPED_ARROW, then -> {
                then.setPotionColor(Color.AQUA);
            });

            setDurationSec(6);
            setCooldownSec(50);
        }

        @Nonnull
        @Override
        public UltimateResponse useUltimate(@Nonnull GamePlayer player) {
            return UltimateResponse.OK;
        }
    }

}
