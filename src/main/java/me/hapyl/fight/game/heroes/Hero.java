package me.hapyl.fight.game.heroes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.fight.event.DamageInput;
import me.hapyl.fight.event.DamageOutput;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GameElement;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.PlayerElement;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.archive.ender.Ender;
import me.hapyl.fight.game.heroes.archive.moonwalker.Moonwalker;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.SmallCaps;
import me.hapyl.spigotutils.module.annotate.Super;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Base Hero class.
 * <p>
 * A hero <b>must</b> contains {@link #getFirstTalent()}, {@link #getSecondTalent()} and {@link #getPassiveTalent()},
 * but may or may not have up to 3 extra talents if needed.
 *
 * @see GameElement
 * @see PlayerElement
 */
public abstract class Hero implements GameElement, PlayerElement {

    private final HeroAttributes attributes;
    private final HeroEquipment equipment;
    private final String name;
    private final Map<Player, Long> usedUltimateAt;
    private final Map<Player, GameTask> reverseTasks;
    private final CachedHeroItem cachedHeroItem;
    private Origin origin;
    private Role role;
    private Archetype archetype;
    private String description;
    private ItemStack guiTexture;
    private Weapon weapon;
    private long minimumLevel;
    private UltimateTalent ultimate;

    @Super
    public Hero(String name) {
        this.name = name;
        this.description = "No description provided.";
        this.guiTexture = new ItemStack(Material.RED_BED);
        this.weapon = new Weapon(Material.WOODEN_SWORD);
        this.usedUltimateAt = Maps.newHashMap();
        this.reverseTasks = Maps.newConcurrentMap();
        this.equipment = new HeroEquipment();
        this.attributes = new HeroAttributes(this);
        this.origin = Origin.NOT_SET;
        this.archetype = Archetype.NOT_SET;
        this.role = Role.NONE;
        this.minimumLevel = 0;
        this.ultimate = new UltimateTalent("Unknown Ultimate", "This hero's ultimate talent is not yet implemented!", Integer.MAX_VALUE);
        this.cachedHeroItem = new CachedHeroItem(this);

        setItem("null"); // default to null because I don't like exceptions
    }

    public Hero(String name, String lore) {
        this(name);
        this.setDescription(lore);
    }

    public Hero(String name, String lore, Material material) {
        this(name);
        setDescription(lore);
        setItem(material);
    }

    @Nonnull
    public CachedHeroItem getCachedHeroItem() {
        return cachedHeroItem;
    }

    /**
     * Returns minimum level required to use this hero.
     *
     * @return minimum level required to use this hero.
     */
    public long getMinimumLevel() {
        return minimumLevel;
    }

    /**
     * Sets minimum level required to use this hero.
     *
     * @param minimumLevel - New minimum level.
     */
    public void setMinimumLevel(long minimumLevel) {
        this.minimumLevel = minimumLevel;
    }

    @Nonnull
    public Archetype getArchetype() {
        return archetype;
    }

    public void setArchetype(@Nonnull Archetype archetype) {
        this.archetype = archetype;
    }

    /**
     * Returns this hero's role.
     *
     * @return this hero's role.
     */
    @Deprecated
    public Role getRole() {
        return role;
    }

    /**
     * Sets this hero role.
     *
     * @param role - New role.
     */
    @Deprecated
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Returns the origin of this hero.
     *
     * @return the origin of this hero.
     */
    public Origin getOrigin() {
        return origin;
    }

    /**
     * Sets the origin for this hero.
     *
     * @param origin - New origin.
     */
    public void setOrigin(Origin origin) {
        this.origin = origin;
    }

    /**
     * Gets hero's attributes.
     *
     * @return hero attributes.
     */
    @Nonnull
    public HeroAttributes getAttributes() {
        return attributes;
    }

    /**
     * Returns this hero's weapon.
     *
     * @return this hero's weapon.
     */
    public HeroEquipment getEquipment() {
        return equipment;
    }

    /**
     * Returns this hero's ultimate duration.
     *
     * @return this hero's ultimate duration.
     */
    public int getUltimateDuration() {
        return ultimate.getDuration();
    }

    /**
     * Sets this hero's ultimate duration.
     *
     * @param duration - New duration.
     */
    public void setUltimateDuration(int duration) {
        this.ultimate.setDuration(duration);
    }

    /**
     * Returns this hero's ultimate duration formatted to string.
     *
     * @return this hero's ultimate duration formatted to string.
     */
    public String getUltimateDurationString() {
        return BukkitUtils.roundTick(getUltimateDuration());
    }

    /**
     * Sets if player is currently using their ultimate, preventing them from gaining points and using their ultimate again.
     * This is automatically handled if {@link this#setUltimateDuration(int)} if used.
     *
     * @param player - Player.
     * @param flag   - New flag.
     */
    public final void setUsingUltimate(Player player, boolean flag) {
        if (flag) {
            usedUltimateAt.put(player, System.currentTimeMillis());
        }
        else {
            usedUltimateAt.remove(player);
            cancelOldReverseTask(player);
            onUltimateEnd(player);
        }
    }

    /**
     * Returns ticks when player used their ultimate. Or -1 if they haven't used yet.
     *
     * @param player - Player.
     * @return ticks when player used their ultimate. Or -1 if they haven't used yet.
     */
    public long getUsedUltimateAt(Player player) {
        return usedUltimateAt.getOrDefault(player, -1L);
    }

    /**
     * Returns millis left until player can use their ultimate again.
     *
     * @param player - Player.
     * @return millis left until player can use their ultimate again.
     */
    public long getUltimateDurationLeft(Player player) {
        final int duration = getUltimateDuration() * 50;

        if (duration == 0) {
            return 0;
        }

        return duration - (System.currentTimeMillis() - getUsedUltimateAt(player));
    }

    /**
     * Clears all players who are currently using their ultimate.
     */
    public void clearUsingUltimate() {
        usedUltimateAt.clear();
    }

    /**
     * Sets if player is currently using their ultimate, then removes them after duration.
     */
    public final void setUsingUltimate(Player player, boolean flag, int reverseAfter) {
        setUsingUltimate(player, flag);

        cancelOldReverseTask(player);

        reverseTasks.put(
                player,
                GameTask.runLater(() -> setUsingUltimate(player, !flag), reverseAfter)
        );
    }

    /**
     * Returns true if player is currently using their ultimate.
     *
     * @param player - Player.
     * @return true if player is currently using their ultimate.
     */
    public final boolean isUsingUltimate(Player player) {
        return usedUltimateAt.containsKey(player);
    }

    /**
     * Returns the name of this hero.
     *
     * @return name of this hero.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns description of this hero.
     *
     * @return description of this hero.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description of this hero.
     *
     * @param about - New description.
     */
    public void setDescription(String about) {
        this.description = about;
    }

    /**
     * Returns this hero GUI item, defaults to RED_BED.
     *
     * @return this hero GUI item, defaults to RED_BED.
     */
    public ItemStack getItem() {
        return (guiTexture.getType() == Material.RED_BED) ? getEquipment().getHelmet() : guiTexture;
    }

    /**
     * Heroes are required to have a player-head in GUI now.
     *
     * @deprecated Use {@link this#setItem(String)} instead.
     */
    @Deprecated
    public void setItem(ItemStack guiTexture) {
        this.guiTexture = guiTexture;
    }

    /**
     * Heroes are required to have a player-head in GUI now.
     *
     * @deprecated Use {@link this#setItem(String)} instead.
     */
    @Deprecated
    public void setItem(Material material) {
        this.guiTexture = new ItemBuilder(material).hideFlags().toItemStack();
    }

    /**
     * Sets this hero's GUI item from a texture link.
     * <p>
     * <b>
     * This link must be 'Minecraft-URL' from <a href="https://minecraft-heads.com/custom-heads">here</a>.
     * <br>
     * Should be a 'short' value, like this: '5be9de7852dc4c66ba337ae37f92b9904e431a4908d01389c7b21ea4644ce845'
     * </b>
     *
     * @param texture64 - Texture in base64 format.
     */
    public void setItem(String texture64) {
        guiTexture = ItemBuilder.playerHeadUrl(texture64).asIcon();
        getEquipment().setTexture(texture64);
    }

    /**
     * Called whenever a player presses the ultimate, no matter if it's ready or not.
     */
    public void onUltimate(Player player, UltimateStatus status) {
    }

    /**
     * Unleashes this hero's ultimate.
     */
    public abstract void useUltimate(Player player);

    /**
     * Returns this hero a talent.
     *
     * @return this hero a talent.
     */
    @ReturnValueMustBeAConstant
    public abstract Talent getFirstTalent();

    /**
     * Returns this hero b talent.
     *
     * @return this hero b talent.
     */
    @ReturnValueMustBeAConstant
    public abstract Talent getSecondTalent();

    /**
     * Returns this hero passive talent.
     *
     * @return this hero passive talent.
     */
    @ReturnValueMustBeAConstant
    public abstract Talent getPassiveTalent();

    @ReturnValueMustBeAConstant
    public Talent getThirdTalent() {
        return null;
    }

    @ReturnValueMustBeAConstant
    public Talent getFourthTalent() {
        return null;
    }

    @ReturnValueMustBeAConstant
    public Talent getFifthTalent() {
        return null;
    }

    /**
     * Called when player DAMAGES something.
     *
     * <h2>Examples:</h2>
     * <blockquote>
     * Increase OUTGOING damage by 50%:
     * <pre>
     *      return new DamageOutput(input.getDamage() * 1.5d);
     * </pre>
     *
     * </blockquote>
     * <blockquote>
     * Cancel OUTGOING damage:
     * <pre>
     *      return DamageOutput.CANCEL;
     * </pre>
     * </blockquote>
     *
     * @param input - Initial damage input.
     * @return new damage output, or null to skip.
     */
    @Nullable
    public DamageOutput processDamageAsDamager(DamageInput input) {
        return null;
    }

    /**
     * Called when player TAKES DAMAGE something.
     *
     * <h2>Examples:</h2>
     * <blockquote>
     * Increase REDUCE damage by 50%:
     * <pre>
     *      return new DamageOutput(input.getDamage() / 1.5d);
     * </pre>
     *
     * </blockquote>
     * <blockquote>
     * Cancel INCOMING damage:
     * <pre>
     *      return DamageOutput.CANCEL;
     * </pre>
     * </blockquote>
     *
     * @param input - Initial damage input.
     * @return new damage output, or null to skip.
     */
    @Nullable
    public DamageOutput processDamageAsVictim(DamageInput input) {
        return null;
    }

    /**
     * Called when player DAMAGES something.
     *
     * <h2>Examples:</h2>
     * <blockquote>
     * Increase OUTGOING damage by 50%:
     * <pre>
     *      return new DamageOutput(input.getDamage() * 1.5d);
     * </pre>
     *
     * </blockquote>
     * <blockquote>
     * Cancel OUTGOING damage:
     * <pre>
     *      return DamageOutput.CANCEL;
     * </pre>
     * </blockquote>
     *
     * @param input      - Initial damage input.
     * @param projectile - Projectile that dealt damage.
     * @return new damage output, or null to skip.
     */
    @Nullable
    public DamageOutput processDamageAsDamagerProjectile(DamageInput input, Projectile projectile) {
        return null;
    }

    /**
     * Called whenever invisible player dealt damage.
     *
     * @param player - Player, who dealt damage. Always invisible.
     * @param entity - Entity that took damage.
     * @param damage - Damage dealt.
     * @return true to cancel damage, false to allow.
     */
    public boolean processInvisibilityDamage(GamePlayer player, LivingGameEntity entity, double damage) {
        player.sendMessage("&cCannot deal damage while invisible!");
        return true;
    }

    /**
     * Executes upon player death.
     * <b>This only trigger if player has this hero selected.</b>
     *
     * @param player - Player.
     */
    public void onDeath(Player player) {
    }

    /**
     * Executes upon ANY player death.
     *
     * @param player - Player.
     * @param killer - Killer.
     * @param cause  - Cause.
     */
    public void onDeathGlobal(@Nonnull GamePlayer player, @Nullable GameEntity killer, @Nullable EnumDamageCause cause) {
    }

    /**
     * @see GameElement#onStart()
     */
    @Override
    public void onStart() {
    }

    /**
     * @see GameElement#onStop() ()
     */
    @Override
    public void onStop() {
    }

    /**
     * Called whenever player's ultimate is over.
     *
     * @param player - Player.
     */
    public void onUltimateEnd(Player player) {
    }

    /**
     * Predicate for ultimate. Return true if a player is able to use their ultimate, false otherwise.
     *
     * @param player - Player, who is trying to use ultimate.
     * @return true if a player is able to use their ultimate, false otherwise.
     * @see Ender#predicateUltimate(Player)
     * @see Moonwalker#predicateUltimate(Player)
     */
    public boolean predicateUltimate(Player player) {
        return true;
    }

    /**
     * Return the message that will be displayed if player CANNOT use their ultimate, aka {@link #predicateUltimate(Player)} returns false.
     *
     * @param player - Player who is trying to use ultimate.
     * @return the message that will be displayed if player CANNOT use their ultimate.
     */

    public String predicateMessage(Player player) {
        return "Unable to use now.";
    }

    /**
     * Called whenever player respawns.
     *
     * @param player - Player.
     */
    public void onRespawn(Player player) {
    }

    /**
     * Returns this hero ultimate.
     *
     * @return this hero ultimate.
     */
    public UltimateTalent getUltimate() {
        return this.ultimate;
    }

    /**
     * Sets this hero's weapon.
     *
     * @param ultimate - New weapon.
     */
    protected void setUltimate(UltimateTalent ultimate) {
        this.ultimate = ultimate;
    }

    protected void setUltimate(UltimateTalent ultimate, Consumer<UltimateTalent> andThen) {
        setUltimate(ultimate);
        andThen.accept(ultimate);
    }

    /**
     * Sets this hero weapon.
     *
     * @param material - Material.
     * @param name     - Name.
     * @param lore     - Lore.
     * @param damage   - Damage.
     */
    public void setWeapon(Material material, String name, String lore, double damage) {
        setWeapon(new Weapon(material, name, lore, damage));
    }

    /**
     * Sets this hero weapon.
     *
     * @param material - Material.
     * @param name     - Name.
     * @param damage   - Damage.
     */
    public void setWeapon(Material material, String name, double damage) {
        setWeapon(new Weapon(material, name, "", damage));
    }

    /**
     * Returns this hero weapon.
     *
     * @return this hero weapon.
     */
    public Weapon getWeapon() {
        return weapon;
    }

    /**
     * Sets this hero weapon.
     *
     * @param weapon - Weapon.
     */
    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    /**
     * Returns true if there is a game in progress and player is in game, and player's selected hero is the same as the one provided.
     *
     * @param player - Player.
     * @return true, if there is a game in progress and player is in game, and player's selected hero is the same as the one provided.
     */
    public final boolean validatePlayer(Player player) {
        final Manager current = Manager.current();
        return validPlayerInGame(player) && current.getCurrentHero(player) == this;
    }

    // Utilities for checks, etc.

    @Nullable
    public Talent getTalent(int slot) {
        return switch (slot) {
            case 1 -> getFirstTalent();
            case 2 -> getSecondTalent();
            case 3 -> getThirdTalent();
            case 4 -> getFourthTalent();
            case 5 -> getFifthTalent();
            default -> null;
        };
    }

    /**
     * Returns all talents of this hero, including nullable.
     *
     * @return all talents of this hero, including nullable.
     */
    public List<Talent> getTalents() {
        final List<Talent> talents = Lists.newArrayList();

        talents.add(getFirstTalent());
        talents.add(getSecondTalent());
        talents.add(getPassiveTalent());

        // extra talents
        talents.add(getThirdTalent());
        talents.add(getFourthTalent());
        talents.add(getFifthTalent());

        return talents;
    }

    /**
     * Returns all talents of this hero, excluding nullable, sorted by:
     * <ol>
     *     <li>First Talent</li>
     *     <li>Second Talent</li>
     *     <li>Third Talent</li>
     *     <li>Fourth Talent</li>
     *     <li>Fifth Talent</li>
     *     <li>Passive Talent</li>
     * </ol>
     *
     * @return all talents of this hero, excluding nullable, sorted.
     */
    public List<Talent> getTalentsSorted() {
        final List<Talent> talents = Lists.newArrayList();
        talents.add(getFirstTalent());
        talents.add(getSecondTalent());

        // Extra talents
        talents.add(getThirdTalent());
        talents.add(getFourthTalent());
        talents.add(getFifthTalent());

        talents.add(getPassiveTalent());

        return talents;
    }

    /**
     * Returns this hero name in SmallCaps.
     *
     * @return this hero name in SmallCaps.
     * @see me.hapyl.fight.util.SmallCaps
     */
    public String getNameSmallCaps() {
        return SmallCaps.format(getName());
    }

    @Override
    public String toString() {
        if (this instanceof HeroPlaque plaque) {
            final long until = plaque.until();

            if (until == -1L || System.currentTimeMillis() <= until) {
                return "&a" + getName() + " " + plaque.text();
            }
        }

        return "&a" + getName();
    }

    private void cancelOldReverseTask(Player player) {
        final GameTask oldTask = reverseTasks.get(player);

        if (oldTask != null && !oldTask.isCancelled()) {
            oldTask.cancel();
        }

        reverseTasks.remove(player);
    }

    /**
     * Returns true if there is a game in progress and player is in game.
     *
     * @param player - Player.
     * @return true, if there is a game in progress and player is in game.
     */
    private boolean validPlayerInGame(Player player) {
        final Manager current = Manager.current();
        return current.isGameInProgress() && current.isPlayerInGame(player);
    }
}
