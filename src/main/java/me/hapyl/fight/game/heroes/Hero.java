package me.hapyl.fight.game.heroes;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import me.hapyl.fight.event.DamageInput;
import me.hapyl.fight.event.DamageOutput;
import me.hapyl.fight.game.GameElement;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.PlayerElement;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.SmallCaps;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public abstract class Hero implements GameElement, PlayerElement {

    private final ClassEquipment equipment;
    private final String name;

    private Role role;
    private String about;
    private ItemStack guiTexture;
    private Weapon weapon;
    private final Set<Player> usingUltimate;

    private long minimumLevel;

    private UltimateTalent ultimate;

    public Hero(String name) {
        this.name = name;
        this.about = "No description provided.";
        this.guiTexture = new ItemStack(Material.RED_BED);
        this.weapon = new Weapon(Material.WOODEN_SWORD);
        this.usingUltimate = Sets.newHashSet();
        this.equipment = new ClassEquipment();
        this.role = Role.NONE;
        this.minimumLevel = 0;
        this.ultimate = new UltimateTalent("Unknown Ultimate", "This hero's ultimate talent is not yet implemented!", Integer.MAX_VALUE);
    }

    public long getMinimumLevel() {
        return minimumLevel;
    }

    public void setMinimumLevel(long minimumLevel) {
        this.minimumLevel = minimumLevel;
    }

    public Hero(String name, String lore) {
        this(name);
        this.setInfo(lore);
    }

    public Hero(String name, String lore, Material material) {
        this(name);
        this.setInfo(lore);
        this.setItem(material);
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Role getRole() {
        return role;
    }

    public ClassEquipment getEquipment() {
        return equipment;
    }

    public void setUltimateDuration(int duration) {
        this.ultimate.setDuration(duration);
    }

    public int getUltimateDuration() {
        return ultimate.getDuration();
    }

    public String getUltimateDurationString() {
        return BukkitUtils.roundTick(getUltimateDuration());
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

    /**
     * @see Hero#setUltimateDuration(int)
     */
    @Deprecated
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

    /**
     * Unleashes hero's ultimate.
     */
    public abstract void useUltimate(Player player);

    public abstract Talent getFirstTalent();

    public abstract Talent getSecondTalent();

    public abstract Talent getPassiveTalent();

    @Nullable
    public DamageOutput processDamageAsDamager(DamageInput input) {
        return null;
    }

    @Nullable
    public DamageOutput processDamageAsVictim(DamageInput input) {
        return null;
    }

    @Nullable
    public DamageOutput processDamageAsDamagerProjectile(DamageInput input, Projectile projectile) {
        return null;
    }

    public boolean processInvisibilityDamage(Player player, LivingEntity entity, double damage) {
        Chat.sendMessage(player, "&cCannot deal damage while invisible!");
        return true;
    }

    /**
     * Moved ultimate initiation and predicates to Hero class because it was just messy using a Talent instance.
     *
     * Though talent is still used at description and cooldown tracker.
     */
    public String predicateMessage() {
        return "Unable to use now.";
    }

    public boolean predicateUltimate(Player player) {
        return true;
    }

    public void onDeath(Player player) {
    }

    public UltimateTalent getUltimate() {
        return this.ultimate;
    }

    public void setWeapon(Material material, String name, String lore, double damage) {
        setWeapon(new Weapon(material, name, lore, damage));
    }

    public void setWeapon(Material material, String name, double damage) {
        setWeapon(new Weapon(material, name, null, damage));
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    // some utils here
    public final boolean validatePlayer(Player player, Heroes heroes) {
        final Manager current = Manager.current();
        return validatePlayer(player) && current.getSelectedHero(player) == heroes;
    }

    public final boolean validatePlayer(Player player) {
        final Manager current = Manager.current();
        return current.isGameInProgress() && current.isPlayerInGame(player);

    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
    }

    public void onRespawn(Player player) {
    }

    public Hero getHandle() {
        return this;
    }

    //    public void resetTalents(Player player) {
    //        for (Talent talent : getTalents()) {
    //            if (talent instanceof ChargedTalent chargedTalent) {
    //                chargedTalent.stopCd(player);
    //            }
    //        }
    //    }

    public Set<Talent> getTalents() {
        final Set<Talent> talents = Sets.newHashSet();

        talents.add(getFirstTalent());
        talents.add(getSecondTalent());
        talents.add(getPassiveTalent());

        if (this instanceof ComplexHero complex) {
            talents.add(complex.getThirdTalent());
            talents.add(complex.getFourthTalent());
            talents.add(complex.getFifthTalent());
        }

        return talents;
    }

    public String getNameSmallCaps() {
        return SmallCaps.format(getName());
    }

    public List<Talent> getTalentsSorted() {
        final List<Talent> talents = Lists.newArrayList();
        talents.add(getFirstTalent());
        talents.add(getSecondTalent());

        if (this instanceof ComplexHero complex) {
            talents.add(complex.getThirdTalent());
            talents.add(complex.getFourthTalent());
            talents.add(complex.getFifthTalent());
        }

        talents.add(getPassiveTalent());

        return talents;
    }


}
