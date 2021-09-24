package kz.hapyl.fight.game.talents;

import kz.hapyl.fight.game.GameElement;
import kz.hapyl.fight.game.Response;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class Talent implements GameElement {

	private ItemStack item;
	private Material material;
	private String texture;

	private final String name;
	private final String description;
	private final Type type;
	private int cd;


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
	}

	private void createItem() {
		final ItemBuilder builder = new ItemBuilder(this.material)
				.setName("&a" + this.name)
				.addLore("&8%s %s", Chat.capitalize(this.type), this.type == Type.ULTIMATE ? "" : "Talent")
				.addLore()
				.addSmartLore(this.description);

		if (texture != null && this.material == Material.PLAYER_HEAD) {
			builder.setHeadTexture(texture);
		}

		if (this.cd > 0) {
			builder.addLore("&9Cooldown%s: &l%ss".formatted(this instanceof ChargedTalent ? " between charges" : "", BukkitUtils.roundTick(this.cd)));
		}
		else if (this.cd <= -1) {
			builder.addLore("&9Cooldown: &lDynamic");
		}

		if (this instanceof ChargedTalent charge) {
			final int maxCharges = charge.getMaxCharges();
			builder.addLore("&9Max Charges: &l%s", maxCharges);
			builder.addLore("&9Recharge Time: &l%ss", BukkitUtils.roundTick(charge.getRechargeTime()));
		}

		if (this instanceof UltimateTalent ult) {
			builder.addLore("&9Ultimate Cost: &l%s ※", ult.getCost());
			builder.glow();
			// ※
		}

		builder.hideFlags();
		this.item = builder.toItemStack();
	}

	public Talent setItem(String headTexture) {
		this.setItem(Material.PLAYER_HEAD);
		this.texture = headTexture;
		return this;
	}

	public Talent setItem(Material material) {
		this.material = material;
		return this;
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
		return player.getCooldown(this.material);
	}

	public int getCd() {
		return cd;
	}

	public Talent setCd(int cd) {
		this.cd = cd;
		return this;
	}

	public Talent setCdSec(int cd) {
		this.cd = cd * 20;
		return this;
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
