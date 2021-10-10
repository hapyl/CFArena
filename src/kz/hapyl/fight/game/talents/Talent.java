package kz.hapyl.fight.game.talents;

import kz.hapyl.fight.game.GameElement;
import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.util.Function;
import kz.hapyl.fight.util.Utils;
import kz.hapyl.spigotutils.module.annotate.Super;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public abstract class Talent implements GameElement {

	private ItemStack item;
	private Material material;
	private String texture;

	private final String name;
	private final Type type;
	private String description;
	private int cd;

	public Talent(String name) {
		this(name, "", Type.COMBAT);
	}

	public Talent(String name, String description) {
		this(name, description, Type.COMBAT);
	}

	public Talent(String name, String description, Type type) {
		this.name = name;
		this.description = description;
		this.type = type;
		this.material = Material.BEDROCK;
	}

	public Talent(String name, String description, Material material) {
		this(name, description);
		this.setItem(material);
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDescription(String description, Object... replacements) {
		this.description = description.formatted(replacements);
	}

	public Material getMaterial() {
		return material;
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

	public void onDeath(Player player) {

	}

	private void createItem() {
		final ItemBuilder builder = new ItemBuilder(this.material)
				.setName("&a" + this.name)
				.addLore("&8%s %s", Chat.capitalize(this.type), this.type == Type.ULTIMATE ? "" : "Talent")
				.addLore()
				.addSmartLore(this.description, 35);

		if (texture != null && this.material == Material.PLAYER_HEAD) {
			builder.setHeadTexture(texture);
		}

		if (itemFunction != null) {
			itemFunction.execute(builder);
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

	private Function<ItemBuilder> itemFunction;

	public Talent setItem(Material material, Function<ItemBuilder> function) {
		this.setItem(material);
		this.itemFunction = function;
		return this;
	}

	protected abstract Response execute(Player player);

	@Super
	@Nonnull
	public final Response execute0(Player player) {
		final Response canUseRes = Utils.playerCanUseAbility(player);
		if (canUseRes.isError()) {
			return canUseRes;
		}

		final Response response = execute(player);
		return response == null ? Response.ERROR_DEFAULT : response;
	}

	public final void startCd(Player player, int customCd) {
		player.setCooldown(this.getItem().getType(), customCd);
	}

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

	public static int DYNAMIC = -1;

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
