package me.hapyl.fight.game.heroes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.eterna.module.annotate.Super;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.player.PlayerSkin;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.eterna.module.util.SmallCaps;
import me.hapyl.fight.CF;
import me.hapyl.fight.annotate.AutoRegisteredListener;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.collection.HeroStatsCollection;
import me.hapyl.fight.database.entry.ExperienceEntry;
import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.database.key.DatabaseKeyed;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.Event;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.skin.Skins;
import me.hapyl.fight.game.element.ElementHandler;
import me.hapyl.fight.game.element.PlayerElementHandler;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.heroes.equipment.Slot;
import me.hapyl.fight.game.heroes.friendship.HeroFriendship;
import me.hapyl.fight.game.heroes.mastery.HeroMastery;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Catchers;
import me.hapyl.fight.util.Formatted;
import me.hapyl.fight.util.NullSafeList;
import me.hapyl.fight.util.SingletonBehaviour;
import me.hapyl.fight.util.displayfield.DisplayFieldProvider;
import me.hapyl.fight.util.strict.StrictPackage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Base Hero class.
 * <p>
 * A hero <b>must</b> contains {@link #getFirstTalent()}, {@link #getSecondTalent()} and {@link #getPassiveTalent()},
 * but may or may not have up to three extra talents if needed.
 *
 * @see ElementHandler
 */
@AutoRegisteredListener
@StrictPackage("me.hapyl.fight.game.heroes")
public abstract class Hero
        extends
        SingletonBehaviour
        implements
        DatabaseKeyed, ElementHandler, PlayerElementHandler,
        Rankable, DisplayFieldProvider, Formatted {

    private final DatabaseKey key;
    private final HeroStatsCollection stats;
    private final HeroAttributes attributes;
    private final Equipment equipment;
    private final String name;
    private final HeroPlayerItemMaker itemMaker;
    private final HeroFriendship friendship;
    private final Map<Talent, HotbarSlots> talentsMapped;
    private final ArchetypeList archetypes;
    @Nonnull public HeroEventHandler eventHandler;

    protected HeroMastery mastery;
    protected UltimateTalent ultimate;

    private Affiliation affiliation;
    private Gender gender;
    private Race race;
    private String description;
    private ItemStack guiTexture;
    private Weapon weapon;
    private long minimumLevel;
    private PlayerSkin skin;
    private int rank;
    private String guiTextureUrl = "";

    @Super
    public Hero(@Nonnull DatabaseKey key, @Nonnull String name) {
        this.key = key;
        this.name = name;
        this.stats = new HeroStatsCollection(key);
        this.description = "No description provided.";
        this.guiTexture = new ItemStack(Material.RED_BED);
        this.weapon = new Weapon(Material.WOODEN_SWORD);
        this.equipment = new Equipment();
        this.attributes = new HeroAttributes(this);
        this.affiliation = Affiliation.NOT_SET;
        this.archetypes = new ArchetypeList(this);
        this.minimumLevel = 0;
        this.itemMaker = new HeroPlayerItemMaker(this);
        this.ultimate = null;
        this.skin = null;
        this.friendship = new HeroFriendship(this);
        this.talentsMapped = Maps.newHashMap();
        this.gender = Gender.UNKNOWN;
        this.race = Race.HUMAN; // defaulted to human
        this.mastery = new HeroMastery(this);

        // Map talents
        mapTalent(HotbarSlots.TALENT_1);
        mapTalent(HotbarSlots.TALENT_2);
        mapTalent(HotbarSlots.TALENT_3);
        mapTalent(HotbarSlots.TALENT_4);
        mapTalent(HotbarSlots.TALENT_5);

        setItem("null"); // default to null because I don't like exceptions

        // Register listener if needed
        if (this instanceof Listener listener) {
            CF.registerEvents(listener);
        }

        this.eventHandler = new HeroEventHandler(this);

        // Instantiate singleton
        SingletonBehaviour.instantiate(this);
    }

    @Nonnull
    @Override
    public final DatabaseKey getDatabaseKey() {
        return key;
    }

    @Nonnull
    public Gender getGender() {
        return gender;
    }

    public void setGender(@Nonnull Gender sex) {
        this.gender = sex;
    }

    @Nonnull
    public Race getRace() {
        return race;
    }

    public void setRace(@Nonnull Race race) {
        this.race = race;
    }

    @Override
    public int getRank() {
        return rank;
    }

    @Override
    public void setRank(int rank) {
        this.rank = rank;
    }

    @Nonnull
    public HeroStatsCollection getStats() {
        return stats;
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
    public HeroPlayerItemMaker getItemMaker() {
        return itemMaker;
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
    public ArchetypeList getArchetypes() {
        return archetypes;
    }

    public void setArchetypes(@Nonnull Archetype... archetype) {
        archetypes.addAll(archetype);
    }

    /**
     * Returns the origin of this hero.
     *
     * @return the origin of this hero.
     */
    public Affiliation getAffiliation() {
        return affiliation;
    }

    /**
     * Sets the origin for this hero.
     *
     * @param affiliation - New origin.
     */
    public void setAffiliation(Affiliation affiliation) {
        this.affiliation = affiliation;
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
        return getUltimate().getDuration();
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
     * Returns ticks when player used their ultimate. Or -1 if they haven't used yet.
     *
     * @param player - Player.
     * @return ticks when player used their ultimate. Or -1 if they haven't used yet.
     */
    public long getUsedUltimateAt(GamePlayer player) {
        return player.usedUltimateAt;
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
     * Gets either this hero's head texture item, or, if the player has a skin, the skin's texture.
     *
     * @param player - Player.
     */
    @Nonnull
    public ItemStack getItem(@Nonnull GamePlayer player) {
        final Skins skin = player.getSelectedSkin(this);

        if (skin == null) {
            return getItem();
        }

        return skin.getSkin().getEquipment().getItem(Slot.HELMET);
    }

    @Nonnull
    public ItemStack getItem(@Nonnull Player player) {
        final PlayerProfile profile = PlayerProfile.getProfile(player);

        if (profile == null) {
            return getItem();
        }

        final PlayerDatabase database = profile.getDatabase();
        final Skins skin = database.skinEntry.getSelected(this);

        if (skin == null) {
            return getItem();
        }

        return skin.getSkin().getEquipment().getItem(Slot.HELMET);
    }

    /**
     * Returns this hero GUI item, defaults to RED_BED.
     *
     * @return this hero GUI item, defaults to RED_BED.
     */
    @Nonnull
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
        this.guiTextureUrl = texture64;

        guiTexture = ItemBuilder.playerHeadUrl(texture64).asIcon();
        getEquipment().setTexture(texture64);
    }

    @Nonnull
    public String getTextureUrl() {
        return guiTextureUrl;
    }

    /**
     * Called whenever a player presses the ultimate, no matter if it's ready or not.
     */
    @Event
    public void onUltimateKeyPressed(@Nonnull GamePlayer player, @Nonnull UltimateStatus status) {
    }

    /**
     * Gets this hero's ultimate.
     *
     * @return this hero's ultimate.
     */
    @Nonnull
    public UltimateTalent getUltimate() {
        Catchers.catchNull(ultimate, "Ultimate is not set for %s!");
        return ultimate;
    }

    public void setUltimate(@Nonnull UltimateTalent ultimate) {
        this.ultimate = ultimate;
        copyDisplayFieldsTo(ultimate);
    }

    /**
     * Returns this hero a talent.
     *
     * @return this hero a talent.
     */
    @ReturnValueMustBeAConstant(of = TalentRegistry.class)
    public abstract Talent getFirstTalent();

    /**
     * Returns this hero b talent.
     *
     * @return this hero b talent.
     */
    @ReturnValueMustBeAConstant(of = TalentRegistry.class)
    public abstract Talent getSecondTalent();

    /**
     * Returns this hero passive talent.
     *
     * @return this hero passive talent.
     */
    @ReturnValueMustBeAConstant(of = TalentRegistry.class)
    public abstract Talent getPassiveTalent();

    /**
     * Gets this hero third talent, if exists.
     *
     * @return this hero third talent, if exists.
     */
    @ReturnValueMustBeAConstant(of = TalentRegistry.class)
    public Talent getThirdTalent() {
        return null;
    }

    /**
     * Gets this hero fourth talent, if exists.
     *
     * @return this hero fourth talent, if exists.
     */
    @ReturnValueMustBeAConstant(of = TalentRegistry.class)
    public Talent getFourthTalent() {
        return null;
    }

    /**
     * Gets this hero fifth talent, if exists.
     *
     * @return this hero fifth talent, if exists.
     */
    @ReturnValueMustBeAConstant(of = TalentRegistry.class)
    public Talent getFifthTalent() {
        return null;
    }

    /**
     * Called whenever a player who uses this hero <b>takes</b> damage.
     *
     * @param instance - Damage instance.
     */
    @Event
    public void processDamageAsVictim(@Nonnull DamageInstance instance) {
    }

    /**
     * Called whenever a player who uses this hero <b>deals</b> damage.
     *
     * @param instance - Damage instance.
     */
    @Event
    public void processDamageAsDamager(@Nonnull DamageInstance instance) {
    }

    /**
     * Called whenever a player who uses this hero <b>deals</b> damage with a {@link Projectile}.
     *
     * @param instance   - Damage instance.
     * @param projectile - Projectile.
     */
    @Event
    public void processDamageAsDamagerProjectile(@Nonnull DamageInstance instance, @Nonnull Projectile projectile) {
    }

    /**
     * Called whenever invisible player dealt damage.
     *
     * @param player - Player, who dealt damage. Always invisible.
     * @param entity - Entity that took damage.
     * @param damage - Damage dealt.
     * @return true to cancel damage, false to allow.
     */
    public boolean processInvisibilityDamage(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity, double damage) {
        player.sendMessage("&cCannot deal damage while invisible!");
        return true;
    }

    /**
     * Called whenever player damages an allied entity.
     *
     * @param player   - Player, who damaged the entity.
     * @param entity   - Entity who was damager.
     * @param instance - The current damage instance.
     * @return true if the damage should be cancelled; false otherwise.
     */
    @Event
    public boolean processTeammateDamage(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity, DamageInstance instance) {
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

    @Nonnull
    public HeroMastery getMastery() {
        return this.mastery;
    }

    protected void setMastery(@Nonnull HeroMastery mastery) {
        this.mastery = mastery;
    }

    @Nonnull
    public PlayerRating getAverageRating() {
        final PlayerRating rating = stats.getAverageRating();

        return rating != null ? rating : PlayerRating.FIVE;
    }

    public int getActiveTalentsCount() {
        return talentsMapped.size();
    }

    /**
     * Returns if this {@link GamePlayer} is considered "valid" if they're invisible.
     *
     * @param player - player.
     * @return true if this player is valid if they're invisible; false otherwise.
     */
    public boolean isValidIfInvisible(@Nonnull GamePlayer player) {
        return false;
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
    public void setWeapon(@Nonnull Weapon weapon) {
        this.weapon = weapon;
    }

    /**
     * Gets a set of {@link GamePlayer} whose selected hero is this hero.
     *
     * @return set of player using this hero.
     */
    @Nonnull
    public Set<GamePlayer> getPlayers() {
        return CF.getPlayers(player -> {
            return player.getHero().equals(this);
        });
    }

    /**
     * Gets all players that are using this hero who is alive.
     *
     * @return set of living player using this hero.
     */
    @Nonnull
    public Set<GamePlayer> getAlivePlayers() {
        return CF.getAlivePlayers(player -> {
            return player.getHero().equals(this) && player.isAlive();
        });
    }

    /**
     * Returns true if there is a game in progress and the player is in game, and the player's selected hero is the same as the one provided.
     *
     * @param player - Player.
     * @return true, if there is a game in progress and player is in game, and player's selected hero is the same as the one provided.
     */
    public final boolean validatePlayer(@Nullable GamePlayer player) {
        return player != null
                && player.isInGameOrTrial()
                && player.getHero().equals(this);
    }

    public final boolean isSelected(@Nonnull GamePlayer player) {
        return player.getHero().equals(this);
    }

    public final boolean validatePlayer(@Nonnull Player player) {
        return validatePlayer(CF.getPlayer(player));
    }

    public final boolean isLocked(@Nonnull Player player) {
        final PlayerDatabase database = PlayerDatabase.getDatabase(player);

        final boolean purchased = database.heroEntry.isPurchased(this);
        final boolean hasLevel = database.experienceEntry.get(ExperienceEntry.Type.LEVEL) >= getMinimumLevel();

        return !purchased && !hasLevel;
    }

    public final void setFavourite(@Nonnull Player player, boolean isFavourite) {
        final PlayerProfile profile = PlayerProfile.getProfile(player);

        if (profile == null) {
            return;
        }

        profile.getDatabase().heroEntry.setFavourite(this, isFavourite);
    }

    @Nonnull
    @Override
    public final String getFormatted() {
        return getFormatted(Color.WHITE);
    }

    @Nonnull
    public final String getFormatted(@Nonnull Color color) {
        return getPrefix() + color + " " + getNameSmallCaps();
    }

    @Nonnull
    public final String getPrefix() {
        return getArchetypes().getFirst().getPrefix();
    }

    public final boolean isValidHero() {
        return !(this instanceof Disabled);
    }

    public final boolean isFavourite(@Nonnull Player player) {
        final PlayerProfile profile = PlayerProfile.getProfile(player);

        return profile != null && profile.getDatabase().heroEntry.isFavourite(this);
    }

    @Nullable
    public ItemStack getTalentItem(@Nonnull HotbarSlots slot) {
        final Talent talent = getTalent(slot);

        return talent != null ? talent.getItem() : null;
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
    @Nonnull
    public List<Talent> getTalents() {
        final List<Talent> talents = Lists.newArrayList();

        talents.add(getFirstTalent());
        talents.add(getSecondTalent());
        talents.add(getPassiveTalent());
        talents.add(getThirdTalent());
        talents.add(getFourthTalent());
        talents.add(getFifthTalent());

        return talents;
    }

    @Nonnull
    public NullSafeList<Talent> getNullSafeTalents() {
        return new NullSafeList<>(getTalents());
    }

    /**
     * Gets a {@link HotbarSlots} for a talent by its handle.
     *
     * @param talent - Talent.
     * @return the slot for this talent.
     * @throws IllegalArgumentException - If the given talent does not belong to this hero.
     */
    @Nonnull
    public HotbarSlots getTalentSlotByHandle(@Nonnull Talent talent) {
        final HotbarSlots slot = talentsMapped.get(talent);

        if (slot == null) {
            throw new IllegalArgumentException("talent '%s' does not belong to this hero!".formatted(talent));
        }

        return slot;
    }

    @Nullable
    public HotbarSlots getTalentSlotByHandleOrNull(@Nonnull Talent talent) {
        return talentsMapped.get(talent);
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
     * @see SmallCaps
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

    @Override
    public final int hashCode() {
        return Objects.hashCode(key);
    }

    private void mapTalent(HotbarSlots slot) {
        final Talent talent = getTalent(slot);

        if (talent == null) {
            return;
        }

        if (talentsMapped.containsKey(talent)) {
            throw new IllegalArgumentException("Duplicate talent in " + getName() + "!");
        }

        talentsMapped.put(talent, slot);
    }

}
