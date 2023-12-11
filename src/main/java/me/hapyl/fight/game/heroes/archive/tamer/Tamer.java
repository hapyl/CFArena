package me.hapyl.fight.game.heroes.archive.tamer;

import com.google.common.collect.Maps;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroPlaque;
import me.hapyl.fight.game.heroes.UltimateCallback;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.tamer.MineOBall;
import me.hapyl.fight.game.talents.archive.tamer.TamingTheWind;
import me.hapyl.fight.game.talents.archive.tamer.pack.ActiveTamerPack;
import me.hapyl.fight.game.talents.archive.tamer.pack.DrWitch;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import javax.annotation.Nonnull;
import java.util.Map;

public class Tamer extends Hero implements Listener, UIComponent, HeroPlaque {

    private final double WEAPON_DAMAGE = 8.0d; // since it's a fishing rod, we're storing the damage here
    private final int WEAPON_COOLDOWN = 10;

    public final double ultimateMultiplier = 2.0d;
    public final Map<ThrownPotion, DrWitch.WitchData> potionMap = Maps.newHashMap();

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
                Improve the &bduration&7 and &aeffectiveness&7 of your talents and beasts.
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

    public ActiveTamerPack getPlayerPack(GamePlayer player) {
        return getFirstTalent().getPack(player);
    }

    @EventHandler()
    public void handleWitchPotion(PotionSplashEvent ev) {
        final ThrownPotion potion = ev.getPotion();
        final DrWitch.WitchData witchData = potionMap.remove(potion);

        if (witchData == null) {
            return;
        }

        witchData.target().heal(witchData.healing());
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

    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
        final ActiveTamerPack pack = getPlayerPack(player);

        if (pack == null) {
            return "";
        }

        final int duration = pack.getDuration();

        return pack.getName() + " " + pack.getPack().toString(pack) + " &e⌚ " + CFUtils.decimalFormatTick(duration);
    }

    @Nonnull
    @Override
    public String text() {
        return "&a&lREWORKED!";
    }
}
