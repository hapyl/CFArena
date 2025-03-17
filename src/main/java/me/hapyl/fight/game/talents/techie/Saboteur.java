package me.hapyl.fight.game.talents.techie;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.reflect.glow.Glowing;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.techie.BugType;
import me.hapyl.fight.game.heroes.techie.Techie;
import me.hapyl.fight.game.heroes.techie.TechieData;
import me.hapyl.fight.game.maps.EnumLevel;
import me.hapyl.fight.game.maps.gamepack.ActivePack;
import me.hapyl.fight.game.maps.gamepack.HackedPack;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;

import javax.annotation.Nonnull;
import java.util.List;

public class Saboteur extends TechieTalent {

    @DisplayField private final double hackDistance = 8.0d;
    @DisplayField private final double hackedSupplyDamage = 10;
    @DisplayField(scaleFactor = 100) private final double hackedSupplyAttackReduction = 0.25d;
    @DisplayField(scaleFactor = 500) private final double hackedSupplySeedReduction = 0.06d; // 30%
    @DisplayField private final int impairDuration = 100;

    private final TemperInstance temperInstance = Temper.HACKED.newInstance()
            .decrease(AttributeType.ATTACK, hackedSupplyAttackReduction)
            .decrease(AttributeType.SPEED, hackedSupplySeedReduction);

    public Saboteur(@Nonnull Key key) {
        super(key, "Saboteur");

        setType(TalentType.IMPAIR);
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
        final Techie hero = HeroRegistry.TECHIE;
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
                    public void onPickup(@Nonnull GamePlayer target) {
                        // Damage
                        target.setLastDamager(target);
                        target.damage(hackedSupplyDamage, DamageCause.HACK);

                        // Fx
                        target.playWorldSound(Sound.ENTITY_ENDERMAN_HURT, 0.75f);
                        target.playWorldSound(Sound.ENTITY_BLAZE_HURT, 0.75f);

                        target.sendTitle("&bʜᴀᴄᴋᴇᴅ ʙʏ", "&3" + this.player.getName(), 0, 20, 5);

                        temperInstance.temper(target, impairDuration, player);
                        data.bugRandomly(target);
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

            player.sendMessage("&b🕸 &3Hacked %s!".formatted(builder.toString()));
        }
    }

    @Nonnull
    private List<ActivePack> getActivePacks() {
        final EnumLevel currentMap = Manager.current().currentEnumLevel();
        final List<ActivePack> list = Lists.newArrayList();

        currentMap.getLevel().getGamePacks().forEach(gamePack -> list.addAll(gamePack.getActivePacks()));
        return list;
    }

}
