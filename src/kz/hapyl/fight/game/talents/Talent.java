package kz.hapyl.fight.game.talents;

import kz.hapyl.fight.game.GameElement;
import kz.hapyl.fight.game.Response;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class Talent implements GameElement {

	private ItemStack item;
	private final String name;
	private final String description;
	private final Type type;
	private int cd;
	private Material material;

	public Talent(String name, String description, Type type) {
		this.name = name;
		this.description = description;
		this.type = type;
		this.material = Material.BEDROCK;
	}

	public ItemStack getItem() {
		if (this.item == null) {
			this.createItem();
		}
		return this.item;
	}

	@Override
	public void onStart() {

	}

	@Override
	public void onStop() {
		Bukkit.getOnlinePlayers().forEach(player -> player.setCooldown(this.material, 0));
	}

	private void createItem() {
		final ItemBuilder builder = new ItemBuilder(this.material)
				.setName("&a" + this.name)
				.addLore("&8%s %s", Chat.capitalize(this.type), this.type == Type.ULTIMATE ? "" : "Talent")
				.addLore()
				.addSmartLore(this.description);

		if (this.cd > 0) {
			builder.addLore("&9Cooldown: &l%ss".formatted(BukkitUtils.roundTick(this.cd)));
		}

		if (this instanceof UltimateTalent ult) {
			builder.addLore("&9Ultimate Cost: &l%s ※", ult.getCost());
			// ※
		}

		this.item = builder.toItemStack();
	}

	public void setItem(Material material) {
		this.material = material;
	}

	public abstract Response execute(Player player);

	public final void startCd(Player player) {
		if (this.cd <= 0) {
			return;
		}
		player.setCooldown(this.getItem().getType(), this.cd);
	}

	public final boolean hasCd(Player player) {
		return getCdTimeLeft(player) > 0L;
	}

	public final int getCdTimeLeft(Player player) {
		return player.getCooldown(this.item.getType());
	}

	public int getCd() {
		return cd;
	}

	public void setCd(int cd) {
		this.cd = cd;
	}

	public void setCdSec(int cd) {
		this.cd = cd * 20;
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	public enum Type {
		PASSIVE,
		COMBAT,
		ULTIMATE
	}
}
