package me.hapyl.fight.game.talents.storage.harbinger;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.storage.extra.StanceData;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class MeleeStance extends Talent {

	private final Map<Player, StanceData> dataMap = new HashMap<>();
	private final int maxDuration = 600;

	private final Weapon abilityItem = new Weapon(Material.IRON_SWORD).setDamage(8.0d)
            .setName("Raging Blade")
            .setDescription("A blade made of unknown material.");

	public MeleeStance() {
		super("Melee Stance");
		this.setDescription(
                "Enter a Melee Stance for maximum of &b%ss&7 to replace you bow with a sword!__Use again in &e&lMelee Stance&7 to get your bow back.____The longer you're in &e&lMelee Stance&7, the longer the cooldown of this ability.",
                BukkitUtils.roundTick(maxDuration)
        );
        this.setItem(Material.IRON_INGOT);
        this.setCd(-1);
    }

    @Override
    public void onStop() {
        dataMap.clear();
    }

    @Override
    public void onDeath(Player player) {
        dataMap.remove(player);
    }

    public boolean isActive(Player player) {
        return getData(player) != null;
    }

    @Override
    public Response execute(Player player) {
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
	private StanceData getData(Player player) {
		return dataMap.get(player);
	}

	public void switchToMelee(Player player) {
		final StanceData data = getData(player);
		if (data != null) {
			data.cancelTask();
		}

		final PlayerInventory inventory = player.getInventory();
		dataMap.put(player, new StanceData(player, inventory.getItem(0)));

		inventory.setItem(0, abilityItem.getItem());
		inventory.setHeldItemSlot(0);

		startCd(player, 20);

		// Fx
		PlayerLib.playSound(player, Sound.ENTITY_ITEM_BREAK, 1.25f);
		Chat.sendTitle(player, "&2⚔", "", 5, 15, 5);

	}

	public void switchToRange(Player player) {
		final StanceData data = getData(player);
		if (data == null) {
			return;
		}

		// 10s + 2s for each second.
		data.cancelTask();
		final int cooldown = (int)((10 + (2 * data.getDuration() / 1000)) * 20);

		startCd(player, cooldown);
		Chat.sendMessage(player, "&aMelee Stance is on cooldown for &l%ss&a!", BukkitUtils.roundTick(cooldown));

		player.getInventory().setItem(0, data.getOriginalWeapon());
		player.getInventory().setHeldItemSlot(0);
		dataMap.remove(player);

		PlayerLib.playSound(player, Sound.ENTITY_ARROW_SHOOT, 0.75f);
		Chat.sendTitle(player, "&2🏹", "", 5, 15, 5);

	}

}
