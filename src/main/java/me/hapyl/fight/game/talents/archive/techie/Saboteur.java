package me.hapyl.fight.game.talents.archive.techie;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.HeroReference;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.techie.BugType;
import me.hapyl.fight.game.heroes.archive.techie.Techie;
import me.hapyl.fight.game.heroes.archive.techie.TechieData;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.game.maps.gamepack.ActivePack;
import me.hapyl.fight.game.maps.gamepack.HackedPack;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.reflect.glow.Glowing;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;

import javax.annotation.Nonnull;
import java.util.List;

public class Saboteur extends TechieTalent implements HeroReference<Techie> {

    @DisplayField private final double hackDistance = 8.0d;
    @DisplayField private final double hackedSupplyDamage = 10;
    @DisplayField(scaleFactor = 100) private final double hackedSupplyAttackReduction = 0.25d;
    @DisplayField(scaleFactor = 500) private final double hackedSupplySeedReduction = 0.06d; // 30%
    @DisplayField private final int impairDuration = 100;

    private final TemperInstance temperInstance = Temper.HACKED.newInstance()
            .decrease(AttributeType.ATTACK, hackedSupplyAttackReduction)
            .decrease(AttributeType.SPEED, hackedSupplySeedReduction);

    public Saboteur() {
        super("Saboteur");

        setType(Type.IMPAIR);
        setItem(Material.IRON_TRAPDOOR);
        setCooldownSec(8);
        setCastingTime(10);
    }

    @Nonnull
    @Override
    public String getHackDescription() {
        return """
                hack all &copponents&7 in front of you, &bimplanting&7 a random &f%s&7 onto them.
                                
                &6Bugs:
                %s&7: %s
                                
                %s&7: %s
                                
                %s&7: %s
                             
                This ability can also hack &aSupply Packs&7, rendering them &4unobtainable&7 for &nyou&7 or your &nteammates&7 but &eimpairing&7 and &bimplanting&7 a random &fbug&7 onto an &cenemy&7 when they pick it up.
                """.formatted(
                Named.BUG,
                BugType.TYPE_A.getName(), BugType.TYPE_A.getDescription(),
                BugType.TYPE_D.getName(), BugType.TYPE_D.getDescription(),
                BugType.TYPE_S.getName(), BugType.TYPE_S.getDescription()
        );
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        getActivePacks().forEach(pack -> {
            if (pack.hacked != null && pack.hacked.player.equals(player)) {
                pack.hacked = null;

                final ArmorStand entity = pack.getEntity();
                if (entity != null) {
                    Glowing.stopGlowing(entity);
                }
            }
        });
    }

    @Override
    public void onHack(@Nonnull GamePlayer player) {
        final Techie hero = getHero();
        final Location location = player.getLocation();
        final TechieData data = hero.getPlayerData(player);

        int hackedPacks = 0;
        int hackedEnemies = 0;

        // Hack supply pack
        for (ActivePack pack : getActivePacks()) {
            if (pack.getLocation().distance(location) <= hackDistance) {
                final ArmorStand entity = pack.getEntity();

                if (entity == null || pack.hacked != null) {
                    continue;
                }

                pack.hacked = new HackedPack(player) {
                    @Override
                    public void onPickup(@Nonnull GamePlayer player) {
                        // Damage
                        player.setLastDamager(player);
                        player.damage(hackedSupplyDamage, EnumDamageCause.HACK);

                        // Fx
                        player.playWorldSound(Sound.ENTITY_ENDERMAN_HURT, 0.75f);
                        player.playWorldSound(Sound.ENTITY_BLAZE_HURT, 0.75f);

                        player.sendTitle("&bʜᴀᴄᴋᴇᴅ ʙʏ", "&3" + this.player.getName(), 0, 20, 5);

                        temperInstance.temper(player, impairDuration);
                        data.bugRandomly(player);
                    }
                };

                // Glow hacked pack
                player.getTeam().getPlayers().forEach(teammate -> {
                    Glowing.glow(teammate.getPlayer(), entity, ChatColor.AQUA, 100000);
                });

                hackedPacks++;
            }
        }

        // Hack enemies
        for (LivingGameEntity entity : Collect.nearbyEntities(location, hackDistance)) {
            if (player.isSelfOrTeammate(entity)) {
                continue;
            }

            data.bugRandomly(entity);
            hackedEnemies++;
        }

        if (hackedPacks == 0 && hackedEnemies == 0) {
            player.sendMessage("&c🕸 &4Didn't hacked anything!");
        }
        else {
            final StringBuilder builder = new StringBuilder();

            if (hackedPacks > 0) {
                builder.append(hackedPacks).append(" supply pack").append(hackedPacks != 1 ? "s" : "");
                if (hackedEnemies > 0) {
                    builder.append(" and ");
                }
            }

            if (hackedEnemies > 0) {
                builder.append(hackedEnemies).append(hackedEnemies == 1 ? " enemy" : " enemies");
            }

            player.sendMessage("&b🕸 &3Hacked %s!", builder.toString());
        }
    }

    @Nonnull
    @Override
    public Techie getHero() {
        return Heroes.TECHIE.getHero(Techie.class);
    }

    @Nonnull
    private List<ActivePack> getActivePacks() {
        final GameMaps currentMap = Manager.current().getCurrentMap();
        final List<ActivePack> list = Lists.newArrayList();

        currentMap.getMap().getGamePacks().forEach(gamePack -> list.addAll(gamePack.getActivePacks()));
        return list;
    }

}
