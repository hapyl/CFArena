package me.hapyl.fight.game.talents.harbinger;

import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MeleeStance extends Talent {

    @DisplayField private final int maxDuration = 600;
    @DisplayField private final int minimumCd = 60;
    @DisplayField private final int cdPerSecond = 30;
    @DisplayField(percentage = true) private final double critChanceIncrease = 0.3d;

    private final PlayerMap<StanceData> dataMap = PlayerMap.newMap();

    private final Weapon abilityItem = new Weapon(Material.IRON_SWORD)
            .setName(Color.STANCE_RANGE + "Raging Blade")
            .setDescription("A blade forged from pure water.")
            .setDamage(8.0d);

    public MeleeStance() {
        super("Melee Stance");

        addDescription("""
                Enter %1$s for maximum of &b{maxDuration}&7 to replace your bow with %2$s!
                &8;;Also gain a Crit Chance increase while in this stance.
                                
                Use again in %1$s to get your bow back.
                                
                &8;;The longer you're in Melee Stance, the longer the cooldown of this ability.
                """, Named.STANCE_MELEE, abilityItem.getName());

        setType(TalentType.ENHANCE);
        setItem(Material.IRON_INGOT);
        setCooldown(-1);
    }

    @Override
    public void onStop() {
        dataMap.clear();
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        final StanceData data = dataMap.get(player);

        if (data != null) {
            data.cancelTask();
        }

        dataMap.remove(player);
    }

    public boolean isActive(GamePlayer player) {
        return getData(player) != null;
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final StanceData data = getData(player);
        final EntityAttributes attributes = player.getAttributes();

        // Switch to Melee
        if (data == null) {
            switchToMelee(player);
            attributes.add(AttributeType.CRIT_CHANCE, critChanceIncrease);
        }
        else {
            switchToRange(player);
            attributes.subtractSilent(AttributeType.CRIT_CHANCE, critChanceIncrease);
        }

        return Response.OK;
    }

    public int getMaxDuration() {
        return maxDuration;
    }

    @Nullable
    public StanceData getData(GamePlayer player) {
        return dataMap.get(player);
    }

    public void switchToMelee(GamePlayer player) {
        final StanceData data = dataMap.remove(player);

        if (data != null) {
            data.cancelTask();
        }

        dataMap.put(player, new StanceData(this, player, player.getItem(HotbarSlots.WEAPON)));
        player.setItemAndSnap(HotbarSlots.WEAPON, abilityItem.getItem());

        // Fix instant use
        startCd(player, 20);

        // Fx
        player.playSound(Sound.ITEM_SHIELD_BREAK, 1.25f);
        player.sendTitle("&2⚔", "", 5, 15, 5);
    }

    public void switchToRange(GamePlayer player) {
        final StanceData data = dataMap.remove(player);

        if (data == null) {
            return;
        }

        data.cancelTask();
        final int cooldown = calculateCooldown(data.getDuration());

        startCd(player, cooldown);
        player.setItemAndSnap(HotbarSlots.WEAPON, data.getOriginalWeapon());

        // Fx
        player.sendMessage("&aMelee Stance is on cooldown for &l%s&a!", CFUtils.decimalFormatTick(cooldown));
        player.playSound(Sound.ENTITY_ARROW_SHOOT, 0.75f);
        player.sendTitle("&2🏹", "", 5, 15, 5);
    }

    private int calculateCooldown(long durationMillis) {
        return (int) (minimumCd + (cdPerSecond * (durationMillis / 1000)));
    }

}
