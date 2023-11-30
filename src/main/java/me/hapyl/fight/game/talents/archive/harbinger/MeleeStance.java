package me.hapyl.fight.game.talents.archive.harbinger;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MeleeStance extends Talent {

    @DisplayField private final int maxDuration = 600;
    @DisplayField private final int minimumCd = 60;
    @DisplayField private final int cdPerSecond = 30;

    private final PlayerMap<StanceData> dataMap = PlayerMap.newMap();

    private final Weapon abilityItem = new Weapon(Material.IRON_SWORD).setDamage(8.0d)
            .setName("Raging Blade")
            .setDescription("A blade, forged from pure water.");

    public MeleeStance() {
        super("Melee Stance");

        addDescription("""
                    Enter a Melee Stance for maximum of &b{maxDuration}&7 to replace you bow with &e%s&7!
                    
                    Use again in &e&lMelee Stance&7 to get your bow back.
                    
                    The longer you're in &e&lMelee Stance&7, the longer the cooldown of this ability.
                """, abilityItem.getName());

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

        // Switch to Melee
        if (data == null) {
            switchToMelee(player);
        }
        else {
            switchToRange(player);
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
        final StanceData data = getData(player);
        if (data != null) {
            data.cancelTask();
        }

        final PlayerInventory inventory = player.getInventory();
        dataMap.put(player, new StanceData(player, inventory.getItem(0)));

        player.setItemAndSnap(HotbarSlots.WEAPON, abilityItem.getItem());

        startCd(player, 20);

        // Fx
        player.playSound(Sound.ITEM_SHIELD_BREAK, 1.25f);
        player.sendTitle("&2⚔", "", 5, 15, 5);
    }

    public void switchToRange(GamePlayer player) {
        final StanceData data = getData(player);
        if (data == null) {
            return;
        }

        // 10s + 2s for each b.
        data.cancelTask();
        final int cooldown = calculateCooldown(data.getDuration());

        startCd(player, cooldown);
        player.sendMessage("&aMelee Stance is on cooldown for &l%ss&a!", BukkitUtils.roundTick(cooldown));

        player.setItemAndSnap(HotbarSlots.WEAPON, data.getOriginalWeapon());
        dataMap.remove(player);

        player.playSound(Sound.ENTITY_ARROW_SHOOT, 0.75f);
        player.sendTitle("&2🏹", "", 5, 15, 5);
    }

    private int calculateCooldown(long duration) {
        return (int) (minimumCd + (cdPerSecond * (duration / 1000)));
    }

}
