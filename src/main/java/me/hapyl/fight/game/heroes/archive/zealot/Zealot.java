package me.hapyl.fight.game.heroes.archive.zealot;

import com.google.common.collect.Maps;
import me.hapyl.fight.event.io.DamageInput;
import me.hapyl.fight.event.io.DamageOutput;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.UltimateCallback;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.zealot.MalevolentHitshield;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.block.display.BlockStudioParser;
import me.hapyl.spigotutils.module.block.display.DisplayData;
import me.hapyl.spigotutils.module.block.display.DisplayEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class Zealot extends Hero {

    protected final Equipment abilityEquipment;
    protected final Map<Player, SoulsRebound> soulsReboundMap;

    private final DisplayData displayData = BlockStudioParser.parse(
            "/summon block_display ~-0.5 ~-0.5 ~-0.5 {Passengers:[{id:\"minecraft:item_display\",item:{id:\"minecraft:golden_sword\",Count:1},item_display:\"none\",transformation:[3.5355f,3.5355f,0.0000f,0.0000f,-3.5355f,3.5355f,0.0000f,0.0000f,0.0000f,-0.0000f,5.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f]}]}"
    );

    public Zealot() {
        super("Zealot");

        setArchetype(Archetype.HEXBANE);
        setItem("131530db74bac84ad9e322280c56c4e0199fbe879883b76c9cf3fd8ff19cf025");
        setWeapon(new ZealotWeapon(this));

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(104, 166, 232, TrimPattern.SILENCE, TrimMaterial.DIAMOND);
        equipment.setLeggings(Material.DIAMOND_LEGGINGS, TrimPattern.SILENCE, TrimMaterial.DIAMOND);
        equipment.setBoots(Material.DIAMOND_BOOTS, TrimPattern.SILENCE, TrimMaterial.DIAMOND);

        abilityEquipment = new Equipment();
        abilityEquipment.setHelmet(getItem());
        abilityEquipment.setChestPlate(104, 166, 232, TrimPattern.SILENCE, TrimMaterial.GOLD);
        abilityEquipment.setLeggings(Material.GOLDEN_LEGGINGS, TrimPattern.SILENCE, TrimMaterial.GOLD);
        abilityEquipment.setBoots(Material.GOLDEN_BOOTS, TrimPattern.RIB, TrimMaterial.GOLD);

        soulsReboundMap = Maps.newConcurrentMap();

        setUltimate(new UltimateTalent("Midas' Punch", """
                Command a giant sword to raise in front of you, dealing AoE damage.
                                
                """, 70).setItem(Material.GOLDEN_SWORD).setDurationSec(5));
    }

    @Nullable
    @Override
    public DamageOutput processDamageAsDamager(DamageInput input) {
        return DamageOutput.OK;
    }

    @Nullable
    @Override
    public DamageOutput processDamageAsVictim(DamageInput input) {
        final MalevolentHitshield shield = getSecondTalent();
        final GamePlayer player = input.getEntityAsPlayer();

        // Cancel even if internal cooldown
        if (player.hasCooldown(shield.cooldownItem)) {
            return DamageOutput.CANCEL;
        }

        if (shield.hasCharge(player)) {
            shield.reduce(player);
            return DamageOutput.CANCEL;
        }

        return DamageOutput.OK;
    }

    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();
        final Vector vector = location.getDirection().normalize().setY(0.0d).multiply(5);
        final UltimateTalent ultimate = getUltimate();

        final double baseY = location.getY();
        location.add(vector);
        location.setPitch(0.0f);

        final DisplayEntity entity = displayData.spawn(location);

        GameTask.runDuration(ultimate, tick -> {
            if (tick == 0) {
                entity.remove();
                return;
            }

            final double y = Math.cos(Math.toRadians(tick));

            location.setY(baseY + y);
            location.setYaw(location.getYaw() + 5);

            entity.teleport(location);
        }, 0, 1);

        return UltimateCallback.OK;
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.BROKEN_HEART_RADIATION.getTalent();
    }

    @Override
    public MalevolentHitshield getSecondTalent() {
        return (MalevolentHitshield) Talents.MALEVOLENT_HITSHIELD.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.MALEDICTION_VEIL.getTalent();
    }
}
