package me.hapyl.fight.game.heroes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.fight.CF;
import me.hapyl.fight.annotate.PreferredReturnValue;
import me.hapyl.fight.annotate.PreprocessingMethod;
import me.hapyl.fight.event.io.DamageInput;
import me.hapyl.fight.event.io.DamageOutput;
import me.hapyl.fight.game.*;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.heroes.equipment.Slot;
import me.hapyl.fight.game.heroes.friendship.HeroFriendship;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.playerskin.PlayerSkin;
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
 * but may or may not have up to three extra talents if needed.
 *
 * @see GameElement
 * @see PlayerElement
 */
public abstract class Hero implements GameElement, PlayerElement {

    private final HeroAttributes attributes;
    private final Equipment equipment;
    private final String name;
    private final Map<GamePlayer, Long> usedUltimateAt;
    private final Map<GamePlayer, GameTask> reverseTasks;
    private final CachedHeroItem cachedHeroItem;
    private final HeroFriendship friendship;

    private Origin origin;
    private Archetype archetype;
    private String description;
    private ItemStack guiTexture;
    private Weapon weapon;
    private long minimumLevel;
    private UltimateTalent ultimate;
    private PlayerSkin skin;

    @Super
    public Hero(String name) {
        this.name = name;
        this.description = "No description provided.";
        this.guiTexture = new ItemStack(Material.RED_BED);
        this.weapon = new Weapon(Material.WOODEN_SWORD);
        this.usedUltimateAt = Maps.newHashMap();
        this.reverseTasks = Maps.newConcurrentMap();
        this.equipment = new Equipment();
        this.attributes = new HeroAttributes(this);
        this.origin = Origin.NOT_SET;
        this.archetype = Archetype.NOT_SET;
        this.minimumLevel = 0;
        this.ultimate = new UltimateTalent("Unknown Ultimate", "This hero's ultimate talent is not yet implemented!", Integer.MAX_VALUE);
        this.cachedHeroItem = new CachedHeroItem(this);
        this.skin = null;
        this.friendship = new HeroFriendship(this);

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
    public HeroFriendship getFriendship() {
        return friendship;
    }

    @Nullable
    public PlayerSkin getSkin() {
        return skin;
    }

    public void setSkin(@Nullable PlayerSkin skin) {
        this.skin = skin;
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
    @Nonnull
    public Equipment getEquipment() {
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
    public final void setUsingUltimate(GamePlayer player, boolean flag) {
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
    public long getUsedUltimateAt(GamePlayer player) {
        return usedUltimateAt.getOrDefault(player, -1L);
    }

    /**
     * Returns millis left until player can use their ultimate again.
     *
     * @param player - Player.
     * @return millis left until player can use their ultimate again.
     */
    public long getUltimateDurationLeft(GamePlayer player) {
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
    public final void setUsingUltimate(GamePlayer player, boolean flag, int reverseAfter) {
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
    public final boolean isUsingUltimate(@Nullable GamePlayer player) {
        return player != null && usedUltimateAt.containsKey(player);
    }

    /**
     * Returns the name of this hero.
     *
     * @return name of this hero.
     */
    @Nonnull
    public String getName() {
        return name;
    }

    /**
     * Returns description of this hero.
     *
     * @return description of this hero.
     */
    @Nonnull
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
        return (guiTexture.getType() == Material.RED_BED) ? getEquipment().getItem(Slot.HELMET) : guiTexture;
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
    @Event
    public void onUltimateKeyPressed(@Nonnull GamePlayer player, @Nonnull UltimateStatus status) {
    }

    /**
     * Unleashes this hero's ultimate.
     *
     * @return the ultimate callback. The callback will be executed if, and only if the ultimate has a cast duration.
     */
    @Nullable
    @PreferredReturnValue("UltimateCallback#OK")
    public abstract UltimateCallback useUltimate(@Nonnull GamePlayer player);

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

    /**
     * Gets this hero third talent, if exists.
     *
     * @return this hero third talent, if exists.
     */
    @ReturnValueMustBeAConstant
    public Talent getThirdTalent() {
        return null;
    }

    /**
     * Gets this hero fourth talent, if exists.
     *
     * @return this hero fourth talent, if exists.
     */
    @ReturnValueMustBeAConstant
    public Talent getFourthTalent() {
        return null;
    }

    /**
     * Gets this hero fifth talent, if exists.
     *
     * @return this hero fifth talent, if exists.
     */
    @ReturnValueMustBeAConstant
    public Talent getFifthTalent() {
        return null;
    }

    /**
     * Called when player DAMAGES something.
     * <p>
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
     * <b>
     * Keep in mind the player who damaged is a damager in the input, not the entity!
     * </b>
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
     * <p>
     * <h2>Examples:</h2>
     * <blockquote>
     * Reduce INCOMING damage by 50%:
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
     * Called when player DAMAGES something via projectile.
     * <p>
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
    @Event
    public void onDeath(@Nonnull GamePlayer player) {
    }

    /**
     * Executes upon ANY player death.
     *
     * @param player - Player.
     * @param killer - Killer.
     * @param cause  - Cause.
     */
    @Event
    public void onDeathGlobal(@Nonnull GamePlayer player, @Nullable GameEntity killer, @Nullable EnumDamageCause cause) {
    }

    /**
     * @see GameElement#onStart()
     */
    @Override
    public void onStart() {
    }

    /**
     * @see GameElement#onStop()
     */
    @Override
    public void onStop() {
    }

    /**
     * Called whenever player's ultimate is over.
     *
     * @param player - Player.
     */
    public void onUltimateEnd(@Nonnull GamePlayer player) {
    }

    /**
     * Predicate for ultimate. Return true if a player is able to use their ultimate, false otherwise.
     *
     * @param player - Player, who is trying to use ultimate.
     * @return true if a player is able to use their ultimate, false otherwise.
     */
    public boolean predicateUltimate(@Nonnull GamePlayer player) {
        return true;
    }

    /**
     * Return the message that will be displayed if player CANNOT use their ultimate, aka {@link #predicateUltimate(GamePlayer)} returns false.
     *
     * @param player - Player, who is trying to use ultimate.
     * @return the message that will be displayed if player CANNOT use their ultimate.
     */

    public String predicateMessage(@Nonnull GamePlayer player) {
        return "Unable to use now.";
    }

    /**
     * Called whenever player respawns.
     *
     * @param player - Player.
     */
    public void onRespawn(@Nonnull GamePlayer player) {
    }

    /**
     * Returns this hero ultimate.
     *
     * @return this hero ultimate.
     */
    @Nonnull
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

    @PreprocessingMethod
    public final void useUltimate0(GamePlayer player) {
        final int castDuration = ultimate.getCastDuration();
        final UltimateCallback callback = useUltimate(player);

        if (castDuration > 0 && callback != null) {
            new GameTask() {
                @Override
                public void run() {
                    callback.callback(player);
                }
            }.runTaskLater(castDuration);
        }
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
     * Gets all players that are using this hero.
     *
     * @return list of player using this hero.
     */
    @Nonnull
    public List<GamePlayer> getPlayers() {
        return CF.getAlivePlayers(predicate -> predicate.getHero() == this);
    }

    /**
     * Gets all players that are using this hero who is alive.
     *
     * @return list of living player using this hero.
     */
    @Nonnull
    public List<GamePlayer> getAlivePlayers() {
        final List<GamePlayer> players = getPlayers();
        players.removeIf(player -> !player.isAlive());

        return players;
    }

    /**
     * Returns true if there is a game in progress and the player is in game, and the player's selected hero is the same as the one provided.
     *
     * @param player - Player.
     * @return true, if there is a game in progress and player is in game, and player's selected hero is the same as the one provided.
     */
    public final boolean validatePlayer(@Nullable GamePlayer player) {
        final Manager current = Manager.current();
        return player != null && validPlayerInGame(player) && current.getCurrentHero(player) == this;
    }

    public final boolean validatePlayer(Player player) {
        final GamePlayer gamePlayer = CF.getPlayer(player);

        return gamePlayer != null && validatePlayer(gamePlayer);
    }

    @Nullable
    public Talent getTalent(@Nonnull HotbarSlots slot) {
        return switch (slot) {
            case TALENT_1 -> getFirstTalent();
            case TALENT_2 -> getSecondTalent();
            case TALENT_3 -> getThirdTalent();
            case TALENT_4 -> getFourthTalent();
            case TALENT_5 -> getFifthTalent();
            default -> null;
        };
    }

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
    @Nonnull
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

    @Nonnull
    public List<Talent> getActiveTalents() {
        final List<Talent> talents = Lists.newArrayList();

        talents.add(getFirstTalent());
        talents.add(getSecondTalent());
        talents.add(getThirdTalent());
        talents.add(getFourthTalent());
        talents.add(getFifthTalent());

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

    protected void setUltimate(UltimateTalent ultimate, Consumer<UltimateTalent> andThen) {
        setUltimate(ultimate);
        andThen.accept(ultimate);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Heroes cannot be cloned.");
    }

    private void cancelOldReverseTask(GamePlayer player) {
        final GameTask oldTask = reverseTasks.remove(player);

        if (oldTask != null && !oldTask.isCancelled()) {
            oldTask.cancel();
        }
    }

    /**
     * Returns true if there is a game in progress and player is in game.
     *
     * @param player - Player.
     * @return true, if there is a game in progress and player is in game.
     */
    private boolean validPlayerInGame(GamePlayer player) {
        final Manager current = Manager.current();
        return current.isGameInProgress() && current.isPlayerInGame(player);
    }
}
