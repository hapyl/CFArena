package me.hapyl.fight.game.talents;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.annotate.SelfReturn;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.eterna.module.util.Described;
import me.hapyl.eterna.module.util.Named;
import me.hapyl.fight.CF;
import me.hapyl.fight.MaterialData;
import me.hapyl.fight.annotate.AutoRegisteredListener;
import me.hapyl.fight.annotate.ExecuteOrder;
import me.hapyl.fight.annotate.PreprocessingMethod;
import me.hapyl.fight.event.custom.TalentPreconditionEvent;
import me.hapyl.fight.event.custom.TalentUseEvent;
import me.hapyl.fight.game.*;
import me.hapyl.fight.game.challenge.ChallengeType;
import me.hapyl.fight.game.element.ElementHandler;
import me.hapyl.fight.game.element.PlayerElementHandler;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.loadout.HotBarLoadout;
import me.hapyl.fight.game.loadout.HotBarSlot;
import me.hapyl.fight.game.stats.StatContainer;
import me.hapyl.fight.registry.Registries;
import me.hapyl.fight.util.SingletonBehaviour;
import me.hapyl.fight.util.displayfield.DisplayFieldInstance;
import me.hapyl.fight.util.displayfield.DisplayFieldProvider;
import me.hapyl.fight.util.strict.StrictPackage;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Represents a base talent class.
 *
 * @see InputTalent
 * @see ChargedTalent
 * @see UltimateTalent
 * @see PassiveTalent
 */
@AutoRegisteredListener
@StrictPackage("me.hapyl.fight.game.talents")
public abstract class Talent
        extends
        SingletonBehaviour
        implements
        ElementHandler, PlayerElementHandler, DisplayFieldProvider,
        Named, Described, Timed,
        Cooldown, Keyed, ItemFactoryProvider {
    
    private final Key key;
    private final List<DisplayFieldInstance> extraDisplayFields;
    private final TalentItemFactory itemFactory;
    
    protected TalentType type;
    protected String name;
    @Nonnull protected String description;
    protected MaterialData material;
    protected String castMessage;
    protected String altUsage;
    protected int point;
    protected int cd;
    protected int duration;
    protected boolean autoAdd;
    
    public Talent(@Nonnull Key key, @Nonnull String name) {
        this.key = key;
        this.name = name;
        this.description = "";
        this.extraDisplayFields = Lists.newArrayList();
        this.type = TalentType.DAMAGE;
        this.material = () -> Material.BEDROCK;
        this.altUsage = "This talent is not given when the game starts, but there is a way to use it.";
        this.autoAdd = true;
        this.point = 1;
        this.duration = 0;
        this.itemFactory = new TalentItemFactory(this);
        
        if (this instanceof Listener listener) {
            CF.registerEvents(listener);
        }
        
        // Instantiate singleton
        SingletonBehaviour.instantiate(this);
    }
    
    /**
     * Juices the base builder, used to change amount, glowing, etc.
     *
     * @param builder - The builder to juice.
     * @see #juiceDescription(ItemBuilder)
     * @see #juiceDetails(ItemBuilder)
     */
    public void juice(@Nonnull ItemBuilder builder) {
    }
    
    /**
     * Juices the description and outline description builder.
     *
     * @param builder - The builder to juice.
     * @see #juice(ItemBuilder)
     * @see #juiceDetails(ItemBuilder)
     */
    public void juiceDescription(@Nonnull ItemBuilder builder) {
    }
    
    /**
     * Juices the details builder.
     *
     * @param builder - The builder to juice.
     * @see #juice(ItemBuilder)
     * @see #juiceDescription(ItemBuilder)
     */
    public void juiceDetails(@Nonnull ItemBuilder builder) {
    }
    
    @Nonnull
    @Override
    public TalentItemFactory itemFactory() {
        return itemFactory;
    }
    
    @Nonnull
    @Override
    public List<DisplayFieldInstance> extraDisplayFields() {
        return extraDisplayFields;
    }
    
    @Nonnull
    public Key cooldownKey() {
        return this.key;
    }
    
    @Nonnull
    @Override
    public final Key getKey() {
        return this.key;
    }
    
    @Override
    public final int hashCode() {
        return Objects.hashCode(this.key);
    }
    
    // defaults to 1 point per 10 seconds of cooldown
    public void defaultPointGeneration() {
        point = calcPointGeneration(cd);
    }
    
    public void setAltUsage(String altUsage) {
        this.altUsage = altUsage;
    }
    
    @Override
    public int getDuration() {
        return duration;
    }
    
    @Override
    public Talent setDuration(int duration) {
        this.duration = duration;
        return this;
    }
    
    @Override
    public Talent setDurationSec(float duration) {
        return setDuration((int) (duration * 20));
    }
    
    public int getPoint() {
        return point;
    }
    
    @ExecuteOrder(after = { "setCooldown(int)", "setCooldownSec(int)" })
    public void setPoint(int point) {
        this.point = point;
    }
    
    public String getCastMessage() {
        return castMessage;
    }
    
    public void setCastMessage(String castMessage) {
        this.castMessage = castMessage;
    }
    
    /**
     * Adds an empty description.
     */
    public void addDescription() {
        addDescription("\n");
    }
    
    /**
     * Adds description to existing one.
     *
     * @param description - Description.
     * @param format      - Formatters if needed.
     */
    public void addDescription(@Nonnull String description, @Nullable Object... format) {
        this.description += description.formatted(format);
    }
    
    /**
     * Adds description to existing one, then, appends a new line.
     *
     * @param description - Description.
     * @param format      - Formatters if needed.
     */
    public void addNlDescription(@Nonnull String description, @Nullable Object... format) {
        addDescription(description, format);
        this.description += "\n";
    }
    
    public boolean isAutoAdd() {
        return autoAdd;
    }
    
    public void setAutoAdd(boolean autoAdd) {
        this.autoAdd = autoAdd;
    }
    
    public Material getMaterial() {
        return material.material();
    }
    
    @SelfReturn
    public Talent setMaterial(@Nonnull Material material) {
        this.material = () -> material;
        return this;
    }
    
    @SelfReturn
    public Talent setMaterial(@Nonnull MaterialData material) {
        this.material = material;
        return this;
    }
    
    /**
     * Called once on game start.
     */
    @Override
    public void onStart(@Nonnull GameInstance instance) {
    }
    
    /**
     * Called once on game stop.
     */
    @Override
    public void onStop(@Nonnull GameInstance instance) {
    }
    
    /**
     * Called every time player dies.
     *
     * @param player - Player that died.
     */
    public void onDeath(@Nonnull GamePlayer player) {
    }
    
    @Nonnull
    public String getTalentClassType() {
        return "Talent";
    }
    
    @Nonnull
    public String getTypeFormattedWithClassType() {
        return type.getName() + " " + getTalentClassType();
    }
    
    @SelfReturn
    public Talent setTexture(@Nonnull String texture64) {
        this.material = new MaterialData() {
            @Nonnull
            @Override
            public Material material() {
                return Material.PLAYER_HEAD;
            }
            
            @Nonnull
            @Override
            public Consumer<ItemBuilder> function() {
                return builder -> builder.setHeadTextureUrl(texture64);
            }
        };
        return this;
    }
    
    @Nullable
    public abstract Response execute(@Nonnull GamePlayer player);
    
    @PreprocessingMethod
    @Nonnull
    public final Response execute0(@Nonnull GamePlayer player) {
        final Response precondition = precondition(player);
        
        if (precondition.isError()) {
            return precondition;
        }
        
        final Response response = execute(player);
        
        if (castMessage != null) {
            player.sendMessage(castMessage);
        }
        
        // If error, don't progress talent
        if (response == null || response == Response.ERROR) {
            return response == null ? Response.ERROR_DEFAULT : response;
        }
        
        if (!response.isError()) {
            postProcessTalent(player);
        }
        
        return response;
    }
    
    // Performs post-process for a talent, such as storing stats, progressing achievements, etc.
    public final void postProcessTalent(@Nonnull GamePlayer player) {
        // Progress ability usage
        final StatContainer stats = player.getStats();
        
        stats.addAbilityUsage(this);
        
        // Progress achievement
        Registries.achievements().USE_TALENTS.complete(player);
        
        // Progress bond
        ChallengeType.USE_TALENTS.progress(player);
        
        new TalentUseEvent(player, this).callEvent();
    }
    
    public final void startCooldown(@Nonnull GamePlayer player, int cooldown) {
        if (cooldown <= 0 || CF.environment().ignoreCooldowns.isEnabled()) {
            return;
        }
        
        player.cooldownManager.setCooldown(cooldownKey(), cooldown);
    }
    
    public void startCooldown(@Nonnull GamePlayer player) {
        startCooldown(player, cd);
    }
    
    public final void stopCooldown(@Nonnull GamePlayer player) {
        player.cooldownManager.setCooldown(cooldownKey(), 0);
    }
    
    public boolean hasCooldown(@Nonnull GamePlayer player) {
        return getCooldownTimeLeft(player) > 0L;
    }
    
    public final int getCooldownTimeLeft(@Nonnull GamePlayer player) {
        return player.cooldownManager.getCooldown(cooldownKey());
    }
    
    @Override
    public int getCooldown() {
        return cd;
    }
    
    @Override
    @ExecuteOrder(before = { "setPoint(int)" })
    public Talent setCooldown(int cd) {
        this.cd = cd;
        defaultPointGeneration();
        return this;
    }
    
    @Nonnull
    public String getCooldownFormatted() {
        if (cd <= 0) {
            return "Dynamic";
        }
        
        return Tick.round(cd) + "s";
    }
    
    @Override
    @ExecuteOrder(before = { "setPoint(int)" })
    public Talent setCooldownSec(float cd) {
        return setCooldown((int) (cd * 20));
    }
    
    @Override
    @Nonnull
    public String getName() {
        return name;
    }
    
    @Override
    public void setName(@Nonnull String name) {
        this.name = name;
    }
    
    @Nonnull
    public TalentType getType() {
        return type;
    }
    
    public Talent setType(@Nonnull TalentType type) {
        this.type = type;
        return this;
    }
    
    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }
    
    /**
     * Replaces the existing description with provided.
     *
     * @param description - New description.
     */
    @Override
    public void setDescription(@Nonnull String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public boolean isDisplayAttributes() {
        return true;
    }
    
    public final void startCdIndefinitely(@Nonnull GamePlayer player) {
        player.cooldownManager.setCooldownIgnoreCooldownModifier(this, Constants.INDEFINITE_COOLDOWN);
    }
    
    @OverridingMethodsMustInvokeSuper
    public void giveItem(@Nonnull GamePlayer player, @Nonnull HotBarSlot slot) {
        if (!autoAdd) {
            return;
        }
        
        final PlayerInventory inventory = player.getInventory();
        final ItemStack talentItem = itemFactory.description.createItem();
        
        final HotBarLoadout loadout = player.getProfile().getHotbarLoadout();
        final int index = loadout.getInventorySlotBySlot(slot);
        
        inventory.setItem(index, talentItem);
    }
    
    @Nonnull
    public String cooldownString() {
        return "Cooldown";
    }
    
    @Nonnull
    public ItemStack getItem() {
        return itemFactory.description.createItem();
    }
    
    @Nonnull
    public ItemStack getItem(@Nonnull GamePlayer player) {
        return getItem();
    }
    
    public static int calcPointGeneration(int cd) {
        if (cd <= 0) {
            return 1;
        }
        
        return Math.clamp(cd / 200, 1, 100);
    }
    
    /**
     * Precondition player talent (or weapon) if it can be used.
     *
     * @param player - Player.
     * @return OK or an error with a message.
     */
    @Nonnull
    public static Response precondition(@Nonnull GamePlayer player) {
        final TalentPreconditionEvent event = new TalentPreconditionEvent(player);
        
        if (event.callEvent()) {
            return Response.error(event.getReason());
        }
        
        if (Manager.current().isGameInProgress()) {
            final State state = Manager.current().currentInstance().getGameState();
            if (state != State.IN_GAME) {
                return Response.error("The game is not yet started!");
            }
        }
        
        return Response.OK;
    }
}