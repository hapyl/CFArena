package me.hapyl.fight.game.talents;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.math.Numbers;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.CF;
import me.hapyl.fight.annotate.AutoRegisteredListener;
import me.hapyl.fight.annotate.ExecuteOrder;
import me.hapyl.fight.annotate.PreprocessingMethod;
import me.hapyl.fight.event.custom.TalentPreconditionEvent;
import me.hapyl.fight.event.custom.TalentUseEvent;
import me.hapyl.fight.game.*;
import me.hapyl.fight.game.challenge.ChallengeType;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.effect.effects.SlowingAuraEffect;
import me.hapyl.fight.game.element.ElementHandler;
import me.hapyl.fight.game.element.PlayerElementHandler;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.stats.StatContainer;
import me.hapyl.fight.registry.Registries;
import me.hapyl.fight.util.Condition;
import me.hapyl.fight.util.Nulls;
import me.hapyl.fight.util.SingletonBehaviour;
import me.hapyl.fight.util.displayfield.DisplayFieldProvider;
import me.hapyl.fight.util.displayfield.DisplayFieldSerializer;
import me.hapyl.fight.util.strict.StrictPackage;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Base talent.
 */
@AutoRegisteredListener
@StrictPackage("me.hapyl.fight.game.talents")
public abstract class Talent
        extends
        SingletonBehaviour
        implements
        ElementHandler, PlayerElementHandler, DisplayFieldProvider,
        Nameable, Timed, Cooldown,
        Keyed, NonNullItemCreator {

    private final Key key;
    private final List<String> attributeDescription;

    private TalentType type;
    private String name;
    @Nonnull
    private String description;
    private ItemStack itemStats;
    private int startAmount;
    private Material material;
    private String texture64;
    private String castMessage;
    private String altUsage;
    private Consumer<ItemBuilder> itemFunction;
    private int point;
    private int cd;
    private int duration;
    private boolean autoAdd;

    public Talent(@Nonnull Key key, @Nonnull String name) {
        this.key = key;
        this.name = name;
        this.description = "";
        this.attributeDescription = Lists.newArrayList();
        this.type = TalentType.DAMAGE;
        this.material = Material.BEDROCK;
        this.startAmount = 1;
        this.altUsage = "This talent is not given when the game starts, but there is a way to use it.";
        this.autoAdd = true;
        this.point = 1;
        this.duration = 0;

        if (this instanceof Listener listener) {
            CF.registerEvents(listener);
        }

        // Instantiate singleton
        SingletonBehaviour.instantiate(this);
    }

    @Nonnull
    @Override
    public final Key getKey() {
        return key;
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(key);
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

    public void addAttributeDescription(String key, Object value) {
        this.attributeDescription.add(DisplayFieldSerializer.DEFAULT_FORMATTER.format(key, String.valueOf(value)));
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

    /**
     * Replaces the existing description with provided.
     *
     * @param description - New description.
     */
    public void setDescription(@Nonnull String description) {
        this.description = description;
    }

    public boolean isAutoAdd() {
        return autoAdd;
    }

    public void setAutoAdd(boolean autoAdd) {
        this.autoAdd = autoAdd;
    }

    public Material getMaterial() {
        return material;
    }

    @Nonnull
    public ItemStack getItemAttributes() {
        if (itemStats == null) {
            // store the actual item, itemStats is assigned in createItem()
            item = createItem();
        }

        return itemStats;
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

    private ItemStack item;

    @Nonnull
    @Override
    public ItemStack getItem() {
        if (item == null) {
            item = createItem();
        }

        return item;
    }

    @Nonnull
    @Override
    public ItemStack createItem() {
        final ItemBuilder builderItem = ItemBuilder.of(material)
                .setName(name)
                .addLore("&8" + getTypeFormattedWithClassType())
                .addLore();

        builderItem.setAmount(startAmount);

        // Add head texture if an item is a player head
        if (material == Material.PLAYER_HEAD && texture64 != null) {
            builderItem.setHeadTextureUrl(texture64);
        }

        // Execute functions is present
        Nulls.runIfNotNull(itemFunction, function -> function.accept(builderItem));

        final String description = StaticFormat.formatTalent(this.description, this);

        builderItem.addTextBlockLore(description);

        // Append lore
        appendLore(builderItem);

        // Is ability having alternative usage, tell how to use it
        if (!autoAdd) {
            builderItem.addLore("");
            builderItem.addSmartLore(altUsage, "&8&o");
        }

        // cooldown
        builderItem.setCooldown(cd -> cd.setCooldownGroup(key.asNamespacedKey()));

        if (this instanceof UltimateTalent) {
            builderItem.glow();
        }

        ////////////////
        // Attributes //
        ////////////////
        final ItemBuilder builderAttributes = new ItemBuilder(builderItem.asIcon());

        // Details
        builderAttributes.removeLore();
        builderAttributes.setName(name).addLore("&8Details").addLore();

        // Type description
        builderAttributes.addLore("&f&l" + type.getName());
        builderAttributes.addSmartLore(type.getDescription());
        builderAttributes.addLore();

        builderAttributes.addLore("&f&lAttributes");

        // Cooldown
        if (cd > 0) {
            builderAttributes.addLore("Cooldown%s: &f&l%ss".formatted(
                    this instanceof ChargedTalent ? " between charges" : "",
                    BukkitUtils.roundTick(this.cd)
            ));
        }
        else if (cd <= -1) {
            builderAttributes.addLore("Cooldown: &f&lDynamic");
        }

        // Duration
        if (duration > 0) {
            builderAttributes.addLore("Duration: &f&l%ss".formatted(BukkitUtils.roundTick(duration)));
        }

        // Points generation
        if (point > 0 && !(this instanceof PassiveTalent || this instanceof UltimateTalent)) {
            builderAttributes.addLore("Point%s Generation: &f&l%s".formatted(point == 1 ? "" : "s", point));
        }

        // Display Fields
        DisplayFieldSerializer.serialize(builderAttributes, this);

        // Extra fields
        if (!attributeDescription.isEmpty()) {
            for (String string : attributeDescription) {
                builderAttributes.addLore(string);
            }
        }

        // Recharge time
        if (this instanceof ChargedTalent charge) {
            final int maxCharges = charge.getMaxCharges();
            final int rechargeTime = charge.getRechargeTime();

            builderAttributes.addLore("Max Charges: &f&l%s".formatted(maxCharges));
            if (rechargeTime >= 0) {
                builderAttributes.addLore("Recharge Time: &f&l%ss".formatted(BukkitUtils.roundTick(rechargeTime)));
            }
        }
        else if (this instanceof UltimateTalent ultimate) {
            final int castDuration = ultimate.getCastDuration();

            builderAttributes.addLore("Ultimate Cost: &f&l%s ※".formatted(ultimate.getMinCost()));

            if (this instanceof OverchargeUltimateTalent overchargedUltimate) {
                builderAttributes.addLore("Overcharge Cost: &f&l%s ※".formatted(overchargedUltimate.getCost()));
            }

            builderAttributes.addLore("Cast Duration: &f&l%s".formatted((castDuration == 0 ? "Instant" : Tick.round(castDuration) + "s")));
            builderAttributes.glow();
        }

        itemStats = builderAttributes.asIcon();

        return builderItem.asIcon();
    }

    public int getStartAmount() {
        return startAmount;
    }

    public Talent setStartAmount(int startAmount) {
        this.startAmount = Numbers.clamp(startAmount, 1, 64);
        return this;
    }

    public Talent setTexture(String texture64) {
        this.setItem(Material.PLAYER_HEAD);
        this.texture64 = texture64;
        return this;
    }

    @Nonnull
    public String getTexture64() {
        return texture64;
    }

    public Talent setItem(Material material) {
        this.material = material;
        return this;
    }

    public Talent setItem(Material material, Consumer<ItemBuilder> function) {
        setItem(material);
        itemFunction = function;

        return this;
    }

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
        Registries.getAchievements().USE_TALENTS.complete(player);

        // Progress bond
        ChallengeType.USE_TALENTS.progress(player);

        new TalentUseEvent(player, this).call();
    }

    public final void startCd(@Nonnull GamePlayer player, int cooldown) {
        if (cooldown <= 0) {
            return;
        }

        // If a player has slowing aura, modify cooldown
        if (player.hasEffect(Effects.SLOWING_AURA)) {
            cooldown *= ((SlowingAuraEffect) Effects.SLOWING_AURA.getEffect()).cooldownModifier;
        }

        // Don't start CD if in debug
        final DebugData debug = Manager.current().getDebug();
        if (debug.is(DebugData.Flag.IGNORE_COOLDOWN)) {
            return;
        }

        player.cooldownManager.setCooldown(this, cooldown);
    }

    public void startCd(@Nonnull GamePlayer player) {
        startCd(player, cd);
    }

    public final void stopCd(@Nonnull GamePlayer player) {
        player.cooldownManager.setCooldown(this, 0);
    }

    public final boolean hasCd(@Nonnull GamePlayer player) {
        return getCdTimeLeft(player) > 0L;
    }

    public final int getCdTimeLeft(@Nonnull GamePlayer player) {
        return player.cooldownManager.getCooldown(this);
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
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isDisplayAttributes() {
        return true;
    }

    @SuppressWarnings("all")
    @Deprecated
    public synchronized void nullifyItem() {
        item = null;
    }

    public final void startCdIndefinitely(@Nonnull GamePlayer player) {
        player.cooldownManager.setCooldown(this, Constants.INDEFINITE_COOLDOWN);
    }

    private String formatIfPossible(@Nonnull String toFormat, @Nullable Object... format) {
        if (format == null || format.length == 0) {
            return toFormat;
        }

        return toFormat.formatted(format);
    }

    public static int calcPointGeneration(int cd) {
        if (cd <= 0) {
            return 1;
        }

        return Math.clamp(cd / 200, 1, 100);
    }

    public static Condition<GamePlayer, Response> preconditionAnd(@Nonnull GamePlayer player) {
        final Response response = precondition(player);
        final Condition<GamePlayer, Response> condition = new Condition<>(player, response);

        if (response.isError()) {
            return condition.setStatus(false);
        }

        return condition.setStatus(true);
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

        if (event.call()) {
            return Response.error(event.getReason());
        }

        if (Manager.current().isGameInProgress()) {
            final State state = Manager.current().getCurrentGame().getGameState();
            if (state != State.IN_GAME) {
                return Response.error("The game is not yet started!");
            }
        }

        return Response.OK;
    }
}