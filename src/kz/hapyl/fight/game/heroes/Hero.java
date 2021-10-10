package kz.hapyl.fight.game.heroes;

import com.google.common.collect.Sets;
import kz.hapyl.fight.event.DamageInput;
import kz.hapyl.fight.event.DamageOutput;
import kz.hapyl.fight.game.GameElement;
import kz.hapyl.fight.game.Manager;
import kz.hapyl.fight.game.PlayerElement;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.talents.UltimateTalent;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.game.weapons.Weapon;
import kz.hapyl.fight.util.CachedItemStack;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public abstract class Hero implements GameElement, PlayerElement {

	private final ClassEquipment equipment;
	private final String name;
	private final CachedItemStack cachedItemStack;

	private String about;
	private ItemStack guiTexture;
	private Weapon weapon;
	private final Set<Player> usingUltimate;

	private UltimateTalent ultimate;

	public Hero(String name) {
		this.name = name;
		this.about = "not much yet";
		this.guiTexture = new ItemStack(Material.RED_BED);
		this.weapon = new Weapon(Material.WOODEN_SWORD);
		this.usingUltimate = Sets.newHashSet();
		this.equipment = new ClassEquipment();
		this.cachedItemStack = new CachedItemStack();
		this.ultimate = new UltimateTalent("invalid ultimate", "", 999) {
			@Override
			public void useUltimate(Player player) {
			}
		};
	}

	public Hero(String name, String lore, Material material) {
		this(name);
		this.setInfo(lore);
		this.setItem(material);
	}

	public ClassEquipment getEquipment() {
		return equipment;
	}

	public CachedItemStack getMenuItem() {
		return cachedItemStack;
	}

	protected void setUltimate(UltimateTalent ultimate) {
		this.ultimate = ultimate;
	}

	public final void setUsingUltimate(Player player, boolean flag) {
		if (flag) {
			usingUltimate.add(player);
		}
		else {
			usingUltimate.remove(player);
		}
	}

	public void clearUsingUltimate() {
		this.usingUltimate.clear();
	}

	public final void setUsingUltimate(Player player, boolean flag, int reverseAfter) {
		this.setUsingUltimate(player, flag);
		GameTask.runLater(() -> setUsingUltimate(player, !flag), reverseAfter);
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

	public void setInfo(String about) {
		this.about = about;
	}

	public ItemStack getItem() {
		return guiTexture;
	}

	public void setItem(ItemStack guiTexture) {
		this.guiTexture = guiTexture;
	}

	public void setItem(Material material) {
		this.guiTexture = new ItemBuilder(material).hideFlags().toItemStack();
	}

	public void setItem(String texture) {
		this.guiTexture = ItemBuilder.playerHead(texture).hideFlags().build();
	}

	public abstract Talent getFirstTalent();

	public abstract Talent getSecondTalent();

	public abstract Talent getPassiveTalent();

	public DamageOutput processDamageAsDamager(DamageInput input) {
		return null;
	}

	public DamageOutput processDamageAsVictim(DamageInput input) {
		return null;
	}

	public void onDeath(Player player) {
	}

	public UltimateTalent getUltimate() {
		return this.ultimate;
	}

	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}

	public Weapon getWeapon() {
		return weapon;
	}

	// some utils here
	protected final boolean validatePlayer(Player player, Heroes heroes) {
		final Manager current = Manager.current();
		return validatePlayer(player) && current.getSelectedHero(player) == heroes;
	}

	protected final boolean validatePlayer(Player player) {
		final Manager current = Manager.current();
		return current.isGameInProgress() && current.isPlayerInGame(player);

	}

	@Override
	public void onStart() {

	}

	@Override
	public void onStop() {

	}

	public Hero getHandle() {
		return this;
	}

}
