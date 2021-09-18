package kz.hapyl.fight.game.heroes;

import com.google.common.collect.Sets;
import kz.hapyl.fight.game.GameElement;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.talents.UltimateTalent;
import kz.hapyl.fight.game.weapons.Weapons;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public abstract class Hero implements GameElement {

	private final String name;
	private String about;
	private ItemStack guiTexture;
	private Weapons weapon;
	private final Set<Player> usingUltimate;

	public Hero(String name) {
		this.name = name;
		this.about = "not much yet.";
		this.guiTexture = new ItemStack(Material.RED_BED);
		this.weapon = Weapons.DEFAULT;
		this.usingUltimate = Sets.newHashSet();
	}

	public final void setUsingUltimate(Player player, boolean flag) {
		if (flag) {
			usingUltimate.add(player);
		} else {
			usingUltimate.remove(player);
		}
	}

	public final boolean isUsingUltimate(Player player) {
		return usingUltimate.contains(player);
	}

	public String getName() {
		return name;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public ItemStack getGuiTexture() {
		return guiTexture;
	}

	public void setItem(ItemStack guiTexture) {
		this.guiTexture = guiTexture;
	}

	public void setItem(Material material) {
		this.guiTexture = new ItemStack(material);
	}

	public void setItem(String texture) {
		this.guiTexture = ItemBuilder.playerHead(texture).build();
	}

	public abstract Talent getFirstTalent();

	public abstract Talent getSecondTalent();

	public abstract UltimateTalent getUltimate();

	public abstract Talent getPassiveTalent();

	public void setWeapon(Weapons weapon) {
		this.weapon = weapon;
	}

	public Weapons getWeapon() {
		return weapon;
	}

	@Override
	public void onStart() {

	}

	@Override
	public void onStop() {

	}
}
